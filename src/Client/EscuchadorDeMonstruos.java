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
    private static final String subject = "MONSTRUOS";
    public int ultimaColumna=-1;
    public int ultimaFila=-1;
    public int coordenadasLeidas=0;
    public Character charColumna;
    public Character charFila;
    private final List<MonstruoListener> listeners = new ArrayList<>();

    public EscuchadorDeMonstruos(){

    }

    public EscuchadorDeMonstruos(String direccionAescuchar){
        url=direccionAescuchar;
    }

    public int getUltimaColumna(){
        return ultimaColumna;
    }

    public int getUltimaFila(){
        return ultimaFila;
    }

    public int getCoordenadasLeidas(){
        return coordenadasLeidas;

    }

    public void addMonstruoListener(MonstruoListener listener) {
        listeners.add(listener);
    }

    private void notifyMonstruoReceived(int columna, int fila) {
        for (MonstruoListener listener : listeners) {
            listener.onMonstruoReceived(columna, fila);
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
            boolean endFinancialSession = false;

            Session session = connection.createSession(false /*Transacter*/, Session.AUTO_ACKNOWLEDGE);


            Topic topic = session.createTopic(subject);
            System.out.println("I'm a escuchador de mosntruos " );
            messageConsumer = session.createConsumer(topic);
            connection.start();
            while (!endFinancialSession) {
                textMessage = (TextMessage) messageConsumer.receive();
                if (textMessage != null) {
                    if (!textMessage.getText().trim().equals("The End")) {
                        coordenadasLeidas++;
                        charColumna=textMessage.getText().charAt(0);
                        charFila=textMessage.getText().charAt(2);

                        ultimaColumna=Integer.parseInt(charColumna.toString());
                        ultimaFila=Integer.parseInt(charFila.toString());

                        //System.out.println(ultimaColumna+" "+ultimaFila);
                        notifyMonstruoReceived(ultimaColumna, ultimaFila);

                    }
                    if (textMessage.getText().trim().equals("The End")) {
                        endFinancialSession = true;
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
        new EscuchadorDeMonstruos().start();
    }

}