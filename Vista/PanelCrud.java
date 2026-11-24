package Vista;

import Excepciones.ConexionException;
import Excepciones.FileyException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


abstract class PanelCrud extends JPanel implements Refrescable {

    /** Acción de la vista que puede fallar con una excepción del sistema. */
    protected interface Accion {
        void hacer() throws FileyException;
    }

    protected final DefaultTableModel modeloTabla;
    protected final JTable tabla;
    private final JPanel formulario = new JPanel(new GridBagLayout());
    private int filaFormulario = 0;

    protected PanelCrud(String[] columnas) {
        super(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(24);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                llenarFormulario(tabla.convertRowIndexToModel(fila));
            }
        });

        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Nuevo / Limpiar");
        JButton btnRefrescar = new JButton("Refrescar");

        btnCrear.addActionListener(e -> ejecutar(this::accionCrear));
        btnActualizar.addActionListener(e -> ejecutar(this::accionActualizar));
        btnEliminar.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el registro indicado en el formulario?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (respuesta == JOptionPane.YES_OPTION) {
                ejecutar(this::accionEliminar);
            }
        });
        btnLimpiar.addActionListener(e -> nuevo());
        btnRefrescar.addActionListener(e -> refrescar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        botones.add(btnCrear);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnRefrescar);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(formulario, BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.SOUTH);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    // ----- lo que cada pantalla debe definir -----
    protected abstract List<Object[]> cargarFilas() throws FileyException;
    protected abstract void accionCrear() throws FileyException;
    protected abstract void accionActualizar() throws FileyException;
    protected abstract void accionEliminar() throws FileyException;
    protected abstract void llenarFormulario(int filaModelo);
    protected abstract void limpiarFormulario();

    /** Gancho opcional: recargar listas desplegables que dependen de otras tablas. */
    protected void recargarOpciones() throws FileyException {
    }

    // ----- lo que comparten todas -----
    protected void agregarCampo(String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = filaFormulario++;
        c.insets = new Insets(3, 4, 3, 4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        formulario.add(new JLabel(etiqueta), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campo, c);
    }

    protected void nuevo() {
        tabla.clearSelection();
        limpiarFormulario();
    }

    @Override
    public void refrescar() {
        try {
            recargarOpciones();
            modeloTabla.setRowCount(0);
            for (Object[] fila : cargarFilas()) {
                modeloTabla.addRow(fila);
            }
        } catch (FileyException ex) {
            mostrarError(ex);
        }
    }

    private void ejecutar(Accion accion) {
        try {
            accion.hacer();
            refrescar();
            nuevo();
        } catch (FileyException ex) {
            mostrarError(ex);
        }
    }

    private void mostrarError(FileyException ex) {
        int tipo = (ex instanceof ConexionException) ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo completar la operación", tipo);
    }

    protected String celda(int filaModelo, int columna) {
        return String.valueOf(modeloTabla.getValueAt(filaModelo, columna));
    }
}
