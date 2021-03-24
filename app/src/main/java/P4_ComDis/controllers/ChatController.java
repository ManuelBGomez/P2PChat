package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import P4_ComDis.ClientManagementInterface;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChatController implements Initializable {

    //Atributo privado: la interfaz del cliente en comunicación:
    private ClientManagementInterface clientInt;
    private ClientManagementInterface senderInt;

    //Atributos públicos: elementos de la interfaz
    public Label nameTag;
    public VBox vBoxMessages;
    public TextField messageText;
    public ScrollPane scrollChatContent;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Se asocia el alto del panel del chat al contenido (al principio vacío, irá creciendo con los mensajes):
        scrollChatContent.vvalueProperty().bind(vBoxMessages.heightProperty());
    }


    public ClientManagementInterface getClientInt() {
        return clientInt;
    }


    public void setClientAndSenderInt(ClientManagementInterface clientInt, ClientManagementInterface senderInt) throws RemoteException {
        //Asignamos la interfaz del cliente:
        this.clientInt = clientInt;
        this.senderInt = senderInt;
        //Establecemos el texto de la etiqueta con el nombre:
        nameTag.setText(clientInt.getClientName());
    }


    public void addMessage(String message, ClientManagementInterface clientInt, String time, boolean send) {
        //Se crea un nuevo mensaje a partir de su fxml:
        try {
            //Asignamos ubicación:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MessageContainer.fxml"));
            //Recuperamos el controlador y le asignamos la interfaz. Con ello tenemos todo establecido
            Node msg = loader.load();
            VBox.setVgrow(msg, Priority.ALWAYS);
            vBoxMessages.getChildren().add(msg);
            loader.<MessageContainerController>getController().loadMessage(message, clientInt, senderInt, time, send);;
        } catch (IOException ex){
            System.out.println("Error loading message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void btnSendOnClick(MouseEvent event){
        //Se utilizará un simpledateformat para el formato de la fecha a enviar:
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        //Se procederá al envío del mensaje (si no salta una excepción):
        try {
            String sendDate = format.format(new Date(System.currentTimeMillis()));
            clientInt.sendMessage(messageText.getText(), senderInt, sendDate);
            //Además, se cargará el mensaje:
            this.addMessage(messageText.getText(), clientInt, sendDate, true);
            //Vaciamos el textField:
            messageText.setText("");
        } catch (RemoteException e) {
            System.out.println("Error on sending: " + e.getMessage());
        }
    }
    
}
