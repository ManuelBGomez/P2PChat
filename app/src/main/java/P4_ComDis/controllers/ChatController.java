package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import P4_ComDis.ClientManagementInterface;
import P4_ComDis.aux.Dialogs;
import P4_ComDis.model.dataClasses.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controlador de la pantalla de gestión de un chat.
 */
public class ChatController implements Initializable {

    //Atributos privados: interfaces clientes y controlador principal:
    private ClientManagementInterface clientInt;
    private ClientManagementInterface senderInt;
    private MainPageController controllerPrincipal;

    //Atributos públicos: elementos de la interfaz
    public Label nameTag;
    public VBox vBoxMessages;
    public TextField messageText;
    public ScrollPane scrollChatContent;


    
    /** 
     * Método ejecutado nada más iniciarse la pantalla.
     * 
     * @param location url
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Se asocia el alto del panel del chat al contenido (al principio vacío, irá creciendo con los mensajes):
        scrollChatContent.vvalueProperty().bind(vBoxMessages.heightProperty());
    }

    
    /** 
     * Método que permite recuperar la interfaz del cliente con el que nos comunicamos.
     * @return ClientManagementInterface la interfaz del cliente
     */
    public ClientManagementInterface getClientInt() {
        return clientInt;
    }

    
    /** 
     * Método que permite pasar diferentes valores a este controlador.
     * 
     * @param clientInt La interfaz del cliente con el que se establece el chat (que está ejecutandose en otro lado).
     * @param senderInt La interfaz del cliente que abre este chat (o sea, el propio cliente).
     * @param controllerPrincipal Referencia al controlador principal.
     * @param messages Lista de mensajes ya almacenados entre esos dos usuarios, para poderlos ir mostrando.
     * @throws RemoteException Excepción remota lanzada en caso de encontrar problemas.
     */
    public void setValues(ClientManagementInterface clientInt, ClientManagementInterface senderInt,
                            MainPageController controllerPrincipal, List<Message> messages) throws RemoteException {
        //Asignamos la interfaz del cliente:
        this.clientInt = clientInt;
        this.senderInt = senderInt;
        this.controllerPrincipal = controllerPrincipal;
        //Establecemos el texto de la etiqueta con el nombre:
        nameTag.setText(clientInt.getClientName());
        //Cargamos todos los mensajes:
        for(Message message: messages){
            //Es el propio si el que envía es este cliente. Si es el otro usuario (el indicado por la tag), será recibido:
            this.addMessage(message, !message.getUserName().equals(nameTag.getText()));
        }
    }


    /**
     * Método que permite añadir un mensaje en la pantalla.
     *  
     * @param message el mensaje a añadir
     * @param send booleano que indica si el mensaje a añadir es propio o no (simplemente para la colocación en pantalla)
     */
    public void addMessage(Message message, boolean send) {
        //Se crea un nuevo mensaje a partir de su fxml:
        try {
            //Asignamos ubicación:
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MessageContainer.fxml"));
            //Recuperamos el controlador y le asignamos la interfaz. Con ello tenemos todo establecido
            Node msg = loader.load();
            VBox.setVgrow(msg, Priority.ALWAYS);
            vBoxMessages.getChildren().add(msg);
            loader.<MessageContainerController>getController().loadMessage(message, send);
        } catch (IOException ex){
            System.out.println("Error loading message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    
    /** 
     * Método ejecutado en caso de pulsar el botón de envío de mensaje.
     * 
     * @param event El evento de ratón que tiene lugar.
     */
    public void btnSendOnClick(MouseEvent event){
        send();
    }

    
    /** 
     * Método ejecutado en caso de pulsar el botón de borrado de la amistad.
     * 
     * @param event El evento que tiene lugar.
     */
    public void btnDeleteFriendshipOnClick(ActionEvent event){
        //Pedimos confirmación
        Boolean result = Dialogs.showConfirmation("Confirmación borrado", 
                                                                "¿Estás seguro que deseas borrar la amistad?", "");
        if(result){
            //Llamamos al controlador principal para gestionar el borrado:
            this.controllerPrincipal.deleteFriendship(nameTag.getText());
        }
    }
 
    
    /** 
     * Método ejecutado en caso de pulsar una tecla en el cuadro de texto de envío de mensajes.
     * 
     * @param ke El evento de teclado que tiene lugar.
     */
    public void onEnter(KeyEvent ke) {
        //Se comprueba que se pulsara la tecla enter (envío de mensaje):
        if (ke.getCode().equals(KeyCode.ENTER)) {
            send();
        }
    }

    /**
     * Método que permite gestionar el envío de un mensaje cuando se solicite (se pone separado porque hay dos eventos que
     * lo pueden accionar).
     */
    private void send(){
        //Se procederá al envío del mensaje (si no salta una excepción Y SI EL MENSAJE NO ESTÁ VACÍO):
        if(!messageText.getText().isEmpty()) {
            try {
                Message message = new Message(messageText.getText(), senderInt);
                clientInt.sendMessage(message);
                //Además, se cargará el mensaje:
                this.addMessage(message, true);
                //Se añade al registro:
                controllerPrincipal.addMessage(nameTag.getText(), message);
                //Vaciamos el textField:
                messageText.setText("");
            } catch (RemoteException e) {
                System.out.println("Error on sending: " + e.getMessage());
            }
        }
    }

}
