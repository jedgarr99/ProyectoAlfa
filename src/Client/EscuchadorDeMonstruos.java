package Client;

import Server.MonstruoListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.ArrayList;
import java.util.List;


public class EscuchadorDeMonstruos extends Thread {

    // URL of the JMS server
    private static  String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    // default broker URL is : tcp://localhost:61616"
    private final String subject;
    public int ultimaColumna=-1;
    public int ultimaFila=-1;
    //public int coordenadasLeidas=0;
    public Character charColumna;
    public Character charFila;
    private final List<MonstruoListener> listeners = new ArrayList<>();





    /*public EscuchadorDeMonstruos(){

    }

     */

    public EscuchadorDeMonstruos(String direccionAescuchar,String canalAescuchar){
        url=direccionAescuchar;
        subject=canalAescuchar;
    }



    public void addMonstruoListener(MonstruoListener listener) {
        listeners.add(listener);
    }



    private void notifyMonstruoReceived(int columna, int fila, int counter, String ganadorUltimoJuego,int numJuego) {
        for (MonstruoListener listener : listeners) {
            listener.onMonstruoReceived(columna, fila,counter,ganadorUltimoJuego,numJuego);
        }
    }

    @Override
    public void run() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            MessageConsumer messageConsumer;
            TextMessage textMessage;

            //arreglar el fiiin ------------------------------
            boolean endFinancialSession = false;

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);


            Topic topic = session.createTopic(subject);
            System.out.println("I'm a escuchador de mosntruos " );
            messageConsumer = session.createConsumer(topic);
            connection.start();

            while (!endFinancialSession) {
                textMessage = (TextMessage) messageConsumer.receive();
                if (textMessage != null) {
                    if (!textMessage.getText().trim().equals("Alguien ya gano")) {
                        //coordenadasLeidas++;
                        charColumna=textMessage.getText().charAt(0);
                        charFila=textMessage.getText().charAt(2);

                        String[] arrOfStr = textMessage.getText().split("-", 4);

                        int  counter = Integer.parseInt(arrOfStr[1]);

                        String ganadorUltimoJuego=arrOfStr[2];

                        int numJuego=  Integer.parseInt( arrOfStr[3]);

                        ultimaColumna=Integer.parseInt(charColumna.toString());
                        ultimaFila=Integer.parseInt(charFila.toString());

                        //System.out.println(ultimaColumna+" "+ultimaFila);
                        notifyMonstruoReceived(ultimaColumna, ultimaFila,counter,ganadorUltimoJuego,numJuego );

                    }
                    if (textMessage.getText().trim().equals("Alguien ya gano")) {
                        endFinancialSession = true;
                        System.out.println("Alguien ya gano");
                    }
                }

            }
            messageConsumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //new EscuchadorDeMonstruos().start();
    }

}