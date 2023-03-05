package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ConnectionTCPsockets extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;

    public ConnectionTCPsockets(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
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


