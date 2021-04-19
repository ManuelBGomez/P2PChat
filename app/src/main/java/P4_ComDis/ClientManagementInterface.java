package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;

import P4_ComDis.model.dataClasses.Message;

/**
 * Interfaz del cliente que se comparte entre clientes. Desde ella se podrán enviar mensajes y recuperar el nombre del cliente
 * (y solamente recuperar).
 * 
 * @author Manuel Bendaña
 */
public interface ClientManagementInterface extends Remote {
    /**
     * Método que permite recuperar el nombre del cliente propietario de la interfaz.
     *  
     * @return String El nombre del cliente
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public String getClientName() throws RemoteException;

    /** 
     * Método que permite enviar un mensaje a otro cliente.
     * @param message Detalles del mensaje encapsulados en un único objeto.
     * @throws RemoteException Excepción lanzada en caso de problemas.
     */
    public void sendMessage(Message message) throws RemoteException;
}
