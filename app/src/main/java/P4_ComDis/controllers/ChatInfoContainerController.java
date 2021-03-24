package P4_ComDis.controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import P4_ComDis.ClientManagementInterface;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ChatInfoContainerController implements Initializable{

    //Atributos públicos de la ventana del fxml:
    public Label chatName;

    //Atributos privados:
    private MainPageController parentController;
    private ClientManagementInterface clientInt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public ClientManagementInterface getClientImpl() {
        return clientInt;
    }

    public ChatInfoContainerController setClientInt(ClientManagementInterface client) throws RemoteException {
        //Asociamos valores:
        this.clientInt = client;
        //A mayores, ponemos en la etiqueta el nombre del usuario:
        chatName.setText(client.getClientName());
        return this;
    }

    public MainPageController getParentController() {
        return parentController;
    }

    public ChatInfoContainerController setParentController(MainPageController parentController) {
        this.parentController = parentController;
        return this;
    }
    
    public void chatOnClick(MouseEvent event){
        //Se cargará la pantalla de chat en la parte derecha:
        parentController.putChatScreen(clientInt);
    }

}
