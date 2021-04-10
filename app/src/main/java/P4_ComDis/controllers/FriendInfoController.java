package P4_ComDis.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FriendInfoController implements Initializable{

    public Label userName;
    public ImageView imgBtn;    

    //Booleano que nos indica si se va a añadir un amigo o a cancelar una solicitud hecha a otro amigo:
    private boolean add;
    //Referencia al controlador padre:
    private FriendshipsController controllerPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public void setValues(String userName, boolean add, FriendshipsController controllerPrincipal){
        this.userName.setText(userName);
        this.add = add;
        if(this.add){
            //Si el cuadro se abre para ofrecer al usuario solicitar una nueva amistad, la imagen a mostrar es la del botón de confirmar:
            imgBtn.setImage(new Image("/img/comprobado.png"));
        } else {
            //Si no (se abre para mostrar una solicitud hecha), se muestra el icono con la X:
            imgBtn.setImage(new Image("/img/cancelar.png"));
        }
        this.controllerPrincipal = controllerPrincipal;
    }
}
