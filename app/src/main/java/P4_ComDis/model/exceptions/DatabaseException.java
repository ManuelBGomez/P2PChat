package P4_ComDis.model.exceptions;

import P4_ComDis.model.dataClasses.ResultType;

public class DatabaseException extends Exception {

    private static final long serialVersionUID = 8938347272936366485L;
    //Un atributo, el tipo de resultado:
    private ResultType rType;

    public DatabaseException(ResultType rType, String message){
        //Llamamos al constructor de la clase padre
        super(message);
        //Asignamos el tipo de resultado:
        this.rType = rType;
    }

    public ResultType getResultType(){
        return this.rType;
    }
}
