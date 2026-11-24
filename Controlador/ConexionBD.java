package Controlador;

import Excepciones.ConexionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Su única responsabilidad es abrir una conexión con H2.
 * Quien la pide (el controlador) se la pasa a las clases de Operaciones y la cierra.
 */
public class ConexionBD {

    private static final String URL = "jdbc:h2:./data/filey";
    private static final String USUARIO = "sa";
    private static final String CONTRASENA = "";

    public static Connection conectar() throws ConexionException {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (ClassNotFoundException e) {
            throw new ConexionException("No se encontró el driver de H2 (lib/h2-*.jar): " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new ConexionException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }
}
