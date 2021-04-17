package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;

import P4_ComDis.model.dataClasses.Message;

public interface ClientManagementInterface extends Remote {
    public String getClientName() throws RemoteException;

    public void sendMessage(Message message) throws RemoteException;
}
