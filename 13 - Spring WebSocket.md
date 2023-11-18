# Introduction

Le **protocole** WebSocket est un **standard** permettant d'établir un **canal de communication** **bi-directionnel** entre un client et un serveur à partir d'une seule connexion **TCP**. Il s'agit d'un protocole TCP différent d'HTTP, bien qu'il ait été conçu pour compléter HTTP en utilisant les 80 et 443, et permettant la réutilisation des règles de pare-feu existantes.

Une interaction WebSocket démarre avec une requête HTTP utilisant le **Header upgrade**, permettant ainsi de switcher sur le protocole WebSocket.

Exemple :

```yaml
GET /spring-websocket-portfolio/portfolio HTTP/1.1
Host: localhost:8080
Upgrade: websocket 
Connection: Upgrade 
Sec-WebSocket-Key: Uc9l9TMkWGbHFD2qnFHltg==
Sec-WebSocket-Protocol: v10.stomp, v11.stomp
Sec-WebSocket-Version: 13
Origin: http://localhost:8080
```

En guise de réponse, le serveur renvoie :

```yaml
HTTP/1.1 101 Switching Protocols 
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: 1qVdfYHU9hPOl4JYYNXF623Gzn0=
Sec-WebSocket-Protocol: v10.stomp
```

Après cette "poignée de main" effectuée, le Socket TCP de la requête HTTP reste ouvert pour le client et pour le serveur, leur permettant d'envoyer et de recevoir des messages.

Le client et le serveur peuvent également s'accorder pour utiliser un protocole de communication de plus haut niveau basé sur le WebSocket : le protocole **STOMP**.

# WebSocket API

Spring Framework fournit une API permettant de gérer la communication WebSocket entre un serveur et un client.

Il faut d'abord définir une classe Handler permettant de réagir à un message reçu :

## Handler

```java
public class MyHandler extends TextWebSocketHandler {

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		// ...
	}

}
```

Puis de l'enregistrer via une classe de configuration :

## Configuration

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myHandler(), "/myHandler");
	}

	@Bean
	public WebSocketHandler myHandler() {
		return new MyHandler();
	}

}
```

Il est possible de configurer le WebSocket plus précisément :

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(8192);
		container.setMaxBinaryMessageBufferSize(8192);
		return container;
	}

}
```

## Allowed Origins

Par défaut, le comportement WebSocket est de n'accepter que les requêtes ayant la même origine.

Il est alors possible d'ajouter des domaines autorisés :

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myHandler(), "/myHandler").setAllowedOrigins("https://mydomain.com");
	}

	@Bean
	public WebSocketHandler myHandler() {
		return new MyHandler();
	}

}
```

# SockJS

SockJS est une **librairie JavaScript** permettant d'émuler une connexion WebSocket entre un **navigateur** et un **serveur** lorsque cette connexion n'est pas tout le temps possible (en raison de contraintes techniques, de sécurité, de proxy, de compatibilité...) et donc de maintenir un semblant de connexion WebSocket, entre ces 2 parties.

## Activation de SockJS

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myHandler(), "/myHandler").withSockJS();
	}

	@Bean
	public WebSocketHandler myHandler() {
		return new MyHandler();
	}

}
```

## SockJsClient

Il est possible pour un client Java de se connecter à des endpoints SockJS sans utiliser de navigateur :

```java
List<Transport> transports = new ArrayList<>(2);
transports.add(new WebSocketTransport(new StandardWebSocketClient()));
transports.add(new RestTemplateXhrTransport());

SockJsClient sockJsClient = new SockJsClient(transports);
sockJsClient.doHandshake(new MyWebSocketHandler(), "ws://example.com:8080/sockjs");
```

