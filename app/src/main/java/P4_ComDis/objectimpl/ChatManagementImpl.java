package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.ClientInternalMgInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;
import P4_ComDis.model.database.BDFacade;
import P4_ComDis.model.exceptions.DatabaseException;

public class ChatManagementImpl extends UnicastRemoteObject implements ChatManagementInterface {

    //Atributo: la lista de clientes conectados:
    private HashMap<String, ClientManagementInterface> clientsShared;
    private HashMap<String, ClientInternalMgInterface> clientsInternal;
    //Referencia a la fachada de la base de datos:
    private BDFacade bdFacade;

    public ChatManagementImpl() throws RemoteException {
        super();
        this.bdFacade = new BDFacade();
        this.clientsShared = new HashMap<>();
        this.clientsInternal = new HashMap<>();
    }
    
    @Override
    public ResultType loginToChat(User user, ClientManagementInterface clientInfo, ClientInternalMgInterface clientInternal) 
        throws RemoteException {
        //Intentamos hacer el login:
        try {
            bdFacade.login(user);
            //Añadimos las interfaces del cliente si el resultado ha sido satisfactorio:
            clientsShared.put(user.getUsername(), clientInfo);
            clientsInternal.put(user.getUsername(), clientInternal);
            //Habrá que notificar al cliente de los usuarios conectados, y a todos sus amigos de la conexión:
            System.out.println("Cliente " + user.getUsername() + " ha iniciado sesión");
            //Desde aqui se procederá a la notificación:
            this.notifyClientsOnConnect(user.getUsername(), clientInfo, clientInternal);
            return ResultType.OK;
        } catch(DatabaseException ex) {
            return ex.getResultType();
        }
    }

    @Override
    public void logoutFromChat(User user) throws RemoteException {
        //Se intenta hacer el logout:
        try{
            bdFacade.logout(user);
            //Se notifica el cierre de sesión correspondiente (si todo es correcto)
            if(clientsShared.containsKey(user.getUsername())){
                //En principio este if se debería cumplir, aunque en todo caso se avisaría si no fuese así.
                this.notifyClientsOnDisconnect(user.getUsername());
                //Se elimina el cliente de los hashmap:
                clientsShared.remove(user.getUsername());            
                clientsInternal.remove(user.getUsername());            
                System.out.println("Cliente " + user.getUsername() + " ha cerrado sesión");
            } else {
                System.out.println("Problema en desconexión del usuario: no se encuentra.");
            }
        } catch(DatabaseException ex){
            //Si se genera alguna excepción (no debería) se avisaría
            //Imaginemos, por ejemplo, que lo hace un usuario no autorizado (esto solo ocurriría si se modificase a posta la contraseña):
            System.out.println("Problemas en el cierre de sesión: " + ex.getMessage());
        }
    }

    @Override
    public ResultType registerInChat(User user, ClientManagementInterface clientInfo, ClientInternalMgInterface clientInternal) throws RemoteException {
        //Trataremos de registrar al usuario:
        try {
            bdFacade.register(user);
            //Si el método finaliza correctamente, entonces hacemos las mismas acciones que con un usuario normal:
            //Añadimos las interfaces del cliente si el resultado ha sido satisfactorio:
            clientsShared.put(user.getUsername(), clientInfo);
            clientsInternal.put(user.getUsername(), clientInternal);
            //Se avisa del registro:
            System.out.println("Cliente " + user.getUsername() + " registrado");
            //Eso sí, no se revisan las amistades, pues la cuenta acaba de crearse, así que no tiene sentido añadir nada.
            return ResultType.OK;
        } catch(DatabaseException ex) {
            //Si hay alguna excepción se devuelve el motivo:
            return ex.getResultType();
        }
    }

    @Override
    public List<String> searchFriends(User user, String pattern) throws RemoteException {
        //Se llama al método de la DB y se devuelve el resultado. Si el usuario no fuese válido ya no se devolvería nada:
        return bdFacade.getUserNamesByPattern(user, pattern);
    }

    @Override
    public ResultType sendFriendRequest(User user, String friendName) throws RemoteException {
        try {
            //Se intenta enviar la petición de amistad:
            bdFacade.sendRequest(user, friendName);
            //Se notifica al usuario de la petición recibida (si está conectado):
            if(clientsInternal.containsKey(friendName)){
                clientsInternal.get(friendName).notifyNewRequest(user.getUsername());
            }
            //Se devuelve estado correcto:
            return ResultType.OK;
        } catch (DatabaseException e) {
            //Se devuelve el resultType indicado en la excepción:
            return e.getResultType();
        }
    }

    @Override
    public ResultType acceptRequest(User user, String friendName) throws RemoteException {
        try {
            //Se intenta aceptar la solicitud.
            //Si se acepta, se notifica a ambos usuarios de la conexión del otro:
            bdFacade.acceptRequest(user, friendName);
            //Se comprueba, por si acaso, si está el cliente conectado:
            if(clientsInternal.containsKey(friendName)){
                //En ese caso procederemos a notificar uno al otro:
                clientsInternal.get(friendName).notifyConfirmation(clientsShared.get(user.getUsername()));
                //Se le notifica también la conexión:
                clientsInternal.get(user.getUsername()).notifyConnection(clientsShared.get(friendName));
            }
            //Se devuelve un OK:
            return ResultType.OK;
        } catch (DatabaseException ex) {
            //Si no se consigue aceptar la solicitud, se devuelve un error.
            return ex.getResultType();
        }
    }

    @Override
    public ResultType rejectFriendship(User user, String friendName) throws RemoteException {
        try{
            //Se borra la amistad: la solicitud es rechazada:
            bdFacade.deleteFriendship(user, friendName);
            //Si sigue correctamente, podemos notificar al cliente correspondiente del rechazo:
            if(clientsInternal.containsKey(friendName)){
                //Como no se llegaron a añadir los usuarios, no hay que notificar nada más:
                clientsInternal.get(friendName).notifyRejection(user.getUsername());
            }
            return ResultType.OK;
        } catch (DatabaseException ex) {
            //Si no se consigue aceptar la solicitud, se devuelve un estado erróneo.
            return ex.getResultType();
        }
    }

    @Override
    public ResultType cancelRequest(User user, String friendName) throws RemoteException {
        try{
            //Se borra la amistad: la solicitud es rechazada:
            bdFacade.deleteFriendship(user, friendName);
            //Si sigue correctamente, podemos notificar al cliente correspondiente si está conectado:
            if(clientsInternal.containsKey(friendName)){
                //Como no se llegaron a añadir los usuarios, no hay que notificar nada más que la cancelación de la solicitud:
                clientsInternal.get(friendName).notifyCancelledRequest(user.getUsername());
            }
            return ResultType.OK;
        } catch (DatabaseException ex) {
            //Si no se consigue aceptar la solicitud, se devuelve un estado erróneo.
            return ex.getResultType();
        }
    }

    @Override
    public ResultType deleteFriendship(User user, String friendName) throws RemoteException {
        try {
            //Borramos la amistad
            bdFacade.deleteFriendship(user, friendName);
            //Notificamos desconexión del usuario:
            if(clientsInternal.containsKey(friendName)){
                //Se notifica la "desconexión" (deja de estar disponible el cliente - cancelada la amistad):
                clientsInternal.get(friendName).notifyDisconnection(user.getUsername());
            }
            //Devolvemos resultado OK:
            return ResultType.OK;
        } catch(DatabaseException ex){ 
            //Devolvemos el resultado obtenido de la excepción:
            return ex.getResultType();
        }
    }

    @Override
    public ResultType changePassword(User user, String newPass) throws RemoteException {
        try {
            //Se procede al cambio:
            bdFacade.changePassword(user, newPass);
            //Se devuelve un estado correcto:
            return ResultType.OK;
        } catch (DatabaseException ex) {
            return ex.getResultType();
        }
    }

    @Override
    public ResultType unregister(User user) throws RemoteException {
        try{
            //Se recupera a las amistades ya (para poder notificar despues):
            List<String> friends = this.bdFacade.getFriendNames(user.getUsername());
            //Se procede al borrado:
            bdFacade.unregister(user);
            //Si se ha hecho el borrado, se notifica de la desconexión a las amistades:
            for(String friendName : friends) {
                //Para cada amigo, si está en el hashmap principal (es decir, conectado), se enviará:
                if(clientsInternal.containsKey(friendName)) {
                    //notificación de la desconexión del usuario:
                    clientsInternal.get(friendName).notifyDisconnection(user.getUsername());
                }
            }
            //Se elimina de los hashmap el usuario:
            clientsInternal.remove(user.getUsername());
            clientsShared.remove(user.getUsername());
            //Se devuelve un estado ok:
            return ResultType.OK;
        } catch (DatabaseException e) {
            return e.getResultType();
        }
    }

    private void notifyClientsOnConnect(String clientName, ClientManagementInterface clientInfo,
        ClientInternalMgInterface clientInternal) throws RemoteException{
        //Se debe enviar al cliente que corresponde el listado completo de sus amigos y, además, enviar
        //a todos los clientes amigos la notificación de conexión del cliente.
        //Lo primero es recuperar la lista de amigos:
        List<String> friends = this.bdFacade.getFriendNames(clientName);
        HashMap<String, ClientManagementInterface> clFriends = new HashMap<>();
        //Los resultados se guardarán en un hashmap:
        for(String friendName : friends) {
            //Para cada amigo, si está en el hashmap principal (es decir, conectado), se enviará:
            if(clientsInternal.containsKey(friendName)) {
                //Solo hay un posible cliente con esa clave. Se añade al hashmap:
                clFriends.put(friendName, clientsShared.get(friendName));
                //Aparte, se debe de notificar al cliente de la conexión de este usuario:
                clientsInternal.get(friendName).notifyConnection(clientInfo);
            }
        }

        List<String> sentRequests = this.bdFacade.getFriendSentRequests(clientName);
        List<String> receivedRequests = this.bdFacade.getFriendRequests(clientName);
        //Toda la información obtenida se debe de pasar al usuario. Éste podrá inicializar todo ya:
        clientInternal.setClientInfo(clFriends, sentRequests, receivedRequests);
    }

    private void notifyClientsOnDisconnect(String clientName) throws RemoteException{
        //Se notificará a los amigos del cliente cuando este se desconecte, para que puedana actualizar su
        //lista de contactos activa.
        List<String> friends = this.bdFacade.getFriendNames(clientName);
        for(String friendName : friends) {
            //Para cada amigo, si está en el hashmap principal (es decir, conectado), se enviará:
            if(clientsInternal.containsKey(friendName)) {
                //notificación de la desconexión del usuario:
                clientsInternal.get(friendName).notifyDisconnection(clientName);
            }
        }
    }
}
