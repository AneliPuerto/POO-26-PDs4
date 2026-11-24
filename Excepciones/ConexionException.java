package Excepciones;

/** No se pudo abrir (o cerrar) la conexión con la base de datos H2. */
public class ConexionException extends FileyException {

    public ConexionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
