import javax.swing.SwingUtilities;
import Controlador.ConexionBD; 
import Vista.VistaFILEY;

public class Main {

    public static void main(String[] args) {

        ConexionBD.inicializarBD();

        ConexionBD.insertarDatosIniciales();

        SwingUtilities.invokeLater(() -> {

            VistaFILEY ventana = new VistaFILEY();
            ventana.setVisible(true);

        });
    }
}