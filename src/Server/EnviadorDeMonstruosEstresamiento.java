
package Server;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;

public class EnviadorDeMonstruosEstresamiento {

    //Adonde envio los monstruos, a mi mismo, yo soy el server
    //private static final String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    public String URLcomunicacion="127.0.0.1";

    public String url ="tcp://"+ URLcomunicacion +":61616";

    private final int avgInterarrivalTime = 500; // in ms

    private static final String subject = "MONSTRUOS";

    public String[] ganadorUltimoJuego={"Nadie"};

    public boolean[] sigueElJuego=  {true};

    public boolean[] mandarMonstruos ={true};

    private int counter=1;

    public int[] numJuego={1};

    public boolean[] monstruosGolpeados = new boolean[2000];

    HashMap<String, Integer> jugadores = new HashMap<String, Integer>();

    public int pruebaEstresamiento=0;

    public EnviadorDeMonstruosEstresamiento(int pruebaEstresamiento){
        this.pruebaEstresamiento=pruebaEstresamiento;
    }

    public EnviadorDeMonstruosEstresamiento(){

    }

    public void enviaMonstruos() {
        MessageProducer messageProducer;
        TextMessage textMessage;
        Session session=null;
        Connection connection=null;
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

            while (mandarMonstruos[0]) {
                Random randomGenerator = new Random();
                int myRow = randomGenerator.nextInt(3);
                int myColumn = randomGenerator.nextInt(3);

                Topic topic = session.createTopic(subject);
                messageProducer = session.createProducer(topic);
                textMessage = session.createTextMessage();
                textMessage.setText(myColumn + " " + myRow + "-" + counter + "-" + ganadorUltimoJuego[0] + "-" + numJuego[0]); // random number from 1 to 10 where 10 represents the worst news
                counter++;
                long delay = (long) (avgInterarrivalTime * (-Math.log(Math.random()))); //  Arrival process is Poisson Distributed

                try {
                    if(pruebaEstresamiento==0){
                        System.out.println("Sending " + textMessage.getText());
                    }

                    messageProducer.send(textMessage);
                    Thread.sleep(delay);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageProducer.close();

                //Para estresador
                if(pruebaEstresamiento>0){

                if (counter >= pruebaEstresamiento) {
                    mandarMonstruos[0] = false;

                    Socket s = null;

                    try {
                        s = new Socket(URLcomunicacion, 49152);
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());
                        out.writeUTF("Ya termino- - - - -");

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

            }
        } catch (JMSException e) {
            e.printStackTrace();
        }finally{
            try{
                session.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
            try{
                connection.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean[] getMandarMonstruos(){
        return mandarMonstruos;
    }

    public void startListeningTCP() {

        new Thread(() -> {
            try {

                int serverPort = 49152;
                ServerSocket listenSocket = new ServerSocket(serverPort);
                while (mandarMonstruos[0]) {
                    System.out.println("Waiting for messages...");
                    Socket clientSocket = listenSocket.accept();  // Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.

                    ConnectionTCPsockets c = new ConnectionTCPsockets(clientSocket,jugadores,monstruosGolpeados,sigueElJuego,URLcomunicacion,subject,ganadorUltimoJuego,numJuego);
                    c.start();
                }

            } catch (IOException e) {
                System.out.println("Listen :" + e.getMessage());
            }

        }).start();
    }


    public static void main(String[] args) {
        EnviadorDeMonstruosEstresamiento sender = new EnviadorDeMonstruosEstresamiento();
       // sender.startListeningTCP();
        sender.enviaMonstruos();
    }

}


