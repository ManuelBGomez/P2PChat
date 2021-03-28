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

import org.controlsfx.control.Notifications;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.model.dataClasses.User;
import P4_ComDis.objectimpl.ClientManagementImpl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainPageController implements Initializable{

    //Atributos del fxml
    public Label userName;
    public VBox userList;
    public VBox rightBox;

    private ChatManagementInterface cm;
    private User user;
    private ClientManagementInterface client;
    private Stage primaryStage;
    private ChatController controllerChat;

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
            System.out.print("Please enter your name and password: ");
            //Asociamos el username a la pantalla:
            this.user = new User(br.readLine(), br.readLine());
            
            this.userName.setText(user.getUsername());

            this.client = new ClientManagementImpl(this, this.userName.getText());
    
            //Le registramos en el chat:
            cm.loginToChat(user, client);
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

        //Se establecen las acciones a realizar en caso de querer cerrar la aplicación:
        primaryStage.setOnCloseRequest(event -> {
            try {
                cm.logoutFromChat(this.user);
            } catch (RemoteException e) {
                System.out.println("Problemas al salir de la aplicación.");
            }
            System.out.println("Salida correcta de la aplicación");
            System.exit(0);
        });
    }


    public void updateUserList(List<ClientManagementInterface> connectedClients) {
        //Vaciamos el contenido del scrollpane y vamos asignando nuevos elementos con los nuevos usuarios:
        try {
            userList.getChildren().clear();
            for(ClientManagementInterface client: connectedClients){
                //Asignamos ubicación (el fxml del contenedor de la información del chat):
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatInfoContainer.fxml"));
                //Lo añadimos al listado de usuarios:
                userList.getChildren().add(loader.load());
                //Recuperamos el controlador y le asignamos la interfaz del cliente y la referencia de este controlador:
                loader.<ChatInfoContainerController>getController().setClientInt(client).setParentController(this);
            }
        } catch(Exception ex) {
            //Si se captura una excepción, se avisa de ello:
            System.out.println("Error loading chat info: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void putChatScreen(ClientManagementInterface clientInt) {
        try {
            //Asignamos ubicación:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Chat.fxml"));
            //Recuperamos el controlador y le asignamos la interfaz. Con ello tenemos todo establecido
            rightBox.getChildren().clear();
            rightBox.getChildren().add(loader.load());
            VBox.setVgrow(rightBox.getChildren().get(0), Priority.ALWAYS);
            loader.<ChatController>getController().setClientAndSenderInt(clientInt, client);
            //Guardamos controlador en chat privado:
            controllerChat = loader.<ChatController>getController();
        } catch (IOException ex){
            System.out.println("Error loading chat screen: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void loadRecievedMessage(String message, ClientManagementInterface clientInt, String time) throws RemoteException {
        //Comprobamos si hay un chat abierto y si es del usuario que se pasa:
        if(controllerChat != null && controllerChat.getClientInt().equals(clientInt)){
            //Entonces se mete mensaje en el chat:
            controllerChat.addMessage(message, clientInt, time, false);
        } else {
            //Si no está abierto el chat, se abrirá una notificación:
            Image img = new Image("/img/comment.png");
            ImageView imv = new ImageView(img);
            imv.setFitHeight(50);
            imv.setFitWidth(50);
            //Se emplea para ello la clase notifications:
            Notifications notfBuilder = Notifications.create()
                .title("Mensaje entrante de: " + clientInt.getClientName())
                .text(message)
                .graphic(imv)
                .hideAfter(Duration.seconds(5))
                .darkStyle()
                .position(Pos.BOTTOM_RIGHT);
            notfBuilder.show();
        }
    }
}
