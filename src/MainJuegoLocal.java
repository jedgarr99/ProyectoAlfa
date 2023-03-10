import Client.JuegoDelWakamole;
import Server.EnviadorDeMonstruos;

import javax.swing.*;

public class MainJuegoLocal {
    public static void main(String[] args) {


        Thread arrancarServidor=new Thread(() -> {

            EnviadorDeMonstruos sender = new EnviadorDeMonstruos();
            sender.startListeningTCP();
            sender.enviaMonstruos();


        });
        arrancarServidor.start();

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JuegoDelWakamole lay = new JuegoDelWakamole("Jorge");
                lay.createAndDisplayGUI();

            }
        });

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JuegoDelWakamole lay = new JuegoDelWakamole("Anairam");
                lay.createAndDisplayGUI();

            }
        });

    }
}