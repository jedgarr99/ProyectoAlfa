
package Server;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.Random;
import java.util.HashMap;
import java.net.*;
import java.io.*;

public class EnviadorDeMonstruos {

    //Adonde envio los monstruos, a mi mismo, yo soy el server
    //private static final String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    public String URLcomunicacion="127.0.0.1";

    public String url ="tcp://"+ URLcomunicacion +":61616";

    private final int avgInterarrivalTime = 3000; // in ms

    private static final String subject = "MONSTRUOS";

    public String[] ganadorUltimoJuego={"Nadie"};

    public boolean[] sigueElJuego=  {true};

    private int counter=1;

    public int[] numJuego={1};

    public boolean[] monstruosGolpeados = new boolean[1000];

    HashMap<String, Integer> jugadores = new HashMap<String, Integer>();

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

            while (true) {
                Random randomGenerator = new Random();
                int myRow = randomGenerator.nextInt(3);
                int myColumn = randomGenerator.nextInt(3);

                Topic topic = session.createTopic(subject);
                messageProducer = session.createProducer(topic);
                textMessage = session.createTextMessage();
                textMessage.setText(myColumn + " " + myRow+"-"+counter+"-"+ganadorUltimoJuego[0]+"-"+numJuego[0]); // random number from 1 to 10 where 10 represents the worst news
                counter++;
                long delay = (long) (avgInterarrivalTime * (-Math.log(Math.random()))); //  Arrival process is Poisson Distributed

                try {
                    System.out.println("Sending " + textMessage.getText());
                    messageProducer.send(textMessage);
                    Thread.sleep(delay);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageProducer.close();
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

    public void startListeningTCP() {

        new Thread(() -> {
            try {
                int serverPort = 49152;
                ServerSocket listenSocket = new ServerSocket(serverPort);
                while (true) {
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
        EnviadorDeMonstruos sender = new EnviadorDeMonstruos();
        sender.startListeningTCP();
        sender.enviaMonstruos();
    }

}


