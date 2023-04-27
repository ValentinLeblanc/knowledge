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

## Externalisation des messages

L'externalisation des messages consiste à créer dans un fichier externe un mapping entre une clé et la valeur d'un message.

Ceci a 2 avantages :

- cela permet de ne pas écrire en dur dans le code les messages et donc de rendre leur utilisation dynamique via un fichier de propriétés
- cela facilite l'internalisation (I18N) d'une application

Ceci est réalisable via la classe ***ResourceBundle*** de l'API standard de Java.

Par exemple, nous pouvons avoir un fichier de propriétés ***messages.properties*** suivant :

```properties
welcome.title = Bienvenue dans l'application
welcome.text = Cette application est disponible en plusieurs langues.
```

Ainsi qu'un fichier spécifique pour l'anglais ***messages_en.properties*** :

```properties
welcome.title = Welcome in this application
welcome.text = This application is available for different languages.
```

Il faut placer ces fichiers dans le classpath et le tour est joué (***src/main/resources***).

Nous pouvons alors utiliser ces messages comme ceci :

```java
ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
String title = bundle.getString("welcome.title");
String message = bundle.getString("welcome.text");

System.out.println(title);
System.out.println(message);
```

Avec **Spring Web MVC**, le ***ResourceBundle*** est créé automatiquement, c'est la raison pour laquelle l'appel à ***getBundle*** renvoie quelque chose.

Par défaut, Spring utilise les fichiers placés dans le dossier ***src/main/resources*** et qui commencent par ***messages*** (ex : *messages.properties*, *messages_it.properties*, *messages_en.properties*...)

### Avec Spring Boot

Il est possible de modifier le préfixe des fichiers de messages avec la propriété suivante :

```properties
spring.messages.basename=keys
```

### Sans Spring Boot

Il suffit d'ajouter un bean de type ***MessageSource*** dans le contexte d'application :

```java
@Bean
public MessageSource messageSource() {
  ResourceBundleMessageSource source = new ResourceBundleMessageSource();
  source.setBasename("messages");
  return source;
}
```

## Validation des paramètres d'une requête

Spring Web MVC permet de valider les données envoyées au serveur et éventuellement d'afficher des messages d'erreur dans les vues.

### Le binding

Le binding est le mécanisme qui consiste à mettre à jour l'état d'un objet avec les données saisies par l'utilisateur. Lors de cette opération, un autre mécanisme entre en jeu : la validation (avec Spring Web MVC). Spring fournit la classe ***BindingResult*** qui stocke le résultat du binding d'un objet et ses erreur éventuelles. Dans la méthode de contrôleur, cet objet doit être placé **après** l'objet qui représente les données.

Exemple :

```java
@Controller
public class ItemController {

  @GetMapping("/item")
  public String displayForm(@ModelAttribute Item item) {
    return "itemForm";
  }

  @PostMapping("/item")
  public String processForm(@ModelAttribute Item item, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "itemForm";
    }
    // ...
    return "successProcessItem";
  }
}
```

### Validation dans la méthode du contrôleur

La manière la plus directe est de faire la validation est de le faire directement dans la méthode du contrôleur.

```java
@PostMapping("/item")
public String processForm(@ModelAttribute Item item, BindingResult bindingResult) {
  ValidationUtils.rejectIfEmpty(bindingResult, "name", "empty");
  ValidationUtils.rejectIfEmpty(bindingResult, "code", "empty");
  if (item.getQuantity() <= 0) {
    bindingResult.rejectValue("quantity", "invalid");
  }
  if (bindingResult.hasErrors()) {
    return "itemForm";
  }
  // ...
  return "successProcessItem";
}
```

La classe ***ValidationUtils*** permet d'ajouter des codes d'erreur dans l'objet ***bindingResult***. (exemple de code d'erreur : "empty.item.code", "invalid.item.quantity")

### Création d'un validateur

Il est mieux de déléguer la validation d'une requête à classe dédiée à cet effet implémentant l'interface ***Validator***.

Exemple précédent repris :

```java
@Component
public class ItemValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return Item.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    Item item = (Item) target;
    ValidationUtils.rejectIfEmpty(errors, "name", "empty");
    ValidationUtils.rejectIfEmpty(errors, "code", "empty");
    if (item.getQuantity() <= 0) {
      errors.rejectValue("quantity", "invalid");
    }
  }

}
```

Et voici le nouveau code du contrôleur :

```java
@Controller
public class ItemController {

  @GetMapping("/item")
  public String displayForm(@ModelAttribute Item item) {
    return "itemForm";
  }

  @PostMapping("/item")
  public String processForm(@Validated @ModelAttribute Item item,
                            BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "itemForm";
    }
    // ...
    return "successProcessItem";
  }
}
```

On voit qu'il faut ajouter l'annotation ***@Validated*** sur le paramètre ***item*** de la méthode POST pour que la validation ait lieu.

### Validation déclarative avec Bean Validation

Il existe une API standard Java pour réaliser une validation déclarative : ***Bean Validation***.

Spring intègre automatiquement ce standard s'il trouve une implémentation de cette API au lancement de l'application.

Avec Spring Boot :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Sans Spring Boot :

```xml
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>5.4.2.Final</version>
</dependency>
```

Bean Validation repose sur une famille d'annotations qui sont positionnées sur les attributs d'un bean pour indiquer les contraintes à respecter.

Exemple :

```java
public class Item {

  @NotBlank(message = "Le nom ne peut pas être vide !")
  private String name;

  @NotBlank(message = "Le code ne peut pas être vide !")
  private String code;

  @Min(value = 1, message = "La quantité doit être positive !")
  private int quantity;

  // Getters/setters omis

}
```

Et le code du contrôleur :

```java
@Controller
public class ItemController {

  @GetMapping("/item")
  public String displayForm(@ModelAttribute Item item) {
    return "itemForm";
  }

  @PostMapping("/item")
  public String processForm(@Validated @ModelAttribute Item item,
                            BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "itemForm";
    }
    // ...
    return "successProcessItem";
  }
}
```

Même chose ici, il faut ajouter l'annotation ***@Validated*** sur le paramètre désiré.

## POST-redirect-GET

Lorsque l'utilisateur envoie une requête de type POST au serveur, il signifie qu'il souhaite apporter des modifications à l'état des données du serveur. Si cette même requête est émise deux fois, la norme HTTP ne garantit pas qu'il n'y aura pas des effets de bord sur le serveur. 

De plus, lorsqu'une page obtenue par une méthode POST est actualisée sur un navigateur Web, alors la réquête est émise une seconde fois.

Ainsi, pour éviter ce genre de problème, il est conseillé d'utiliser le principe du **POST/redirect/GET** qui consiste à produire une réponse HTTP de redirection (avec le statut 302 ou 303) pour rediriger le navigateur vers une nouvelle adresse et lui faire "oublier" la requête POST.

Un contrôleur Spring Web MVC peut déclencher automatiquement une réponse de redirection en préfixant la chaine de caractères retournée par ***redirect:*** . Il y a également la notion **d'attributs Flash** : ils sont stockés en session jusqu'à la prochaine requête de l'utilisateur. Pour les utiliser, il faut ajouter en paramètre de la méthode du contrôleur un argument de type ***RedirectAttributes***. L'attribut flash sera automatiquement ajouté dans le modèle.

Exemple :

```java
@Controller
public class IndexController {

    @GetMapping(path="/")
    public String home(RedirectAttributes redirectAttributes) {
        Item item = new Item();
        item.setCode("BV-34");
        item.setName("Mon item");
        redirectAttributes.addFlashAttribute("item", item);
        return "redirect:/autre-page";
    }

    @GetMapping(path="/autre-page")
    public String redirectHome(@ModelAttribute Item item) {
        // Le paramètre item correspond à l'instance ajoutée comme attribut flash
        return "view";
    }
}

```

## La gestion des exceptions

Lorsqu'un contrôleur échoue dans le traitement d'une requête et lève une exception, le serveur retourne par défaut une réponse HTTP 500 (erreur interne). Cependant, il est possible d'annoter une exception avec ***@ResponseStatus*** pour signifier que le code de la réponse doit être différent (ainsi que le message d'erreur). Ceci est fait grâce à la classe ***DefaultHandlerExceptionResolver***.

Exemple :

```java
 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Order")  // 404
 public class OrderNotFoundException extends RuntimeException {
     // ...
 }
```

Et le contrôleur :

```java
 @RequestMapping(value="/orders/{id}", method=GET)
 public String showOrder(@PathVariable("id") long id, Model model) {
     Order order = orderRepository.findOrderById(id);

     if (order == null) throw new OrderNotFoundException(id);

     model.addAttribute(order);
     return "orderDetail";
 }
```

Il est également possible de prendre en charge n'importe quel type d'exception dans le contrôleur avec l'annotation ***@ExceptionHandler*** et de retourner un identifiant de vue spécifique à l'erreur :

```java
@Controller
public class ItemController {

    @ExceptionHandler(ItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleItemException(ItemException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "itemError";
    }

    @PostMapping("/item")
    public String processForm(@ModelAttribute Item item) throws ItemException {
        if (item.getQuantity() == 0) {
            throw new ItemException("Item not available");
        }
        // ...
        return "successProcessItem";
    }
```

## Méthodes de modèle @ModelAttribute

Il est possible d'ajouter des éléments dans le modèle quel que soit la requête émise vers un contrôleur. Ainsi, ces éléments seront disponibles dans la vue. Il faut pour cela ajouter l'annotation ***@ModelAttribute*** à une méthode, et elle sera appelée avant la méthode de traitement de la requête, et l'objet qu'elle retourne sera ajouté au modèle.

Exemple :

```java
@Controller
@RequestMapping(path="/item/{code}")
public class ItemEditController {

        @ModelAttribute
        public Item getItem(@PathVariable String code) {
                Item item = new Item();
                item.setCode(code);

                // ...

                return item;
        }

        @GetMapping
        public String viewItem(@ModelAttribute Item item) {

                // ...

                return "showItem";
        }
}
```

L'instance ***Item*** créée par la méthode ***getItem()*** est ajoutée au modèle et peut être récupérée dans la méthode ***viewItem()*** via l'annotation ***@ModelAttribute*** sur le paramètre ***item***. 

## @ControllerAdvice

Il est possible de centraliser des méthodes annotées avec ***@ExceptionHandler***, ***@ModelAttribute*** et ***@InitBinder*** dans une classe annotée avec ***@ControllerAdvice*** pour qu'elles s'appliquent à un ensemble de contrôleurs sans avoir besoin de les dupliquer.

Exemple :

```java
@ControllerAdvice("fr.leblanc")
public class ItemControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @ExceptionHandler(ItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleItemException(ItemException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "itemError";
    }
}
```

Ici, tous les contrôleurs déclarés dans le package "fr.leblanc" bénéficieront automatiquement de la méthode de binding et de la méthode de gestion d'exeption.

# Les API Web avec Spring Web MVC

Une API Web est une application qui fonctionne de la même façon qu'un site Web traditionnel, la différence se situe principalement sur le format des données échangées entre le serveur et le client. Pour un site Web, il s'agit principalement de contenu au format HTML, alors qu'une API Web échange du contenu **JSON** ou **XML**, de façon à ce que le client puisse directement traiter les données.

## Configuration

Pour une application **avec** Spring Boot, aucune configuration n'est à prévoir pour le format de représentation des données échangées car tout est fait par défaut.

Pour une application **sans** Spring Boot, il est nécessaire d'ajouter les dépendances suivantes pour la production de réponses au formats JSON et XML :

Pour JSON (sans Spring boot) :

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.4</version>
</dependency>
```

Pour XML (sans Spring boot) :

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.9.4</version>
</dependency>
```

## L'annotation @RestController

Cette annotation permet de signaler qu'un contrôleur est spécialisé pour le développement d'une API Web. Il regroupe ***@Controller*** et ***@ResponseBody***. Il s'agit donc d'un contrôleur dont les méthodes retournent par défaut les données à envoyer au client plutôt qu'un identifiant de vue.

Prenons un exemple avec une classe java ***Item*** simple pour représenter les données du modèle :

```java
public class Item {

    private String name;

    private String code;

    private int quantity;

    // Getters/setters omis

}
```

Nous pouvons alors déclarer un contrôleur permettant de d'obtenir une représentation JSON d'une instance d'***Item*** :

```java
@RestController
@RequestMapping("/api")
public class ItemController {

    @GetMapping(path="/item", produces= "application/json")
    public Item getItem() {
        Item item = new Item();
        item.setCode("XV-32");
        item.setName("Weird stuff");
        item.setQuantity(10);
        return item;
    }

}
```

L'attribut ***produces*** permet de signifier à Spring Web MVC qu'il doit convertir l'instance d'***Item*** au format JSON avant de l'envoyer au client.

C'est la bibliothèque **Jackson** qui réalise la sérialisation au format JSON de l'objet Java.

Si nous interrogeons notre API localement, voici le résultat obtenu :

```json
curl http://localhost:8080/myapp/api/item

{"name":"Weird stuff","code":"XV-32","quantity":10}
```

## La négociation de contenu

HTTP permet la négociation de contenu proactive : un client peut envoyer ses préférences au serveur lors d'une requête. Par exemple, un client peut envoyer le format dans lequel il souhaite recevoir les données du serveur.

Ceci est fait via l'en-tête ***Accept*** de la requête HTTP.

Un contrôleur peut donc produire plusieurs formats différents pour une même requête, cela se traduit par l'attribut ***produces*** :

```java
@RestController
@RequestMapping("/api")
public class ItemController {

    @GetMapping(path="/item", produces= {"application/json", "application/xml"})
    public Item getItem() {
        Item item = new Item();
        item.setCode("XV-32");
        item.setName("Weird stuff");
        item.setQuantity(10);
        return item;
    }

}
```

Par défaut, c'est le format **JSON** qui sera privilégié car c'est le premier de la liste. Cependant, si un client émet la requête suivante :

```
curl -H "Accept: application/xml" http://localhost:8080/myapp/api/item
```

Alors la réponse sera :

```xml
<Item><name>Weird stuff</name><code>XV-32</code><quantity>10</quantity></Item>
```

## L'envoi de données

Pour envoyer des données au serveur via un corps de requête, il faut utiliser l'attribut ***consumes*** ainsi que l'annotation ***@RequestBody*** :

```java
@RestController
@RequestMapping("/api")
public class ItemController {

    @PostMapping(path="/items", consumes="application/json")
    @ResponseStatus(code=HttpStatus.CREATED)
    public void createItem(@RequestBody Item item) {
        // ...
    }

}
```

Nous pouvons alors envoyer la requête suivante à notre serveur local :

```
curl -H "Content-type: application/json" -d '{"name":"mon item","code":"1337","quantity":1}' http://localhost:8080/myapp/api/items
```

## La réponse

Par défaut, la réponse envoyée par un contrôleur Web API est 200 si la méthode retourne un objet et 204 (No Content) si la méthode retourne void.

Il est possible de modifier le code de la réponse avec ***@ResponseStatus*** :

```java
@PostMapping(path="/items", consumes={"application/json", "application/xml"})
@ResponseStatus(code=HttpStatus.CREATED)
public void createItem(@RequestBody Item item) {
    // ...
}
```

Il est possible de contrôler plus finement le contenu de la réponse avec le type ***ResponseEntity<T>*** :

```java
@RestController
@RequestMapping("/api")
public class ItemController {

    @PostMapping(path="/items", consumes="application/json", produces="application/json")
    public ResponseEntity<Item> createItem(@RequestBody Item item,
                                           UriComponentsBuilder uriBuilder) {

        // ...

        URI uri = uriBuilder.path("/api/items/{code}").buildAndExpand(item.getCode()).toUri();
        return ResponseEntity.created(uri).body(item);
    }

}
```

Ici, on crée un objet ***ResponseEntity*** qui aura le code 201 (Created) et dont l'en-tête ***location*** sera le lien vers la ressource créée. C'est pour cela que l'on crée un objet ***URI*** avec le lien de notre Item.



Si nous envoyons la requête suivante au serveur :

```
curl -i -H "Content-type: application/json" -d '{"name":"mon item","code":"1337","quantity":1}' http://localhost:8080/myapp/api/items
```

Alors la réponse ressemblera à ça :

```json
HTTP/1.1 201
Location: http://localhost:8080/myapp/api/items/1337
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Tue, 06 Mar 2018 10:00:00 GMT

{"name":"mon item","code":"1337","quantity":1}
```

## *@RestControllerAdvice*

Cette annotation est composée de ***@ControllerAdvice*** et de ***@ResponseBody***. Elle permet de réutiliser les méthodes annotées avec ***@ExceptionHandler***, ***@InitBinder*** et ***@ModelAttribute***. 

Elle permet en plus de sérialiser la réponse des méthodes de gestion des exceptions.

## Les annotations Jackson

Pour tester la sérialisation d'un objet avec **Jackson**, on peut utiliser la classe ***ObjectMapper*** ou ***XmlMapper*** :

```java
Object obj = new Item();

ObjectMapper objectMapper = new ObjectMapper();
System.out.println(objectMapper.writeValueAsString(obj));
```

Voici un exemple d'utilisation des annotations **Jackson** sur notre classe ***Item*** :

```java
@JsonRootName("item")
@JsonPropertyOrder({"quantite", "nom"})
public class Item {

    @JsonProperty("nom")
    private String name;
	
    @JsonIgnore
    private String code;

    private int quantity;

    // Getters/setters omis

}
```

La sérialisation JSON donnera alors : 

```json
{"quantity":1,"nom":"Weird stuff"}
```

## Implémentation d'un client : RestTemplate

La classe ***RestTemplate*** permet d'effectuer des requêtes HTTP, tout en effectuant les conversions JSON/XML => Java et Java => JSON/XML.

Exemple :

```java
public class WebApiClient {

    public static void main(String[] args) throws Exception {
        RestTemplate client = new RestTemplate();
        URI uri = new URI("http://localhost:8080/myapp/api/items");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Content-type", "application/json");

        Item item = new Item();
        item.setCode("1337");
        item.setName("weird stuff");
        item.setQuantity(1);

        HttpEntity<Item> entity = new HttpEntity<Item>(item, requestHeaders);
        ResponseEntity<Item> responseEntity = client.postForEntity(uri, entity, Item.class);

        System.out.println(responseEntity.getHeaders().getLocation());
        Item itemResultat = responseEntity.getBody();
        System.out.println(itemResultat.getCode());
    }

}
```
