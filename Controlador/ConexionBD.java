package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static final String URL = "jdbc:h2:~/test" ;
    private static final String USUARIO = "sa";
    private static final String CONTRASENA = "";


    public static Connection conectar() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USUARIO,
                CONTRASENA
        );
    }

    public static void inicializarBD() {

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

        try (
            Connection conexion = conectar();
            Statement statement = conexion.createStatement()
        ) {

            statement.execute(sqlStand);
            statement.execute(sqlEditorial);
            statement.execute(sqlReservacion);

            System.out.println("Estado: conexion exitosa (H2)");

        } catch (SQLException e) {

            System.out.println(
                    "Error al crear las tablas: "
                    + e.getMessage()
            );
        }
    }

    public static String leerStands() {

        StringBuilder resultado = new StringBuilder();

        String sql = "SELECT * FROM stand";

        try (
            Connection conexion = conectar();
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
        ) {

            resultado.append("\n--- STANDS ---\n");

            resultado.append(
                    String.format(
                            "%-8s %-8s %-15s %-15s%n",
                            "ID",
                            "M2",
                            "PRECIO",
                            "ESTADO"
                    )
            );

            while (resultSet.next()) {

                String id =
                        resultSet.getString("id");

                double m2 =
                        resultSet.getDouble("m2");

                double precio =
                        resultSet.getDouble("precio");

                String estado =
                        resultSet.getString("estado");

                resultado.append(
                        String.format(
                                "%-8s %-8.1f $%-14.2f %-15s%n",
                                id,
                                m2,
                                precio,
                                estado
                        )
                );
            }

        } catch (SQLException e) {

            resultado.append(
                    "Error al leer stands: "
                    + e.getMessage()
                    + "\n"
            );
        }

        return resultado.toString();
    }

    public static String leerEditoriales() {

        StringBuilder resultado = new StringBuilder();

        String sql = "SELECT * FROM editorial";

        try (
            Connection conexion = conectar();
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
        ) {

            resultado.append("\n--- EDITORIALES ---\n");

            resultado.append(
                    String.format(
                            "%-8s %-30s %-35s%n",
                            "ID",
                            "NOMBRE",
                            "CORREO"
                    )
            );

            while (resultSet.next()) {

                String id =
                        resultSet.getString("id");

                String nombre =
                        resultSet.getString("nombre");

                String correo =
                        resultSet.getString("correo");

                resultado.append(
                        String.format(
                                "%-8s %-30s %-35s%n",
                                id,
                                nombre,
                                correo
                        )
                );
            }

        } catch (SQLException e) {

            resultado.append(
                    "Error al leer editoriales: "
                    + e.getMessage()
                    + "\n"
            );
        }

        return resultado.toString();
    }

    public static String leerReservaciones() {

        StringBuilder resultado = new StringBuilder();

        String sql = "SELECT * FROM reservaciones";

        try (
            Connection conexion = conectar();
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)
        ) {

            resultado.append("\n--- RESERVACIONES ---\n");

            resultado.append(
                    String.format(
                            "%-8s %-12s %-15s %-18s %-18s %-10s%n",
                            "ID",
                            "STAND_ID",
                            "EDITORIAL_ID",
                            "FECHA_RESERVA",
                            "FECHA_VIGENCIA",
                            "ACTIVA"
                    )
            );

            while (resultSet.next()) {

                String id =
                        resultSet.getString("id");

                String standId =
                        resultSet.getString("stand_id");

                String editorialId =
                        resultSet.getString("editorial_id");

                String fechaReserva =
                        resultSet.getDate("fecha_reserva")
                                .toString();

                String fechaVigencia =
                        resultSet.getDate("fecha_vigencia")
                                .toString();

                boolean activa =
                        resultSet.getBoolean("activa");

                resultado.append(
                        String.format(
                                "%-8s %-12s %-15s %-18s %-18s %-10s%n",
                                id,
                                standId,
                                editorialId,
                                fechaReserva,
                                fechaVigencia,
                                activa
                        )
                );
            }

        } catch (SQLException e) {

            resultado.append(
                    "Error al leer reservaciones: "
                    + e.getMessage()
                    + "\n"
            );
        }

        return resultado.toString();
    }

    public static String leerTodo() {

        StringBuilder resultado =
                new StringBuilder();

        resultado.append(
                "Estado: conexion exitosa (H2)\n"
        );

        resultado.append(leerStands());
        resultado.append(leerEditoriales());
        resultado.append(leerReservaciones());

        return resultado.toString();
    }
    public static void insertarDatosIniciales() {

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

    try (
        Connection conexion = conectar();
        Statement statement = conexion.createStatement()
    ) {

        statement.executeUpdate(sqlStand);
        statement.executeUpdate(sqlEditorial);
        statement.executeUpdate(sqlReservaciones);

        System.out.println("Datos iniciales insertados.");

    } catch (SQLException e) {

        System.out.println(
                "Los datos ya existen o hubo un error: "
                + e.getMessage()
        );
    }
}
}