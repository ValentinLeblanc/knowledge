# Spring Boot et Docker

Source : https://spring.io/guides/topicals/spring-boot-docker/

Une application Spring Boot est facilement convertible en fichier JAR exécutable. Par exemple, on peut utiliser la commande Maven ***mvn clean install*** pour packager l'application en un fichier JAR.

Un fichier ***Dockerfile*** basique permettant de lancer ce fichier JAR ressemblerait à ça :

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Il est possible de passer l'argument JAR_FILE via la commande :

```
docker build --build-arg JAR_FILE=target/*.jar -t myorg/myapp .
```

Ou alors directement le passer en dur dans le Dockerfile :

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Puis le build de l'image : 

```
docker build -t myorg/myapp .
```

Enfin, pour lancer le conteneur : 

```
docker run -p 8080:8080 myorg/myapp
```

## Accéder à un conteneur qui tourne

```
docker exec -ti myapp /bin/sh
```

## Le point d'entrée

Il est possible de spécifier un script en tant que point d'entrée d'exécution :

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY run.sh .
COPY target/*.jar app.jar
ENTRYPOINT ["run.sh"]
```

Avec le fichier ***run.sh*** :

```sh
#!/bin/sh
exec java -jar /app.jar
```
