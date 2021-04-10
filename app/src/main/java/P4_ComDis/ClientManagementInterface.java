package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface ClientManagementInterface extends Remote {
    public void setClientInfo(HashMap<String, ClientManagementInterface> connectedClients,
                              List<String> sentRequests,
                              List<String> receivedRequests) throws RemoteException;

    public void notifyConnection(ClientManagementInterface newClient) throws RemoteException;
    
    public void notifyDisconnection(ClientManagementInterface loggedOutClient) throws RemoteException;

    public String getClientName() throws RemoteException;

    public void sendMessage(String message, ClientManagementInterface clientInt, String time) throws RemoteException;
}
