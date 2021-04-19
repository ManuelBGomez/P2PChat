package P4_ComDis.model.dataClasses;

import java.io.Serializable;

/**
 * Clase que representa la información del usuario que se almacena en la base de datos.
 * 
 * @author Manuel Bendaña
 */
public class User implements Serializable {
    //Atributos de la clase: nombre de usuario, contraseña y booleano que indica si está conectado.
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private boolean connected;
    
    /**
     * Constructor con el booleano.
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param connected Si el usuario está conectado o no
     */
    public User(String username, String password, boolean connected) {
        this.username = username;
        this.password = password;
        this.connected = connected;
    }
    
    /**
     * Constructor sin el booleano.
     * @param username Nombre de usuario
     * @param password Contraseña
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    
    /** 
     * Método que permite recuperar el nombre de usuario.
     * @return String El nombre de usuario almacenado
     */
    public String getUsername() {
        return username;
    }
    
    
    /** 
     * Método que permite recuperar la información de conectividad
     * @return boolean Información de conectividad almacenada
     */
    public boolean isConnected() {
        return connected;
    }
    
    
    /** 
     * Método que permite establecer la conectividad.
     * @param connected El valor a establecer.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    
    /** 
     * Método que permite recuperar la contraseña del usuario.
     * @return String La contraseña almacenada.
     */
    public String getPassword() {
        return password;
    }
    
    
    /** 
     * Método que permite establecer la contraseña del usuario.
     * @param password La contraseña a establecer.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    
    /** 
     * Método que permite establecer el nombre del usuario
     * @param username El nombre del usuario a establecer.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
