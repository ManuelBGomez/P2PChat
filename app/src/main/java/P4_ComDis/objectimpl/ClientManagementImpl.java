package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import P4_ComDis.ClientManagementInterface;
import P4_ComDis.controllers.AccessController;
import javafx.application.Platform;

public class ClientManagementImpl extends UnicastRemoteObject implements ClientManagementInterface{
    
    private static final long serialVersionUID = 1L;

    //Atributo: nombre del cliente.
    private String clientName;
    //Atributo: controlador principal de la aplicacion
    private final AccessController controller;
    
    public ClientManagementImpl(AccessController controller, String clientName) throws RemoteException {
        super();
        this.controller = controller;
        this.clientName = clientName;
    }

    public String getClientName() throws RemoteException {
        return clientName;
    }

    @Override
    public void updateConnectedUsers(List<ClientManagementInterface> connectedClients) throws RemoteException {
        /*Platform.runLater( () -> {
            controller.updateUserList(connectedClients);
        });*/
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public void sendMessage(String message, ClientManagementInterface clientInt, String time) throws RemoteException {
        /*Platform.runLater(()->{
            try {
                controller.loadRecievedMessage(message, clientInt, time);
            } catch (RemoteException e) {
                System.out.println("Error on message load: " + e.getMessage());
            }
        });*/
    }
    
}
