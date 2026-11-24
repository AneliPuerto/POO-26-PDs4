package Operaciones;

import Datos.Stand;
import Excepciones.ErroresSQL;
import Excepciones.FileyException;
import Excepciones.RegistroNoEncontradoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** CRUD de la tabla stand. Recibe la conexión ya abierta; quien la creó es quien la cierra. */
public class StandOperaciones {

    private final Connection conexion;

    public StandOperaciones(Connection conexion) {
        this.conexion = conexion;
    }

    // ---------- CREATE ----------
    public void crear(Stand stand) throws FileyException {
        String sql = "INSERT INTO stand (id, m2, precio, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, stand.getId());
            ps.setDouble(2, stand.getM2());
            ps.setDouble(3, stand.getPrecio());
            ps.setString(4, stand.getEstado());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "crear el stand");
        }
    }

    // ---------- READ ----------
    public List<Stand> leerTodos() throws FileyException {
        List<Stand> stands = new ArrayList<>();
        String sql = "SELECT id, m2, precio, estado FROM stand ORDER BY id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stands.add(new Stand(rs.getString("id"), rs.getDouble("m2"),
                        rs.getDouble("precio"), rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "leer los stands");
        }
        return stands;
    }

    public Stand buscarPorId(String id) throws FileyException {
        String sql = "SELECT id, m2, precio, estado FROM stand WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Stand(rs.getString("id"), rs.getDouble("m2"),
                            rs.getDouble("precio"), rs.getString("estado"));
                }
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "buscar el stand");
        }
        throw new RegistroNoEncontradoException("No existe el stand " + id + ".");
    }

    /** Cada stand con la editorial que lo tiene en una reservación activa (o "—" si no tiene). */
    public List<String[]> leerConEditorial() throws FileyException {
        List<String[]> filas = new ArrayList<>();
        String sql = "SELECT s.id, s.m2, s.precio, s.estado, e.nombre, r.fecha_vigencia " +
                     "FROM stand s " +
                     "LEFT JOIN reservaciones r ON r.stand_id = s.id AND r.activa = TRUE " +
                     "LEFT JOIN editorial e ON e.id = r.editorial_id " +
                     "ORDER BY s.id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String editorial = rs.getString("nombre");
                Object vigencia = rs.getObject("fecha_vigencia");
                filas.add(new String[]{
                        rs.getString("id"),
                        String.format(Locale.US, "%.1f", rs.getDouble("m2")),
                        String.format(Locale.US, "%.2f", rs.getDouble("precio")),
                        rs.getString("estado"),
                        editorial == null ? "—" : editorial.trim(),
                        vigencia == null ? "—" : vigencia.toString()});
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "leer los stands con su editorial");
        }
        return filas;
    }

    // ---------- UPDATE ----------
    public void actualizar(Stand stand) throws FileyException {
        String sql = "UPDATE stand SET m2 = ?, precio = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, stand.getM2());
            ps.setDouble(2, stand.getPrecio());
            ps.setString(3, stand.getEstado());
            ps.setString(4, stand.getId());
            if (ps.executeUpdate() == 0) {
                throw new RegistroNoEncontradoException("No existe el stand " + stand.getId() + ".");
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "actualizar el stand");
        }
    }

    // ---------- DELETE ----------
    public void eliminar(String id) throws FileyException {
        String sql = "DELETE FROM stand WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RegistroNoEncontradoException("No existe el stand " + id + ".");
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "eliminar el stand");
        }
    }
}
