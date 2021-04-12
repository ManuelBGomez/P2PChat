package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.aux.Dialogs;
import P4_ComDis.model.dataClasses.User;
import P4_ComDis.objectimpl.ClientManagementImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class AccessController implements Initializable {

    //Atributos públicos: elementos de la interfaz.
    public TextField userName;
    public PasswordField password;
    public Button btnLogin;
    public Button btnRegister;
    public Label labelError;

    //Atributos privados.
    private Stage primaryStage;
    private MainPageController mainController;
    private Scene nextScene;
    private ClientManagementImpl cImpl;
    private ChatManagementInterface cmInt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Listeners para los campos de texto, para permitir activar/desactivar botones:
        ChangeListener<String> listText = new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //Comprobamos que ambos campos tengan algo
                if(!userName.getText().isEmpty() && !password.getText().isEmpty()){
                    //Si es así, se activan botones:
                    btnLogin.setDisable(false);
                    btnRegister.setDisable(false);
                } else {
                    //Si no, se desactivan:
                    btnLogin.setDisable(true);
                    btnRegister.setDisable(true);
                }
            }
        };

        userName.textProperty().addListener(listText);
        password.textProperty().addListener(listText);

        //Creamos ya de antemano la siguiente escena que se mostrará:

        try {
            //Todo oK: se procede a iniciar la siguiente pantalla.
            URL fxmlLocation = getClass().getResource("/fxml/MainPage.fxml");
            //Cargamos el fichero fxml que se mostrará en la ventana:
            FXMLLoader root = new FXMLLoader(fxmlLocation);
            //Establecemos parámetros de la escena: el fichero fxml cargado y las dimensiones.
            this.nextScene = new Scene(root.load(), 1280, 720);

            //Recuperamos el controlador:
            this.mainController = root.<MainPageController>getController();
            //Además, se recupera la interfaz del servidor:
            String registryURL = "rmi://localhost:1099/chatManager";

            //Recuperamos la interfaz del objeto servidor:
            this.cmInt = (ChatManagementInterface) Naming.lookup(registryURL);
        } catch (NotBoundException | IOException e) {
            //Si no se puede, se sale directamente:
            System.out.println("Excepción recibida en inicialización:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void btnLoginOnClick(ActionEvent event){
        //Se hace el inicio de sesión, haciendo la petición al servidor:
        //Creamos el usuario a pasar al servidor:
        User user = new User(userName.getText(), password.getText());
        try {
            //Se establece el nombre del cliente en la interfaz del cliente (será la referencia del nombre que tendrán los
            //demás clientes con los que nos comuniquemos)
            //Vamos creando ya la interfaz cliente (sin nada):    
            this.cImpl = new ClientManagementImpl(this.mainController, user.getUsername());
            //Se hace el login y se evalúa el resultado obtenido:
            switch(cmInt.loginToChat(user, this.cImpl)){
            case ALREADY_CONNECTED:
                //Usuario ya conectado, se avisa (y se oculta la label de error por si se abriese antes):
                labelError.setVisible(false);
                Dialogs.showError("Error", "Error iniciando sesión", "Usuario con la sesión ya iniciada.");
                break;
            case DATABASE_ERROR:
                //Error en la DB, se avisa (y se oculta la label de error por si se abriese antes):
                labelError.setVisible(false);
                Dialogs.showError("Error", "Error iniciando sesión", "Error en la base de datos, por favor, inténtelo de nuevo más tarde.");
                break;
            case OK:
                //Establecemos parámetros de la escena (cargada ya con anterioridad)
                primaryStage.setScene(this.nextScene);
                //Definimos dimensiones mínimas:
                primaryStage.setMinHeight(720);
                primaryStage.setMinWidth(1280);
                //Asignamos los valores pertinentes, pues a partir de ahora trabajaremos en esa pantalla:
                this.mainController.setValues(this.primaryStage, this.cImpl, user, this.cmInt);
                break;
            case UNAUTHORIZED:
                //Se abre el diálogo oculto:
                labelError.setText("Credenciales erróneas");
                labelError.setVisible(true);
                break;
            default:
                //No hay más casos que pueden salir.
                break;
                
            }
        } catch (RemoteException e) {
            //Si salta una remoteException, avisamos de ella y se sale:
            Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + e.getMessage() +  ". Saliendo...");
            e.printStackTrace();
            System.exit(1);
        }
        
    }

    public void btnRegisterOnClick(ActionEvent event){
        //Se hace el registro, haciendo la petición al servidor:
        //Creamos el usuario a pasar al servidor:
        User user = new User(userName.getText(), password.getText());

        //Llamamos al método necesario, capturando excepción remota:
        try {
            //Se establece el nombre del cliente en la interfaz del cliente (será la referencia del nombre que tendrán los
            //demás clientes con los que nos comuniquemos)
            //Vamos creando ya la interfaz cliente (sin nada):    
            this.cImpl = new ClientManagementImpl(this.mainController, user.getUsername());
            
            //Se hace el registro y se evalúa el resultado obtenido:
            switch(cmInt.registerInChat(user, this.cImpl)){
            case DATABASE_ERROR:
                //Error en la DB, se avisa (y se oculta la label de error por si se abriese antes):
                labelError.setVisible(false);
                Dialogs.showError("Error", "Error en registro", "Error en la base de datos, por favor, inténtelo de nuevo más tarde.");
                break;
            case OK:
                //Establecemos parámetros de la escena (cargada ya con anterioridad)
                primaryStage.setScene(this.nextScene);
                //Definimos dimensiones mínimas:
                primaryStage.setMinHeight(720);
                primaryStage.setMinWidth(1280);
                //Asignamos los valores pertinentes, pues a partir de ahora trabajaremos en esa pantalla:
                this.mainController.setValues(this.primaryStage, this.cImpl, user, this.cmInt);
                break;
            case UNAUTHORIZED:
                //Se abre el diálogo oculto:
                labelError.setText("Usuario ya existente");
                labelError.setVisible(true);
                break;
            default:
                //No hay más casos que pueden salir.
                break;
                
            }
        } catch (RemoteException e) {
            //Si salta una remoteException, avisamos de ella y se sale:
            Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + e.getMessage() +  ". Saliendo...");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
