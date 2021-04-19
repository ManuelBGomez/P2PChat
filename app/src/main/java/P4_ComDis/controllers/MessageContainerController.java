package P4_ComDis.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import P4_ComDis.model.dataClasses.Message;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controlador que representa cada uno de los mensajes que se envían en un chat.
 * 
 * @author Manuel Bendaña
 */
public class MessageContainerController implements Initializable{

    public VBox messagePane;
    public Label userName;
    public Label messageContent;
    public Label time;

    
    /** 
     * Método ejecutado al iniciar la interfaz correspondiente.
     * 
     * @param location url
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    
    /** 
     * Método que permite cargar un mensaje para ser visualizado.
     * 
     * @param message El contenido del mensaje
     * @param send Si es enviado o recibido. Dependiendo de ello aparecerá en un lado u otro.
     */
    public void loadMessage(Message message, boolean send) {
        //Llenamos los campos de texto:
        this.messageContent.setText(message.getMessageContent());
        this.time.setText(message.getDate());
        //Si send vale true, quiere decir que el mensaje es enviado y no recibido:
        if(!send) {
            //RECIBIDO: Colocamos mensaje a la izquierda y le damos fondo blanco y radio adecuado.
            this.userName.setText(message.getUserName());
            messagePane.setStyle("-fx-background-color: white; -fx-background-radius: 0px 10px 10px 10px;");
            AnchorPane.setLeftAnchor(messagePane, 0.0);
        } else {
            //ENVIADO: Se mantiene estilo original y se coloca a la derecha.
            this.userName.setText(message.getUserName());
            AnchorPane.setRightAnchor(messagePane, 0.0);
        }
    }
    
}
