package Excepciones;

/** Datos inválidos o una regla del negocio que no se cumple (ej. stand bloqueado). */
public class ReglaNegocioException extends FileyException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

    public ReglaNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
