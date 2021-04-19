package P4_ComDis.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import P4_ComDis.model.dataClasses.FriendRequestType;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Controlador del objeto que representa información de algún amigo. Se trata del contenedor que se mostrará 
 * cuando se quiere añadir/confirmar/cancelar una solicitud de amistad.
 * 
 * @author Manuel Bendaña
 */
public class FriendInfoController implements Initializable{

    public Label userName;
    public ImageView imgBtnAdd;
    public ImageView imgBtnConf;
    public ImageView imgBtnRec;

    //Objeto FriendRequestType que nos indica el tipo de solicitud (enviada, recibida o buscada)
    private FriendRequestType rType;
    //Referencia al controlador padre:
    private FriendshipsController controllerFriendships;

    
    /**
     * Método ejecutado al iniciar la interfaz.
     *  
     * @param location url
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) { }
    
    
    /** 
     * Método que permite pasar valores a esta parte de la interfaz.
     * 
     * @param userName el nombre del usuario.
     * @param rType el tipo de solicitud que se hace (hay tres tipos definidos en un enum).
     * @param controllerFriendships Referencia al controlador de la pantalla de amistades (el padre).
     */
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

    
    /** 
     * Método ejecutado al pulsar el botón de añadir un amigo.
     * 
     * @param event El evento de ratón que tiene lugar.
     */
    public void btnAddOnClick(MouseEvent event) {
        //Llamaremos al controlador padre para gestionar la llamada:
        controllerFriendships.manageSendRequest(userName.getText());
    }
    
    
    /** 
     * Método ejecutado al pulsar el botón de confirmación de una amistad.
     * 
     * @param event El evento de ratón que tiene lugar.
     */
    public void btnConfOnClick(MouseEvent event) {
        //Llamaremos al controlador padre para gestionar la llamada:
        controllerFriendships.manageConfirmation(userName.getText());
    }

    
    /** 
     * Método ejecutado al pulsar el botón de rechazo de una amistad.
     * 
     * @param event El evento de ratón que tiene lugar.
     */
    public void btnRecOnClick(MouseEvent event) {
        controllerFriendships.manageRejection(userName.getText(), this.rType);
    }
}
