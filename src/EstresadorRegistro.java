import Client.JuegoDelWakamole;
import Server.EnviadorDeMonstruosEstresamiento;

public class EstresadorRegistro {

    private static double mean(long[] list) {
        double sum = 0.0;

        for (int i = 0; i < list.length; i++)
            sum += list[i];

        return sum/list.length;
    }

    private static double stdDev(long[] list) {
        double sum = 0.0;
        double num = 0.0;
        for (int i = 0; i < list.length; i++) sum += list[i];
        double mean = sum / list.length;
        for (int i = 0; i < list.length; i++)
            num += Math.pow((list[i] - mean), 2);
        return Math.sqrt(num / list.length);
    }

    public static void main(String[] args) throws InterruptedException {

        int numeroClientes=4000;
        long[] tiemposRespuesta = new long[numeroClientes];


        Thread arrancarServidor=new Thread(() -> {

            EnviadorDeMonstruosEstresamiento sender = new EnviadorDeMonstruosEstresamiento(30);

            sender.startListeningTCP();
            sender.enviaMonstruos();



        });
        Thread arrancarClientes=new Thread(() -> {
       for (int i=0; i<numeroClientes; i++){
           int finalI = i;


                   JuegoDelWakamole lay = new JuegoDelWakamole(("Cliente "+ finalI),true);
                   //lay.createAndDisplayGUI();
                   tiemposRespuesta[finalI]=lay.registroEstresamiento();

       }
        });

        arrancarServidor.start();
        arrancarClientes.start();
        arrancarClientes.join();


        System.out.println();
        System.out.println("---------------------------------------");
        //System.out.println("Promedio de tiempo de respuesta de registro para "+numeroClientes+" clientes");
        System.out.println(mean(tiemposRespuesta)+"-"+stdDev(tiemposRespuesta));
        System.out.println();
        System.out.println();
        System.out.println();
        System.exit(1);


    }
}
