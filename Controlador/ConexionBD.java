package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:h2:./data/filey";
    private static final String USUARIO = "sa";
    private static final String CONTRASENA = "";

    public static Connection conectar() throws SQLException {
        try {
           
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró la clase del driver de H2: " + e.getMessage());
        }

        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}