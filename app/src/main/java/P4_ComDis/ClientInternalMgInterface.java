package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;;

public interface ClientInternalMgInterface extends Remote {
    public void setClientInfo(HashMap<String, ClientManagementInterface> connectedClients,
                              List<String> sentRequests,
                              List<String> receivedRequests) throws RemoteException;

    public void notifyConnection(ClientManagementInterface newClient) throws RemoteException;
    
    public void notifyDisconnection(String loggedOutClient) throws RemoteException;

    public void notifyNewRequest(String username) throws RemoteException;

    public void notifyConfirmation(ClientManagementInterface clientManagementInterface) throws RemoteException;

    public void notifyRejection(String username) throws RemoteException;

    public void notifyCancelledRequest(String username) throws RemoteException;
}
