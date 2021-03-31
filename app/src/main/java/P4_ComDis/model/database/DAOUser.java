package P4_ComDis.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;
import P4_ComDis.model.exceptions.DatabaseException;

public final class DAOUser extends AbstractDAO {

    public DAOUser(Connection connection) {
        super(connection);
    }

    public void login(User user) throws DatabaseException  {
        //Usaremos varios preparedstatement para hacer la consulta y efectuar el login:
        PreparedStatement stmUsers = null;
        PreparedStatement stmUpdateUsers = null;
        PreparedStatement stmConnectedUser = null;
        ResultSet rsUsers;
        //Recuperamos la conexión
        Connection con = super.getConnection();

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE lower(userName) = lower(?) AND password = sha2(?, 256)");
            //Completamos los parámetros con interrogantes:
            stmUsers.setString(1, user.getUsername());
            stmUsers.setString(2, user.getPassword());

            //Ejecutamos la consulta:
            rsUsers = stmUsers.executeQuery();;
            
            //Si hay resultado, estableceremos que el usuario pasa a estar conectado:
            if(rsUsers.next()){
                //Antes, comprobaremos si está conectado ya.
                //Lo primero es la consulta:
                stmConnectedUser = con.prepareStatement("SELECT * FROM user WHERE userName = ? and connected = false");
                stmConnectedUser.setString(1, user.getUsername());
                //La ejecutamos:
                rsUsers = stmConnectedUser.executeQuery();
                //Comprobamos si hay resultado. Si lo hay, podemos loguear al usuario. Si no, no (ya lo estará)
                if(rsUsers.next()){
                    //Preparamos una nueva consulta:
                    stmUpdateUsers = con.prepareStatement("UPDATE user SET connected = true WHERE userName = ?");
                    //Establecemos el nombre de usuario:
                    stmUpdateUsers.setString(1, user.getUsername());
                    //Ejecutamos la actualización:
                    stmUpdateUsers.executeUpdate();
                } else {
                    throw new DatabaseException(ResultType.ALREADY_CONNECTED, "Usuario ya conectado");
                }
            } else {
                throw new DatabaseException(ResultType.UNAUTHORIZED, "Credenciales erróneas");
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
            //Se envía una excepción propia:
            throw new DatabaseException(ResultType.DATABASE_ERROR, ex.getMessage());
        } finally {
            //Para terminar, se cierran los statements:
            try {
                stmUsers.close();
                if(stmUpdateUsers!=null) stmUpdateUsers.close();
                if(stmConnectedUser!=null) stmConnectedUser.close();
            } catch (SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }
    }
    
    public void logout(User user) throws DatabaseException{
        //Usaremos varios preparedstatement para hacer la consulta y efectuar el logout:
        PreparedStatement stmUsers = null;
        PreparedStatement stmLogoutUser = null;
        ResultSet rsUsers;
        //Recuperamos la conexión
        Connection con = super.getConnection();

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE lower(userName) = lower(?) AND password = sha2(?, 256)");
            //Completamos los parámetros con interrogantes:
            stmUsers.setString(1, user.getUsername());
            stmUsers.setString(2, user.getPassword());

            //Ejecutamos la consulta:
            rsUsers = stmUsers.executeQuery();;
            
            //Si hay resultado, estableceremos que el usuario pasa a estar desconectado:
            if(rsUsers.next()){
                //Preparamos una nueva consulta:
                stmLogoutUser = con.prepareStatement("UPDATE user SET connected = false WHERE userName = ?");
                //Establecemos el nombre de usuario:
                stmLogoutUser.setString(1, user.getUsername());
                //Ejecutamos la actualización:
                stmLogoutUser.executeUpdate();
            } else {
                throw new DatabaseException(ResultType.UNAUTHORIZED, "Usuario o contraseña inválidos");
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
            //Se imprime el stack trace:
            throw new DatabaseException(ResultType.DATABASE_ERROR, ex.getMessage());
        } finally {
            //Para terminar, se cierran los statements (si es posible):
            try {
                stmUsers.close();
                if(stmLogoutUser!=null) stmLogoutUser.close();
            } catch (SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }
    }
}
