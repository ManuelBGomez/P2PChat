package P4_ComDis.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import P4_ComDis.aux.Dialogs;
import P4_ComDis.model.dataClasses.User;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class FriendshipsController implements Initializable{

    //Atributos públicos en representación de elementos de la interfaz:
    public TextField searchText;
    public VBox usersAddList;

    //Atributos privados útiles:
    //Referencia al controlador principal:
    private MainPageController controllerPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void setValues(MainPageController controllerPrincipal, User user){
        this.controllerPrincipal = controllerPrincipal;
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
        try {
            List<String> result = this.controllerPrincipal.performSearch(searchText.getText());
            usersAddList.getChildren().clear();
            for(String name: result){
                //Para cada usuario, creamos un elemento de interfaz que contenga su nombre y un botón para añadir al amigo:
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FriendInfo.fxml"));
                usersAddList.getChildren().add(loader.load());
                loader.<FriendInfoController>getController().setValues(name, true, this);
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
