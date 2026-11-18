import Vista.VistaFILEY;
import javax.swing.*;
import Controlador.ConexionBD;
import Controlador.Read;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        
      
        try (Connection conexion = ConexionBD.conectar()) {
            System.out.println("Estado: conexion exitosa (H2)");
            
            
            Read.inicializarBD(conexion);
            
          
            Read.insertarDatosIniciales(conexion);

        } catch (SQLException e) {
            System.out.println("Error de conexión al inicializar: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            VistaFILEY ventana = new VistaFILEY();
            ventana.setVisible(true);
        });
    }
}