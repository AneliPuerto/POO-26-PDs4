package Vista;

import Controlador.ConexionBD; 
import Controlador.Read;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class VistaFILEY extends JFrame {

    private JTextArea areaTexto;
    private JButton botonLeer;

    public VistaFILEY() {

        setTitle("Sistema Administrativo FILEY");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal =
                new JPanel(new BorderLayout());

        botonLeer =
                new JButton("Leer información de H2");

        areaTexto =
                new JTextArea();

        areaTexto.setEditable(false);

        areaTexto.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        13
                )
        );

        JScrollPane scroll =
                new JScrollPane(areaTexto);

        panelPrincipal.add(
                botonLeer,
                BorderLayout.NORTH
        );

        panelPrincipal.add(
                scroll,
                BorderLayout.CENTER
        );
        botonLeer.addActionListener(e -> {
          
            try (Connection conexion = ConexionBD.conectar()) {
                
        
                String datos = Read.leerTodo(conexion);
                areaTexto.setText(datos);

            } catch (SQLException ex) {
                areaTexto.setText("Error al conectar con la base de datos:\n" + ex.getMessage());
            }
        });

        add(panelPrincipal);
    }
}