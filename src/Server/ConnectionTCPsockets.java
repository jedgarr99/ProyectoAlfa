package Server;

import org.apache.activemq.ActiveMQConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionTCPsockets extends Thread {

    private static final int puertoConexionesTCP=49152;
    private DataInputStream in;
    private Socket clientSocket;
    private final static int PUNTOS_PARA_GANAR=5;
    public int[] numJuego;
    public String[] ganadorUltimoJuego;
    private final String url ;
    private final String subject ;
    public HashMap<String, Integer> jugadores;
    public boolean[] monstruosGolpeados;
    public boolean[] sigueElJuego;

    private DataOutputStream out;
    public ConnectionTCPsockets(Socket aClientSocket,HashMap<String, Integer> jugadores,boolean[] monstruosGolpeados,boolean[] sigueElJuego,String url, String subject,String[] ganadorUltimoJuego ,int[]numJuego){
        this.jugadores=jugadores;
        this.monstruosGolpeados=monstruosGolpeados;
        this.sigueElJuego=sigueElJuego;
        this.url=url;
        this.subject=subject;
        this.ganadorUltimoJuego=ganadorUltimoJuego;
        this.numJuego=numJuego;

        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public synchronized void updateJugadores(String jugador, int puntos ) {
        jugadores.put(jugador,puntos);
    }

    public synchronized void updateGanador(String jugador ) {
        ganadorUltimoJuego[0]=jugador;
    }

    public synchronized void updateNumeroJuego() {
        numJuego[0]++;
    }

    public synchronized void resetearPuntos() {
        for ( String key : jugadores.keySet() ) {
            jugadores.put(key,0);
        }
    }

    @Override
    public void run() {
        try {

            String data = in.readUTF();

            System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress()+" "+data);

            String[] arrOfStr = data.split("-", 4);
            String usuario = arrOfStr[3];

            if(data.charAt(0)=='M'){

                //Añadir Synchronicity

                if(!jugadores.containsKey(usuario)){
                    updateJugadores(usuario,0);
                    System.out.println("Jugador nuevo registrado: "+usuario);
                    out.writeUTF(url+"-"+puertoConexionesTCP+"-"+subject);
                }

            }else{

                if(jugadores.containsKey(usuario)){

                    int counter = Integer.parseInt(arrOfStr[2]);

                    if(!monstruosGolpeados[counter-1]){
                        int puntos= jugadores.get(usuario);
                        puntos++;
                        updateJugadores(usuario,puntos);
                        //jugadores.put(usuario,puntos);
                        System.out.println(usuario+" ahora tiene  "+puntos+"  puntos");
                        monstruosGolpeados[counter-1]=true;

                        if(puntos>=PUNTOS_PARA_GANAR){
                            System.out.println("Nuevo Juego");

                            updateGanador(usuario);
                            updateNumeroJuego();
                            resetearPuntos();
                        }

                    }
                }else{
                    System.out.println("Jugador no registrado ");
                }
            }

        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}


