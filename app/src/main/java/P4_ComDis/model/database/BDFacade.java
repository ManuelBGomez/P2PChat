package P4_ComDis.model.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import P4_ComDis.model.dataClasses.User;
import P4_ComDis.model.exceptions.DatabaseException;

/**
 * Clase fachada de la Base de Datos. Incluye la referencia a todos los métodos de los DAO.
 * 
 * @author Manuel Bendaña
 */
public class BDFacade {

    //Conexión:
    private Connection connection;

    //Atributos de los DAOs:
    private DAOFriendship daoFriendship;
    private DAOUser daoUser;

    /**
     * Constructor de la clase.
     * Establecimiento de la conexión.
     */
    public BDFacade() {
        //Creamos atributo de tipo properties para guardar la información para la configuración:
        Properties configuration = new Properties();
        FileInputStream prop;

        try {
            //Cargamos los datos incluidos en el archivo .properties:
            prop = new FileInputStream("baseDatos.properties");
            configuration.load(prop);
            prop.close();
        } catch (Exception ex) {
            //Mostramos un error:
            System.out.println("No se ha podido cargar el archivo properties");
            ex.printStackTrace();
            System.exit(1);
        }

        //A partir de la información leída asociamos las propiedades al usuario para el acceso a la base de datos:
        Properties user = new Properties();
        //Nombre del usuario de la DB:
        user.setProperty("user", configuration.getProperty("usuario"));
        //Contraseña del usuario:
        user.setProperty("password", configuration.getProperty("password"));
        //Creamos el string para poder solicitar la conexión con la base de datos:
        String con = String.format("jdbc:%s://%s:%s/%s", configuration.getProperty("gestor"), configuration.getProperty("servidor"),
                                    configuration.getProperty("puerto"), configuration.getProperty("baseDatos"));
        
        try {
            //Intentamos establecer la conexión con la base de datos:
            this.connection = DriverManager.getConnection(con, user);
            //Ponemos el autoCommit a false (commitearemos automáticamente):
            this.connection.setAutoCommit(false);
        } catch(SQLException ex){
            //Mostramos un error:
            System.out.println("No se ha podido recuperar la conexión");
            ex.printStackTrace();
            System.exit(1);
        }

        //Creamos os DAOs:
        this.daoUser = new DAOUser(this.connection);
        this.daoFriendship = new DAOFriendship(this.connection);
    }

    
    /** 
     * Método que le permite a un usuario iniciar sesión.
     * 
     * @param user datos del usuario
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void login(User user) throws DatabaseException{
        this.daoUser.login(user);
    }

    
    /** 
     * Método que le permite a un usuario cerrar sesión.
     * @param user datos del usuario.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void logout(User user) throws DatabaseException{
        this.daoUser.logout(user);
    }

    
    /**
     * Método que permite formalizar el registro de un usuario.
     * @param user los datos del usuario que se quiere registrar.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void register(User user) throws DatabaseException{
        //Llamamos al método de registro:
        this.daoUser.register(user);
    }

    
    /** 
     * Método que permite recuperar nombres de los amigos de un usuario dado.
     * @param userName El nombre del usuario para el cual se quieren recuperar los amigos.
     * @return List<String> la lista de amistades
     */
    public List<String> getFriendNames(String userName) {
        return this.daoFriendship.getFriendNames(userName);
    }

    
    /** 
     * Método que permite cambiar la contraseña al usuario especificado.
     * @param user Los datos del usuario.
     * @param newPass La nueva contraseña a modificar.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void changePassword(User user, String newPass) throws DatabaseException{
        daoUser.changePassword(user, newPass);
    }

    
    /** 
     * Método que le permite al usuario derregistrarse (eliminar su cuenta).
     * @param user Los datos del usuario.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void unregister(User user) throws DatabaseException {
        daoUser.unregister(user);
    }

    
    /** 
     * Método que permite a un usuario recuperar nombres de usuarios (no amigos del solicitante) en base a un patrón.
     * @param user Los datos del usuario.
     * @param pattern Patrón a partir del cual se quieren recuperar los nombres
     * @return List<String> Lista de usuarios no amigos del solicitante cuyos nombres coinciden con el patrón
     */
    public List<String> getUserNamesByPattern(User user, String pattern){
        return this.daoUser.getUserNamesByPattern(user, pattern);
    }

    
    /** 
     * Método que permite a un usuario mandar una solicitud a otro.
     * @param user Los datos del usuario
     * @param friendName El nombre del amigo al que se le quiere enviar la solicitud.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void sendRequest (User user, String friendName) throws DatabaseException {
        daoFriendship.sendRequest(user, friendName);
    }

    
    /** 
     * Método que permite recuperar todas las solicitudes de amistad del usuario con el nombre indicado.
     * @param userName El nombre del usuario.
     * @return List<String> Lista de nombres de usuarios que le han enviado solicitudes.
     */
    public List<String> getFriendRequests(String userName) {
        return daoFriendship.getFriendRequests(userName);
    }

    
    /** 
     * Método que permite recuperar las amistades que ha enviado por el usuario.
     * @param userName El nombre del usuario.
     * @return List<String> Lista de nombres de usuarios a los que el especificado le ha enviado solicitudes.
     */
    public List<String> getFriendSentRequests(String userName){
        return daoFriendship.getFriendSentRequests(userName);
    }

    
    /** 
     * Método que permite formalizar la aceptación de una solicitud de amistad.
     * @param user los datos del usuario.
     * @param friendName El nombre del amigo al cual se le quiere aceptar la solicitud.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void acceptRequest(User user, String friendName) throws DatabaseException {
        daoFriendship.acceptRequest(user, friendName);
    }

    
    /** 
     * Método que permite formalizar el borrado de una amistad entre dos usuarios.
     * @param user Los datos del usuario que quiere borrar la amistad.
     * @param friendName El nombre del amigo con el que se quiere borrar la amistad.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void deleteFriendship(User user, String friendName) throws DatabaseException {
        daoFriendship.deleteFriendship(user, friendName);
    }
}
