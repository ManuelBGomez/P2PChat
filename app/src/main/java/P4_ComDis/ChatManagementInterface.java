package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;

public interface ChatManagementInterface extends Remote {

    public ResultType loginToChat(User user, ClientManagementInterface clientInfo, ClientInternalMgInterface clientInternal) 
        throws RemoteException;

    public void logoutFromChat(User user) throws RemoteException;

    public ResultType registerInChat(User user, ClientManagementInterface clientInfo, ClientInternalMgInterface clientInternal) 
        throws RemoteException;

    public List<String> searchFriends(User user, String pattern) throws RemoteException;

    public ResultType sendFriendRequest(User user, String friendName) throws RemoteException;

    public ResultType acceptRequest(User user, String friendName) throws RemoteException;

    public ResultType rejectFriendship(User user, String friendName) throws RemoteException;

    public ResultType cancelRequest(User user, String friendName) throws RemoteException;

    public ResultType deleteFriendship(User user, String friendName) throws RemoteException;

    public ResultType changePassword(User user, String newPass) throws RemoteException;

    public ResultType unregister(User user) throws RemoteException;

}
