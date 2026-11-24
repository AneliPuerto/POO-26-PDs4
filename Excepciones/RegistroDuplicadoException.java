package Excepciones;

/** Se intentó insertar un registro cuyo ID (llave primaria) ya existe. */
public class RegistroDuplicadoException extends FileyException {

    public RegistroDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
