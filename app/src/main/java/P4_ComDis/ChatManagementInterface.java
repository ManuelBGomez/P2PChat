package P4_ComDis;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import P4_ComDis.model.dataClasses.ResultType;
import P4_ComDis.model.dataClasses.User;

/**
 * Interfaz del servidor.
 * @author Manuel Bendaña
 */
public interface ChatManagementInterface extends Remote {

    /** 
     * Método que permite loguearse en el servidor de chat. Sólo será posible el logueo si el usuario está registrado
     * y, además, no se encuentra logueado en este momento.
     * 
     * @param user Datos del usuario que quiere iniciar sesión.
     * @param clientInfo Interfaz del cliente que se puede compartir con otros clientes (para la comunicación p2p)
     * @param clientInternal Interfaz del cliente interna: para ser usada unicamente desde el propio servidor.
     * @return ResultType - el resultado de la operación: si todo va bien será OK, si no, será un tipo representativo del error.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType loginToChat(User user, ClientManagementInterface clientInfo, ClientInternalMgInterface clientInternal) 
        throws RemoteException;

    /** 
     * Método que permite a un usuario salir del sistema.
     * @param user Los datos del usuario que se quiere desloguear.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public void logoutFromChat(User user) throws RemoteException;

    /** 
     * Método que permite a un usuario registrarse en el servidor de chat con una cuenta nueva. Sólo se permitirá
     * registro de usuarios con un nombre único. Si la ejecución es correcta, el usuario quedará logueado en la aplicación.
     * 
     * @param user Los datos del nuevo usuario.
     * @param clientInfo La interfaz de ese cliente, que se permitirá compartir con otros.
     * @param clientInternal La interfaz del cliente interna, que usará solo el servidor.
     * @return ResultType el resultado de la operación en forma de enum.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType registerInChat(User user, ClientManagementInterface clientInfo, ClientInternalMgInterface clientInternal) 
        throws RemoteException;

    /** 
     * Método que permite al usuario especificado hacer búsqueda de usuarios que podrían ser potenciales amigos en base
     * a un patrón específico.
     * 
     * @param user El usuario que desea hacer la búsqueda. Si sus datos no son válidos, no se devolverá resultado.
     * @param pattern El patrón de búsqueda (nombre completo o parte)
     * @return List<String> resultado de la búsqueda, en caso de que pudiera ser hecha.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public List<String> searchFriends(User user, String pattern) throws RemoteException;

    /** 
     * Método que permite reflejar el envío de una solicitud de amistad a un usuario.
     * 
     * @param user El usuario que desea hacer la solicitud.
     * @param friendName La persona con la que quiere entablar la amistad.
     * @return ResultType resultado de la operación.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType sendFriendRequest(User user, String friendName) throws RemoteException;

    /** 
     * Método que permite reflejar la aceptación de la solicitud de amistad por parte de un usuario.
     * 
     * @param user El usuario que desea aceptar la solicitud.
     * @param friendName El nombre del amigo del que recibió la solicitud.
     * @return ResultType el resultado de la operación.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType acceptRequest(User user, String friendName) throws RemoteException;

    /** 
     * Método que permite rechazar la solicitud de amistad por parte de un usuario.
     * 
     * @param user El usuario que desea rechazar la solicitud.
     * @param friendName El nombre del amigo del que recibió la solicitud.
     * @return ResultType el resultado de la operación.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType rejectFriendship(User user, String friendName) throws RemoteException;

    /** 
     * Método que permite formalizar la cancelación de una solicitud de amistad enviada por un usuario
     * determinado.
     * 
     * @param user El usuario que desea cancelar la solicitud.
     * @param friendName El nombre del amigo al que se envió la solicitud.
     * @return ResultType el resultado de la operación.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType cancelRequest(User user, String friendName) throws RemoteException;

    /** 
     * Método que permite borrar una amistad entre dos usuarios, independientemente de su situación.
     * 
     * @param user El usuario que desea borrar la amistad.
     * @param friendName El nombre del amigo.
     * @return ResultType el resultado de la operación
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType deleteFriendship(User user, String friendName) throws RemoteException;

    /** 
     * Método que permite formalizar el cambio de contraseña por parte de un usuario.
     * 
     * @param user Los datos del usuario ACTUALES.
     * @param newPass La nueva contraseña
     * @return ResultType el resultado de la operación.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType changePassword(User user, String newPass) throws RemoteException;

    /** 
     * Método que permite borrar la cuenta de un usuario.
     * 
     * @param user Los datos del usuario que se quiere borrar.
     * @return ResultType resultado de la operación.
     * @throws RemoteException Excepción remota que puede ocurrir durante la ejecución del método.
     */
    public ResultType unregister(User user) throws RemoteException;

}
