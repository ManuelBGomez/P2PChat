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

/**
 * DAO de amistades - métodos relacionados con la gestión de las amistades.
 * 
 * @author Manuel Bendaña
 */
public final class DAOFriendship extends AbstractDAO{

    /**
     * Constructor de la clase.
     * @param connection instancia del objeto de la conexión.
     */
    public DAOFriendship(Connection connection) {
        super(connection);
    }

    /** 
     * Método que permite recuperar nombres de los amigos de un usuario dado.
     * @param userName El nombre del usuario para el cual se quieren recuperar los amigos.
     * @return List<String> la lista de amistades
     */
    public List<String> getFriendNames(String userName) {
        //Usaremos varios preparedstatement para hacer la consulta:
        PreparedStatement stmFriendships = null;
        //ResultSet para el resultado:
        ResultSet rsFriendships;
        //Recuperamos la conexión:
        Connection con = super.getConnection();
        //El resultado se meterá en un array de strings:
        ArrayList<String> friends = new ArrayList<>();

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta (amistades en las que cualquiera de las partes sea el usuario y esté confirmada):
            stmFriendships = con.prepareStatement("SELECT userReceiver as friend FROM friendship WHERE userSender = ? and confirmedFriendship = true " +
                                                  "UNION SELECT userSender as friend FROM friendship WHERE userReceiver = ? and confirmedFriendship = true");
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

    /** 
     * Método que permite recuperar todas las solicitudes de amistad del usuario con el nombre indicado.
     * @param userName El nombre del usuario.
     * @return List<String> Lista de nombres de usuarios que le han enviado solicitudes.
     */
    public List<String> getFriendRequests(String userName){
        //Usaremos varios preparedstatement para hacer la consulta:
        PreparedStatement stmFriendships = null;
        //ResultSet para el resultado:
        ResultSet rsFriendships;
        //Recuperamos la conexión:
        Connection con = super.getConnection();
        //El resultado se meterá en un array de strings:
        ArrayList<String> friends = new ArrayList<>();

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta (amistades en las que el usuario es el receptor y sin confirmar):
            stmFriendships = con.prepareStatement("SELECT userSender as friend FROM friendship WHERE userReceiver = ? and confirmedFriendship = false");
            //Completamos los parámetros con interrogantes:
            stmFriendships.setString(1, userName);

            //Ejecutamos la consulta:
            rsFriendships = stmFriendships.executeQuery();;
            
            //Si hay resultados, los vamos procesando:
            while(rsFriendships.next()){
                //Añadimos el nombre de las personas que hacen cada solicitud (almacenado como friend):
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

    /** 
     * Método que permite recuperar las amistades que ha enviado por el usuario.
     * @param userName El nombre del usuario.
     * @return List<String> Lista de nombres de usuarios a los que el especificado le ha enviado solicitudes.
     */
    public List<String> getFriendSentRequests(String userName){
        //Usaremos varios preparedstatement para hacer la consulta:
        PreparedStatement stmFriendships = null;
        //ResultSet para el resultado:
        ResultSet rsFriendships;
        //Recuperamos la conexión:
        Connection con = super.getConnection();
        //El resultado se meterá en un array de strings:
        ArrayList<String> friends = new ArrayList<>();

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta (amistades en las que el usuario es el emisor y sin confirmar):
            stmFriendships = con.prepareStatement("SELECT userReceiver as friend FROM friendship WHERE userSender = ? and confirmedFriendship = false");
            //Completamos los parámetros con interrogantes:
            stmFriendships.setString(1, userName);

            //Ejecutamos la consulta:
            rsFriendships = stmFriendships.executeQuery();;
            
            //Si hay resultados, los vamos procesando:
            while(rsFriendships.next()){
                //Añadimos el nombre de las personas que reciben cada solicitud (almacenado como friend):
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

    /** 
     * Método que permite a un usuario mandar una solicitud a otro.
     * @param user Los datos del usuario
     * @param friendName El nombre del amigo al que se le quiere enviar la solicitud.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void sendRequest(User user, String friendName) throws DatabaseException{
        //Usaremos varios preparedstatement para hacer la consulta de validación del usuario,
        //comprobar si la amistad existe y registrar la petición:
        PreparedStatement stmUsers = null;
        PreparedStatement stmSearchFriendship = null;
        PreparedStatement stmAddFriendship = null;
        ResultSet rsUsers;
        ResultSet rsFriendships;
        //Recuperamos la conexión
        Connection con = super.getConnection();

        //Usaremos una variable resultType para saber el resultado del cálculo (por defecto, OK):
        ResultType resultType = ResultType.OK;

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE userName = ? AND password = sha2(?, 256)");
            //Completamos los parámetros con interrogantes:
            stmUsers.setString(1, user.getUsername());
            stmUsers.setString(2, user.getPassword());

            //Ejecutamos la consulta:
            rsUsers = stmUsers.executeQuery();;
            
            //Si hay resultado, el usuario existe - procedemos a realizar la búsqueda de la amistad:
            if(rsUsers.next()){
                //Buscaremos amistades en cualquiera de los dos posibles sentidos:
                stmSearchFriendship = con.prepareStatement("SELECT * FROM friendship WHERE (userSender = ? AND userReceiver = ?) " +
                                                                                    "OR (userSender = ? AND userReceiver = ?)");
                                                                                    
                //Se completa la consulta:
                stmSearchFriendship.setString(1, user.getUsername());
                stmSearchFriendship.setString(2, friendName);
                stmSearchFriendship.setString(3, friendName);
                stmSearchFriendship.setString(4, user.getUsername());

                //Se hace la consulta. Para poder enviar la amistad no puede existir resultado:
                rsFriendships = stmSearchFriendship.executeQuery();

                //Hecha la consulta, se comprueba:
                if(!rsFriendships.next()){
                    //No hay un resultado: se añade la amistad.
                    stmAddFriendship = con.prepareStatement("INSERT INTO friendship (userSender, userReceiver) VALUES (?, ?)");
                    
                    //Completamos los campos:
                    stmAddFriendship.setString(1, user.getUsername());
                    stmAddFriendship.setString(2, friendName);

                    //Ejecutamos la actualización:
                    stmAddFriendship.executeUpdate();
                } else {
                    //Hay resultado: se impide hacer nada más.
                    resultType = ResultType.ALREADY_FRIENDS;
                }
            } else {
                //No autorizado para pedir una amistad:
                resultType = ResultType.UNAUTHORIZED;
            }

            //Hecho todo esto, haremos el commit:
            con.commit();

        } catch (SQLException ex){
            ex.printStackTrace();
            //Tratamos de hacer el rollback:
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            throw new DatabaseException(ResultType.DATABASE_ERROR, ex.getMessage());
        } finally {
            //Para terminar, se cierran los statements (si es posible):
            try {
                stmUsers.close();
                if(stmSearchFriendship!=null) stmSearchFriendship.close();
                if(stmAddFriendship!=null) stmAddFriendship.close();
            } catch (SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }

        //Si no se terminó correctamente (resultType != OK) se lanza excepción:
        if(!resultType.equals(ResultType.OK)) {
            throw new DatabaseException(resultType, "Error intentando añadir la amistad");
        }
    }

    /** 
     * Método que permite formalizar la aceptación de una solicitud de amistad.
     * @param user los datos del usuario.
     * @param friendName El nombre del amigo al cual se le quiere aceptar la solicitud.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void acceptRequest(User user, String friendName) throws DatabaseException{
        //Usaremos varios preparedstatement para hacer la consulta de validación del usuario,
        //comprobar si la amistad existe y registrar la aceptación:
        PreparedStatement stmUsers = null;
        PreparedStatement stmSearchFriendship = null;
        PreparedStatement stmConfirmFriendship = null;
        ResultSet rsUsers;
        ResultSet rsFriendships;
        //Recuperamos la conexión
        Connection con = super.getConnection();

        //Usaremos una variable resultType para saber el resultado del cálculo (por defecto, OK):
        ResultType resultType = ResultType.OK;

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE userName = ? AND password = sha2(?, 256)");
            //Completamos los parámetros con interrogantes:
            stmUsers.setString(1, user.getUsername());
            stmUsers.setString(2, user.getPassword());

            //Ejecutamos la consulta:
            rsUsers = stmUsers.executeQuery();;
            
            //Si hay resultado, el usuario existe - procedemos a realizar la búsqueda de la amistad:
            if(rsUsers.next()){
                //Buscaremos amistades SÓLO EN UN SENTIDO, sin estar confirmada:
                stmSearchFriendship = con.prepareStatement("SELECT * FROM friendship WHERE userSender = ? AND userReceiver = ? AND confirmedFriendship = false");
                                                                                    
                //Se completa la consulta (el receptor debe ser el usuario que quiere aceptar la amistad):
                stmSearchFriendship.setString(1, friendName);
                stmSearchFriendship.setString(2, user.getUsername());

                //Se hace la consulta. Para poder enviar la amistad debe existir un resultado:
                rsFriendships = stmSearchFriendship.executeQuery();

                //Hecha la consulta, se comprueba:
                if(rsFriendships.next()){
                    //Hay un resultado: se acepta la amistad.
                    stmConfirmFriendship = con.prepareStatement("UPDATE friendship SET confirmedFriendship = true WHERE userSender = ? AND userReceiver = ?");
                    
                    //Completamos los campos (el receptor debe ser el usuario que quiere aceptar la amistad):
                    stmConfirmFriendship.setString(1, friendName);
                    stmConfirmFriendship.setString(2, user.getUsername());

                    //Ejecutamos la actualización:
                    stmConfirmFriendship.executeUpdate();
                } else {
                    //No hay resultado: se impide hacer nada más.
                    resultType = ResultType.NOT_VALID;
                }
            } else {
                //No autorizado para pedir una amistad:
                resultType = ResultType.UNAUTHORIZED;
            }

            //Hecho todo esto, haremos el commit:
            con.commit();

        } catch (SQLException ex){
            ex.printStackTrace();
            //Tratamos de hacer el rollback:
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            throw new DatabaseException(ResultType.DATABASE_ERROR, ex.getMessage());
        } finally {
            //Para terminar, se cierran los statements (si es posible):
            try {
                stmUsers.close();
                if(stmSearchFriendship!=null) stmSearchFriendship.close();
                if(stmConfirmFriendship!=null) stmConfirmFriendship.close();
            } catch (SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }

        //Si no se terminó correctamente (resultType != OK) se lanza excepción:
        if(!resultType.equals(ResultType.OK)) {
            throw new DatabaseException(resultType, "Error intentando añadir la amistad");
        }
    }

    /** 
     * Método que permite formalizar el borrado de una amistad entre dos usuarios.
     * @param user Los datos del usuario que quiere borrar la amistad.
     * @param friendName El nombre del amigo con el que se quiere borrar la amistad.
     * @throws DatabaseException Excepción ocurrida por algún problema en la base de datos.
     */
    public void deleteFriendship(User user, String friendName) throws DatabaseException {
        //Usaremos varios preparedstatement para hacer la consulta de validación del usuario,
        //comprobar si la amistad existe y borrarla:
        PreparedStatement stmUsers = null;
        PreparedStatement stmSearchFriendship = null;
        PreparedStatement stmDeleteFriendship = null;
        ResultSet rsUsers;
        ResultSet rsFriendships;
        //Recuperamos la conexión
        Connection con = super.getConnection();

        //Usaremos una variable resultType para saber el resultado del cálculo (por defecto, OK):
        ResultType resultType = ResultType.OK;

        //A partir de aquí, intentamos hacer la consulta:
        try {
            //Preparamos consulta:
            stmUsers = con.prepareStatement("SELECT * FROM user WHERE userName = ? AND password = sha2(?, 256)");
            //Completamos los parámetros con interrogantes:
            stmUsers.setString(1, user.getUsername());
            stmUsers.setString(2, user.getPassword());

            //Ejecutamos la consulta:
            rsUsers = stmUsers.executeQuery();;
            
            //Si hay resultado, el usuario existe - procedemos a realizar la búsqueda de la amistad:
            if(rsUsers.next()){
                //Buscaremos amistades en cualquiera de los sentidos:
                stmSearchFriendship = con.prepareStatement("SELECT * FROM friendship WHERE (userSender = ? AND userReceiver = ?) " +
                                                                                       "OR (userSender = ? AND userReceiver = ?)");
                
                //Se completa la consulta:
                stmSearchFriendship.setString(1, user.getUsername());
                stmSearchFriendship.setString(2, friendName);
                stmSearchFriendship.setString(3, friendName);
                stmSearchFriendship.setString(4, user.getUsername());

                //Se hace la consulta. Para poder enviar la amistad debe existir un resultado:
                rsFriendships = stmSearchFriendship.executeQuery();

                //Hecha la consulta, se comprueba:
                if(rsFriendships.next()){
                    //hay un resultado: se elimina la amistad:
                    stmDeleteFriendship = con.prepareStatement("DELETE FROM friendship WHERE (userSender = ? AND userReceiver = ?) " +
                                                                                         "OR (userSender = ? AND userReceiver = ?)");

                    //Completamos los campos (el receptor debe ser el usuario que quiere aceptar la amistad):
                    stmDeleteFriendship.setString(1, user.getUsername());
                    stmDeleteFriendship.setString(2, friendName);
                    stmDeleteFriendship.setString(3, friendName);
                    stmDeleteFriendship.setString(4, user.getUsername());

                    //Ejecutamos la actualización:
                    stmDeleteFriendship.executeUpdate();
                } else {
                    //No hay amistad: se impide hacer nada más.
                    resultType = ResultType.NOT_VALID;
                }
            } else {
                //No autorizado para pedir una amistad:
                resultType = ResultType.UNAUTHORIZED;
            }

            //Hecho todo esto, haremos el commit:
            con.commit();

        } catch (SQLException ex){
            ex.printStackTrace();
            //Tratamos de hacer el rollback:
            try {
                con.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            throw new DatabaseException(ResultType.DATABASE_ERROR, ex.getMessage());
        } finally {
            //Para terminar, se cierran los statements (si es posible):
            try {
                stmUsers.close();
                if(stmSearchFriendship!=null) stmSearchFriendship.close();
                if(stmDeleteFriendship!=null) stmDeleteFriendship.close();
            } catch (SQLException e){
                System.out.println("Imposible cerrar los cursores");
            }
        }

        //Si no se terminó correctamente (resultType != OK) se lanza excepción:
        if(!resultType.equals(ResultType.OK)) {
            throw new DatabaseException(resultType, "Error intentando borrar la amistad");
        }
    }
}
