package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.aux.Dialogs;
import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;
import P4_ComDis.objectimpl.ClientManagementImpl;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainPageController implements Initializable{

    //Atributos del fxml
    public Label userName;
    public VBox userList;
    public VBox rightBox;

    //Interfaz de gestión del chat (servidor)
    private ChatManagementInterface cm;
    //Usuario de esta sesión (con su nombre y contraseña):
    private User user;
    //Interfaz de este cliente
    private ClientManagementInterface client;
    //Referencia a la primaryStage donde se muestra la pantalla:
    private Stage primaryStage;
    //Controlador del chat abierto en un momento determinado:
    private ChatController controllerChat;
    //Controlador de una posible pantalla de amistades abierta en un momento determinado:
    private FriendshipsController controllerFriendships;
    //Controlador de una posible pantalla de ajustes de la cuenta abierta en un momento determinado:
    private AccountToolsController controllerAccount;
    //HashMap con los clientes amigos conectados:
    private HashMap<String, ClientManagementInterface> friendsConnected;
    //Lista de usuarios acompañadas de los nodos que les corresponden (se usa para controlar el listado de chats disponibles):
    private HashMap<String, Node> chatsInfo;
    //Lista de peticiones de amistad recibidas:
    private List<String> receivedRequests;
    //Lista de peticiones de amistad enviadas (sin confirmar):
    private List<String> sentRequests;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatsInfo = new HashMap<>();
        friendsConnected = new HashMap<>();
    }

    public void setValues(Stage primaryStage, ClientManagementImpl clientInfo, User user, ChatManagementInterface cm) {
        //Se asocian los valores pertinentes:
        this.primaryStage = primaryStage;
        this.client = clientInfo;
        this.user = user;
        this.cm = cm;

        //Asociamos el username:
        this.userName.setText(user.getUsername());

        //Se establecen las acciones a realizar en caso de querer cerrar la aplicación:
        this.primaryStage.setOnCloseRequest(event -> {
            try {
                this.cm.logoutFromChat(this.user);
            } catch (RemoteException e) {
                //Si salta una remoteException, avisamos de ella y se sale:
                Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + e.getMessage() +  ". Saliendo...");
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println("Salida correcta de la aplicación");
            System.exit(0);
        });
    }

    public void setLists(HashMap<String, ClientManagementInterface> connectedClients,
                         List<String> sentRequests,
                         List<String> receivedRequests) {
        //Asignamos los listados:
        this.friendsConnected = connectedClients;
        this.receivedRequests = receivedRequests;
        this.sentRequests = sentRequests;
        //Vaciamos el contenido del scrollpane y vamos asignando nuevos elementos con los nuevos usuarios:
        try {
            userList.getChildren().clear();
            for(Map.Entry<String, ClientManagementInterface> valueEntry: connectedClients.entrySet()){
                //Asignamos ubicación (el fxml del contenedor de la información del chat):
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatInfoContainer.fxml"));
                //Lo añadimos al listado de usuarios:
                Node newNode = loader.load();
                userList.getChildren().add(newNode);
                //Recuperamos el controlador y le asignamos la interfaz del cliente y la referencia de este controlador:
                loader.<ChatInfoContainerController>getController().setClientInt(valueEntry.getValue()).setParentController(this);
                //Asignamos la nueva entrada al hashmap:
                chatsInfo.put(valueEntry.getKey(), newNode);
            }
        } catch(IOException ex) {
            //Si se captura una excepción, se avisa de ello:
            System.out.println("Error cargando la información del chat: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void updateNewConnection(ClientManagementInterface newConnection){
        //Añadimos al hashmap:
        try {
            this.friendsConnected.put(newConnection.getClientName(), newConnection);
            //Si va bien, entonces se añade su entrada accesible para chatear con el usuario:
            //Asignamos ubicación (el fxml del contenedor de la información del chat):
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatInfoContainer.fxml"));
            //Lo añadimos al listado de usuarios:
            Node newNode = loader.load();
            userList.getChildren().add(newNode);
            //Recuperamos el controlador y le asignamos la interfaz del cliente y la referencia de este controlador:
            loader.<ChatInfoContainerController>getController().setClientInt(newConnection).setParentController(this);
            //Asignamos la nueva entrada al hashmap:
            chatsInfo.put(newConnection.getClientName(), newNode);
        } catch (IOException e) {
            System.out.println("Error. Cliente no responde adecuadamente en nueva conexión.");
            e.printStackTrace();
        }
    }

    public void updateNewDisconnect(ClientManagementInterface disconnected){
        //Se elimina el cliente que se desconecta:
        try {
            this.friendsConnected.remove(disconnected.getClientName());
            //Buscamos entre los elementos de la lista de usuarios conectados y eliminamos el del usuario desconectado:
            Node node = this.chatsInfo.get(disconnected.getClientName());
            //El nodo correspondiente se borra de la lista, para que se deje de ver:
            if(node != null){
                userList.getChildren().remove(node);
            }
        } catch (IOException e) {
            System.out.println("Error. No se ha podido mostrar correctamente la desconexión del cliente.");
            e.printStackTrace();
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
            //Ponemos el de amistades y el de la cuenta a null (se ha cargado un chat encima):
            this.controllerAccount = null;
            this.controllerFriendships = null;
        } catch (IOException ex){
            System.out.println("Error cargando el chat: " + ex.getMessage());
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

    public void btnFriendsOnClick(MouseEvent event){
        try {
            //Si se pulsa este botón, abriremos la pantalla de amigos:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Friendships.fxml"));
            //Recuperamos el controlador y le asignamos parámetros que le serán de utilidad:
            rightBox.getChildren().clear();
            rightBox.getChildren().add(loader.load());
            //Se permite crecimiento del VBox en vertical (diseño responsivo)
            VBox.setVgrow(rightBox.getChildren().get(0), Priority.ALWAYS);
            controllerFriendships = loader.<FriendshipsController>getController();
            controllerFriendships.setValues(this, this.sentRequests, this.receivedRequests);
            //Ahora mismo deja de haber un controllerChat o de cuenta activo, por lo que directamente se pone a null:
            this.controllerChat = null;
            this.controllerAccount = null;
        } catch (IOException ex) {
            System.out.println("Error cargando pantalla de amigos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void btnAccountOnClick(MouseEvent event){
        try {
            //Si se pulsa este botón, abriremos la pantalla de ajustes de la cuenta (borrar/cambiar password):
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AccountTools.fxml"));
            //Recuperamos el controlador y le asignamos parámetros que le serán de utilidad:
            rightBox.getChildren().clear();
            rightBox.getChildren().add(loader.load());
            //Se permite crecimiento del VBox en vertical (diseño responsivo)
            VBox.setVgrow(rightBox.getChildren().get(0), Priority.ALWAYS);
            this.controllerAccount = loader.<AccountToolsController>getController();
            this.controllerAccount.setValues(this);
            //El controlador activo pasa a ser el de la gestión de la cuenta, el resto se ponen a null:
            this.controllerChat = null;
            this.controllerFriendships = null;
        } catch (IOException ex) {
            System.out.println("Error cargando pantalla de amigos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public List<String> performSearch(String pattern) throws RemoteException{
        //Hacemos la búsqueda mediante llamada al servidor y devolvemos el resultado:
        return this.cm.searchFriends(this.user, pattern);
    }

    public void updateNewRequest(String userName) {
        //Tomamos la lista de solicitudes y añadimos ésta:
        this.receivedRequests.add(userName);
        //Si está abierto el controlador de la pestaña de amigos, se añade:
        if(this.controllerFriendships != null){
            this.controllerFriendships.addRequest(userName);
        }
    }

    public boolean manageSendRequest(String userName) {
        //Enviamos la solicitud y devolvemos el resultado:
        try {
            switch(cm.sendFriendRequest(this.user, userName)) {
                case ALREADY_FRIENDS:
                    //Si los usuarios ya tienen una petición, se avisa:
                    Dialogs.showError("Error", "Error al añadir el amigo", "Ya se tiene registrada amistad entre estos usuarios.");
                    return false;
                case DATABASE_ERROR:
                    //Se avisa en caso de error en la base de datos:
                    Dialogs.showError("Error", "Error al añadir el amigo", "Fallo en la base de datos. Inténtelo de nuevo más tarde.");
                    return false;
                case OK:
                    //En caso de acabar bien, se añade la solicitud enviada:
                    this.sentRequests.add(userName);
                    //Si está abierto el controlador de la pestaña de amigos (en teoría sí), se añade:
                    if(this.controllerFriendships != null){
                        this.controllerFriendships.addSent(userName);
                    }
                    return true;
                case UNAUTHORIZED:
                    //Se avisa en caso de no tener permiso:
                    Dialogs.showError("Error", "Error al añadir el amigo", "Usuario no autorizado para hacer la modificación");
                    return false;
                default:
                    return false;
            }
        } catch (RemoteException ex) {
            //Si se recibe una excepción al comunicarse con el servidor, se avisa de ella:
            Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + ex.getMessage() +  ". Saliendo...");
            return false;
        }
    }

    public boolean manageConfirmation(String userName) {
        //Enviamos la solicitud de confirmación y analizamos el resultado:
        try {
            switch(cm.acceptRequest(this.user, userName)){
                case DATABASE_ERROR:
                    //Se avisa en caso de error en la base de datos:
                    Dialogs.showError("Error", "Error al confirmar la solicitud",
                                        "Fallo en la base de datos. Inténtelo de nuevo más tarde.");
                    return false;
                case NOT_VALID:
                    //Se avisa en caso de confirmación errónea:
                    Dialogs.showError("Error", "Error al confirmar la solicitud",
                                        "No se encuentra la amistad correspondiente.");
                    return false;
                case OK:
                    //En este caso se elimina la solicitud pendiente del usuario:
                    receivedRequests.remove(userName);
                    return true;
                case UNAUTHORIZED:
                    Dialogs.showError("Error", "Error al confirmar la solicitud", 
                                        "Usuario no autorizado para hacer la modificación");
                    return false;
                default:
                    return false;
            }
        } catch (RemoteException ex) {
            //Si se recibe una excepción al comunicarse con el servidor, se avisa de ella:
            Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + ex.getMessage() +  ". Saliendo...");
            return false;
        }
    }

    public void deleteSentRequest(String clientName) {
        //Se actualiza la lista de solicitudes enviadas:
        this.sentRequests.remove(clientName);
        //Si la pantalla de amigos está abierta, se notifica:
        if(controllerFriendships != null) {
            controllerFriendships.removeSentReq(clientName);
        }
    }

    public boolean rejectRequest(String userName) {
        //Se intenta llamar al método del servidor:
        try {
            switch(cm.rejectFriendship(this.user, userName)){
            case DATABASE_ERROR:
                Dialogs.showError("Error", "Error al rechazar la solicitud",
                                    "Fallo en la base de datos. Inténtelo de nuevo más tarde.");
                return false;
            case NOT_VALID:
                Dialogs.showError("Error", "Error al confirmar la solicitud",
                                    "No existe ninguna solicitud que haya enviado " + userName + ".");
                return false;
            case OK:            
                //Se elimina la solicitud recibida:
                this.receivedRequests.remove(userName);
                return true;
            case UNAUTHORIZED:        
                Dialogs.showError("Error", "Error al confirmar la solicitud",
                                    "Usuario no autorizado a realizar la modificación.");
                return false;
            default:
                //Los resultados que puede devolver son los anteriores. En caso de obtener otro, se devolvería algo incorrecto:
                return false;
            }
        } catch (RemoteException ex) {
            //En caso de excepción remota, se avisa y se termina:
            Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + ex.getMessage() +  ". Saliendo...");
            return false;
        }
    }

    public boolean cancelRequest(String userName) {
        //Se intenta llamar al método correspondiente del servidor que permite cancelar la petición:
        try {
            switch(cm.cancelRequest(this.user, userName)){
            case DATABASE_ERROR:
                Dialogs.showError("Error", "Error al rechazar la solicitud",
                                    "Fallo en la base de datos. Inténtelo de nuevo más tarde.");
                return false;
            case NOT_VALID:
                Dialogs.showError("Error", "Error al confirmar la solicitud",
                                    "No existe ninguna solicitud enviada a " + userName + ".");
                return false;
            case OK:            
                //Se elimina la solicitud recibida:
                this.sentRequests.remove(userName);
                return true;
            case UNAUTHORIZED:        
                Dialogs.showError("Error", "Error al confirmar la solicitud",
                                    "Usuario no autorizado a realizar la cancelación de la solicitud.");
                return false;
            default:
                //Los resultados que puede devolver son los anteriores. En caso de obtener otro, se devolvería algo incorrecto:
                return false;
            }
        } catch (RemoteException ex) {
            //En caso de excepción remota, se avisa y se termina:
            Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + ex.getMessage() +  ". Saliendo...");
            return false;
        }
    }

    public void deleteReceivedRequest(String userName) {
        //Se actualiza la lista de solicitudes recibidas:
        this.receivedRequests.remove(userName);
        //Si la pantalla de amigos está abierta, se notifica:
        if(controllerFriendships != null) {
            controllerFriendships.removeRecReq(userName);
        }
    }

    public ResultType changePassword(String oldPass, String newPass) throws RemoteException {
        //Se crea un objeto usuario específico con el nuevo usuario:
        User user = new User(this.user.getUsername(), oldPass);
        //Se envía la solicitud:
        ResultType rt =  cm.changePassword(user, newPass);
        if(rt.equals(ResultType.OK)){
            //Si el resultado fue correcto, se asigna nueva contraseña al usuario:
            this.user.setPassword(newPass);
        }
        return rt;
    }

    public ResultType deleteAccount() throws RemoteException {
        return cm.unregister(this.user);
    }
}
