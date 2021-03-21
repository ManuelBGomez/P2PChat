package P4_ComDis.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import P4_ComDis.ChatManagementInterface;
import P4_ComDis.objectimpl.ClientManagementImpl;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class MainPageController implements Initializable{

    private ChatManagementInterface cm;
    private ClientManagementImpl client;
    private Stage primaryStage;

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
            this.client = new ClientManagementImpl();

            System.out.print("Please enter your name: ");
            client.setClientName(br.readLine());

            //Le registramos en el chat:
            cm.registerInChat(client);
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

        primaryStage.setOnCloseRequest(event -> {
            try {
                cm.unregisterFromChat(client);
            } catch (RemoteException e) {
                System.out.println("Problem when unregistering.");
            }
            System.out.println("Unregistered from chat succesfully");
            System.exit(0);
        });
    }
}
