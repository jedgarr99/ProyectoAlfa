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
                JuegoDelWakamole lay = new JuegoDelWakamole();
                lay.createAndDisplayGUI();
                lay.startGame();
               // new Server.EnviadorDeMonstruos().enviaMonstruos();
            }
        });

        EnviadorDeMonstruos sender = new EnviadorDeMonstruos();
        sender.startListeningTCP();
        sender.enviaMonstruos();

    }
}