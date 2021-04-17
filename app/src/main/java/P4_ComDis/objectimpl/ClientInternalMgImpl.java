package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

import P4_ComDis.ClientInternalMgInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.controllers.MainPageController;
import javafx.application.Platform;

public class ClientInternalMgImpl extends UnicastRemoteObject implements ClientInternalMgInterface {
    
    //Atributo: controlador principal de la aplicacion
    private final MainPageController controller;
        
    public ClientInternalMgImpl(MainPageController controller) throws RemoteException {
        super();
        this.controller = controller;
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
    public void notifyDisconnection(String loggedOutClient) throws RemoteException {
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
        Platform.runLater(() -> {
            controller.updateNewConnection(clientManagementInterface);
            try {
                controller.deleteSentRequest(clientManagementInterface.getClientName());
            } catch (RemoteException e) {
                System.out.println("Error en la carga de la confirmación: " + e.getMessage());
            }
        });
    }

    @Override
    public void notifyRejection(String username) throws RemoteException {
        //Actualizamos lista de solicitudes enviadas:
        Platform.runLater(() -> {
            controller.deleteSentRequest(username);
        });
    }

    @Override
    public void notifyCancelledRequest(String username) throws RemoteException{
        //Actualizamos lista de solicitudes recibidas:
        Platform.runLater(() -> {
            controller.deleteReceivedRequest(username);
        });
    }
}
