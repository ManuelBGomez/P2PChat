package P4_ComDis.objectimpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import P4_ComDis.ClientManagementInterface;
import P4_ComDis.controllers.MainPageController;
import P4_ComDis.model.dataClasses.Message;
import javafx.application.Platform;

/**
 * Implementación de la interfaz compartida entre clientes.
 * 
 * @author Manuel Bendaña
 */
public class ClientManagementImpl extends UnicastRemoteObject implements ClientManagementInterface{
    
    //Atributo: nombre del cliente.
    private final String clientName;
    //Atributo: controlador principal de la aplicacion
    private final MainPageController controller;
    
    /**
     * Constructor de la clase
     * 
     * @param controller Referencia al controlador principal de la interfaz gráfica.
     * @param clientName Referencia al nombre del usuario cliente. Se evita pasarle el usuario completo con la contraseña.
     * @throws RemoteException
     */
    public ClientManagementImpl(MainPageController controller, String clientName) throws RemoteException {
        super();
        this.controller = controller;
        this.clientName = clientName;
    }

    
    /**
     * Método que permite recuperar el nombre del cliente propietario de la interfaz.
     *  
     * @return String El nombre del cliente
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public String getClientName() throws RemoteException {
        return clientName;
    }

    
    /** 
     * Método que permite enviar un mensaje a otro cliente.
     * @param message Detalles del mensaje encapsulados en un único objeto.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    @Override
    public void sendMessage(Message message) throws RemoteException {
        Platform.runLater(()->{
            try {
                //Se carga el mensaje recibido en el controlador.
                controller.loadRecievedMessage(message);
            } catch (RemoteException e) {
                System.out.println("Error en la carga del mensaje: " + e.getMessage());
            }
        });
    }
}
