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

## Les contrôleurs

Dans le modèle MVC, un contrôleur gère les **interactions** entre l'utilisateur et le système. Pour une application Web, les interactions correspondent aux **requêtes HTTP** émises par le navigateur client.

Les contrôleurs Spring Web MVC permettent donc de gérer les requêtes HTTP entrantes et notamment de gérer le **binding**, la **validation**, la **gestion** du modèle...

Les contrôleurs portent l'annotation ***@Controller***. Chaque méthode publique d'une telle classe peut être annotée avec ***@GetMapping***, ***@PutMapping***, ***@PostMapping***, ***@DeleteMappng***....

On utilise l'annotation ***@RequestMapping*** sur la classe pour donner des informations applicable sur l'ensemble des méthodes. Si on donne un chemin, alors ce dernier s'ajoute avant celui déclaré par une méthode.

Exemple : 

```java
@Controller
@RequestMapping(path="/admin")
public class ItemController {

    @PostMapping(path="/item")
    public String addItem() {
        // ...
        return "itemDetail";
    }

}
```

Ici, la méthode ***addItem*** traite les requêtes pour le chemin ***/admin/item***.

Ces méthodes retournent une ***String*** qui correspond à l'identifiant de la vue à afficher en réponse à l'utilisateur. La page HTML de la réponse va la plupart du temps être générée à la volée par un moteur de rendu comme JSP, Thymelead, Freemarker...

## Les vues

### JSP

***pom.xml*** :

```xml
<groupId>fr.leblanc</groupId>
<artifactId>monapplication</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>war</packaging>
```

```xml
<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>javax.servlet-api</artifactId>
  <version>3.0.1</version>
  <scope>provided</scope>
</dependency>

<dependency>
  <groupId>javax.servlet.jsp</groupId>
  <artifactId>jsp-api</artifactId>
  <version>2.2</version>
  <scope>provided</scope>
</dependency>

<dependency>
  <groupId>javax.servlet</groupId>
  <artifactId>jstl</artifactId>
  <version>1.2</version>
</dependency>
```

#### Avec Spring Boot

***application.properties*** :

```properties
spring.mvc.view.prefix = /WEB-INF/jsp/
spring.mvc.view.suffix = .jsp
```

#### Sans Spring Boot

```java
@EnableWebMvc
@Configuration
@ComponentScan
public class WebAppConfiguration implements WebMvcConfigurer {

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.jsp().prefix("/WEB-INF/jsp/").suffix(".jsp");
  }
}
```

Ainsi, dans notre exemple, le gestionnaire de vue ira chercher le fichier suivant : ***/WEB-INF/jsp/itemDetails.jsp***

#### Utilisation

Voici un exemple de fichier itemDetails.jsp :

```html
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
  <meta charset="UTF-8">
  </head>
  <body>
    Item details...
  </body>
</html>
```

### Thymeleaf

#### Avec Spring Boot

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

Par défaut, les modèles Thymeleaf doivent être placés dans ***src/main/resources/templates*** et les fichiers statiques dans ***src/main/resources/static***.

Il est possible de modifier ces paramètres avec les propriétés ***spring.thymeleaf.prefix*** et ***spring.web.resources.static-locations***.

#### Utilisation

En reprenant le contrôleur précédant :

```java
@Controller
@RequestMapping(path="/admin")
public class ItemController {

	@PostMapping(path="/item")
    public String addItem() {
        return "itemDetail";
    }
}
```

Voici un exemple de fichier ***src/main/resources/templates/itemDetails.html*** :

```html
<!DOCTYPE html>
<html>
  <head>
  <meta charset="UTF-8">
  </head>
  <body>
    Item details...
  </body>
</html>
```

## Le modèle

Dans un contexte MVC, le **modèle** correspond à l'ensemble des **données** qui sont nécessaires à la **construction** de la vue. Pour une application Web, il s'agit donc des données nécessaires à la construction de la page HTML de réponse. Le contrôleur a la charge de mettre à disposition ces données à la vue.

**Spring Web MVC** représente cette notion avec l'interface ***Model*** qui permet au contrôleur d'ajouter des attributs (des objets) au modèle en les associant à un nom unique. La vue pourra alors y accéder via leur nom.

Pour obtenir une instance de ***Model***, il suffit de l'ajouter comme paramètre à une méthode de contrôleur.

Exemple : nous souhaitons afficher l'heure courante du serveur dans une page HTML :

```java
@Controller
public class DateController {

  @GetMapping(path = "/date")
  public String afficherDate(Model model) {
    model.addAttribute("now", new Date());
    return "affichageDate";
  }

}

```

Puis une vue JSP :

```html
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
  </head>
  <body>
    <p><fmt:formatDate value="${now}" pattern="dd MMMM yyyy HH:mm:ss"/></p>
  </body>
</html>
```

Une vue Thymeleaf :

```html
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
  </head>
  <body>
    <p data-th-text="${#dates.format(now, 'dd MMMM yyyy HH:mm:ss')}">la date ici</p>
  </body>
</html>
```

## Une application multi-couches (N-Tiers)

Dans le développement d'applications Web, il est nécessaire de réaliser une application en couches. La plupart des applications utilisent un modèle de 3 couches :

- **la couche présentation** : l'ensemble des classes qui permettent d'assurer l'interaction avec l'utilisateur. Pour une application Web, on l'appelle aussi la couche Web. Les contrôleurs et les modèles de vue en font partie
- **la couche métier** (couche service) : l'ensemble des classes qui réalisent les fonctionnalités de l'application
- **la couche d'accès aux données** : l'ensemble des classes qui permettent d'interagir avec le système de données de l'entreprise

Ces couches permettent d'isoler plus facilement chaque partie de l'application. Les relations de dépendances entre ces couches doivent être respectées : la couche présentation dépend de la couche métier qui dépend elle-même de la couche de données.

Avec Spring, il existe les stéréotypes ***@Service*** et ***@Repository*** pour identifier les beans de la couche métier et ceux de la couche de données.

Exemple : 

```java
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/items")
    public String getAllItems(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "listeItems";
    }

}
```

On voit ici que la responsabilité du contrôleur est limitée au strict minimum. En effet, il s'adresse au service injecté pour récupérer les **données**, les fournit à la **vue** en les plaçant dans le modèle, et c'est lui qui fournit **l'identifiant** de la vue à afficher.

## La signature des méthodes de contrôleur

Il existe un grand nombre de signatures disponibles pour les méthodes d'un contrôleur gérant les requêtes HTTP : un large choix concernant le type, le nombre de paramètres et le type de la valeur de retour de la méthode.

### Les paramètres

A titre d'exemples :

- ***@RequestParam*** permet de récupérer la valeur d'un paramètre de la requête HTTP

  ```java
  @PostMapping("/item")
  public String addItem(@RequestParam String itemName) {
      // ...
      return "itemDetail";
  }
  ```

- ***@RequestHeader*** permet de récupérer la valeur d'un en-tête de la requête HTTP

  ```java
  @PostMapping("/item")
  public String addItem(@RequestHeader String host) {
      // ...
      return "itemDetail";
  }
  ```

- ***@PathVariable*** permet de récupérer une valeur dans le chemin de la ressource

  ```java
  @PostMapping("{subpath}/item")
  public String addItem(@PathVariable String subpath) {
      // ...
      return "itemDetail";
  }
  ```

- ***@RequestAttribute*** et ***@SessionAttribute*** permettent de récupérer des attributs de requête ou de session

  ```java
  @PostMapping("/item")
  public String addItem(@SessionAttribute Basket basket) {
      // ...
      return "itemDetail";
  }
  ```

Par défaut, les paramètres présents sont requis et s'ils sont absents, la méthode n'est appelée et une erreur 400 (Bad request) est entraînée. Il est cependant possible de les rendre optionnels en utilisant un ***Optional*** :

```java
@PostMapping("/item")
public String addItem(@SessionAttribute Optional<Basket> basket) {
    if (basket.isPresent()) {
        // ...
    }
    return "itemDetail";
}
```

Il est également possible d'attendre un objet Java présent dans le modèle grâce à l'annotation ***@ModelAttribute***. Si aucune instance n'existe, l'objet sera **automatiquement** instancié. De plus, les propriétés de cet objet seront préremplies avec la valeur des paramètres de la requête portant le même nom.

Exemple :

```java
public class Item {

    private String name;
    private String code;
    private int quantity;

    // Getters/setters omis

}
```

```java
@Controller
public class ItemController {

    @PostMapping("/item")
    public String addItem(@ModelAttribute Item item) {
        // ...
        return "itemDetail";
    }

}
```

Lors du traitement de la requête POST sur ***/item***, un objet de type ***Item*** sera créé avec propriétés renseignées avec la valeur des paramètres HTTP ***name***, ***code*** et ***quantity***.

=> L'annotation ***@ModelAtribute*** peut être omise car c'est l'interprétation par défaut d'un paramètre d'une méthode d'un contrôleur.

Enfin, Spring Web MVC reconnaît certains types particuliers pour les paramètres. C'est notamment le cas du type ***Model*** vu précédemment.

### La valeur de retour

Plusieurs types de retour peuvent être utilisés dans Spring Web MVC :

- ***String*** : cela permet au ***ViewResolver*** de déduire la vue qui doit être appelée
- ***void*** ou ***null*** : la méthode est supposée avoir correctement traité la requête et aucune vue ne sera appelée
- ***ModelAndView*** : l'objet est alors utilisé pour déduire l'identifiant de la vue et les données du modèle
- ***@ResponseBody*** : si la méthode possède cette annotation, cela signifie que l'objet retourné constitue la réponse. Il est possible d'utiliser un convertisseur pour le transformer, par exemple, en réponse JSON

## La gestion des formulaires avec Thymeleaf

**Thymeleaf** permet de créer un formulaire HTML lié à un objet présent dans le modèle. Chaque champ du formulaire sera initialisé avec la valeur d'une propriété de cet objet. Lorsque l'utilisateur soumettra les données du formulaire au serveur, champ champ du formulaire alimentera une propriété d'un objet du même type reçu par le contrôleur.

Cette opération qui consiste à remplir un objet Java via des données de formulaire s'appelle le ***binding***.

Exemple :

```java
public class Item {

    private String name;
    private String code;
    private int quantity;

    // Getters/setters omis

}
```

Voici le contrôleur associé :

```java
@Controller
public class ItemController {

    @GetMapping(path = "/item")
    public String displayForm(@ModelAttribute Item item) {
        // TODO initialiser le bean de formulaire si nécessaire
        return "itemForm";
    }

    @PostMapping(path = "/item")
    public String processForm(@ModelAttribute Item item) {
        // TODO traiter le formulaire
        return "successProcessItem";
    }
}

```

Voici la vue qui permet de saisir les informations d'un ***Item*** :

```html
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
</head>
<body>

<form action="#" method="post" data-th-action="@{/item}" data-th-object="${item}" accept-charset="utf-8">
  <p><label>Code : </label><input type="text" data-th-field="*{code}"></p>
  <p><label>Nom : </label><input type="text" data-th-field="*{name}"></p>
  <p><label>Quantité : </label><input type="number" data-th-field="*{quantity}"></p>
  <button type="submit">Envoyer</button>
</form>

</body>
</html>

```

Pour la méthode GET ci-dessus, l'exemple suivant est équivalent :

```java
@GetMapping(path = "/item")
public String displayForm(Model model) {
    Item item = new Item();
    model.addAttribute("item", item);
    return "itemForm";
}
```
