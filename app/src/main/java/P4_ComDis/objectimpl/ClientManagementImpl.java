package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import P4_ComDis.ClientManagementInterface;
import P4_ComDis.controllers.MainPageController;
import P4_ComDis.model.dataClasses.Message;
import javafx.application.Platform;

public class ClientManagementImpl extends UnicastRemoteObject implements ClientManagementInterface{
    
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

    @Override
    public void sendMessage(Message message) throws RemoteException {
        Platform.runLater(()->{
            try {
                controller.loadRecievedMessage(message);
            } catch (RemoteException e) {
                System.out.println("Error en la carga del mensaje: " + e.getMessage());
            }
        });
    }
}
