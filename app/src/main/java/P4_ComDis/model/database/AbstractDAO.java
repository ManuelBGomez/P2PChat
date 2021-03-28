package P4_ComDis.model.database;

import java.sql.Connection;

public class AbstractDAO {
    private Connection connection;

    public AbstractDAO(Connection connection){
        this.setConnection(connection);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
