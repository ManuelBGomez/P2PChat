package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;

import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;

public interface ChatManagementInterface extends Remote {

    public ResultType loginToChat(User user, ClientManagementInterface clientInfo) throws RemoteException;

    public void logoutFromChat(User user) throws RemoteException;

    public ResultType registerInChat(User user, ClientManagementInterface clientInfo) 
        throws RemoteException;

    public void unregisterFromChat(ClientManagementInterface clientInfo) throws RemoteException;
}
