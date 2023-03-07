package Client;//Version con Listener

import Server.MonstruoListener;
import org.apache.activemq.ActiveMQConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.net.*;
import java.io.*;

public class JuegoDelWakamole extends JFrame implements MonstruoListener
{
    public static int FILAS=3;
    private JLabel positionLabel;
    private JLabel puntajeLabel;
    private JButton resetButton;
    private static final int gridSize = 3;
    private final JButton[][] botones;
    public int numJugador=1;
    public  int[][] grid;
    public  int points;
    public int coordenadasLeidas=0;
    public EscuchadorDeMonstruos escuchador;

    public String URLservidor=ActiveMQConnection.DEFAULT_BROKER_URL; //Misma computadora

    public String URLhost="localhost";

    //public String URLservidor = "tcp://10.10.26.139:61616"; //Otra computadora, servidor


    public JuegoDelWakamole() {
        super("Client.JuegoDelWakamole");
        botones = new JButton[3][3];
    }

    public  void createAndDisplayGUI()
    {

        grid = new int[FILAS][FILAS];
        points=0;
        for(int i=0; i<3;i++){
            for(int j=0; j<3;j++){
                grid[i][j] = 0;
            }
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT, 40, 20));
        contentPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        JPanel leftPanel = new JPanel();
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JPanel labelPanel = new JPanel();
        positionLabel = new JLabel("Jugador: "+numJugador, JLabel.CENTER);
        puntajeLabel= new JLabel("Puntaje: "+points, JLabel.CENTER);

        JPanel buttonLeftPanel = new JPanel();
        labelPanel.add(positionLabel);
        buttonLeftPanel.add(puntajeLabel);
        leftPanel.add(labelPanel);
        leftPanel.add(buttonLeftPanel);

        contentPane.add(leftPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize, gridSize, 10, 10));
        for (int i = 0; i < gridSize; i++)
        {
            for (int j = 0; j < gridSize; j++)
            {
                botones[i][j] = new JButton("O");
                botones[i][j].setActionCommand("(" + i + ", " + j + ")");
                botones[i][j].setBackground(Color.RED);
                botones[i][j].setOpaque(true);

                botones[i][j].addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        JButton but = (JButton) ae.getSource();
                        but.setText("O");

                        Character k = but.getActionCommand().charAt(1);
                        Character l = but.getActionCommand().charAt(4);

                        int kInt=Character.getNumericValue(k);
                        int lInt=Character.getNumericValue(l);

                        if(grid[kInt][lInt]==0){
                            System.out.println("No puedes presionarlo");
                        }else{
                            grid[kInt][lInt]=0;
                            System.out.println("Se presiono el boton en posicion  "+kInt+"   "+lInt);


                            but.setText("O");
                            but.setBackground(Color.RED);
                            points++;
                            puntajeLabel.setText("Puntaje: "+points);

                            // ------------Enviar el mensaje TCP--------------

                            Socket s = null;

                            try {
                                int serverPort = 49152;
                                // -------------------direccion IP del servidor------------------------
                                s = new Socket(URLhost, serverPort);
                                // s = new Socket("127.0.0.1", serverPort);
                                //DataInputStream in = new DataInputStream(s.getInputStream());
                                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                                out.writeUTF(kInt+" "+lInt);            // UTF is a string encoding

                               // String data = in.readUTF();
                                //System.out.println("Received: " + data);

                            } catch (UnknownHostException e) {
                                System.out.println("Sock:" + e.getMessage());
                            } catch (EOFException e) {
                                System.out.println("EOF:" + e.getMessage());
                            } catch (IOException e) {
                                System.out.println("IO:" + e.getMessage());
                            } finally {
                                if (s != null) try {
                                    s.close();
                                } catch (IOException e) {
                                    System.out.println("close:" + e.getMessage());
                                }
                            }

                        }


                    }
                });
                buttonPanel.add(botones[i][j]);
            }
        }
        contentPane.add(buttonPanel);

        setContentPane(contentPane);
        pack();
        setLocationByPlatform(true);
        setVisible(true);
    }

    public void autoSelectRandomButton() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int randomRow = ThreadLocalRandom.current().nextInt(FILAS);
                int randomCol = ThreadLocalRandom.current().nextInt(FILAS);


                if(grid[randomRow][randomCol]==0){
                    grid[randomRow][randomCol]=1;
                    System.out.println("Se presiono el boton en posicion  "+randomRow+"   "+randomCol);
                    botones[randomRow][randomCol].setText("X");
                    botones[randomRow][randomCol].setBackground(Color.GREEN);
                }else{
                    System.out.println("Ya estaba presionado");
                }


            }
        }, 0, 2000);
    }

    public void startGame() {
        // ...

        escuchador = new EscuchadorDeMonstruos(URLservidor);
        escuchador.addMonstruoListener(this);
        escuchador.start();

    }

    @Override
    public void onMonstruoReceived(int columna, int fila) {
       // System.out.println("Prueba Listener "+columna+" "+fila);

        if(grid[columna][fila]==0){
            grid[columna][fila]=1;
            System.out.println("Sale un topo en   "+columna+"   "+fila);
            botones[columna][fila].setText("X");
            botones[columna][fila].setBackground(Color.GREEN);
        }else{
            System.out.println("Ya habia topo");
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JuegoDelWakamole lay = new JuegoDelWakamole();
                lay.createAndDisplayGUI();
                lay.startGame();
            }
        });
    }
}