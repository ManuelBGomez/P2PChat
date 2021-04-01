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
            System.out.println("Cliente ha iniciado sesión");
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
                System.out.println("Cliente ha cerrado sesión");
            } else {
                System.out.println("Problema en desconexión del usuario: no se encuentra.");
            }
        } catch(DatabaseException ex){
            //Si se genera alguna excepción (no debería) se avisaría
            //Imaginemos, por ejemplo, que lo hace un usuario no autorizado:
            System.out.println("Problemas en el cierre de sesión: " + ex.getMessage());
        }
    }

    @Override
    public void registerInChat(ClientManagementInterface clientInfo) throws RemoteException {
        /*//Procedemos a almacenar el nuevo valor (si no lo estaba antes):
        if(!clients.contains(clientInfo)){
            //Lo añadimos:
            clients.add(clientInfo);
        }
        //Notificamos la nueva lista a los clientes conectados:
        this.notifyClients();
        System.out.println("Added client and notified");*/
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
        //Todo el hashmap obtenido se debe de pasar al usuario. Éste podrá inicializar todo ya:
        clientInfo.setConnectedUsers(clFriends);
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
