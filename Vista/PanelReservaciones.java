package Vista;

import Controlador.Read;
import Datos.Editorial;
import Datos.Reservacion;
import Datos.Stand;
import Excepciones.FileyException;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class PanelReservaciones extends PanelCrud {

    private static final int DIAS_VIGENCIA = 15; // las reservaciones de ejemplo duran 15 días

    private final Read controlador;
    private final JTextField txtId = new JTextField(12);
    private final JComboBox<Stand> cmbStand = new JComboBox<>();
    private final JComboBox<Editorial> cmbEditorial = new JComboBox<>();
    private final JTextField txtFechaReserva = new JTextField(12);
    private final JTextField txtFechaVigencia = new JTextField(12);
    private final JCheckBox chkActiva = new JCheckBox("Activa", true);

    PanelReservaciones(Read controlador) {
        super(new String[]{"ID", "Stand", "Editorial", "Fecha reserva", "Vigencia", "Activa"});
        this.controlador = controlador;
        agregarCampo("ID:", txtId);
        agregarCampo("Stand:", cmbStand);
        agregarCampo("Editorial:", cmbEditorial);
        agregarCampo("Fecha reserva (AAAA-MM-DD):", txtFechaReserva);
        agregarCampo("Vigencia hasta (AAAA-MM-DD):", txtFechaVigencia);
        agregarCampo("", chkActiva);
    }

    protected void recargarOpciones() throws FileyException {
        Object standPrevio = cmbStand.getSelectedItem();
        Object editorialPrevia = cmbEditorial.getSelectedItem();

        cmbStand.removeAllItems();
        for (Stand s : controlador.listarStands()) {
            cmbStand.addItem(s);
        }
        cmbEditorial.removeAllItems();
        for (Editorial e : controlador.listarEditoriales()) {
            cmbEditorial.addItem(e);
        }

        if (standPrevio != null) {
            seleccionarStand(standPrevio.toString());
        }
        if (editorialPrevia instanceof Editorial) {
            seleccionarEditorial(((Editorial) editorialPrevia).getId());
        }
    }
    protected List<Object[]> cargarFilas() throws FileyException {
        List<Object[]> filas = new ArrayList<>();
        for (Reservacion r : controlador.listarReservaciones()) {
            filas.add(new Object[]{r.getId(), r.getStandId(), r.getEditorialId(),
                    String.valueOf(r.getFechaReserva()), String.valueOf(r.getFechaVigencia()),
                    r.isActiva() ? "Sí" : "No"});
        }
        return filas;
    }


    protected void accionCrear() throws FileyException {
        controlador.crearReservacion(txtId.getText(), standElegido(), editorialElegida(),
                txtFechaReserva.getText(), txtFechaVigencia.getText(), chkActiva.isSelected());
    }

    protected void accionActualizar() throws FileyException {
        controlador.actualizarReservacion(txtId.getText(), standElegido(), editorialElegida(),
                txtFechaReserva.getText(), txtFechaVigencia.getText(), chkActiva.isSelected());
    }

    
    protected void accionEliminar() throws FileyException {
        controlador.eliminarReservacion(txtId.getText());
    }

   
    protected void llenarFormulario(int fila) {
        txtId.setText(celda(fila, 0));
        seleccionarStand(celda(fila, 1));
        seleccionarEditorial(celda(fila, 2));
        txtFechaReserva.setText(celda(fila, 3));
        txtFechaVigencia.setText(celda(fila, 4));
        chkActiva.setSelected("Sí".equals(celda(fila, 5)));
    }

    
    protected void limpiarFormulario() {
        try {
            txtId.setText(controlador.siguienteIdReservacion()); // sugerencia: R-7, R-8...
        } catch (FileyException e) {
            txtId.setText("");
        }
        if (cmbStand.getItemCount() > 0) {
            cmbStand.setSelectedIndex(0);
        }
        if (cmbEditorial.getItemCount() > 0) {
            cmbEditorial.setSelectedIndex(0);
        }
        txtFechaReserva.setText(LocalDate.now().toString());
        txtFechaVigencia.setText(LocalDate.now().plusDays(DIAS_VIGENCIA).toString());
        chkActiva.setSelected(true);
    }

    private String standElegido() {
        Object s = cmbStand.getSelectedItem();
        return s == null ? "" : ((Stand) s).getId();
    }

    private String editorialElegida() {
        Object e = cmbEditorial.getSelectedItem();
        return e == null ? "" : ((Editorial) e).getId();
    }

    private void seleccionarStand(String id) {
        for (int i = 0; i < cmbStand.getItemCount(); i++) {
            if (cmbStand.getItemAt(i).getId().equals(id)) {
                cmbStand.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarEditorial(String id) {
        for (int i = 0; i < cmbEditorial.getItemCount(); i++) {
            if (cmbEditorial.getItemAt(i).getId().equals(id)) {
                cmbEditorial.setSelectedIndex(i);
                return;
            }
        }
    }
}
