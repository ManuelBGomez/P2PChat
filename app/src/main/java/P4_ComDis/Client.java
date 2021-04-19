package P4_ComDis;

import java.net.URL;

import P4_ComDis.controllers.AccessController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase desde la que se inicia la aplicación JavaFX en el cliente.
 * 
 * @author Manuel Bendaña
 */
public class Client extends Application{
    /** 
     * Método ejecutado para iniciar la carga de la interface gráfica.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL fxmlLocation = getClass().getResource("/fxml/Access.fxml");
        //Cargamos el fichero fxml que se mostrará en la ventana:
        FXMLLoader root = new FXMLLoader(fxmlLocation);
        //Establecemos título sobre la ventana:
        primaryStage.setTitle("Chat");
        //Establecemos parámetros de la escena: el fichero fxml cargado y las dimensiones.
        primaryStage.setScene(new Scene(root.load(), 350, 500));
        //Definimos dimensiones mínimas:
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(350);
        //Asignamos primarystage:
        root.<AccessController>getController().setPrimaryStage(primaryStage);
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
