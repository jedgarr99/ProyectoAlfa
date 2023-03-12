# ¡Pégale al monstruo!
## _Sistemas Distribuidos: Proyecto Alpha_




"¡Pégale al monstruo!" es un juego multijugador que utiliza sockets TCP y tópicos para comunicarse entre el servidor y los clientes. El objetivo del juego es golpear al mayor número de monstruos antes que tus oponentes. El servidor ofrece un servicio de registro para que los jugadores puedan unirse al juego y proporciona la información necesaria para jugar, como la dirección IP. 


##### ¿De qué se trata el juego "¡Pégale al monstruo!"?

- El juego consiste en golpear al mayor número de monstruos posible y convertirse en el jugador que golpea N monstruos primero para ganar el juego.

##### ¿Cómo se comunican el servidor y los clientes?

- El servidor se comunica con los clientes mediante sockets TCP y tópicos. Los tópicos se utilizan para enviar mensajes de monstruos a los clientes y para informar a los jugadores quién ha ganado el juego.

##### ¿Cómo funciona la interfaz del cliente?

- La interfaz del cliente tiene una rejilla de objetos, precisamente botones. Estos botones se utilizan para golpear al monstruo.

##### ¿Cómo se realiza el registro de los jugadores?

- El registro de los jugadores se realiza mediante sockets TCP. El servidor ofrece un servicio de registro y, como respuesta al registro, el servidor proporciona al cliente toda la información necesaria para jugar, como la dirección IP.

##### ¿Pueden los jugadores entrar y salir del juego en cualquier momento?

- Sí, los jugadores pueden entrar y salir del juego en cualquier momento sin afectar la partida actual. Además, el contexto de los jugadores, incluyendo el número de monstruos golpeados, se conserva incluso si un jugador sale y vuelve a entrar.


## Pruebas de Estresamiento 
Para realizar estas pruebas se crearon 3 clases nuevas el estresador de registro, el estresador de juego y enviador de monstruos estresador. En el estresador de registro se implementó un método que instanciara de manera automática una cantidad N de usuarios registrándose. Además se implementaron métodos para simular el registro sin generar la interfaz gráfica. Para el estresador de juego se implementó un método que instanciara de manera automática una cantidad N de usuarios con su respectiva interfaz. También se generó un nuevo enviador de monstruos en donde le damos una cantidad M de monstruos y una vez que esa cantidad fue enviada el servidor se detiene automáticamente (a diferencia del enviador de monstruos original en donde el servidor no se detiene).
##### Pruebas de tiempo de respuesta en el registro
Las pruebas del tiempo de respuesta en el registro consistieron en tomar una cantidad de clientes y ejecutar el juego 10 veces para recuperar el tiempo promedio de respuesta en el registro y la desviación estándar. 
El tiempo de respuesta en el registro TCP empieza cuando el cliente envía un mensaje de solicitud de registro con su nombre de usuario y termina cuando recibe la respuesta del servidor de que recibió correctamente los datos del cliente.
Las pruebas se hicieron inicialmente para 50, 100, 200, 500 queríamos ir aumentando el número de clientes lentamente, sin embargo, notamos que el cambio era mínimo, casi nulo. Por esto, decidimos aumentar el número de clientes y realizamos las pruebas variando los clientes aumentandolos en 500 hasta llegar a 4500.
Como resultado encontramos que el promedio del tiempo de respuesta disminuía hasta alcanzar un equilibrio (ver Figura 1). 

<div style="text-align: center;">
  
![Grafica de promedio](https://github.com/jedgarr99/ProyectoAlfa/blob/main/GR%C3%81FICAS/Promedio%20de%20tiempo%20de%20respuesta%20por%20n%C3%BAmero%20de%20clientes.png). 
  
*Figura 1. Gráfica de tiempo de respuesta promedio*
  
 </div>

Con respecto a la desviación estándar pudimos notar que había una gran variabilidad en la cantidad de tiempo que el servidor tarda en responder a las solicitudes de registro de los clientes. Esto podría deberse a varios factores, como la carga del servidor. Las pruebas de estresamiento se realizaron de mnera continua una tras otra, sin embargo, notamos que si dejabamos descansar el servidor un par de minutos los tiempos de respuesta se recuperaban y disminuian. Esta variabilidad puede ser un problema porque el usuario podría recibir una respuesta de registro muy rápida en un momento dado ymuy lenta en otro lo cual generaría incertidumbre y desconfianza en el sistema lo cual significaría una experiencia negativa para el usuario.  
  
   
![Grafica de desviacion](https://github.com/jedgarr99/ProyectoAlfa/blob/main/GR%C3%81FICAS/Desviaci%C3%B3n%20est%C3%A1ndar%20del%20tiempo%20de%20respuesta%20por%20n%C3%BAmero%20de%20clientes.png?raw=true). 

*Figura 2. Gráfica de desviación estándar del tiempo de respuesta*
##### Pruebas de tiempo de respuesta en del juego
Las pruebas del tiempo de respuesta del juego consistieron en tomar una cantidad de clientes y para cada cantidad de clientes ejecutar 10 veces el juego para recuperar el tiempo promedio de respuesta en el juego y la desviación estándar. 
El tiempo de respuesta en el juego TCP empieza a medirse cuando el jugador golpea un monstruo. En ese momento envía un mensaje al servidor con las coordenadas del monstruo que golpeo. El servidor checa si fue el primero en golpear el monstruo y si lo fue le anota el punto al jugador y le responde con su número de puntos actualizado, si ganó el juego el servidor le envía un mensaje de victoria. Si el jugador no fue el primero en golpear el monstruo le regresa su número de puntos acumulado. El tiempo de respuesta termina cuando el jugador recibe la respuesta del servidor ya sea de victoria o de número de puntos acumulado.
Para estas pruebas decidimos aumentar el número de clientes lentamente aumentando la cantidad en desde 5 hasta llegar a 95. El tiempo de respuesta promedio fue aumentando mientras aumentábamos el número de clientes de manera similar a una raíz cuadrada, crecimiento conocido como modelo de crcimiento lento (ver figura 3). El crecimiento aumenta lentamente hasta llegar a un punto en donde se estabiliza.   
  



![Grafica de promedio](https://github.com/jedgarr99/ProyectoAlfa/blob/main/GR%C3%81FICAS/Promedio%20de%20tiempo%20de%20respuesta%20del%20juego%20por%20nu%CC%81mero%20de%20clientes.png). 
  
*Figura 3. Gráfica de tiempo de respuesta promedio del juego*

En cuanto a la desviación estándar el crecimiento fue casi lineal (ver figura 4). Esto quiere decir que mientras más se estrsa el servidor la calidad del servicio va disminuyendo. Por un lado porque el promedio de tiempo de respuesta es mayor y por el otro lado porque la variabilidad de la respuesta también aumenta, y como explicamos anteriormente, una variabilidad en el tiempo de respuesta afecta negativamente en la experiencia de usuario.
  
  

![Grafica de desviacion](https://github.com/jedgarr99/ProyectoAlfa/blob/main/GR%C3%81FICAS/Desviacio%CC%81n%20esta%CC%81ndar%20del%20tiempo%20de%20respuesta%20de%20juego%20por%20nu%CC%81mero%20de%20clientes.png). 
  
*Figura 4. Gráfica de desviación estándar del tiempo de respuesta del juego*

Una vez que llegamos a los 95 clientes comenzamos a tener fallas en el juego y había una cantidad de clientes que no podían ser atendidos, no obstante, esta cantidad era mínima. Continuamos realizando pruebas hasta llegar a los 160 clientes, aquí el servidor comenzaba a trabarse y el juego para todos los clientes se trababa por lo que consideramos este el límite de estresamiento de nuestro juego. 







