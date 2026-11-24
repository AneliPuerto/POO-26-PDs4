package Vista;

import Controlador.Read;
import Datos.Stand;
import Excepciones.FileyException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class PanelStands extends PanelCrud {

    private final Read controlador;
    private final JTextField txtId = new JTextField(12);
    private final JTextField txtM2 = new JTextField(12);
    private final JTextField txtPrecio = new JTextField(12);
    private final JComboBox<String> cmbEstado = new JComboBox<>(Stand.ESTADOS);

    PanelStands(Read controlador) {
        super(new String[]{"ID", "m²", "Precio", "Estado"});
        this.controlador = controlador;
        agregarCampo("ID (ej. C-01):", txtId);
        agregarCampo("m²:", txtM2);
        agregarCampo("Precio ($):", txtPrecio);
        agregarCampo("Estado:", cmbEstado);
    }

   
    protected List<Object[]> cargarFilas() throws FileyException {
        List<Object[]> filas = new ArrayList<>();
        for (Stand s : controlador.listarStands()) {
            filas.add(new Object[]{s.getId(),
                    String.format(Locale.US, "%.1f", s.getM2()),
                    String.format(Locale.US, "%.2f", s.getPrecio()),
                    s.getEstado()});
        }
        return filas;
    }

    
    protected void accionCrear() throws FileyException {
        controlador.crearStand(txtId.getText(), txtM2.getText(), txtPrecio.getText(),
                (String) cmbEstado.getSelectedItem());
    }

    
    protected void accionActualizar() throws FileyException {
        controlador.actualizarStand(txtId.getText(), txtM2.getText(), txtPrecio.getText(),
                (String) cmbEstado.getSelectedItem());
    }

    
    protected void accionEliminar() throws FileyException {
        controlador.eliminarStand(txtId.getText());
    }

    
    protected void llenarFormulario(int fila) {
        txtId.setText(celda(fila, 0));
        txtM2.setText(celda(fila, 1));
        txtPrecio.setText(celda(fila, 2));
        cmbEstado.setSelectedItem(celda(fila, 3));
    }

    protected void limpiarFormulario() {
        txtId.setText("");
        txtM2.setText("");
        txtPrecio.setText("");
        cmbEstado.setSelectedIndex(0);
    }
}
