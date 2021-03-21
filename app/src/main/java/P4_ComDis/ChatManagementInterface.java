package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatManagementInterface extends Remote {

    public void registerInChat(ClientManagementInterface clientInfo) 
        throws RemoteException;

    public void unregisterFromChat(ClientManagementInterface clientInfo) throws RemoteException;
}
