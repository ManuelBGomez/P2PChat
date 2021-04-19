package P4_ComDis.controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import P4_ComDis.ClientManagementInterface;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * Controlador de la parte de la interfaz contenedora de la información de un chat determinado.
 * 
 * @author Manuel Bendaña
 */
public class ChatInfoContainerController implements Initializable{

    //Atributos públicos de la ventana del fxml:
    public Label chatName;

    //Atributos privados:
    private MainPageController parentController;
    private ClientManagementInterface clientInt;

    
    /** 
     * Método ejecutado nada más inicializar la interfaz correspondiente.
     * 
     * @param location url
     * @param resources recursos
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    
    /**
     * Método que permite recuperar la interfaz perteneciente al usuario representado dentro de este contenedor.
     *  
     * @return ClientManagementInterface la interfaz del cliente asociado.
     */
    public ClientManagementInterface getClientImpl() {
        return clientInt;
    }

    
    /** 
     * Método que permite establecer la interfaz del cliente perteneciente al usuario que se representa dentro
     * de este contenedor.
     * 
     * @param client La interfaz a asociar.
     * @return ChatInfoContainerController datos de este controlador.
     * @throws RemoteException Excepción lanzada en caso de que haya algún problema al comunicarse con la interfaz remota.
     */
    public ChatInfoContainerController setClientInt(ClientManagementInterface client) throws RemoteException {
        //Asociamos valores:
        this.clientInt = client;
        //A mayores, ponemos en la etiqueta el nombre del usuario:
        chatName.setText(client.getClientName());
        return this;
    }

    
    /** 
     * Método que permite recuperar el controlador principal.
     * 
     * @return MainPageController el controlador principal almacenado.
     */
    public MainPageController getParentController() {
        return parentController;
    }

    
    /** 
     * Método que permite establecer el controlador principal.
     * 
     * @param parentController El controlador principal a establecer.
     * @return ChatInfoContainerController referencia a este controlador y sus datos.
     */
    public ChatInfoContainerController setParentController(MainPageController parentController) {
        this.parentController = parentController;
        return this;
    }
    
    
    /** 
     * Método ejecutado en caso de pulsar sobre el contenedor (para a raíz de ello abrir el chat correspondiente con el
     * usuario representado en este contenedor).
     * 
     * @param event El evento de ratón que se produce.
     */
    public void chatOnClick(MouseEvent event){
        //Se cargará la pantalla de chat en la parte derecha:
        parentController.putChatScreen(clientInt);
    }

}
