package Operaciones;

import Datos.Reservacion;
import Datos.Stand;
import Excepciones.ErroresSQL;
import Excepciones.FileyException;
import Excepciones.RegistroNoEncontradoException;
import Excepciones.ReglaNegocioException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*CRUD de la tabla reservaciones. Recibe la conexión ya abierta;*/
public class ReservacionOperaciones {

    /** Un bloque de trabajo SQL que se ejecuta dentro de una transacción. */
    private interface Trabajo {
        void hacer() throws SQLException, FileyException;
    }

    private final Connection conexion;

    public ReservacionOperaciones(Connection conexion) {
        this.conexion = conexion;
    }

    // ---------- CREATE ----------
    public void crear(Reservacion r) throws FileyException {
        validar(r, "");
        enTransaccion("crear la reservación", () -> {
            String sql = "INSERT INTO reservaciones " +
                         "(id, stand_id, editorial_id, fecha_reserva, fecha_vigencia, activa) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, r.getId());
                ps.setString(2, r.getStandId());
                ps.setString(3, r.getEditorialId());
                ps.setObject(4, r.getFechaReserva());
                ps.setObject(5, r.getFechaVigencia());
                ps.setBoolean(6, r.isActiva());
                ps.executeUpdate();
            }
            sincronizarEstadoStand(r.getStandId());
        });
    }

    // ---------- READ ----------
    public List<Reservacion> leerTodas() throws FileyException {
        List<Reservacion> reservaciones = new ArrayList<>();
        String sql = "SELECT id, stand_id, editorial_id, fecha_reserva, fecha_vigencia, activa " +
                     "FROM reservaciones ORDER BY id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                reservaciones.add(desdeFila(rs));
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "leer las reservaciones");
        }
        return reservaciones;
    }

    public Reservacion buscarPorId(String id) throws FileyException {
        String sql = "SELECT id, stand_id, editorial_id, fecha_reserva, fecha_vigencia, activa " +
                     "FROM reservaciones WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return desdeFila(rs);
                }
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "buscar la reservación");
        }
        throw new RegistroNoEncontradoException("No existe la reservación " + id + ".");
    }

    public String siguienteId() throws FileyException {
        return GeneradorId.siguiente(conexion, "reservaciones", "R");
    }

    // ---------- UPDATE ----------
    public void actualizar(Reservacion r) throws FileyException {
        Reservacion anterior = buscarPorId(r.getId()); // lanza RegistroNoEncontradoException si no existe
        validar(r, r.getId());
        enTransaccion("actualizar la reservación", () -> {
            String sql = "UPDATE reservaciones SET stand_id = ?, editorial_id = ?, " +
                         "fecha_reserva = ?, fecha_vigencia = ?, activa = ? WHERE id = ?";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, r.getStandId());
                ps.setString(2, r.getEditorialId());
                ps.setObject(3, r.getFechaReserva());
                ps.setObject(4, r.getFechaVigencia());
                ps.setBoolean(5, r.isActiva());
                ps.setString(6, r.getId());
                ps.executeUpdate();
            }
            sincronizarEstadoStand(anterior.getStandId()); // el stand que dejó
            sincronizarEstadoStand(r.getStandId());        // el stand que tomó
        });
    }

    // ---------- DELETE ----------
    public void eliminar(String id) throws FileyException {
        Reservacion anterior = buscarPorId(id);
        enTransaccion("eliminar la reservación", () -> {
            try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM reservaciones WHERE id = ?")) {
                ps.setString(1, id);
                ps.executeUpdate();
            }
            sincronizarEstadoStand(anterior.getStandId());
        });
    }

    // ---------- apoyo interno ----------
    private Reservacion desdeFila(ResultSet rs) throws SQLException {
        return new Reservacion(
                rs.getString("id"),
                rs.getString("stand_id"),
                rs.getString("editorial_id"),
                rs.getObject("fecha_reserva", LocalDate.class),
                rs.getObject("fecha_vigencia", LocalDate.class),
                rs.getBoolean("activa"));
    }

    /** @param idExcluir ID de la propia reservación al actualizar ("" al crear), para no chocar consigo misma */
    private void validar(Reservacion r, String idExcluir) throws FileyException {
        if (r.getFechaVigencia().isBefore(r.getFechaReserva())) {
            throw new ReglaNegocioException("La fecha de vigencia no puede ser anterior a la fecha de reserva.");
        }

        Stand stand = new StandOperaciones(conexion).buscarPorId(r.getStandId());
        new EditorialOperaciones(conexion).buscarPorId(r.getEditorialId());

        if (!r.isActiva()) {
            return; // una reservación inactiva no ocupa el stand
        }
        if ("BLOQUEADO".equals(stand.getEstado())) {
            throw new ReglaNegocioException("El stand " + stand.getId() + " está BLOQUEADO y no se puede reservar.");
        }

        String sql = "SELECT id FROM reservaciones WHERE stand_id = ? AND activa = TRUE AND id <> ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, r.getStandId());
            ps.setString(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new ReglaNegocioException("El stand " + r.getStandId()
                            + " ya tiene una reservación activa (" + rs.getString("id") + ").");
                }
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, "validar la reservación");
        }
    }

    /**
     * Deja el estado del stand coherente con sus reservaciones: RESERVADO si tiene una activa,
     * DISPONIBLE si no. OCUPADO y BLOQUEADO los decide el usuario, así que no se tocan.
     */
    private void sincronizarEstadoStand(String standId) throws SQLException {
        String sql = "UPDATE stand s SET estado = CASE WHEN EXISTS " +
                     "(SELECT 1 FROM reservaciones r WHERE r.stand_id = s.id AND r.activa = TRUE) " +
                     "THEN 'RESERVADO' ELSE 'DISPONIBLE' END " +
                     "WHERE s.id = ? AND s.estado IN ('DISPONIBLE', 'RESERVADO')";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, standId);
            ps.executeUpdate();
        }
    }

    /** Ejecuta varias sentencias como una sola unidad: o se guardan todas (commit) o ninguna (rollback). */
    private void enTransaccion(String contexto, Trabajo trabajo) throws FileyException {
        try {
            conexion.setAutoCommit(false);
            try {
                trabajo.hacer();
                conexion.commit();
            } catch (SQLException | FileyException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ErroresSQL.traducir(e, contexto);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException ignorada) {
                // la conexión se cierra enseguida de todos modos
            }
        }
    }
}
