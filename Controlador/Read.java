package Controlador;

import Datos.Editorial;
import Datos.Reservacion;
import Datos.Stand;
import Excepciones.ConexionException;
import Excepciones.FileyException;
import Excepciones.ReglaNegocioException;
import Operaciones.EditorialOperaciones;
import Operaciones.ReservacionOperaciones;
import Operaciones.StandOperaciones;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;


public class Read{

    /** Trabajo que devuelve un resultado (lecturas). */
    private interface Consulta<T> {
        T ejecutar(Connection conexion) throws FileyException;
    }

    /** Trabajo que no devuelve nada (crear, actualizar, eliminar). */
    private interface Modificacion {
        void ejecutar(Connection conexion) throws FileyException;
    }

    //  STANDS //
    public List<Stand> listarStands() throws FileyException {
        return consultar(c -> new StandOperaciones(c).leerTodos());
    }

    public List<String[]> listarStandsConEditorial() throws FileyException {
        return consultar(c -> new StandOperaciones(c).leerConEditorial());
    }

    public void crearStand(String id, String m2, String precio, String estado) throws FileyException {
        Stand stand = construirStand(id, m2, precio, estado);
        modificar(c -> new StandOperaciones(c).crear(stand));
    }

    public void actualizarStand(String id, String m2, String precio, String estado) throws FileyException {
        Stand stand = construirStand(id, m2, precio, estado);
        modificar(c -> new StandOperaciones(c).actualizar(stand));
    }

    public void eliminarStand(String id) throws FileyException {
        String idLimpio = textoObligatorio(id, "El ID del stand");
        modificar(c -> new StandOperaciones(c).eliminar(idLimpio));
    }

    //  EDITORIALES  //
    public List<Editorial> listarEditoriales() throws FileyException {
        return consultar(c -> new EditorialOperaciones(c).leerTodas());
    }

    public List<String[]> listarEditorialesConStands() throws FileyException {
        return consultar(c -> new EditorialOperaciones(c).leerConStands());
    }

    public String siguienteIdEditorial() throws FileyException {
        return consultar(c -> new EditorialOperaciones(c).siguienteId());
    }

    public void crearEditorial(String id, String nombre, String correo) throws FileyException {
        Editorial editorial = construirEditorial(id, nombre, correo);
        modificar(c -> new EditorialOperaciones(c).crear(editorial));
    }

    public void actualizarEditorial(String id, String nombre, String correo) throws FileyException {
        Editorial editorial = construirEditorial(id, nombre, correo);
        modificar(c -> new EditorialOperaciones(c).actualizar(editorial));
    }

    public void eliminarEditorial(String id) throws FileyException {
        String idLimpio = textoObligatorio(id, "El ID de la editorial");
        modificar(c -> new EditorialOperaciones(c).eliminar(idLimpio));
    }

    //  RESERVACIONES //
    public List<Reservacion> listarReservaciones() throws FileyException {
        return consultar(c -> new ReservacionOperaciones(c).leerTodas());
    }

    public String siguienteIdReservacion() throws FileyException {
        return consultar(c -> new ReservacionOperaciones(c).siguienteId());
    }

    public void crearReservacion(String id, String standId, String editorialId,
                                 String fechaReserva, String fechaVigencia, boolean activa) throws FileyException {
        Reservacion r = construirReservacion(id, standId, editorialId, fechaReserva, fechaVigencia, activa);
        modificar(c -> new ReservacionOperaciones(c).crear(r));
    }

    public void actualizarReservacion(String id, String standId, String editorialId,
                                      String fechaReserva, String fechaVigencia, boolean activa) throws FileyException {
        Reservacion r = construirReservacion(id, standId, editorialId, fechaReserva, fechaVigencia, activa);
        modificar(c -> new ReservacionOperaciones(c).actualizar(r));
    }

    public void eliminarReservacion(String id) throws FileyException {
        String idLimpio = textoObligatorio(id, "El ID de la reservación");
        modificar(c -> new ReservacionOperaciones(c).eliminar(idLimpio));
    }

    // conexión  //
    private <T> T consultar(Consulta<T> consulta) throws FileyException {
        try (Connection conexion = ConexionBD.conectar()) {
            return consulta.ejecutar(conexion);
        } catch (SQLException e) { // solo puede venir de close()
            throw new ConexionException("Error al cerrar la conexión: " + e.getMessage(), e);
        }
    }

    private void modificar(Modificacion modificacion) throws FileyException {
        consultar(conexion -> {
            modificacion.ejecutar(conexion);
            return null;
        });
    }
//validación y construcción de objetos  //
    private Stand construirStand(String id, String m2, String precio, String estado) throws FileyException {
        String idLimpio = textoObligatorio(id, "El ID del stand");
        double metros = numeroPositivo(m2, "Los m²");
        double costo = numeroPositivo(precio, "El precio");
        String estadoLimpio = textoObligatorio(estado, "El estado");
        if (!Arrays.asList(Stand.ESTADOS).contains(estadoLimpio)) {
            throw new ReglaNegocioException("Estado inválido: " + estadoLimpio + ".");
        }
        return new Stand(idLimpio, metros, costo, estadoLimpio);
    }

    private Editorial construirEditorial(String id, String nombre, String correo) throws FileyException {
        String idLimpio = textoObligatorio(id, "El ID de la editorial");
        String nombreLimpio = textoObligatorio(nombre, "El nombre");
        String correoLimpio = textoObligatorio(correo, "El correo");
        if (!correoLimpio.contains("@")) {
            throw new ReglaNegocioException("El correo debe contener '@'.");
        }
        return new Editorial(idLimpio, nombreLimpio, correoLimpio);
    }

    private Reservacion construirReservacion(String id, String standId, String editorialId,
                                             String fechaReserva, String fechaVigencia, boolean activa)
            throws FileyException {
        return new Reservacion(
                textoObligatorio(id, "El ID de la reservación"),
                textoObligatorio(standId, "El stand"),
                textoObligatorio(editorialId, "La editorial"),
                fecha(fechaReserva, "La fecha de reserva"),
                fecha(fechaVigencia, "La fecha de vigencia"),
                activa);
    }

    private String textoObligatorio(String valor, String campo) throws ReglaNegocioException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException(campo + " es obligatorio.");
        }
        return valor.trim();
    }

    private double numeroPositivo(String valor, String campo) throws ReglaNegocioException {
        String texto = textoObligatorio(valor, campo);
        try {
            double numero = Double.parseDouble(texto.replace(',', '.'));
            if (numero <= 0 || Double.isNaN(numero) || Double.isInfinite(numero)) {
                throw new ReglaNegocioException(campo + " debe ser mayor que cero.");
            }
            return numero;
        } catch (NumberFormatException e) {
            throw new ReglaNegocioException(campo + " debe ser un número (ej. 12.5).", e);
        }
    }

    private LocalDate fecha(String valor, String campo) throws ReglaNegocioException {
        String texto = textoObligatorio(valor, campo);
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException(campo + " debe tener el formato AAAA-MM-DD (ej. 2026-11-30).", e);
        }
    }
}
