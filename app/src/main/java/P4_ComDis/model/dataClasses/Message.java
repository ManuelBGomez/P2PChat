package P4_ComDis.model.dataClasses;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import P4_ComDis.ClientManagementInterface;

/**
 * Clase que representa los mensajes.
 * 
 * @author Manuel Bendaña
 */
public class Message implements Serializable{
    //Atributos
    private final String messageContent;
    private final String date;
    private final String userName;
    private final ClientManagementInterface clientInt;

    /**
     * Constructor de la clase
     * 
     * @param messageContent Contenido del mensaje
     * @param clientInt Interfaz del cliente que envía el mensaje
     * @throws RemoteException Excepción remota lanzada en caso de problemas de conexión.
     */
    public Message(String messageContent, ClientManagementInterface clientInt) throws RemoteException {
        this.messageContent = messageContent;
        this.clientInt = clientInt;
        //Se utilizará un simpledateformat para el formato de la fecha a enviar:
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        this.date = format.format(new Date(System.currentTimeMillis()));
        this.userName = clientInt.getClientName();
    }

    
    /** 
     * Getter del nombre 
     * @return String el nombre del usuario almacenado (que se recupera en el constructor).
     */
    public String getUserName() {
        return userName;
    }

    
    /** 
     * Método que permite recuperar la interfaz del cliente que envía el mensaje
     * @return ClientManagementInterface la interfaz almacenada.
     */
    public ClientManagementInterface getClientInt() {
        return clientInt;
    }

    
    /** 
     * Método que permite recuperar la fecha de envío del mensaje.
     * @return String la fecha almacenada.
     */
    public String getDate() {
        return date;
    }

    
    /** 
     * Método que permite recuperar el contenido del mensaje
     * @return String
     */
    public String getMessageContent() {
        return messageContent;
    }    
}
