package P4_ComDis.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DAOFriendship extends AbstractDAO{

    public DAOFriendship(Connection connection) {
        super(connection);
    }

    public List<String> getFriendNames(String userName) {
        //Usaremos varios preparedstatement para hacer la consulta:
        PreparedStatement stmFriendships = null;
        //ResultSet para el resultado:
        ResultSet rsFriendships;
        //Recuperamos la conexión:
        Connection con = super.getConnection();
        //El resultado se meterá en un array de strins:
        ArrayList<String> friends = new ArrayList<>();

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta (amistades en las que cualquiera de las partes sea el usuario y esté confirmada):
            stmFriendships = con.prepareStatement("SELECT userReceiver as friend FROM friendship WHERE userSender = 'manu' and confirmedFriendship = true " +
                                                  "UNION SELECT userSender as friend FROM friendship WHERE userReceiver = 'manu' and confirmedFriendship = true");
            //Completamos los parámetros con interrogantes:
            stmFriendships.setString(1, userName);
            stmFriendships.setString(2, userName);

            //Ejecutamos la consulta:
            rsFriendships = stmFriendships.executeQuery();;
            
            //Si hay resultados, los vamos procesando:
            while(rsFriendships.next()){
                //Añadimos el nombre de los amigos (almacenado como friend):
                friends.add(rsFriendships.getString("friend"));
            }

            //Hecho todo esto, haremos el commit:
            con.commit();

        } catch (SQLException ex){
            //Tratamos de hacer el rollback:
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Se imprime el stack trace (salvo un error de sintaxis, no es necesario contemplar esta excepción):
            ex.printStackTrace();
        } finally {
            //Para terminar, se cierran los statements (si es posible):
            try {
                stmFriendships.close();
            } catch (SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }

        //Devolvemos el resultado:
        return friends;
    }
}
