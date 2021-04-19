package P4_ComDis;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import P4_ComDis.objectimpl.ChatManagementImpl;

/**
 * Clase main del servidor
 * @author Manuel Bendaña
 */
public class Server {
    
    /** 
     * Método main
     * @param args Argumentos de entrada
     */
    public static void main(String[] args) {
        try {
            //Tratamos de iniciar un registro en el puerto que corresponde (usaremos siempre el que hay por defecto):
            startRegistry();
            ChatManagementImpl cm = new ChatManagementImpl();
            //Definimos URL para el registro:
            String registryURL = "rmi://localhost:1099/chatManager";
            Naming.rebind(registryURL, cm);
            System.out.println("Escuchando en el puerto 1099");
        } catch (RemoteException | MalformedURLException ex) {
            System.out.println("Error iniciando servidor del chat: " + ex.getMessage());
            System.exit(0);
        }
    }

    
    /**
     * Método que permite iniciar el registro RMI o recuperarlo: 
     * @throws RemoteException
     */
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
