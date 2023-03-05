package Server;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;

import java.net.*;
import java.io.*;

public class EnviadorDeMonstruos {

    // URL of the JMS server
    private static final String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    // default broker URL is : tcp://localhost:61616"

    private final int avgInterarrivalTime = 3000; // in ms

    private static final String subject = "MONSTRUOS";

    public void enviaMonstruos() {
        MessageProducer messageProducer;
        TextMessage textMessage;
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);

            while (true) {
                Random randomGenerator = new Random();
                int myRow = randomGenerator.nextInt(3);
                int myColumn = randomGenerator.nextInt(3);

                Topic topic = session.createTopic(subject);
                messageProducer = session.createProducer(topic);
                textMessage = session.createTextMessage();
                textMessage.setText(myColumn + " " + myRow); // random number from 1 to 10 where 10 represents the worst news

                long delay = (long) (avgInterarrivalTime * (-Math.log(Math.random()))); //  Arrival process is Poisson Distributed
                try {
                    System.out.println("Sending " + textMessage.getText());
                    messageProducer.send(textMessage);
                    Thread.sleep(delay);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Mensaje Final
                /*
                if ((i + 1) == maxNumberOfNews) {
                    for (int j = 0; j < newsTypes.length; j++) {
                        Topic innerTopic = session.createTopic(newsTypes[j]);
                        messageProducer = session.createProducer(innerTopic);
                        textMessage = session.createTextMessage();
                        textMessage.setText("The End");
                        System.out.println("Notifying the end of the financial session to " + newsTypes[j] + " Floor Brokers");
                        messageProducer.send(textMessage);
                    }
                }
                 */


                messageProducer.close();
            }
            //session.close();
            //connection.close();


        } catch (JMSException e) {
            e.printStackTrace();
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
                ConnectionTCPsockets c = new ConnectionTCPsockets(clientSocket);
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



