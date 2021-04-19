package P4_ComDis.controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import P4_ComDis.aux.Dialogs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * Controlador de la pantalla de gestión de la cuenta.
 * 
 * @author Manuel Bendaña
 */
public class AccountToolsController implements Initializable{

    //Atributos públicos (interfaz):
    public PasswordField textOldPass;
    public PasswordField textNewPass;
    public PasswordField textConfPass;
    public Button btnChangePass;
    public Label labelError;

    //Referencia al controlador principal:
    private MainPageController controllerPrincipal;

    
    /** 
     * Método ejecutado nada más inicializar la pantalla
     * 
     * @param location url
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Botón de cambio de contraseña por defecto deshabilitado:
        btnChangePass.setDisable(true);
        ChangeListener<String> listText = new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //Comprobamos que ambos campos tengan algo
                if(!textOldPass.getText().isEmpty() && !textNewPass.getText().isEmpty() && !textConfPass.getText().isEmpty()){
                    //Si es así, se activa el botón:
                    btnChangePass.setDisable(false);
                } else {
                    //Si no, se desactiva:
                    btnChangePass.setDisable(true);
                }
            }
        };

        //Añadimos los listeners:
        textOldPass.textProperty().addListener(listText);
        textNewPass.textProperty().addListener(listText);
        textConfPass.textProperty().addListener(listText);
    }

    
    /** 
     * Método que permite pasar valores a este controlador:
     * 
     * @param mainPageController Controlador de la pantalla principal para gestionarlo desde aquí.
     */
    public void setValues(MainPageController mainPageController) {
        this.controllerPrincipal = mainPageController;
    }

    
    /** 
     * Método que se ejecuta al pulsar el botón de cambio de contraseña.
     * 
     * @param event El evento que tiene lugar.
     */
    public void btnChangePassOnClick(ActionEvent event){
        //Se comprueba si las contraseñas son iguales:
        if(textNewPass.getText().equals(textConfPass.getText())){
            //Coinciden, se llama al método de cambio de contraseña:
            try {
                switch(controllerPrincipal.changePassword(textOldPass.getText(), textNewPass.getText())){
                    case DATABASE_ERROR:
                        labelError.setVisible(false);
                        Dialogs.showError("Error", "Error cambiando contraseña",
                                            "Fallo en la base de datos. Inténtelo de nuevo más tarde.");
                        break;
                    case OK:
                        labelError.setVisible(false);
                        //Se limpian los campos:
                        textConfPass.setText("");
                        textOldPass.setText("");
                        textNewPass.setText("");
                        //Mostramos mensaje confirmando:
                        Dialogs.showInfo("Información", "Contraseña cambiada correctamente", 
                                            "Puedes cambiarla de nuevo cuando necesites.");
                        break;
                    case UNAUTHORIZED:
                        labelError.setText("La contraseña antigua es incorrecta");
                        labelError.setVisible(true);
                        break;
                    default:
                        break;
                }
            } catch(RemoteException ex) {
                Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + ex.getMessage() +  ". Saliendo...");
                labelError.setVisible(false);
            }
        } else {
            labelError.setText("Las contraseñas no coinciden");
            labelError.setVisible(true);
        }
    }

    
    /** 
     * Método que se ejecuta al pulsar el botón de borrado de la cuenta.
     * 
     * @param event El evento que tiene lugar.
     */
    public void btnDelAccountOnClick(ActionEvent event){
        Boolean result = Dialogs.showConfirmation("Confirmación borrado", 
                                                                "¿Estás seguro que deseas borrar la cuenta?", "");
        if(result){
            //Si se confirma, se procede al borrado:
            try {
                switch(controllerPrincipal.deleteAccount()){
                    case DATABASE_ERROR:
                        Dialogs.showError("Error", "Error borrando usuario",
                                            "Fallo en la base de datos. Inténtelo de nuevo más tarde.");
                        break;
                    case OK:
                        //Mostramos mensaje confirmando:
                        Dialogs.showInfo("Información", "Usuario borrado correctamente", 
                                            "Saliendo de la aplicación.");
                        //Se sale (se habrá borrado):
                        System.exit(1);
                        break;
                    case UNAUTHORIZED:
                        Dialogs.showError("Error", "Error borrando usuario", 
                                            "Usuario no autorizado a realizar el borrado.");
                        break;
                    default:
                        break;
                }
            } catch(RemoteException ex) {
                Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + ex.getMessage() +  ". Saliendo...");
            }
        }
    }
}
