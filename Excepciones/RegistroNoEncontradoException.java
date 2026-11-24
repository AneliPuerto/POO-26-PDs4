package Excepciones;

/** Se buscó, actualizó o eliminó un registro que no existe. */
public class RegistroNoEncontradoException extends FileyException {

    public RegistroNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
