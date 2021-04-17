package P4_ComDis.model.dataClasses;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import P4_ComDis.ClientManagementInterface;

public class Message implements Serializable{
    //Atributos
    private final String messageContent;
    private final String date;
    private final String userName;
    private final ClientManagementInterface clientInt;

    public Message(String messageContent, ClientManagementInterface clientInt) throws RemoteException {
        this.messageContent = messageContent;
        this.clientInt = clientInt;
        //Se utilizará un simpledateformat para el formato de la fecha a enviar:
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        this.date = format.format(new Date(System.currentTimeMillis()));
        this.userName = clientInt.getClientName();
    }

    public String getUserName() {
        return userName;
    }

    public ClientManagementInterface getClientInt() {
        return clientInt;
    }

    public String getDate() {
        return date;
    }

    public String getMessageContent() {
        return messageContent;
    }    
}
