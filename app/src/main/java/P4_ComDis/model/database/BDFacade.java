package P4_ComDis.model.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import P4_ComDis.model.dataClasses.User;
import P4_ComDis.model.exceptions.DatabaseException;

public class BDFacade {

    //Conexión:
    private Connection connection;

    //Atributos de los DAOs:
    private DAOFriendship daoFriendship;
    private DAOUser daoUser;

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

    public void login(User user) throws DatabaseException{
        this.daoUser.login(user);
    }

    public void logout(User user) throws DatabaseException{
        this.daoUser.logout(user);
    }

    public void register(User user) throws DatabaseException{
        //Llamamos al método de registro:
        this.daoUser.register(user);
    }

    public List<String> getFriendNames(String userName) {
        return this.daoFriendship.getFriendNames(userName);
    }

    public List<String> getUserNamesByPattern(User user, String pattern){
        return this.daoUser.getUserNamesByPattern(user, pattern);
    }

    public void sendRequest (User user, String friendName) throws DatabaseException {
        daoFriendship.sendRequest(user, friendName);
    }

    public List<String> getFriendRequests(String userName) {
        return daoFriendship.getFriendRequests(userName);
    }

    public List<String> getFriendSentRequests(String userName){
        return daoFriendship.getFriendSentRequests(userName);
    }
}
