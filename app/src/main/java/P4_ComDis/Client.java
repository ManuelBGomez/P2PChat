package P4_ComDis;

import java.net.URL;

import P4_ComDis.controllers.MainPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application{
    /** 
     * Método ejecutado para iniciar la carga de la interface gráfica.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL fxmlLocation = getClass().getResource("/fxml/MainPage.fxml");
        //Cargamos el fichero fxml que se mostrará en la ventana:
        FXMLLoader root = new FXMLLoader(fxmlLocation);
        //Establecemos título sobre la ventana:
        primaryStage.setTitle("Chat");
        //Establecemos parámetros de la escena: el fichero fxml cargado y las dimensiones.
        primaryStage.setScene(new Scene(root.load(), 1280, 720));
        //Definimos dimensiones mínimas:
        primaryStage.setMinHeight(720);
        primaryStage.setMinWidth(1280);
        //Asignamos primarystage:
        root.<MainPageController>getController().setPrimaryStage(primaryStage);
        //Mostramos la pantalla:
        primaryStage.show();
    }
    
    /** 
     * Método main de la clase, llamado desde el main del cliente
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
