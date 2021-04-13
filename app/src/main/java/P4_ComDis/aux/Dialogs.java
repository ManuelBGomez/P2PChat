package P4_ComDis.aux;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Clase Dialogs: clase formada por métodos estáticos que permiten abrir cuadros de diálogo.
 *
 * @author Manuel Bendaña
 */
public class Dialogs {
    /**
     * Método que permite mostrar un error por pantalla.
     * @param windowTitle El título a darle a la ventana que se abrirá.
     * @param dialogTitle El título a darle al cuadro de diálogo que se abrirá.
     * @param text El contenido del diálogo.
     */
    public static void showError(String windowTitle, String dialogTitle, String text){
        //Se crea la alerta de tipo error:
        Alert alert = new Alert(Alert.AlertType.ERROR);
        //Se establecen los parámetros:
        alert.setTitle(windowTitle);
        alert.setHeaderText(dialogTitle);
        alert.setContentText(text);
        //Se muestra y se espera a que el usuario la cierre.
        alert.showAndWait();
    }

    /**
     * Método que permite mostrar un mensaje informativo por pantalla.
     * @param windowTitle El título a darle a la ventana que se abrirá.
     * @param dialogTitle El título a darle al cuadro de diálogo que se abrirá.
     * @param text El contenido del diálogo.
     */
    public static void showInfo(String windowTitle, String dialogTitle, String text){
        //Se crea la alerta de tipo error:
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //Se establecen los parámetros:
        alert.setTitle(windowTitle);
        alert.setHeaderText(dialogTitle);
        alert.setContentText(text);
        //Se muestra y se espera a que el usuario la cierre.
        alert.showAndWait();
    }

    /**
     * Método que permite solicitar confirmación por pantalla.
     * @param windowTitle El título a darle a la ventana que se abrirá.
     * @param dialogTitle El título a darle al cuadro de diálogo que se abrirá.
     * @param text El contenido del diálogo.
     * @return True si la confirmación es OK, falso en caso contrario.
     */
    public static boolean showConfirmation(String windowTitle, String dialogTitle, String text){
        //Se crea la alerta de tipo error:
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        //Se establecen los parámetros:
        alert.setTitle(windowTitle);
        alert.setHeaderText(dialogTitle);
        alert.setContentText(text);
        //Se muestra y se devuelve el resultado.
        Optional<ButtonType> res = alert.showAndWait();
        if(res.get().equals(ButtonType.OK)){
            return true;
        } else {
            return false;
        }
    }
}
