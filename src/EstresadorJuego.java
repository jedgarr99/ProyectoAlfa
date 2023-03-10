import Client.JuegoDelWakamole;
import Server.EnviadorDeMonstruosEstresamiento;


public class EstresadorJuego {

    private static double mean(double[] list) {
        double sum = 0.0;

        for (int i = 0; i < list.length; i++)
            sum += list[i];

        return sum/list.length;
    }
    private static double stdDev(double[] list) {
        double sum = 0.0;
        double num = 0.0;
        for (int i = 0; i < list.length; i++) sum += list[i];
        double mean = sum / list.length;
        for (int i = 0; i < list.length; i++)
            num += Math.pow((list[i] - mean), 2);
        return Math.sqrt(num / list.length);
    }

    public static void main(String[] args) throws InterruptedException {

        int numeroClientes=20;
        int numeroDeMonstruos=20;

        double[] tiemposRespuesta = new double[numeroClientes];
        final boolean[] acabo;
        JuegoDelWakamole[] juegos =new JuegoDelWakamole[numeroClientes];

        Thread arrancarServidor=new Thread(() -> {

            EnviadorDeMonstruosEstresamiento sender = new EnviadorDeMonstruosEstresamiento(numeroDeMonstruos);
            sender.startListeningTCP();
            sender.enviaMonstruos();



        });
        Thread arrancarClientes=new Thread(() -> {
            for (int i=0; i<numeroClientes; i++){
                int finalI = i;


                juegos[finalI] = new JuegoDelWakamole(("Cliente "+ finalI),true);
                juegos[finalI] .createAndDisplayGUI();
                juegos[finalI] .registroAutomatico();
                //tiemposRespuesta[finalI]=lay.registroEstresamiento();

            }
        });

        arrancarServidor.start();
        arrancarClientes.start();
        arrancarServidor.join();

        int i=0;
        for(JuegoDelWakamole juego : juegos){
           tiemposRespuesta[i]=juego.promedioEstresamiento();
           i++;
        }
        System.out.print("");
        System.out.println();
        System.out.println("-------------------");
        System.out.println(mean(tiemposRespuesta)+"-"+stdDev(tiemposRespuesta));
        System.exit(1);




    }




}
