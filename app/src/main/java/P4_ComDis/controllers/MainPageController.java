package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.ClientManagementInterface;
import P4_ComDis.aux.Dialogs;
import P4_ComDis.model.dataClasses.Message;
import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;
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

/**
 * Clase que representa al controlador principal
 * 
 * @author Manuel Bendaña
 */
public class MainPageController implements Initializable{

    //Atributos del fxml
    public Label alertLabel;
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
    //Lista de chats con todos los usuarios (almacén temporal de mensajes):
    private HashMap<String, List<Message>> messages;

    
    /** 
     * Método ejecutado al inicializar la pantalla.
     * 
     * @param location url
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatsInfo = new HashMap<>();
        friendsConnected = new HashMap<>();
        messages = new HashMap<>();
        receivedRequests = new ArrayList<>();
        sentRequests = new ArrayList<>();

        //Se apaga el aviso de solicitudes pendientes por defecto:
        alertLabel.setVisible(false);
    }

    
    /** 
     * Método que permite pasar valores del controlador de acceso a este.
     * 
     * @param primaryStage la referencia a la stage mostrada en pantalla.
     * @param clientInfo información de este mismo cliente
     * @param user información del usuario (se tendrá que facilitar al servidor en cualquier solicitud que se haga).
     * @param cm Interfaz del servidor, para poder comunicarnos desde aquí con ella.
     */
    public void setValues(Stage primaryStage, ClientManagementInterface clientInfo, User user, ChatManagementInterface cm) {
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

    
    /** 
     * Método que nos permite establecer listas recibidas del servidor.
     * 
     * @param connectedClients lista de clientes amigos conectados actualmente.
     * @param sentRequests lista de solicitudes de amistad recibidas.
     * @param receivedRequests lista de solicitudes de amistad enviadas.
     */
    public void setLists(HashMap<String, ClientManagementInterface> connectedClients,
                         List<String> sentRequests,
                         List<String> receivedRequests) {
        //Asignamos los listados:
        this.friendsConnected = connectedClients;
        this.receivedRequests = receivedRequests;
        //Si se han recibido solicitudes, se activa el icono de alerta:
        this.checkAlert();
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

    
    /** 
     * Método que permite actualizar la lista de amigos conectados debido a una conexión.
     * 
     * @param newConnection interfaz del cliente que se ha conectado.
     */
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

    
    /** 
     * Método que permite actualizar la lista de amigos conectados debido a la desconexión de un cliente.
     * 
     * @param disconnected nombre del cliente amigo desconectado.
     */
    public void updateNewDisconnect(String disconnected){
        //Se elimina el cliente que se desconecta:
        this.friendsConnected.remove(disconnected);
        //Buscamos entre los elementos de la lista de usuarios conectados y eliminamos el del usuario desconectado:
        Node node = this.chatsInfo.get(disconnected);
        //El nodo correspondiente se borra de la lista, para que se deje de ver:
        if(node != null){
            userList.getChildren().remove(node);
        }
        //Si está abierto el chat con ese usuario, se cierra:
        if(controllerChat != null && controllerChat.nameTag.getText().equals(disconnected)) {
            rightBox.getChildren().clear();
            this.controllerChat = null;
        }
        //Eliminamos mensajes con esa persona (en caso de haberlos):
        this.messages.remove(disconnected);
    }

    
    /** 
     * Método que permite abrir un chat en la parte derecha de la ventana principal.
     * 
     * @param clientInt La interfaz cliente con la que nos comunicaremos a través de ese chat.
     */
    public void putChatScreen(ClientManagementInterface clientInt) {
        try {
            //Asignamos ubicación:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Chat.fxml"));
            //Recuperamos el controlador y le asignamos la interfaz. Con ello tenemos todo establecido
            rightBox.getChildren().clear();
            rightBox.getChildren().add(loader.load());
            VBox.setVgrow(rightBox.getChildren().get(0), Priority.ALWAYS);
            //Se carga la información (los mensajes solo si existen)
            loader.<ChatController>getController().setValues(clientInt, client, this, 
                messages.containsKey(clientInt.getClientName()) ? messages.get(clientInt.getClientName()) : new ArrayList<>());
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

    
    /** 
     * Método que permite cargar un mensaje recibido, almacenándolo localmente o mostrándolo en pantalla si 
     * es necesario.
     * 
     * @param message el mensaje recibido.
     * @throws RemoteException Excepción que puede ocurrir en la recuperación de información del cliente que manda el mensaje.
     */
    public void loadRecievedMessage(Message message) throws RemoteException {
        //Añadimos el mensaje al hashmap:
        if(!this.messages.containsKey(message.getClientInt().getClientName())) {
            //Se crea la entrada del hashmap si no existia
            this.messages.put(message.getClientInt().getClientName(), new ArrayList<>());
        } 
        //Se añade:
        this.messages.get(message.getClientInt().getClientName()).add(message);
        //Comprobamos si hay un chat abierto y si es del usuario que se pasa:
        if(controllerChat != null && controllerChat.getClientInt().equals(message.getClientInt())){
            //Entonces se mete mensaje en el chat:
            controllerChat.addMessage(message, false);
        } else {
            //Si no está abierto el chat, se abrirá una notificación:
            Image img = new Image("/img/comment.png");
            ImageView imv = new ImageView(img);
            imv.setFitHeight(50);
            imv.setFitWidth(50);
            //Se emplea para ello la clase notifications:
            Notifications notfBuilder = Notifications.create()
                .title("Mensaje entrante de: " + message.getClientInt().getClientName())
                .text(message.getMessageContent())
                .graphic(imv)
                .hideAfter(Duration.seconds(5))
                .darkStyle()
                .position(Pos.BOTTOM_RIGHT);
            notfBuilder.show();
        }
    }

    
    /** 
     * Método ejecutado en caso de pulsar el botón de las amistades, para abrir el chat de gestión de amistades.
     * 
     * @param event el evento de ratón que tuvo lugar.
     */
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

    
    /** 
     * Método ejecutado en caso de pulsar el botón de la gestión de la cuenta, para abrir la configuración.
     * 
     * @param event el evento de ratón que tuvo lugar.
     */
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

    
    /** 
     * Método que permite llevar a cabo una búsqueda de usuarios con el servidor.
     * 
     * @param pattern el patrón de búsqueda
     * @return List<String> la lista de usuarios recibida.
     * @throws RemoteException excepción remota que puede ocurrir en la comunicación con el servidor.
     */
    public List<String> performSearch(String pattern) throws RemoteException{
        //Hacemos la búsqueda mediante llamada al servidor y devolvemos el resultado:
        return this.cm.searchFriends(this.user, pattern);
    }

    
    /** 
     * Método que permite registrar una nueva solicitud de amistad recibida.
     * 
     * @param userName el nombre del usuario que la manda.
     */
    public void updateNewRequest(String userName) {
        //Tomamos la lista de solicitudes y añadimos ésta:
        this.receivedRequests.add(userName);
        this.checkAlert();
        //Si está abierto el controlador de la pestaña de amigos, se añade:
        if(this.controllerFriendships != null){
            this.controllerFriendships.addRequest(userName);
        }
    }

    
    /** 
     * Método que permite gestionar una solicitud enviada a otro usuario a través del servidor.
     * 
     * @param userName el usuario destinatario.
     * @return boolean si la solicitud se ha podido enviar correctamente o no (sólo nos interesa el booleano,
     *  no el tipo de resultado como en otros casos).
     */
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

    
    /** 
     * Método que permite gestionar la confirmación de una solicitud recibida mediante el servidor. 
     * 
     * @param userName el usuario al cual se le confirma la solicitud.
     * @return boolean si la solicitud se confirmó correctamente o no.
     */
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
                    //Se comprueba si hay más solicitudes:
                    this.checkAlert();
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

    
    /** 
     * Método que permite borrar una solicitud de amistad enviada.
     * 
     * @param clientName el destinatario de la solicitud a eliminar.
     */
    public void deleteSentRequest(String clientName) {
        //Se actualiza la lista de solicitudes enviadas:
        this.sentRequests.remove(clientName);
        //Si la pantalla de amigos está abierta, se notifica:
        if(controllerFriendships != null) {
            controllerFriendships.removeSentReq(clientName);
        }
    }

    
    /** 
     * Método que gestiona el rechazo de una solicitud de amistad.
     * 
     * @param userName el usuario al que se le rechaza la solicitud.
     * @return boolean si la solicitud se pudo rechazar o no correctamente.
     */
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
                //Se comprueba si hay más para mantener la notificación:
                this.checkAlert();
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

    
    /** 
     * Método que ppermite gestionar la cancelación de una solicitud de amistad por parte de un usuario.
     * 
     * @param userName el usuario al que se había enviado la solicitud.
     * @return boolean si la solicitud se pudo cancelar o no.
     */
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

    
    /** 
     * Método que permite borrar una solicitud de amistad recibida
     * 
     * @param userName el usuario que había enviado la solicitud.
     */
    public void deleteReceivedRequest(String userName) {
        //Se actualiza la lista de solicitudes recibidas:
        this.receivedRequests.remove(userName);
        //Se comprueba si hay más solicitudes para mantener el aviso.
        this.checkAlert();
        //Si la pantalla de amigos está abierta, se notifica:
        if(controllerFriendships != null) {
            controllerFriendships.removeRecReq(userName);
        }
    }

    
    /** 
     * Método que permite gestionar el cambio de la contraseña.
     * 
     * @param oldPass la contraseña antigua.
     * @param newPass la contraseña nueva.
     * @return ResultType el tipo de resultado a raíz del cambio de contraseña.
     * @throws RemoteException excepción remota lanzada en caso de problemas en la comunicación con el servidor.
     */
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

    
    /** 
     * Método que permite gestionar el borrado de una cuenta.
     * 
     * @return ResultType el resultado del borrado.
     * @throws RemoteException excepción remota lanzada en caaso de problemas en la comunicación con el servidor.
     */
    public ResultType deleteAccount() throws RemoteException {
        return cm.unregister(this.user);
    }

    
    /** 
     * Método que permite gestionar el borrado de una amistad.
     * 
     * @param friendName el amigo con el que se borra la amistad definitiva.
     */
    public void deleteFriendship(String friendName) {
        //Se intenta borrar la amistad:
        try {
            switch(cm.deleteFriendship(this.user, friendName)) {
                case DATABASE_ERROR:
                    Dialogs.showError("Error", "Error al borrar la amistad",
                                        "Fallo en la base de datos. Inténtelo de nuevo más tarde.");
                    break;
                case NOT_VALID:
                    Dialogs.showError("Error", "Error al borrar la amistad",
                                    "No se ha encontrado la amistad en la base de datos.");
                    break;
                case OK:
                    //Se borra el amigo:
                    this.updateNewDisconnect(friendName);
                    break;
                case UNAUTHORIZED:
                    Dialogs.showError("Error", "Error al borrar la amistad",
                                    "Usuario no autorizado para realizar este borrado.");
                    break;
                default:
                    //No se debería devolver otro resultado.
                    break;
            }
        } catch (RemoteException ex) {
            //En caso de excepción remota, se avisa y se termina:
            Dialogs.showError("Error", "Error en la conexión con el servidor", "Motivo: " + ex.getMessage() +  ". Saliendo...");
        }
    }

    
    /** 
     * Método que permite añadir un mensaje nuevo al hashmap:
     * 
     * @param user el nombre del usuario que lo envía
     * @param message el mensaje enviado
     */
    public void addMessage(String user, Message message) {
        //Añadimos el mensaje al hashmap:
        if(!this.messages.containsKey(user)) {
            //Se crea la entrada del hashmap si no existia
            this.messages.put(user, new ArrayList<>());
        } 
        //Se añade:
        this.messages.get(user).add(message);
    }

    /**
     * Método que permite gestionar la aparición de una alerta en pantalla en caso de existir solicitudes
     * de amistad sin gestionar.
     */
    private void checkAlert(){
        //Si hay solicitudes, se activa la etiqueta con el aviso:
        if(!this.receivedRequests.isEmpty()){
            alertLabel.setVisible(true);
        } else {
            //Si no, permanece desactivado:
            alertLabel.setVisible(false);
        }
    }
}
