package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;;

/**
 * Interfaz que contiene métodos que se usarán para la gestión interna del cliente. Solamente tendrá acceso a ella
 * el servidor y el propio cliente (que evidentemente tendrá que instanciarla).
 * 
 * @author Manuel Bendaña
 */
public interface ClientInternalMgInterface extends Remote {


    /** 
     * Método que permite al servidor enviar información al cliente.
     * 
     * @param connectedClients Lista de interfaces cliente conectadas.
     * @param sentRequests Lista de solicitudes de amistad enviadas a otros clientes.
     * @param receivedRequests Lista de solicitudes recibidas de otros clientes.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void setClientInfo(HashMap<String, ClientManagementInterface> connectedClients,
                              List<String> sentRequests,
                              List<String> receivedRequests) throws RemoteException;


    /** 
     * Método que permite notificar la conexión de un nuevo cliente a este.
     * 
     * @param newClient La interfaz del nuevo cliente conectado.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void notifyConnection(ClientManagementInterface newClient) throws RemoteException;
    
    /** 
     * Método que permite notificar la desconexión de un cliente.
     * 
     * @param loggedOutClient El nombre del cliente que se desconecta.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void notifyDisconnection(String loggedOutClient) throws RemoteException;

    /** 
     * Método que permite notificar la llegada de una nueva solicitud de amistad
     * 
     * @param username El nombre del usuario que solicita la amistad.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void notifyNewRequest(String username) throws RemoteException;

    /** 
     * Método que permite notificar la confirmación de una solicitud de amistad.
     * 
     * @param clientManagementInterface El cliente que ha confirmado la solicitud, que está también conectado (si no
     *                                  no podría enviar esta solicitud) y que por lo tanto se va a establecer como
     *                                  conectado. Se trata de su interfaz para comunicación con otros clientes.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void notifyConfirmation(ClientManagementInterface clientManagementInterface) throws RemoteException;

    /** 
     * Método que permite notificar el rechazo de una solicitud de amistad.
     * 
     * @param username El nombre del usuario que ha rechazado la amistad.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void notifyRejection(String username) throws RemoteException;

    /** 
     * Método que permite notificar la cancelación de una solicitud de amistad.
     * 
     * @param username El usuario que ha decidido cancelar la solicitud.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void notifyCancelledRequest(String username) throws RemoteException;
}
