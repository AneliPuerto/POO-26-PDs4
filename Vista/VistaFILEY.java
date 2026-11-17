package Vista;
import Controlador.ConexionBD;

import javax.swing.*;
import java.awt.*;

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

            String datos =
                    ConexionBD.leerTodo();

            areaTexto.setText(datos);
        });

        add(panelPrincipal);
    }
}