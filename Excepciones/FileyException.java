package Excepciones;

/**
 * Excepción base del sistema FILEY.
 * Es "checked": obliga a quien llama (controlador/vista) a decidir qué hacer con el error.
 */
public class FileyException extends Exception {

    public FileyException(String mensaje) {
        super(mensaje);
    }

    public FileyException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
