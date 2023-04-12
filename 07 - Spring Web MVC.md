# Introduction

**Spring Web MVC** est le module de Spring consacré au développement d'applications Web et d'API Web.

Son nom renvoie directement au modèle d'architecture **MVC** (Modèle Vue Contrôleur), même s'il n'est pas vraiment utilisé dans le cadre du développement d'API Web.

# Le modèle MVC

Il s'agit d'un modèle d'architecture permettant de guider la conception d'applications qui nécessitent une interaction de l'utilisateur avec le système. 

Il définit trois grandes catégories de responsabilité :

- <u>Le modèle</u> : l'ensemble des classes qui définissent les **données** applicatives échangées entre l'utilisateur et le système
- <u>La vue</u> : l'ensemble des classes qui gèrent la **représentation graphique** des données et l'interface utilisateur
- <u>Le contrôleur</u> : l'ensemble des classes qui gèrent les **interactions** de l'utilisateur et la **mise à jour** des vues après la modification des données. Elles assurent la **cohérence** entre le modèle et la vue

Un utilisateur interagit donc avec un contrôleur. En particulier, dans une application Web, cette **interaction** est définie par une **requête HTTP** envoyée au serveur qui est prise en charge par un contrôleur.

# Intégration de Spring Web MVC

Chaque **application Spring** embarque son propre **conteneur** avec les services dont elle a **besoin**. En particulier, une application **Spring Web MVC** n'a pas besoin d'un serveur d'application Java EE complet pour s'exécuter. Elle peut le faire dans un **conteneur Web** plus léger comme Tomcat ou Jetty qui offre le service minimal nécessaire, à savoir le lancement d'un **serveur HTTP** et la possibilité de déléguer le **traitement des requêtes** au code de l'application.

## Avec Spring Boot

Il faut ajouter une dépendance au module ***spring-boot-starter-web*** :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Il est conseillé d'ajouter la dépendance ***spring-boot-devtools*** également qui permet est pratique pour la phase de développement (notamment le redémarrage à chaud lors d'une modification du code).

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-devtools</artifactId>
  <scope>runtime</scope>
  <optional>true</optional>
</dependency>
```

Lorsque la dépendance ***spring-boot-starter-web*** est présente, Spring Boot lance automatiquement un serveur HTTP (par défaut Tomcat) au démarrage de l'application et déployer l'application dedans. Chaque application dispose donc de son **propre serveur** embarqué.

Le serveur est configurable via les nombreux **paramètres** fournis par Spring Boot dans le fichier ***application.properties***.

Par exemple, le paramètre ***server.port*** permet de définir le port d'écoute du serveur (par défaut 8080).

Exemple :

```properties
server.port = 9090
```

## Sans Spring Boot

Une application Spring Web MVC est censée être déployée dans un conteneur Web Java EE. Il faut donc générer une application War.

```xml
<groupId>fr.leblanc</groupId>
<artifactId>monapplication</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>war</packaging>
```

Il faut également ajouter les dépendances vers l'API Servlet ainsi qu'au module ***spring-mvc*** :

```xml
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>javax.servlet-api</artifactId>
  <version>3.0.1</version>
  <scope>provided</scope>
</dependency>

<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-webmvc</artifactId>
  <version>5.3.1</version>
</dependency>
```

Puis l'initialisation de la Servlet de l'application :

```java
public class MainWebAppInitializer implements WebApplicationInitializer {
  @Override
  public void onStartup(final ServletContext sc) throws ServletException {
    // Chargement du contexte d'application
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.register(WebAppConfiguration.class);

    // Création de la servlet
    DispatcherServlet servlet = new DispatcherServlet(context);
    ServletRegistration.Dynamic registration = sc.addServlet("app", servlet);
    registration.setLoadOnStartup(1);
    registration.addMapping("/");
  }
}
```

Il faut également fournir une classe de configuration du contexte d'application :

```java
@EnableWebMvc
@Configuration
@ComponentScan
public class WebAppConfiguration implements WebMvcConfigurer {
}
```

## Encodage des paramètres de requête

L'encodage des paramètres d'une requête envoyée au serveur est important et peut être source d'erreur fréquente.

Il est conseillé de munir ses formulaires de l'attribut ***accept-charset*** sur la balise ***<form>*** :

```html
<form action="..." method="post" accept-charset="utf-8">
  <input type="text" name="nom">
  <input type="submit">Envoyer</input>
</form>
```

Côté serveur, l'encodage pour traiter les requêtes doit également être défini, ceci peut être fait via le fichier de propriétés :

```properties
server.tomcat.uri-encoding = iso-8859-1
```

Ou bien sans Spring Boot :

```java
public class MainWebAppInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext sc) throws ServletException {
    // Chargement du contexte d'application
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.register(WebAppConfiguration.class);

    // Création de la servlet
    DispatcherServlet servlet = new DispatcherServlet(context);
    ServletRegistration.Dynamic registration = sc.addServlet("app", servlet);
    registration.setLoadOnStartup(1);
    registration.addMapping("/");

    // Ajout du filtre UTF-8 pour les paramètres des requêtes
    sc.addFilter("characterEncoding", new CharacterEncodingFilter("UTF-8"))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

  }
}
```

# Les applications Web avec Spring Web MVC
