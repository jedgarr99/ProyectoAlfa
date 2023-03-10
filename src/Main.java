import Client.JuegoDelWakamole;
import Server.ConnectionTCPsockets;
import Server.EnviadorDeMonstruos;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {

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

        /*
        EnviadorDeMonstruos sender = new EnviadorDeMonstruos();
        sender.startListeningTCP();
        sender.enviaMonstruos();

         */

    }
}