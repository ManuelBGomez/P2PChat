package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import P4_ComDis.ClientManagementInterface;
import P4_ComDis.model.dataClasses.Message;
import javafx.event.ActionEvent;
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
    private MainPageController controllerPrincipal;

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


    public void setValues(ClientManagementInterface clientInt, ClientManagementInterface senderInt,
                            MainPageController controllerPrincipal, List<Message> messages) throws RemoteException {
        //Asignamos la interfaz del cliente:
        this.clientInt = clientInt;
        this.senderInt = senderInt;
        this.controllerPrincipal = controllerPrincipal;
        //Establecemos el texto de la etiqueta con el nombre:
        nameTag.setText(clientInt.getClientName());
        //Cargamos todos los mensajes:
        for(Message message: messages){
            //Es el propio si el destinatario no es este cliente, si no es mensaje recibido del otro cliente.
            this.addMessage(message, !message.getClientInt().equals(clientInt));
        }
    }


    public void addMessage(Message message, boolean send) {
        //Se crea un nuevo mensaje a partir de su fxml:
        try {
            //Asignamos ubicación:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MessageContainer.fxml"));
            //Recuperamos el controlador y le asignamos la interfaz. Con ello tenemos todo establecido
            Node msg = loader.load();
            VBox.setVgrow(msg, Priority.ALWAYS);
            vBoxMessages.getChildren().add(msg);
            loader.<MessageContainerController>getController().loadMessage(message, clientInt, send);
        } catch (IOException ex){
            System.out.println("Error loading message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void btnSendOnClick(MouseEvent event){
        //Se procederá al envío del mensaje (si no salta una excepción):
        try {
            Message message = new Message(messageText.getText(), senderInt);
            clientInt.sendMessage(message);
            //Además, se cargará el mensaje:
            this.addMessage(message, true);
            //Se añade al registro:
            controllerPrincipal.addMessage(nameTag.getText(), message);
            //Vaciamos el textField:
            messageText.setText("");
        } catch (RemoteException e) {
            System.out.println("Error on sending: " + e.getMessage());
        }
    }

    public void btnDeleteFriendshipOnClick(ActionEvent event){
        //Llamamos al controlador principal para gestionar el borrado:
        this.controllerPrincipal.deleteFriendship(nameTag.getText());
    }
    
}
