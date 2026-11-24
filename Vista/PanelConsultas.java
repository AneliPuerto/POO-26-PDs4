package Vista;

import Controlador.Read;
import Excepciones.FileyException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
  Pantalla de solo lectura con la relación entre las tres tablas:
  cada stand con su editorial;
 cada editorial con todos sus stands (si reserva más de uno, aparecen todos).
 */
class PanelConsultas extends JPanel implements Refrescable {

    private final Read controlador;
    private final DefaultTableModel modeloStands = nuevoModelo("Stand", "m²", "Precio", "Estado", "Editorial", "Vigencia hasta");
    private final DefaultTableModel modeloEditoriales = nuevoModelo("ID", "Editorial", "Nº de stands", "Stands reservados");

    PanelConsultas(Read controlador) {
        super(new GridLayout(2, 1, 8, 8));
        this.controlador = controlador;
        setBorder(new EmptyBorder(10, 10, 10, 10));
        add(envolver("Stands y su editorial (reservación activa)", modeloStands));
        add(envolver("Editoriales y sus stands", modeloEditoriales));
    }

    
    public void refrescar() {
        try {
            llenar(modeloStands, controlador.listarStandsConEditorial());
            llenar(modeloEditoriales, controlador.listarEditorialesConStands());
        } catch (FileyException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo leer la información",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static DefaultTableModel nuevoModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private static JScrollPane envolver(String titulo, DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        return scroll;
    }

    private static void llenar(DefaultTableModel modelo, List<String[]> filas) {
        modelo.setRowCount(0);
        for (String[] fila : filas) {
            modelo.addRow(fila);
        }
    }
}
