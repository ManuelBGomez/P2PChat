package P4_ComDis.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import P4_ComDis.model.dataClasses.FriendRequestType;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class FriendInfoController implements Initializable{

    public Label userName;
    public ImageView imgBtnConf;
    public ImageView imgBtnRec;

    //Objeto FriendRequestType que nos indica el tipo de solicitud (enviada, recibida o buscada)
    private FriendRequestType rType;
    //Referencia al controlador padre:
    private FriendshipsController controllerPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void setValues(String userName, FriendRequestType rType, FriendshipsController controllerPrincipal){
        this.userName.setText(userName);
        this.rType = rType;
        switch(rType){
        case RECV:
            //Recibida: se muestran ambos botones:
            imgBtnConf.setVisible(true);
            imgBtnRec.setVisible(true);
            //El botón se pone a una distancia menor del borde derecho:
            AnchorPane.setRightAnchor(imgBtnConf, 20.0);
            break;
        case SEARCH:
            //Resultado de búsqueda para tratar de añadir amistad: sólo botón de confirmar:
            imgBtnConf.setVisible(true);
            imgBtnRec.setVisible(false);
            //El botón se pone a una distancia menor del borde derecho:
            AnchorPane.setRightAnchor(imgBtnConf, 20.0);
            break;
        case SENT:
            //Solicitud enviada: se muestra sólo botón de cancelar:
            imgBtnConf.setVisible(false);
            imgBtnRec.setVisible(true);
            break;
        }
        this.controllerPrincipal = controllerPrincipal;
    }
}
