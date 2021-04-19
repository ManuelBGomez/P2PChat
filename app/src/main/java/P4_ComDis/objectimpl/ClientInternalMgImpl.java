package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

import P4_ComDis.ClientInternalMgInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.controllers.MainPageController;
import javafx.application.Platform;

/**
 * Clase que implementa la interfaz interna del cliente.
 * 
 * @author Manuel Bendaña
 */
public class ClientInternalMgImpl extends UnicastRemoteObject implements ClientInternalMgInterface {
    
    //Atributo: controlador principal de la aplicacion
    private final MainPageController controller;
        
    /**
     * Constructor de la clase
     * @param controller Controlador principal de la aplicación.
     * @throws RemoteException Excepción remota lanzada en caso de problemas.
     */
    public ClientInternalMgImpl(MainPageController controller) throws RemoteException {
        super();
        this.controller = controller;
    }


    
    /** 
     * Método que permite al servidor enviar información al cliente.
     * 
     * @param connectedClients Lista de interfaces cliente conectadas.
     * @param sentRequests Lista de solicitudes de amistad enviadas a otros clientes.
     * @param receivedRequests Lista de solicitudes recibidas de otros clientes.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    @Override
    public void setClientInfo(HashMap<String, ClientManagementInterface> connectedClients,
                              List<String> sentRequests,
                              List<String> receivedRequests) throws RemoteException {
        //Llamamos a este método que nos guardará las listas:
        Platform.runLater( () -> {
            controller.setLists(connectedClients, sentRequests, receivedRequests);
        });
    }

    
    /** 
     * Método que permite notificar la conexión de un nuevo cliente a este.
     * 
     * @param newClient La interfaz del nuevo cliente conectado.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    @Override
    public void notifyConnection(ClientManagementInterface newClient) throws RemoteException {
        //Llamamos a este método que nos actualizará la lista de conectados:
        Platform.runLater( () -> {
            controller.updateNewConnection(newClient);
        });
    }

    
    /** 
     * Método que permite notificar la desconexión de un cliente.
     * 
     * @param loggedOutClient El nombre del cliente que se desconecta.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    @Override
    public void notifyDisconnection(String loggedOutClient) throws RemoteException {
        //Llamamos a este método que nos actualizará la lista de conectados:
        Platform.runLater( () -> {
            controller.updateNewDisconnect(loggedOutClient);
        });
    }

    
    /** 
     * Método que permite notificar la llegada de una nueva solicitud de amistad
     * 
     * @param username El nombre del usuario que solicita la amistad.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    @Override
    public void notifyNewRequest(String username) throws RemoteException {
        //Directamente se llama a un método del controlador para actualizar la lista de solicitudes:
        Platform.runLater(()->{
            controller.updateNewRequest(username);
        });        
    }

    
    /** 
     * Método que permite notificar la confirmación de una solicitud de amistad.
     * 
     * @param clientManagementInterface El cliente que ha confirmado la solicitud, que está también conectado (si no
     *                                  no podría enviar esta solicitud) y que por lo tanto se va a establecer como
     *                                  conectado. Se trata de su interfaz para comunicación con otros clientes.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
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

    
    /** 
     * Método que permite notificar el rechazo de una solicitud de amistad.
     * 
     * @param username El nombre del usuario que ha rechazado la amistad.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    @Override
    public void notifyRejection(String username) throws RemoteException {
        //Actualizamos lista de solicitudes enviadas:
        Platform.runLater(() -> {
            controller.deleteSentRequest(username);
        });
    }

    
    /** 
     * Método que permite notificar la cancelación de una solicitud de amistad.
     * 
     * @param username El usuario que ha decidido cancelar la solicitud.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    @Override
    public void notifyCancelledRequest(String username) throws RemoteException{
        //Actualizamos lista de solicitudes recibidas:
        Platform.runLater(() -> {
            controller.deleteReceivedRequest(username);
        });
    }
}
