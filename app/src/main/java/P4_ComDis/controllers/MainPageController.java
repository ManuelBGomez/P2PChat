package P4_ComDis.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.objectimpl.ClientManagementImpl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainPageController implements Initializable{

    //Atributos del fxml
    public Label userName;
    public VBox userList;

    private ChatManagementInterface cm;
    private ClientManagementImpl client;
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);

        //Definimos la URL del registro:
        String registryURL = "rmi://localhost:1099/chatManager";

        try {
            //Recuperamos la interfaz del objeto servidor:
            this.cm = (ChatManagementInterface) Naming.lookup(registryURL);
            //Creamos objeto cliente:
            System.out.print("Please enter your name: ");
            //Asociamos el username a la pantalla:
            this.userName.setText(br.readLine());
            this.client = new ClientManagementImpl(this, this.userName.getText());
    
            //Le registramos en el chat:
            cm.registerInChat(client);
        } catch (NotBoundException | IOException e) {
            System.out.println("Problem in connection with server: " + e.getMessage());
            System.exit(0);
        }
    }


    public Stage getPrimaryStage() {
        return primaryStage;
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        primaryStage.setOnCloseRequest(event -> {
            try {
                cm.unregisterFromChat(client);
            } catch (RemoteException e) {
                System.out.println("Problem when unregistering.");
            }
            System.out.println("Unregistered from chat succesfully");
            System.exit(0);
        });
    }


    public void updateUserList(List<ClientManagementInterface> connectedClients) {
        //Vaciamos el contenido del scrollpane y vamos asignando nuevos elementos con los nuevos usuarios:
        try {
            userList.getChildren().clear();
            for(ClientManagementInterface client: connectedClients){
                System.out.println(connectedClients.size());
                //Asignamos ubicación (el fxml del contenedor de la información del chat):
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatInfoContainer.fxml"));
                //Lo añadimos al listado de usuarios:
                userList.getChildren().add(loader.load());
                //Recuperamos el controlador y le asignamos la interfaz del cliente y la referencia de este controlador:
                loader.<ChatInfoContainerController>getController().setClientImpl(client).setParentController(this);
            }
        } catch(Exception ex) {
            //Si se captura una excepción, se avisa de ello:
            System.out.println("Error loading chat info: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
