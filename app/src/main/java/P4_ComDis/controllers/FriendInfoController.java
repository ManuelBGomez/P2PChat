package P4_ComDis.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import P4_ComDis.model.dataClasses.FriendRequestType;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class FriendInfoController implements Initializable{

    public Label userName;
    public ImageView imgBtnAdd;
    public ImageView imgBtnConf;
    public ImageView imgBtnRec;

    //Objeto FriendRequestType que nos indica el tipo de solicitud (enviada, recibida o buscada)
    private FriendRequestType rType;
    //Referencia al controlador padre:
    private FriendshipsController controllerFriendships;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void setValues(String userName, FriendRequestType rType, FriendshipsController controllerFriendships){
        this.userName.setText(userName);
        this.rType = rType;
        switch(rType){
            case RECV:
                //Recibida: se muestran botones de aceptar y rechazar:
                imgBtnConf.setVisible(true);
                imgBtnRec.setVisible(true);
                imgBtnAdd.setVisible(false);
                break;
            case SEARCH:
                //Resultado de búsqueda para tratar de añadir amistad: sólo botón de añadir:
                imgBtnConf.setVisible(false);
                imgBtnRec.setVisible(false);
                imgBtnAdd.setVisible(true);
                break;
            case SENT:
                //Solicitud enviada: se muestra sólo botón de cancelar:
                imgBtnConf.setVisible(false);
                imgBtnRec.setVisible(true);
                imgBtnAdd.setVisible(false);
                break;
        }
        this.controllerFriendships = controllerFriendships;
    }

    public void btnAddOnClick(MouseEvent event) {
        //Llamaremos al controlador padre para gestionar la llamada:
        controllerFriendships.manageSendRequest(userName.getText());
    }
    
    public void btnConfOnClick(MouseEvent event) {
        //Llamaremos al controlador padre para gestionar la llamada:
        controllerFriendships.manageConfirmation(userName.getText());
    }

    public void btnRecOnClick(MouseEvent event) {
        controllerFriendships.manageRejection(userName.getText(), this.rType);
    }
}
