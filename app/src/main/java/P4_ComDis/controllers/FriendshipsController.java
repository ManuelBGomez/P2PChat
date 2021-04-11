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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

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

    public void onEnter(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            requestSearch();
        }
    }

    public void btnSearchOnClick(MouseEvent event){
        requestSearch();
    }

    private void requestSearch(){
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

    public void manageSendRequest(String userName) {
        //Llamamos al controlador principal para que gestione esta llamada:
        if(this.controllerPrincipal.manageSendRequest(userName)){
            //Si ha terminado correctamente (resultado true), entonces se quita el elemento de la búsqueda:
            usersAddList.getChildren().remove(addList.get(userName));
            //Se elimina también del hashmap:
            addList.remove(userName);
        }
    }

    public void manageConfirmation(String userName) {
        //Llamamos al controlador principal para la gestión de la llamada:
        if(this.controllerPrincipal.manageConfirmation(userName)){
            //Si termina correctamente, entonces se quita el elemento de la búsqueda:
            usersReceivedList.getChildren().remove(receivedList.get(userName));
            //Se elimina también del hashmap:
            receivedList.remove(userName);
        }
    }

    public void removeSentReq(String clientName) {
        //Eliminamos la solicitud:
        usersSentList.getChildren().remove(sentList.get(clientName));
        //Se elimina también del hashmap:
        sentList.remove(clientName);
    }

    public void manageRejection(String text, FriendRequestType rType) {
        //En función del tipo se llamará a un método adecuado.
    }

    
}
