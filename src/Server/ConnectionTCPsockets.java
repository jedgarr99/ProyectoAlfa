package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionTCPsockets extends Thread {
    private DataInputStream in;
   // private DataOutputStream out;
    private Socket clientSocket;


    public HashMap<String, Integer> jugadores;

    public boolean[] monstruosGolpeados;



    public ConnectionTCPsockets(Socket aClientSocket,HashMap<String, Integer> jugadores,boolean[] monstruosGolpeados){
        this.jugadores=jugadores;
        this.monstruosGolpeados=monstruosGolpeados;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
           // out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // an echo server
            String data = in.readUTF();         // recibo solicitud

            System.out.println("Message received from: " + clientSocket.getRemoteSocketAddress()+" "+data);

            String jugadorQueEnvia=(" "+clientSocket.getRemoteSocketAddress()).substring(2,11);

            //Añadir Synchronicity
            if(!jugadores.containsKey(jugadorQueEnvia)){
                jugadores.put(jugadorQueEnvia,0);
                System.out.println("Jugador nuevo registrado:"+jugadorQueEnvia);
            }


            String[] arrOfStr = data.split("-", 3);

            int  myColumn = Integer.parseInt(arrOfStr[0]);
            int myRow = Integer.parseInt(arrOfStr[1]);
            int counter = Integer.parseInt(arrOfStr[2]);
            System.out.println("Ya fue golpeado el monstruo "+counter);

            if(!monstruosGolpeados[counter-1]){
                int puntos= jugadores.get(jugadorQueEnvia);
                puntos++;
                jugadores.put(jugadorQueEnvia,puntos);
                System.out.println("Nuevo puntaje para "+jugadorQueEnvia+" "+puntos);
                monstruosGolpeados[counter-1]=true;
            }



            /*
            int[] estaCoordenada={myColumn,myColumn};
            //-----------corregir que sea la misma coordenada y concurrencia--------------
            if(coordenadasEnviadas.contains(estaCoordenada)){
                coordenadasEnviadas.remove(estaCoordenada);
                int puntos= jugadores.get(jugadorQueEnvia);
                puntos++;
                jugadores.put(jugadorQueEnvia,puntos);
                System.out.println("Nuevo puntaje para "+jugadorQueEnvia+" "+puntos);

            }else {
                System.out.println("coordenada no esta ");

                            /*Iterator itr = coordenadasEnviadas.iterator();

                            // check element is present or not. if not loop will
                            // break.
                            while (itr.hasNext()) {
                                System.out.println(itr.next());
                            }
                   }
                             */


            //out.writeUTF(data);                // envio respuesta

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


