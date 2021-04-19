package P4_ComDis.model.exceptions;

import P4_ComDis.model.dataClasses.ResultType;

/**
 * Clase que representa las excepciones lanzadas desde los métodos relacionados con la base de datos.
 * 
 * @author Manuel Bendaña
 */
public class DatabaseException extends Exception {

    private static final long serialVersionUID = 8938347272936366485L;
    //Un atributo, el tipo de resultado:
    private ResultType rType;

    /**
     * Constructor de la clase - creación de la excepción.
     * @param rType Tipo de resultado que lleva a lanzar la excepción.
     * @param message Mensaje asociado a la excepción.
     */
    public DatabaseException(ResultType rType, String message){
        //Llamamos al constructor de la clase padre
        super(message);
        //Asignamos el tipo de resultado:
        this.rType = rType;
    }

    /**
     * Método que permite recuperar el tipo de resultado asociado a la excepción.
     * @return El tipo de resultado almacenado.
     */
    public ResultType getResultType(){
        return this.rType;
    }
}
