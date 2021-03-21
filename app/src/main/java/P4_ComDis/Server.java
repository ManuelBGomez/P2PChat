package P4_ComDis;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import P4_ComDis.objectimpl.ChatManagementImpl;

public class Server {
    public static void main(String[] args){
        try {
            //Tratamos de iniciar un registro en el puerto que corresponde (usaremos siempre el que hay por defecto):
            startRegistry();
            ChatManagementImpl cm = new ChatManagementImpl();
            //Definimos URL para el registro:
            String registryURL = "rmi://localhost:1099/chatManager";
            Naming.rebind(registryURL, cm);
            System.out.println("Listening on port 1099");
        } catch (Exception ex) {
            System.out.println("Error while starting chat server: " + ex.getMessage());
            System.exit(0);
        }
    }

    private static void startRegistry() throws RemoteException{
        try {
            //Tratamos de recuperar un registro de java RMI en el puerto 1099:
            Registry registry = LocateRegistry.getRegistry(1099);
            registry.list();
        } catch (RemoteException e) { 
            //Si se captura esta excepción, es porque el registro aún no existe: se crea uno.
            LocateRegistry.createRegistry(1099);
        }
    }
}
