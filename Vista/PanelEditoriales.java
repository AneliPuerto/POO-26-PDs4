package Vista;

import Controlador.Read;
import Datos.Editorial;
import Excepciones.FileyException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class PanelEditoriales extends PanelCrud {

    private final Read controlador;
    private final JTextField txtId = new JTextField(12);
    private final JTextField txtNombre = new JTextField(30);
    private final JTextField txtCorreo = new JTextField(30);

    PanelEditoriales(Read controlador) {
        super(new String[]{"ID", "Nombre", "Correo"});
        this.controlador = controlador;
        agregarCampo("ID:", txtId);
        agregarCampo("Nombre:", txtNombre);
        agregarCampo("Correo:", txtCorreo);
    }

  
    protected List<Object[]> cargarFilas() throws FileyException {
        List<Object[]> filas = new ArrayList<>();
        for (Editorial e : controlador.listarEditoriales()) {
            filas.add(new Object[]{e.getId(), e.getNombre().trim(), e.getCorreo()});
        }
        return filas;
    }

    protected void accionCrear() throws FileyException {
        controlador.crearEditorial(txtId.getText(), txtNombre.getText(), txtCorreo.getText());
    }

    protected void accionActualizar() throws FileyException {
        controlador.actualizarEditorial(txtId.getText(), txtNombre.getText(), txtCorreo.getText());
    }

   
    protected void accionEliminar() throws FileyException {
        controlador.eliminarEditorial(txtId.getText());
    }

   
    protected void llenarFormulario(int fila) {
        txtId.setText(celda(fila, 0));
        txtNombre.setText(celda(fila, 1));
        txtCorreo.setText(celda(fila, 2));
    }


    protected void limpiarFormulario() {
        try {
            txtId.setText(controlador.siguienteIdEditorial()); // sugerencia: E-5, E-6...
        } catch (FileyException e) {
            txtId.setText("");
        }
        txtNombre.setText("");
        txtCorreo.setText("");
    }
}
