package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientManagementInterface extends Remote {
    public void updateConnectedUsers(List<ClientManagementInterface> connectedClients) throws RemoteException;

    public String getClientName() throws RemoteException;

    public void sendMessage(String message, ClientManagementInterface clientInt, String time) throws RemoteException;
}
