package P4_ComDis.model.database;

import java.sql.Connection;

/**
 * Clase padre de todas las clases que incluyan métodos de acceso a los datos de la base de datos.
 * 
 * @author Manuel Bendaña
 */
public class AbstractDAO {
    //Conexión con la base de datos.
    private Connection connection;

    /**
     * Constructor de la case
     * @param connection conexión a asociar a la base de datos.
     */
    public AbstractDAO(Connection connection){
        this.setConnection(connection);
    }

    
    /**
     * Getter de la conexión 
     * @return Connection la información de conexión guardada.
     */
    public Connection getConnection() {
        return connection;
    }

    
    /** 
     * Setter de la conexión
     * @param connection la información de conexión que se desea vincular.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
