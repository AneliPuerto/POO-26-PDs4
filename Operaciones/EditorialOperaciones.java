package Operaciones;

import Datos.Editorial;
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

/** CRUD de la tabla editorial. Recibe la conexión ya abierta; quien la creó es quien la cierra. */
public class EditorialOperaciones {

    private final Connection conexion;

    public EditorialOperaciones(Connection conexion) {
        this.conexion = conexion;
    }

    // ---------- CREATE ----------
    public void crear(Editorial editorial) throws FileyException {
        String sql = "INSERT INTO editorial (id, nombre, correo) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, editorial.getId());
            ps.setString(2, editorial.getNombre());
            ps.setString(3, editorial.getCorreo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "crear la editorial");
        }
    }

    // ---------- READ ----------
    public List<Editorial> leerTodas() throws FileyException {
        List<Editorial> editoriales = new ArrayList<>();
        String sql = "SELECT id, nombre, correo FROM editorial ORDER BY id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                editoriales.add(new Editorial(rs.getString("id"), rs.getString("nombre"), rs.getString("correo")));
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "leer las editoriales");
        }
        return editoriales;
    }

    public Editorial buscarPorId(String id) throws FileyException {
        String sql = "SELECT id, nombre, correo FROM editorial WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Editorial(rs.getString("id"), rs.getString("nombre"), rs.getString("correo"));
                }
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "buscar la editorial");
        }
        throw new RegistroNoEncontradoException("No existe la editorial " + id + ".");
    }

    /*Cada editorial con todos los stands que tiene reservados (reservaciones activas), Si una editorial reserva varios stands, aparecen todos juntos en una sola fila.*/
    public List<String[]> leerConStands() throws FileyException {
        List<String[]> filas = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, COUNT(r.id) AS total, " +
                     "LISTAGG(r.stand_id, ', ') WITHIN GROUP (ORDER BY r.stand_id) AS stands " +
                     "FROM editorial e " +
                     "LEFT JOIN reservaciones r ON r.editorial_id = e.id AND r.activa = TRUE " +
                     "GROUP BY e.id, e.nombre ORDER BY e.id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String stands = rs.getString("stands");
                filas.add(new String[]{
                        rs.getString("id"),
                        rs.getString("nombre").trim(),
                        String.valueOf(rs.getInt("total")),
                        stands == null ? "—" : stands});
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "leer las editoriales con sus stands");
        }
        return filas;
    }

    public String siguienteId() throws FileyException {
        return GeneradorId.siguiente(conexion, "editorial", "E");
    }

    // ---------- UPDATE ----------
    public void actualizar(Editorial editorial) throws FileyException {
        String sql = "UPDATE editorial SET nombre = ?, correo = ? WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, editorial.getNombre());
            ps.setString(2, editorial.getCorreo());
            ps.setString(3, editorial.getId());
            if (ps.executeUpdate() == 0) {
                throw new RegistroNoEncontradoException("No existe la editorial " + editorial.getId() + ".");
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "actualizar la editorial");
        }
    }

    // DELETE 
    public void eliminar(String id) throws FileyException {
        String sql = "DELETE FROM editorial WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RegistroNoEncontradoException("No existe la editorial " + id + ".");
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "eliminar la editorial");
        }
    }
}
