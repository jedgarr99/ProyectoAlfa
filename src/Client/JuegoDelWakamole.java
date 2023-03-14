package Client;

import Server.MonstruoListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    private JLabel jugadorLabel;
    private JLabel puntosLabel;
    private JButton botonRegistro;
    private JButton resetButton;
    private static final int gridSize = 3;
    private final JButton[][] botones;
    public int numJuego=1;
    private final String nombreUsuario;
    private final boolean registrado=false;
    public  int[][] grid;
    public  int points;

    //informacion para primer mensaje Tcp del registro
    public int serverPort = 49152;
    public String URLcomunicacionTCP = "127.0.0.1"; //Para juegos locales
    //public String URLcomunicacionTCP="192.168.1.81";   //Para jugar con otra compu


    //Informacion que da el registro
    public int puertoMonstruos=0;
    public String canalMonstruos;
    public String URLcomunicacionTCP2;
    public String URLcomunicacionTopicos ="tcp://"+ URLcomunicacionTCP2 +":61616";
    public EscuchadorDeMonstruos escuchador;
    public long startTime=0;
    public long spentTime=0;
    public double promedioTiemRespuesta=0;
    ArrayList<Long> tiemposRespuesta= new ArrayList<>();



    public boolean juegoAutomatico=false;

    public JuegoDelWakamole(String nombreUsuario) {
        super("Client.JuegoDelWakamole");
        botones = new JButton[3][3];
        this.nombreUsuario=nombreUsuario;
    }

    public JuegoDelWakamole(String nombreUsuario,boolean juegoAutomatico) {
        super("Client.JuegoDelWakamole");
        botones = new JButton[3][3];
        this.nombreUsuario=nombreUsuario;
        this.juegoAutomatico=juegoAutomatico;
    }

    public  void createAndDisplayGUI() {

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
        jugadorLabel=new JLabel("Jugador: "+nombreUsuario, JLabel.CENTER);
        positionLabel = new JLabel("Juego: "+numJuego, JLabel.CENTER);
        puntosLabel= new JLabel("Puntos: "+points, JLabel.CENTER);

        //puntajeLabel= new JLabel("Puntaje: "+points, JLabel.CENTER);
        puntajeLabel= new JLabel("Ganador Juego "+(numJuego-1)+" :-------", JLabel.CENTER);

        botonRegistro= new JButton("Registrarse");

        botonRegistro.addActionListener(new ActionListener()
    {
        public void actionPerformed(ActionEvent ae)
        {
            if(!registrado)
                registro();
        }
    });
        JPanel jugadorPanel = new JPanel();
        JPanel buttonLeftPanel = new JPanel();
        JPanel registroPanel = new JPanel();
        JPanel puntosPanel = new JPanel();


        jugadorPanel.add(jugadorLabel);
        labelPanel.add(positionLabel);
        buttonLeftPanel.add(puntajeLabel);
        puntosPanel.add(puntosLabel);
        registroPanel.add(botonRegistro);
        leftPanel.add(jugadorPanel);
        leftPanel.add(labelPanel);
        leftPanel.add(puntosPanel);
        leftPanel.add(buttonLeftPanel);
        leftPanel.add(registroPanel);

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

                        Character k = but.getActionCommand().charAt(1);
                        Character l = but.getActionCommand().charAt(4);
                        int kInt=Character.getNumericValue(k);
                        int lInt=Character.getNumericValue(l);

                        if(grid[kInt][lInt]==0){
                            System.out.println("No puedes presionarlo");
                        }else{
                            int counter =grid[kInt][lInt];
                            grid[kInt][lInt]=0;
                            System.out.println("Se presiono el boton en posicion  "+kInt+"   "+lInt);


                            but.setText("O");
                            but.setBackground(Color.RED);

                            // ------------Enviar el mensaje TCP--------------

                            Socket s = null;

                            try {

                                // -------------------direccion IP del servidor------------------------
                                s = new Socket(URLcomunicacionTCP2, puertoMonstruos);
                                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                                DataInputStream in = new DataInputStream(s.getInputStream());

                                startTime=System.nanoTime();
                                out.writeUTF(kInt+"-"+lInt+"-"+counter+"-"+nombreUsuario);

                                String data = in.readUTF();
                                spentTime = System.nanoTime() - startTime;
                                //System.out.println("Received: " + data);
                                tiemposRespuesta.add(spentTime);

                                int sumaPuntos=Integer.parseInt(data);
                                //System.out.println("se le van a sumar"+sumaPuntos);
                                points=sumaPuntos;
                                if(points<9999){
                                    puntosLabel.setText("Puntos: "+points);
                                }else{
                                    puntosLabel.setText("Puntos: GANASTE");
                                }


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

    public void registro(){

        // ------------Enviar el mensaje TCP registro--------------

        Socket s = null;

        try {
            s = new Socket(URLcomunicacionTCP, serverPort);

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            out.writeUTF("Me quiero registrar-a-b-"+nombreUsuario);

            String data = in.readUTF();
            System.out.println("Received: " + data);

            String[] arrOfStr = data.split("-", 6);

            URLcomunicacionTCP2=arrOfStr[0];

            URLcomunicacionTopicos ="tcp://"+ URLcomunicacionTCP2 +":61616";

            puertoMonstruos=Integer.parseInt(arrOfStr[1]);
            canalMonstruos=arrOfStr[2];


            startGame();
            botonRegistro.setVisible(false);

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

    public long registroEstresamiento(){

        // ------------Enviar el mensaje TCP registro--------------

        Socket s = null;

        try {
            s = new Socket(URLcomunicacionTCP, serverPort);

            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            startTime=System.nanoTime();

            out.writeUTF("Me quiero registrar-a-b-"+nombreUsuario);

            String data = in.readUTF();
            spentTime = System.nanoTime() - startTime;
            System.out.println("Received: " + data);

            String[] arrOfStr = data.split("-", 6);

            URLcomunicacionTCP2=arrOfStr[0];

            URLcomunicacionTopicos ="tcp://"+ URLcomunicacionTCP2 +":61616";

            puertoMonstruos=Integer.parseInt(arrOfStr[1]);
            canalMonstruos=arrOfStr[2];






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

        return spentTime;
    }

    public void startGame() {
        escuchador = new EscuchadorDeMonstruos(URLcomunicacionTopicos,canalMonstruos);
        escuchador.addMonstruoListener(this);
        escuchador.start();
    }

    public void registroAutomatico(){
        botonRegistro.doClick();
    }

    public double promedioEstresamiento(){
        double suma=0;
        int contador=0;
        for (Long tiempo:tiemposRespuesta){
            if(tiempo==0){
                System.out.println("Error en tiempos de respuesta ");
            }else{
                suma+=tiempo;
                contador++;
            }
        }

        return suma/contador;
    }

    @Override
    public void onMonstruoReceived(int columna, int fila,int counter,String ganadorUltimoJuego, int numJuego) {

        grid[columna][fila]=counter;
        //System.out.println("Sale el monstruo "+counter+" en   "+columna+"   "+fila);
        botones[columna][fila].setText("X");
        botones[columna][fila].setBackground(Color.GREEN);

        //----------------Para el estresamiento-----------------
        if(juegoAutomatico)
            botones[columna][fila].doClick();



        if(this.numJuego!=numJuego){
            puntosLabel.setText("Puntos: 0");
        }
        this.numJuego=numJuego;
        positionLabel.setText("Juego: "+numJuego);
        puntajeLabel.setText("Ganador Juego "+(numJuego-1)+": "+ganadorUltimoJuego);

    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JuegoDelWakamole lay = new JuegoDelWakamole("Jorge");
                lay.createAndDisplayGUI();
            }
        });
    }
}