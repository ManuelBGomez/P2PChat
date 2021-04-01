package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

import P4_ComDis.ClientManagementInterface;
import P4_ComDis.controllers.MainPageController;
import javafx.application.Platform;

public class ClientManagementImpl extends UnicastRemoteObject implements ClientManagementInterface{
    
    private static final long serialVersionUID = 1L;

    //Atributo: nombre del cliente.
    private String clientName;
    //Atributo: controlador principal de la aplicacion
    private final MainPageController controller;
    
    public ClientManagementImpl(MainPageController controller, String clientName) throws RemoteException {
        super();
        this.controller = controller;
        this.clientName = clientName;
    }

    public String getClientName() throws RemoteException {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public void sendMessage(String message, ClientManagementInterface clientInt, String time) throws RemoteException {
        Platform.runLater(()->{
            try {
                controller.loadRecievedMessage(message, clientInt, time);
            } catch (RemoteException e) {
                System.out.println("Error on message load: " + e.getMessage());
            }
        });
    }

    @Override
    public void setConnectedUsers(HashMap<String, ClientManagementInterface> connectedClients) throws RemoteException {
        //Llamamos a este método que nos actualizará la lista de conectados:
        Platform.runLater( () -> {
            controller.setUserList(connectedClients);
        });
    }

    @Override
    public void notifyConnection(ClientManagementInterface newClient) throws RemoteException {
        //Llamamos a este método que nos actualizará la lista de conectados:
        Platform.runLater( () -> {
            controller.updateNewConnection(newClient);
        });
    }

    @Override
    public void notifyDisconnection(ClientManagementInterface loggedOutClient) throws RemoteException {
        //Llamamos a este método que nos actualizará la lista de conectados:
        Platform.runLater( () -> {
            controller.updateNewDisconnect(loggedOutClient);
        });
    }
    
}
