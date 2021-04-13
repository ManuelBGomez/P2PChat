package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;
import P4_ComDis.model.database.BDFacade;
import P4_ComDis.model.exceptions.DatabaseException;

public class ChatManagementImpl extends UnicastRemoteObject implements ChatManagementInterface {

    private static final long serialVersionUID = 1L;

    //Atributo: la lista de clientes conectados:
    private HashMap<String, ClientManagementInterface> clients;
    //Referencia a la fachada de la base de datos:
    private BDFacade bdFacade;

    public ChatManagementImpl() throws RemoteException {
        super();
        this.bdFacade = new BDFacade();
        this.clients = new HashMap<>();
    }
    
    @Override
    public ResultType loginToChat(User user, ClientManagementInterface clientInfo) throws RemoteException {
        //Intentamos hacer el login:
        try {
            bdFacade.login(user);
            //Añadimos la interfaz del cliente si el resultado ha sido satisfactorio:
            clients.put(user.getUsername(), clientInfo);
            //Habrá que notificar al cliente de los usuarios conectados, y a todos sus amigos de la conexión:
            System.out.println("Cliente " + user.getUsername() + " ha iniciado sesión");
            //Desde aqui se procederá a la notificación:
            this.notifyClientsOnConnect(clientInfo);
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
            if(clients.containsKey(user.getUsername())){
                //En principio este if se debería cumplir, aunque en todo caso se avisaría si no fuese así.s
                this.notifyClientsOnDisconnect(clients.get(user.getUsername()));
                //Se elimina el cliente del hashmap:
                clients.remove(user.getUsername());            
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
    public ResultType registerInChat(User user, ClientManagementInterface clientInfo) throws RemoteException {
        //Trataremos de registrar al usuario:
        try {
            bdFacade.register(user);
            //Si el método finaliza correctamente, entonces hacemos las mismas acciones que con un usuario normal:
            //Añadimos la interfaz del cliente si el resultado ha sido satisfactorio:
            clients.put(user.getUsername(), clientInfo);
            System.out.println("Cliente " + user.getUsername() + " registrado");
            //Eso sí, no se revisan las amistades, pues la cuenta acaba de crearse, así que no tiene sentido añadir nada.
            return ResultType.OK;
        } catch(DatabaseException ex) {
            //Si hay alguna excepción se devuelve el motivo:
            return ex.getResultType();
        }
    }

    @Override
    public void unregisterFromChat(ClientManagementInterface clientInfo) throws RemoteException {
        /*//Procedemos a eliminar el cliente:
        if(clients.remove(clientInfo)){
            //Notificamos la nueva lista a los clientes conectados:
            this.notifyClients();
            //Si se elimina se avisa
            System.out.println("Removed client and notified");
        }*/
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
            if(clients.containsKey(friendName)){
                clients.get(friendName).notifyNewRequest(user.getUsername());
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
            if(clients.containsKey(friendName)){
                //En ese caso procederemos a notificar uno al otro:
                clients.get(friendName).notifyConfirmation(clients.get(user.getUsername()));
                //Se le notifica también la conexión:
                clients.get(user.getUsername()).notifyConnection(clients.get(friendName));
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
            if(clients.containsKey(friendName)){
                //Como no se llegaron a añadir los usuarios, no hay que notificar nada más:
                clients.get(friendName).notifyRejection(user.getUsername());
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
            if(clients.containsKey(friendName)){
                //Como no se llegaron a añadir los usuarios, no hay que notificar nada más que la cancelación de la solicitud:
                clients.get(friendName).notifyCancelledRequest(user.getUsername());
            }
            return ResultType.OK;
        } catch (DatabaseException ex) {
            //Si no se consigue aceptar la solicitud, se devuelve un estado erróneo.
            return ex.getResultType();
        }
    }

    @Override
    public ResultType deleteFriendship(User user, String friendName) throws RemoteException {
        return null;
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
                if(clients.containsKey(friendName)) {
                    //notificación de la desconexión del usuario:
                    clients.get(friendName).notifyDisconnection(clients.get(user.getUsername()));
                }
            }
            //Se elimina del hashmap el usuario:
            clients.remove(user.getUsername());
            //Se devuelve un estado ok:
            return ResultType.OK;
        } catch (DatabaseException e) {
            return e.getResultType();
        }
    }

    private void notifyClientsOnConnect(ClientManagementInterface clientInfo) throws RemoteException{
        //Se debe enviar al cliente que corresponde el listado completo de sus amigos y, además, enviar
        //a todos los clientes amigos la notificación de conexión del cliente.
        //Lo primero es recuperar la lista de amigos:
        List<String> friends = this.bdFacade.getFriendNames(clientInfo.getClientName());
        HashMap<String, ClientManagementInterface> clFriends = new HashMap<>();
        //Los resultados se guardarán en un hashmap:
        for(String friendName : friends) {
            //Para cada amigo, si está en el hashmap principal (es decir, conectado), se enviará:
            if(clients.containsKey(friendName)) {
                //Solo hay un posible cliente con esa clave. Se añade al hashmap:
                clFriends.put(friendName, clients.get(friendName));
                //Aparte, se debe de notificar al cliente de la conexión de este usuario:
                clients.get(friendName).notifyConnection(clientInfo);
            }
        }

        List<String> sentRequests = this.bdFacade.getFriendSentRequests(clientInfo.getClientName());
        List<String> receivedRequests = this.bdFacade.getFriendRequests(clientInfo.getClientName());
        //Toda la información obtenida se debe de pasar al usuario. Éste podrá inicializar todo ya:
        clientInfo.setClientInfo(clFriends, sentRequests, receivedRequests);
    }

    private void notifyClientsOnDisconnect(ClientManagementInterface clientInfo) throws RemoteException{
        //Se notificará a los amigos del cliente cuando este se desconecte, para que puedana actualizar su
        //lista de contactos activa.
        List<String> friends = this.bdFacade.getFriendNames(clientInfo.getClientName());
        for(String friendName : friends) {
            //Para cada amigo, si está en el hashmap principal (es decir, conectado), se enviará:
            if(clients.containsKey(friendName)) {
                //notificación de la desconexión del usuario:
                clients.get(friendName).notifyDisconnection(clientInfo);
            }
        }
    }
}
