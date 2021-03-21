package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import P4_ComDis.ClientManagementInterface;

public class ClientManagementImpl extends UnicastRemoteObject implements ClientManagementInterface{
    
    private static final long serialVersionUID = 1L;

    //Atributo: nombre del cliente.
    private String clientName;
    
    public ClientManagementImpl() throws RemoteException {
        super();
    }

    public String getClientName() throws RemoteException {
        return clientName;
    }
    
    @Override
    public void updateConnectedUsers(List<ClientManagementInterface> connectedClients) throws RemoteException {
        System.out.println("Updated connected users:");
        connectedClients.forEach(client -> {
            try {
                System.out.println("Client name: " + client.getClientName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
}
