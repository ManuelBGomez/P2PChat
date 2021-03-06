package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import P4_ComDis.aux.Dialogs;
import P4_ComDis.model.dataClasses.FriendRequestType;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Controlador de la pantalla de gestión de las amistades y las solicitudes.
 * 
 * @author Manuel Bendaña
 */
public class FriendshipsController implements Initializable{

    //Atributos públicos en representación de elementos de la interfaz:
    public TextField searchText;
    public VBox usersAddList;
    public VBox usersReceivedList;
    public VBox usersSentList;

    //Atributos privados útiles:
    //Referencia al controlador principal:
    private MainPageController controllerPrincipal;
    //Hashmaps para las diferentes listas utilizadas:
    private HashMap<String, Node> addList;
    private HashMap<String, Node> receivedList;
    private HashMap<String, Node> sentList;

    
    /** 
     * Método ejecutado al iniciar la pantalla correspondiente.
     * 
     * @param location url
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    
    /** 
     * Método que permite pasarle valores a este controlador.
     * 
     * @param controllerPrincipal referencia del controlador principal.
     * @param sentRequests Lista de solicitudes enviadas, para cargarlas.
     * @param receivedRequests Lista de solicitudes recibidas, para cargarlas.
     */
    public void setValues(MainPageController controllerPrincipal, List<String> sentRequests, List<String> receivedRequests){
        this.controllerPrincipal = controllerPrincipal;
        usersSentList.getChildren().clear();
        usersReceivedList.getChildren().clear();

        //Inicializamos hashMaps:
        this.receivedList = new HashMap<>();
        this.sentList = new HashMap<>();

        try {
            //Para el listado de solicitudes enviadas, se añaden al vbox que corresponde:
            for(String name: sentRequests) {
                //Para cada usuario, creamos un elemento de interfaz que contenga su nombre y un botón para añadir al amigo:
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendInfo.fxml"));
                Node nodeSent = loader.load();
                usersSentList.getChildren().add(nodeSent);
                //Añadimos a la lista de enviados:
                this.sentList.put(name, nodeSent);
                loader.<FriendInfoController>getController().setValues(name, FriendRequestType.SENT, this);
            }
            //Para el listado de solicitudes recibidas, se hace lo análogo:
            for(String name: receivedRequests) {
                //Para cada usuario, creamos un elemento de interfaz que contenga su nombre y un botón para añadir al amigo:
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendInfo.fxml"));
                Node nodeRec = loader.load();
                usersReceivedList.getChildren().add(nodeRec);
                //Añadimos a la lista de recibidos:
                this.receivedList.put(name, nodeRec);
                loader.<FriendInfoController>getController().setValues(name, FriendRequestType.RECV, this);
            }
        } catch (IOException ex) {
            //Se imprime mensaje si hay excepción:
            System.out.println("Error iniciando la pantalla: " + ex.getMessage());
        }
    }

    
    /** 
     * Método ejecutado en caso de pulsar una tecla en el cuadro de texto de búsqueda de amistades.
     * 
     * @param ke El evento de teclado que tiene lugar.
     */
    public void onEnter(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            requestSearch();
        }
    }

    
    /** 
     * Método ejecutado en caso de pulsar el botón de búsqueda.
     * 
     * @param event El evento de ratón que tiene lugar.
     */
    public void btnSearchOnClick(MouseEvent event){
        requestSearch();
    }

    /**
     * Método encargado de gestionar la búsqueda de personas no amigas a partir de los términos especificados.
     */
    private void requestSearch(){
        //Comprobamos que la búsqueda tenga una longitud mínima:
        if(searchText.getText().length() >= 4){
            //Llamamos al controlador principal para que se encargue de la consulta:
            this.addList = new HashMap<>();
            try {
                List<String> result = this.controllerPrincipal.performSearch(searchText.getText());
                usersAddList.getChildren().clear();
                for(String name: result){
                    //Para cada usuario, creamos un elemento de interfaz que contenga su nombre y un botón para añadir al amigo:
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendInfo.fxml"));
                    //Cargamos el nodo:
                    Node nodeAdd = loader.load();
                    usersAddList.getChildren().add(nodeAdd);
                    this.addList.put(name, nodeAdd);
                    loader.<FriendInfoController>getController().setValues(name, FriendRequestType.SEARCH, this);
                }
            } catch(RemoteException ex){
                //Si se captura excepción remota se avisa de ello:
                Dialogs.showError("Error", "Fallo al comunicarse con el servidor", ex.getMessage());
            } catch (IOException ex) {
                //También si hay problemas al abrir la pantalla
                System.out.println("Error iniciando la pantalla: " + ex.getMessage());
            }
        }
    }

    
    /** 
     * Método que permite añadir una solicitud de amistad recibida del servidor.
     * 
     * @param userName El usuario que la envía.
     */
    public void addRequest(String userName) {
        //Se crea el nuevo elemento de interfaz:
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendInfo.fxml"));
            Node node = loader.load();
            usersReceivedList.getChildren().add(node);
            receivedList.put(userName, node);
            loader.<FriendInfoController>getController().setValues(userName, FriendRequestType.RECV, this);
        } catch(IOException ex) {
            //También si hay problemas al abrir la pantalla se avisa de ello:
            System.out.println("Error iniciando la pantalla: " + ex.getMessage());
        }
    }
    
    
    /** 
     * Método que permite añadir una solicitud de amistad que este usuario ha decidido enviar.
     * 
     * @param userName el nombre del destinatario
     */
    public void addSent(String userName) {
        //Se crea el nuevo elemento de interfaz:
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendInfo.fxml"));
            Node node = loader.load();
            usersSentList.getChildren().add(node);
            sentList.put(userName, node);
            loader.<FriendInfoController>getController().setValues(userName, FriendRequestType.SENT, this);
        } catch(IOException ex) {
            //También si hay problemas al abrir la pantalla se avisa de ello:
            System.out.println("Error iniciando la pantalla: " + ex.getMessage());
        }
    }

    
    /** 
     * Método que permite gestionar el envio de una solicitud de amistad.
     * 
     * @param userName el destinatario de la solicitud.
     */
    public void manageSendRequest(String userName) {
        //Llamamos al controlador principal para que gestione esta llamada:
        if(this.controllerPrincipal.manageSendRequest(userName)){
            //Si ha terminado correctamente (resultado true), entonces se quita el elemento de la búsqueda:
            usersAddList.getChildren().remove(addList.get(userName));
            //Se elimina también del hashmap:
            addList.remove(userName);
        }
    }

    
    /** 
     * Método que permite gestionar la confirmación de una solicitud de amistad.
     * 
     * @param userName el usuario que envió la solicitud.
     */
    public void manageConfirmation(String userName) {
        //Llamamos al controlador principal para la gestión de la llamada:
        if(this.controllerPrincipal.manageConfirmation(userName)){
            //Si termina correctamente, entonces se quita el elemento de la búsqueda:
            usersReceivedList.getChildren().remove(receivedList.get(userName));
            //Se elimina también del hashmap:
            receivedList.remove(userName);
        }
    }

    
    /** 
     * Método que permite gestionar el borrado de una solicitud creada por este propio usuario.
     * 
     * @param clientName el destinatario de la solicitud enviada que se quiere eliminar.
     */
    public void removeSentReq(String clientName) {
        //Eliminamos la solicitud:
        usersSentList.getChildren().remove(sentList.get(clientName));
        //Se elimina también del hashmap:
        sentList.remove(clientName);
    }

    
    /** 
     * Método que permite borrar una solicitud recibida debido a una cancelación.
     * 
     * @param userName El usuario que había enviado la solicitud.
     */
    public void removeRecReq(String userName) {
        //Eliminamos la solicitud:
        usersReceivedList.getChildren().remove(receivedList.get(userName));
        //Se elimina también del hashmap:
        receivedList.remove(userName);
    }
    
    
    /** 
     * Método que permite gestionar el rechazo de una solicitud.
     * 
     * @param userName El usuario al que se le rechaza
     * @param rType El tipo de rechazo hecho (solicitud recibida o enviada -que se cancela-)
     */
    public void manageRejection(String userName, FriendRequestType rType) {
        //En función del tipo se llamará a un método adecuado.
        switch(rType){
        case RECV:
            //Solicitud recibida: se gestionará el rechazo de la solicitud:
            if(controllerPrincipal.rejectRequest(userName)){
                //Si se ha rechazado, se elimina de las solicitudes recibidas:
                usersReceivedList.getChildren().remove(receivedList.get(userName));
                //Se elimina también del hashmap:
                receivedList.remove(userName);
            }
            
            break;
        case SENT:
            //Solicitud enviada: se gestionará la cancelación de una solicitud.
            if(controllerPrincipal.cancelRequest(userName)){
                //Si se cancela correctamente, se elimina de las solicitudes recibidas:
                usersSentList.getChildren().remove(sentList.get(userName));
                //Se elimina también del hashmap:
                sentList.remove(userName);
            }
            break;
        default:
            break;
        }
    }


    
}
