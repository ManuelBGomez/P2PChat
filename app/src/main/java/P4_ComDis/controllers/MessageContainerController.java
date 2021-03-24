package P4_ComDis.controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import P4_ComDis.ClientManagementInterface;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MessageContainerController implements Initializable{

    public VBox messagePane;
    public Label userName;
    public Label messageContent;
    public Label time;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void loadMessage(String message, ClientManagementInterface clientInt, ClientManagementInterface senderInt, String time, boolean send) throws RemoteException{
        //Llenamos los campos de texto:
        this.messageContent.setText(message);
        this.time.setText(time);
        //Si send vale true, quiere decir que el mensaje es enviado y no recibido:
        if(!send) {
            //RECIBIDO: Colocamos mensaje a la izquierda y le damos fondo blanco y radio adecuado.
            this.userName.setText(clientInt.getClientName());
            messagePane.setStyle("-fx-background-color: white; -fx-background-radius: 0px 10px 10px 10px;");
            AnchorPane.setLeftAnchor(messagePane, 0.0);
        } else {
            //ENVIADO: Se mantiene estilo original y se coloca a la derecha.
            this.userName.setText(senderInt.getClientName());
            AnchorPane.setRightAnchor(messagePane, 0.0);
        }
    }
    
}
