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




