import Client.JuegoDelWakamole;
import Server.EnviadorDeMonstruos;

import javax.swing.*;

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
        new EnviadorDeMonstruos().enviaMonstruos();



    }
}