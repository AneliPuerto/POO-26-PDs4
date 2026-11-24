import Controlador.ConexionBD;
import Controlador.InicializadorBD;
import Excepciones.FileyException;
import Vista.VistaFILEY;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        try (Connection conexion = ConexionBD.conectar()) {
            System.out.println("Estado: conexion exitosa (H2)");
            InicializadorBD.crearTablas(conexion);
            InicializadorBD.insertarDatosIniciales(conexion);
            System.out.println("Estado: base de datos lista");
        } catch (SQLException | FileyException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "No se pudo iniciar la base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            VistaFILEY ventana = new VistaFILEY();
            ventana.setVisible(true);
        });
    }
}
