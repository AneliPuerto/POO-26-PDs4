package Vista;

import Controlador.Read;

import javax.swing.*;

/** Ventana principal: una pestaña por entidad (CRUD) y una de consultas. */
public class VistaFILEY extends JFrame {

    public VistaFILEY() {
        Read controlador= new Read();

        setTitle("Sistema Administrativo FILEY");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Stands", new PanelStands(controlador));
        pestanas.addTab("Editoriales", new PanelEditoriales(controlador));
        pestanas.addTab("Reservaciones", new PanelReservaciones(controlador));
        pestanas.addTab("Consultas", new PanelConsultas(controlador));

        // Cada vez que se cambia de pestaña se vuelve a leer la BD (así se ven los cambios de las demás)
        pestanas.addChangeListener(e -> refrescarPestanaActual(pestanas));

        add(pestanas);
        refrescarPestanaActual(pestanas);
    }

    private void refrescarPestanaActual(JTabbedPane pestanas) {
        java.awt.Component actual = pestanas.getSelectedComponent();
        if (actual instanceof Refrescable) {
            ((Refrescable) actual).refrescar();
            if (actual instanceof PanelCrud) {
                ((PanelCrud) actual).nuevo();
            }
        }
    }
}
