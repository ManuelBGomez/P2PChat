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
                System.out.println("Error en la carga del mensaje: " + e.getMessage());
            }
        });
    }

    @Override
    public void setClientInfo(HashMap<String, ClientManagementInterface> connectedClients,
                              List<String> sentRequests,
                              List<String> receivedRequests) throws RemoteException {
        //Llamamos a este método que nos guardará las listas:
        Platform.runLater( () -> {
            controller.setLists(connectedClients, sentRequests, receivedRequests);
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

    @Override
    public void notifyNewRequest(String username) throws RemoteException {
        //Directamente se llama a un método del controlador para actualizar la lista de solicitudes:
        Platform.runLater(()->{
            controller.updateNewRequest(username);
        });        
    }

    @Override
    public void notifyConfirmation(ClientManagementInterface clientManagementInterface) throws RemoteException {
        //Actualizamos lista de conectados:
        Platform.runLater( () -> {
            controller.updateNewConnection(clientManagementInterface);
            try {
                controller.updateConfirmation(clientManagementInterface.getClientName());
            } catch (RemoteException e) {
                System.out.println("Error en la carga de la confirmación: " + e.getMessage());
            }
        });
    }
    
}
