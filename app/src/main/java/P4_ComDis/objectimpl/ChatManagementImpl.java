package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.ClientManagementInterface;

public class ChatManagementImpl extends UnicastRemoteObject implements ChatManagementInterface {

    private static final long serialVersionUID = 1L;

    //Atributo: la lista de clientes conectados:
    private List<ClientManagementInterface> clients;

    public ChatManagementImpl() throws RemoteException {
        super();
        this.clients = new ArrayList<>();
    }

    @Override
    public void registerInChat(ClientManagementInterface clientInfo) throws RemoteException {
        //Procedemos a almacenar el nuevo valor (si no lo estaba antes):
        if(!clients.contains(clientInfo)){
            //Lo añadimos:
            clients.add(clientInfo);
        }
        //Notificamos la nueva lista a los clientes conectados:
        this.notifyClients();
        System.out.println("Added client and notified");
    }

    @Override
    public void unregisterFromChat(ClientManagementInterface clientInfo) throws RemoteException {
        //Procedemos a eliminar el cliente:
        if(clients.remove(clientInfo)){
            //Notificamos la nueva lista a los clientes conectados:
            this.notifyClients();
            //Si se elimina se avisa
            System.out.println("Removed client and notified");
        }
    }

    private void notifyClients() throws RemoteException{
        //Se envia a todos los usuarios del array la lista de clientes:
        for(ClientManagementInterface client: clients){
            client.updateConnectedUsers(clients);
        }
    }
    
}
