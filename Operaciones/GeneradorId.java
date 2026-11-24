package Operaciones;

import Excepciones.ErroresSQL;
import Excepciones.FileyException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


class GeneradorId {

    static String siguiente(Connection conexion, String tabla, String prefijo) throws FileyException {
        int mayor = 0;
        // 'tabla' es siempre una constante del programa, nunca texto del usuario
        try (Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery("SELECT id FROM " + tabla)) {
            while (rs.next()) {
                String id = rs.getString("id");
                if (id != null && id.startsWith(prefijo + "-")) {
                    try {
                        mayor = Math.max(mayor, Integer.parseInt(id.substring(prefijo.length() + 1)));
                    } catch (NumberFormatException ignorado) {
                        // un ID con otro formato (ej. E-ABC) simplemente no cuenta
                    }
                }
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "calcular el siguiente ID");
        }
        return prefijo + "-" + (mayor + 1);
    }
}
