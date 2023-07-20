# JWT

## Introduction à JWT

**JWT** (**JSON Web Token**) est un standard permettant l'échangé sécurisé de jetons (tokens) entre plusieurs parties.

Chaque jeton JWT est consituté de 3 parties :

- un JSON header pour décrire le jeton
- un JSON payload pour représenter les informations embarquées dans le jeton
- une signature numérique permettant de vérifier l'intégrité et l'authenticité du jeton

Avec **Spring Security**, lorsqu'une requête est envoyée depuis le client vers le serveur, elle passe par un **JwtAuthFilter** qui va extraire le JWT Token de la requête. 

Si le token est absent, le filtre renvoie une erreur HTTP 403 "Missing JWT".

Le filtre va ensuite faire appel au **UserDetailsService** pour extraire l'utilisateur de la base de données. Si l'utilisateur n'existe pas, le serveur renvoie une erreur HTTP 403 "User does not exist".

Le filtre fait ensuite appel au **JwtService** pour valider le Token. S'il est invalide, le serveur renvoie une erreur HTTP 403 "Invalid JWT Token".

Le filtre fait ensuite appel au **SecurityContextHolder** qui va indiquer que l'utilisateur est authentifié pour le reste de la requête.

Puis la requête est transmise au **DispatcherServlet** pour exécuter le reste des traitements et retourner la réponse.

## Création d'une base de données PostgreSQL sur linux

Il faut d'abord intaller **PostgreSQL** (ici sur Linux) https://www.postgresqltutorial.com/postgresql-getting-started/install-postgresql-linux/

Puis se connecter avec l'utilisateur créé par défaut par PostgreSQL :

```
sudo -i -u postgres
```

Puis lancer la commande pour utiliser l'invite de commande Postgresql :

```
psql
```

Création d'une base de données : 

```
CREATE DATABASE security;
```

Pour lister les bases de données présentes :

```
\l
```

Pour se connecter à notre base de données :

```
\c security
```

Pour avoir les infos de connection (notamment le port utilisé par la base) :

```
\conninfo
```

```
sudo netstat -plunt |grep postgres
```

Résultat :

```
You are connected to database "security" as user "postgres" via socket in "/var/run/postgresql" at port "5432".
```

```
tcp        0      0 127.0.0.1:5432          0.0.0.0:*               LISTEN      33039/postgres
```

Pour changer le mot de passe :

```
\password
```

## Création d'une application sécurisée avec JWT

Créer une application Spring Boot avec les dépendances :

- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Spring Security

Dans le fichier application.properties :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/security
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgre
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database=postgresql
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Il faut ensuite créer une classe **User** qui implémente l'interface **UserDetails** de **Spring Security**.

Cette classe sera une entité **JPA**.

Il faut ensuite créer un **UserRepository** pour notre classe User.

Il faut ensuite créer le **JwtAuthenticationFilter** et le **JwtService**;

Il faut ensuite créer les beans **SecurityConfiguration**, **UserDetailsService**, **AuthenticationProvider**, **PasswordEncoder**, **AuthenticationService**, **AuthenticationManager**, et **AuthenticationController**.

Lorsqu'on lance l'application, on peut d'abord lancer une requête GET :

```
http://localhost:8080/api/v1/demo-controller
```

Et constater qu'une erreur 403 (unauthorized) apparaît.

Il faut donc d'abord créer le compte avec la requête POST :

```
http://localhost:8080/api/v1/auth/register
```

et le corps de requête :

```json
{
    "firstname": "valentin",
    "lastname": "leblanc",
    "email": "valentin.leblanc53@gmail.com",
    "password": "1234"
}
```

Pour obtenir notre token d'authentification :

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YWxlbnRpbi5sZWJsYW5jNTNAZ21haWwuY29tIiwiaWF0IjoxNjg4MDcyMzkxLCJleHAiOjE2ODgwNzM4MzF9.tTweJwUDxd9x1Ge6jd3HAO9yNHk5QC6VPIf8jSFDKOw"
}
```

Puis de ré-exécuter la requête GET avec le Header :

```properties
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YWxlbnRpbi5sZWJsYW5jNTNAZ21haWwuY29tIiwiaWF0IjoxNjg4MDcxODkwLCJleHAiOjE2ODgwNzMzMzB9.nmDvfV2iBKzuw1B-X8lKEDV8_O830H36VJ-BVbmD1Ts
```

Pour obtenir le résultat :

```
Hello from secure endpoint
```

# OAuth

## Introduction

**OAuth** est un standard créé pour permettre à un <u>service d'accéder à d'autres services</u> au nom d'un utilisateur donné.

**OAuth** est fait pour **l'autorisation**, pas pour l'authentification, et pour **l'autorisation de services**, pas pour l'autorisation de personnes physiques.

## Analogie

Une analogie connue de OAuth est le concept de **voiturier**.

Lorsqu'une personne confie sa voiture à un voiturier, elle lui donne une clé différence de la sienne : une clé avec un accès restreint. Cette clé permet d'allumer et d'éteindre la voiture, mais elle ne permet pas d'ouvrir le coffre ou la boite à gant, par exemple.

Le **propriétaire** utilise alors deux <u>services</u> : la **voiture** et le **voiturier**.

Le voiturier a besoin d'un accès direct à la voiture pour faire son travail, le propriétaire lui donne alors une clé avec un accès restreint.

## Le flux OAuth

Premièrement : le service tiers **demande au service principal** des informations concernant un utilisateur.

Puis, le service principal **demande à l'utilisateur** une confirmation de cette demande.

Si elle est acceptée, le service principal **donne un Token** d'autorisation avec accès restreint au service tiers.

Ce Token est au format **JWT**.

## Terminologies

Nous utiliserons l'exemple suivant :

Un service Web d'impression/livraison de photos numériques souhaite accéder à certaines photos du Google Drive d'un utilisateur.

### Ressource

C'est l'élément auquel un service tiers souhaite accéder (les photos).

### Propriétaire de ressource

Celui qui a accès aux ressources (l'utilisateur Google Drive) et qui peut donner l'accès au service tiers (le service d'impression).

### Serveur de ressource

C'est le serveur qui héberge les ressources (Google Drive).

### Client

L'application qui a besoin d'un accès aux ressources (le service d'impression).

### Serveur d'autorisation

C'est le serveur qui fournit des Token d'accès au client.

## Flux code d'autorisation

- L'utilisateur est connecté au Client et lui demande d'accéder aux ressources du serveur de ressource
- Le client demande au serveur d'autorisation un accès aux ressources
- Le serveur d'autorisation demande au propriétaire de ressource si **ce** Client peut accéder à **ces** ressources
- Le propriétaire confirme la demande
- Le serveur d'autorisation donne un **Token d'autorisation** au Client
- Le Client contacte le serveur autorisation avec le Token d'autorisation et lui demande un **Token d'accès**
- Le client contacte le serveur de ressource avec le Token d'accès

## Flux implicite (moins sécurisé)

- L'utilisateur est connecté au Client et lui demande d'accéder aux ressources du serveur de ressource
- Le client demande au serveur d'autorisation un accès aux ressources
- Le serveur d'autorisation demande au propriétaire de ressource si **ce** Client peut accéder à **ces** ressources
- Le propriétaire confirme la demande
- Le serveur d'autorisation donne un **Token d'accès** au Client
- Le client contacte le serveur de ressource avec le Token d'accès

## Flux d'informations d'identification du client (pour microservices)

Imaginons que le Microservice 1 fasse une requête au Microservice 2 qui lui-même est en contact avec une base de données.

Le Microservice 2 travaille de pair avec un serveur d'autorisation.

- MS1 fait un appel au serveur d'autorisation avec un ID client
- MS2 donne un Token d'accès restreint
- MS1 fait un appel à MS2 avec le Token
- MS2 vérifie l'autorisation

## Conclusion

- OAtuh permet l'autorisation, pas l'authentification
- Il permet à un service d'accéder à des ressources d'un autre service
- Plusieurs flux sont possibles
