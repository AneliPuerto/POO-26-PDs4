package Controlador;

import Excepciones.ErroresSQL;
import Excepciones.FileyException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Crea las tres tablas y, si la base está vacía, inserta los datos de ejemplo. */
public class InicializadorBD {

    public static void crearTablas(Connection conexion) throws FileyException {
        String sqlStand =
                "CREATE TABLE IF NOT EXISTS stand (" +
                "id VARCHAR(20) PRIMARY KEY, " +
                "m2 DOUBLE, " +
                "precio DOUBLE, " +
                "estado VARCHAR(30))";

        String sqlEditorial =
                "CREATE TABLE IF NOT EXISTS editorial (" +
                "id VARCHAR(20) PRIMARY KEY, " +
                "nombre VARCHAR(100), " +
                "correo VARCHAR(100))";

        String sqlReservacion =
                "CREATE TABLE IF NOT EXISTS reservaciones (" +
                "id VARCHAR(20) PRIMARY KEY, " +
                "stand_id VARCHAR(20), " +
                "editorial_id VARCHAR(20), " +
                "fecha_reserva DATE, " +
                "fecha_vigencia DATE, " +
                "activa BOOLEAN, " +
                "FOREIGN KEY (stand_id) REFERENCES stand(id), " +
                "FOREIGN KEY (editorial_id) REFERENCES editorial(id))";

        try (Statement statement = conexion.createStatement()) {
            statement.execute(sqlStand);
            statement.execute(sqlEditorial);
            statement.execute(sqlReservacion);
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "crear las tablas");
        }
    }

    /** Solo inserta si las tres tablas están vacías (así no reaparecen datos que el usuario borró). */
    public static void insertarDatosIniciales(Connection conexion) throws FileyException {
        if (!estaVacia(conexion, "stand") || !estaVacia(conexion, "editorial")
                || !estaVacia(conexion, "reservaciones")) {
            return;
        }

        String sqlStand =
                "INSERT INTO stand (id, m2, precio, estado) VALUES " +
                "('A-01', 9.0, 13500.00, 'OCUPADO')," +
                "('A-02', 9.0, 13500.00, 'RESERVADO')," +
                "('A-03', 12.0, 18000.00, 'RESERVADO')," +
                "('A-04', 12.0, 18000.00, 'DISPONIBLE')," +
                "('B-01', 18.0, 27000.00, 'OCUPADO')," +
                "('B-02', 18.0, 27000.00, 'RESERVADO')," +
                "('B-03', 24.0, 36000.00, 'BLOQUEADO')," +
                "('B-04', 36.0, 54000.00, 'RESERVADO')";

        String sqlEditorial =
                "INSERT INTO editorial (id, nombre, correo) VALUES " +
                "('E-1', 'Editorial Dante', 'contacto@dante.example')," +
                "('E-2', 'Editorial Universidad ', 'Universidad@Uady.example')," +
                "('E-3', 'Editorial Kamite', 'info@Kamite.example')," +
                "('E-4', 'Editorial El Estudiante', 'El@estudiante.example')";

        String sqlReservaciones =
                "INSERT INTO reservaciones " +
                "(id, stand_id, editorial_id, fecha_reserva, fecha_vigencia, activa) VALUES " +
                "('R-1', 'A-01', 'E-1', '2026-10-31', '2026-11-15', TRUE)," +
                "('R-2', 'A-02', 'E-2', '2026-11-02', '2026-11-17', TRUE)," +
                "('R-3', 'B-01', 'E-3', '2026-11-05', '2026-11-20', TRUE)," +
                "('R-4', 'A-03', 'E-1', '2026-11-11', '2026-11-26', TRUE)," +
                "('R-5', 'B-02', 'E-2', '2026-11-14', '2026-11-29', TRUE)," +
                "('R-6', 'B-04', 'E-4', '2026-11-14', '2026-11-29', TRUE)";

        try (Statement statement = conexion.createStatement()) {
            statement.executeUpdate(sqlStand);
            statement.executeUpdate(sqlEditorial);
            statement.executeUpdate(sqlReservaciones);
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "insertar los datos iniciales");
        }
    }

    private static boolean estaVacia(Connection conexion, String tabla) throws FileyException {
        // 'tabla' es siempre una constante del programa, nunca texto del usuario
        try (Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "revisar la tabla " + tabla);
        }
    }
}
