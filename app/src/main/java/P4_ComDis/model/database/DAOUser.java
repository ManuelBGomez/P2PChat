package P4_ComDis.model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;
import P4_ComDis.model.exceptions.DatabaseException;

public final class DAOUser extends AbstractDAO {

    public DAOUser(Connection connection) {
        super(connection);
    }

    public void login(User user) throws DatabaseException{
        //Usaremos varios preparedstatement para hacer la consulta y efectuar el login:
        PreparedStatement stmUsers = null;
        PreparedStatement stmUpdateUsers = null;
        PreparedStatement stmConnectedUser = null;
        ResultSet rsUsers;
        //Recuperamos la conexión
        Connection con = super.getConnection();

        //Para saber si se ha podido hacer el login correctamente, mantenemos una variable resulttype (por defecto con un error de la DB):
        ResultType res = ResultType.DATABASE_ERROR;

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE lower(userName) = lower(?) AND password = sha2(?, 256)");
            //Completamos los parámetros con interrogantes:
            stmUsers.setString(1, user.getUsername());
            stmUsers.setString(2, user.getPassword());

            //Ejecutamos la consulta:
            rsUsers = stmUsers.executeQuery();
            
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
                    //Marcamos el result type como OK:
                    res = ResultType.OK;
                } else {
                    res = ResultType.ALREADY_CONNECTED;
                }
            } else {
                res = ResultType.UNAUTHORIZED;
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

        //Comprobamos el resulttype:
        switch(res){
            case ALREADY_CONNECTED:
                throw new DatabaseException(ResultType.ALREADY_CONNECTED, "Usuario ya conectado");
            case UNAUTHORIZED:
                throw new DatabaseException(ResultType.UNAUTHORIZED, "Credenciales de usuario incorrectas");
            default:
                //Para el resto de valores no  se hace nada especial.
                break;
        }
    }
    
    public void logout(User user) throws DatabaseException{
        //Usaremos varios preparedstatement para hacer la consulta y efectuar el logout:
        PreparedStatement stmUsers = null;
        PreparedStatement stmLogoutUser = null;
        ResultSet rsUsers;
        //Recuperamos la conexión
        Connection con = super.getConnection();
        //Utilizamos un booleano como referencia de si hay problemas:
        boolean valid = false;

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
                valid = true;
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
            //Enviamos excepción:
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
        if(!valid) {
            throw new DatabaseException(ResultType.UNAUTHORIZED, "Usuario o contraseña inválidos");
        }
    }

    public void register(User user) throws DatabaseException{
        //Usaremos varios preparedstatement para la consulta:
        PreparedStatement stmUsers = null;
        PreparedStatement stmRegister = null;

        ResultSet rsUser;
        boolean valid = false;

        //Recuperamos la conexión:
        Connection con = super.getConnection();

        //Intentaremos registrar al usuario, verificando antes que no haya otro usuario con el mismo username:
        try{
            //Preparamos la consulta de existencia del usuario:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE lower(userName) = lower(?)");
            //Completamos campos:
            stmUsers.setString(1, user.getUsername());

            //Ejecutamos la consulta:
            rsUser = stmUsers.executeQuery();

            //A continuación, comprobamos que no se han devuelto resultados:
            if(!rsUser.next()){
                //En ese caso, podemos seguir adelante.
                //Insertamos el usuario - lo ponemos conectado porque directamente iniciaremos sesión:
                stmRegister = con.prepareStatement("INSERT INTO user(userName, password, connected)" + 
                " VALUES (?, sha2(?, 256), true)");
                //Completamos campos
                stmRegister.setString(1, user.getUsername());
                stmRegister.setString(2, user.getPassword());
                
                //Hecho esto, ejecutamos la actualización:
                stmRegister.executeUpdate();
                //La variable que nos indica que el registro ha sido válido se pone a true para confirmarlo
                valid = true;
            }

            //Hacemos commit:
            con.commit();
        } catch (SQLException ex){
            //Tratamos de hacer el rollback:
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Se envía una excepción indicando el error:
            throw new DatabaseException(ResultType.DATABASE_ERROR, ex.getMessage());
        } finally {
            //Se cierran statements (si es posible):
            try {
                stmUsers.close();
                if(stmRegister != null) stmRegister.close();
            } catch(SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }

        //Si se pudo registrar, no ocurre nada. Si no, se envia una excepción:
        if(!valid){
            throw new DatabaseException(ResultType.UNAUTHORIZED, "Ya existe un usuario con ese nombre");
        }
    }

    public List<String> getUserNamesByPattern(User user, String pattern) {
        //Usaremos varios preparedstatement para hacer la consulta de validación del usuario y hacer logout:
        PreparedStatement stmUsers = null;
        PreparedStatement stmSearch = null;
        ResultSet rsUsers;
        ResultSet rsSearch;
        //Recuperamos la conexión
        Connection con = super.getConnection();
        //Creamos un arraylist vacío para contener los resultados. En principio, no tendrá nada si la consulta no
        //se llega a hacer o si no hay resultados, y tendrá algo siempre y cuando haya resultados para la búsqueda hecha.
        ArrayList<String> users = new ArrayList<>();

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE lower(userName) = lower(?) AND password = sha2(?, 256)");
            //Completamos los parámetros con interrogantes:
            stmUsers.setString(1, user.getUsername());
            stmUsers.setString(2, user.getPassword());

            //Ejecutamos la consulta:
            rsUsers = stmUsers.executeQuery();;
            
            //Si hay resultado, el usuario existe - procedemos a realizar la consulta:
            if(rsUsers.next()){
                //Preparamos una nueva consulta: usuarios que sigan el patrón pero que no sean ya amigos ni sean el propio usuario que hace la búsqueda
                stmSearch = con.prepareStatement("SELECT * FROM user WHERE lower(userName) LIKE ?  " + 
                                "AND userName not in " +
                                "(SELECT userReceiver as friend FROM friendship WHERE lower(userSender) = lower(?) " + 
                                "UNION " +
                                "SELECT userSender as friend FROM friendship WHERE lower(userReceiver) = lower(?)) " +
                                "AND lower(userName) != lower(?)");
                //Establecemos los parámetros (similitud en el patrón):
                stmSearch.setString(1, "%" + pattern + "%");
                stmSearch.setString(2, user.getUsername());
                stmSearch.setString(3, user.getUsername());
                stmSearch.setString(4, user.getUsername());
                //Ejecutamos la consulta:
                rsSearch = stmSearch.executeQuery();
                while(rsSearch.next()){
                    users.add(rsSearch.getString("userName"));
                }
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
            //En este caso no enviamos nada, simplemente no devolveremos resultado al final.
            ex.printStackTrace();
        } finally {
            //Para terminar, se cierran los statements (si es posible):
            try {
                stmUsers.close();
                if(stmSearch!=null) stmSearch.close();
            } catch (SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }

        //Devolvemos el resultado (se tendrá algo cuando la consulta tenga éxito, no habrá resultado si
        //el usuario no es válido o si no se encuentran usuarios con el término buscado)
        return users;
    }
}
