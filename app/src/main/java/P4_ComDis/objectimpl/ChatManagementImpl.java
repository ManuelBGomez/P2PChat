package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

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
            System.out.println("Cliente logged in");
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
            //Se notifica el cierre de sesión correspondiente
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

    private void notifyClients() throws RemoteException{
        //Se envia a todos los usuarios del array la lista de clientes:
        /*for(ClientManagementInterface client: clients){
            client.updateConnectedUsers(clients);
        }*/
    }

    
}
