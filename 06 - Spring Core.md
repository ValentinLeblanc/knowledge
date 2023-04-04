# Introduction

Spring est un **framework** largement utilisé dans la communauté Java. Il accélère le **développement** d'applications d'entreprise. Il se présente comme une **alternative** au modèle d'architecture proposé par la plate-forme **J2EE** du début des années 2000.

J2EE est initialement basé sur des serveurs d'applications qui déploient et gèrent des composants (**Servlet**, **EJB**...) fournis par les développeurs au sein de conteneurs logiciels, et qui doivent être conformes à une spécification technique pour qu'ils puissent être pris en charge par le serveur. Les services tels que la **sécurité**, l'accès aux **bases de données**, la gestion des **transactions**... sont gérés par le serveur d'application. Le développeur J2EE doit donc connaître les API nécessaire à la conformité de ses composants, qui sont complexes. Ainsi, la **virtualisation**, le **cloud computing** et les architectures **microservices** tendent à mettre en inadéquation J2EE et sa plate-forme avec les problématiques actuelles.

**Spring Framework** propose de bâtir des applications qui **embarque** elle-même les **services** dont elle a besoin. Il offre des solutions déjà existantes aux problématiques techniques évoquées et il est possible de s'affranchir des services non nécessaires.

L'idée centrale du Spring Framework est de n'imposer aucune norme de développement ni aucune contrainte technique sur la façon de coder. Il se veut non-intrusif tout en se basant sur le principe de <u>l'inversion de contrôle</u> (**IoC**) et sur la <u>programmation orientée aspect</u> (**AOP**). Il met en œuvre des **Design Patterns** pour fournir un environnement le plus souple possible.

# L'inversion de contrôle

L'inversion de contrôle est un **patron d'architecture** qui fonctionne selon le principe que le **flot d'exécution** d'un logiciel n'est plus sous le **contrôle** direct de l'application mais du **framework** ou de la couche logicielle sous-jacente. Un framework de ce type fournit une ossature, une charpente à mon application sur laquelle va reposer le code spécifique du développeur. 

Traditionnellement, chaque objet est responsable de la **création** et de la **gestion** de ses propres **dépendances**, ce qui peut entraîner un fort **couplage** entre les objets et rendre le code difficile à **maintenir** et à faire **évoluer**. Les objets sont également plus difficilement **testables** individuellement.

Avec l'inversion de contrôle, cette responsabilité est **inversée** et  déléguée à un **conteneur IoC**.

Spring se base sur **l'injection de dépendances** pour mettre en place l'inversion de contrôle.

## L'injection de dépendance

Il s'agit d'un mécanisme dans la programmation objet qui permet de réduire le **couplage** entre plusieurs objets. Il consiste à faire en sorte qu'un objet **reçoive** une **instance** d'une autre objet dont il dépend plutôt que de le construire directement. L'objet en question est alors instancié dans une autre partie du programme. 

Cela permet de :

- considérer une abstraction ou une interface puisqu'aucun constructeur n'est appelé
- construire l'objet en question avec les paramètres adéquats et au bon endroit
- partager la même instance de l'objet à travers toute l'application
- bien illustrer la dépendance via un *constructeur* ou via un *setter* (et pas de façon erratique dans le code)

## Le conteneur IoC

Pour pouvoir être mise en pratique, l'inversion de contrôle implique l'existence d'un composant supplémentaire : le **conteneur IoC**.

C'est lui qui est responsable de la création des instances et des injections de dépendances, il gère le cycle de vie d'un ensemble d'objets. Il est constitué d'un ou de plusieurs **contextes d'application**.

# Le contexte d'application

Un contexte d'application est un **composant** qui contient la **définition** des **objets** que le conteneur IoC doit créer ainsi que leurs **interdépendances**.

## Création du contexte et des beans

L'API Spring Framework définit l'interface ***ApplicationContext*** pour laquelle plusieurs implémentations existent et donc plusieurs façons de définir un contexte d'application également. L'une d'entre elles est la classe ***AnnotationConfigApplicationContext*** qui permet d'utiliser des **annotations** sur les classes de l'application pour indiquer au conteneur IoC comment définir les objets.

Il est possible de créer une instance d'un objet (un **bean**) et de l'enregistrer dans le contexte d'application via l'annotation ***@Bean***.

Exemple :

```java
import java.time.LocalTime;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class TimeApplication {

  @Bean
  public LocalTime maintenant() {
    return LocalTime.now();
  }

  public static void main(String[] args) {
    try (AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext(TimeApplication.class)) {
      LocalTime time = appCtx.getBean("maintenant", LocalTime.class);
      System.out.println(time);
    }
  }
}
```

Spring va ici créer une instance de la classe ***TimeApplication***, puis il va chercher des annotations particulières sur cet objet. La méthode ***maintenant()*** possède l'annotation ***@Bean*** : Spring va donc l'appeler et l'objet ***LocalTime*** qu'elle retourne sera placé dans le contexte d'application sous le nom ***maintenant***.

Il est possible de donner plusieurs noms à un même **bean** : 

```java
@Bean(name = {"maintenant", "now", "ahora", "jetzt"})
public LocalTime getLocalTime() {
  return LocalTime.now();
}
```

## Portée (scope) d'un bean

Les beans ajoutés au contexte d'application ont une portée. Par défaut, Spring en définit deux :

- **singleton** (par défaut) : une seule instance de ce bean existe dans le conteneur IoC
- **prototype** : à chaque appel du bean, une nouvelle instance est retournée

La portée est changée via l'annotation ***@Scope***.

Exemple :

```java
  @Bean
  @Scope("prototype")
  public LocalTime maintenant() {
    return LocalTime.now();
  }
```

## Injection de beans

Il est possible d'injecter un bean dans une méthode.

Exemple :

```java
public class TaskApplication {

  @Bean
  public Supplier<String> dataSupplier() {
    return new HardcodedSupplier();
  }

  @Bean
  public Runnable task(Supplier<String> dataSupplier) {
    return new WriterService(dataSupplier);
  }

  public static void main(String[] args) throws InterruptedException {
    try (AnnotationConfigApplicationContext appCtx
                  = new AnnotationConfigApplicationContext(TaskApplication.class)) {
      appCtx.getBean(Runnable.class).run();
    }
  }
}
```

Ici, la méthode ***<u>task</u>*** fabrique un bean car elle possède l'annotation **@Bean**. Elle attend un paramètre en argument, ce qui signifie que Spring va devoir trouver dans le contexte d'application un bean ayant pour nom "dataSupplier" et qui est compatible avec le type. C'est la méthode ***dataSupplier*** qui fournit ce bean.

Ce type d'**injection** permet de garantir un niveau d'**abstraction** important entre les différents objets.

Le Spring Framework est capable de **déduire** l'ordre d'appel des différentes méthodes de fabrique en analysant le **graphe** des **dépendances**.

## Méthodes d'initialisation et de destruction

Le conteneur IoC de Spring permet de gérer le cycle de vie des beans. Il permet notamment d'invoquer des méthodes d'initialisation et de destruction de ces beans. Cela se fait via l'annotation ***@Bean*** et les attributs ***initMethod*** et ***destroyMethod***.

Exemple :

```java
  @Bean(initMethod = "readData")
  public Supplier<String> dataSupplier() {
    return new FileDataSupplier();
  }
```

# Déclaration par annotations

L'annotation ***@Bean*** permet d'ajouter des objets dans un contexte d'application sans être intrusif dans le code des classes. En effet, dans l'exemple précédent, on injecte via le constructeur d'un objet un autre objet sans modifier leurs classes.

Pour les classes que nous développons spécifiquement pour l'application il est possible d'utiliser d'autres annotations, plus **intrusives**.

## @Autowired

Cette annotation active l'injection automatique de dépendance. Elle peut être placée sur un **constructeur**, un **setter** ou directement sur un **attribut**.

Exemple :

```java
public class WriterService implements Runnable {

  @Autowired
  private Supplier<String> supplier;

  @Override
  public void run() {
    System.out.println(supplier.get());
  }

}
```

Nous n'avons plus besoin du constructeur. Spring est capable d'**injecter** le bean correspondant (à condition qu'il n'en existe qu'un seul).

La classe principale de l'application devient alors : 

```java
public class TaskApplication {

  @Bean
  public Supplier<String> dataSupplier() {
    return new HardcodedSupplier();
  }

  @Bean
  public Runnable task() {
    return new WriterService();
  }

  public static void main(String[] args) throws InterruptedException {
    try (AnnotationConfigApplicationContext appCtx =
                  new AnnotationConfigApplicationContext(TaskApplication.class)) {
      appCtx.getBean(Runnable.class).run();
        
}

```

Il est possible de définir une **dépendance optionnelle** avec ***@Autowired(required = false)***.

## @Primary / @Qualifier

Pour gérer automatiquement l'injection de dépendances, Spring se base sur le **type** du bean. Il va cherche un bean dans le contexte d'application qui est du type demandé ou qui implémente le type demandé.

Si plusieurs beans sont compatibles, il choisit celui qui porte le même nom que l'attribut. Le nom d'un bean est définit soit par le nom de sa méthode de fabrique, soit par l'attribut ***name*** de l'annotation ***@Bean***.

Si plusieurs beans sont compatibles mais qu'aucun d'eux ne porte le bon nom, l'exception ***UnsatisfiedDependencyException*** est levée.

L'annotation ***@Primary*** permet d'indiquer qu'un bean devra être sélectionné en priorité en cas d'ambiguïté.

L'annotation ***@Qualifier*** permet de préciser le nom du bean à injecter.

Exemple :

```java
public class TaskManager {

  @Autowired
  @Qualifier("tache")
  private Runnable runnable;

  public void executer() {
    runnable.run();
  }

}
```

## @Value

Cette annotation est utilisable sur un **attribut** ou sur un **paramètre** de type **primitif** ou **String**.

Elle donne la valeur par défaut à injecter.

Exemple :

```java
public class ValueSupplier implements Supplier<String> {

  @Value("hello world")
  private String value;

  @Override
  public String get() {
    return value;
  }

}
```

Cette annotation devient très utilise pour charger des données depuis un fichier de configuration en utilisant le langage d'expression **SpEL**.

## Détection automatique de composants (component scan)

Spring peut également rechercher dans les packages les classes à instancier sous forme de beans dans le contexte d'application.

Ceci se fait grâce à l'annotation ***@ComponentScan*** placée sur la classe qui est passée en paramètre de création du contexte d'application.

Le framework va scruter toutes les classes qui sont présentes dans les sous-packages de cette classes et créer un bean pour celles qui ont l'annotation ***@Component***.

Exemple :

```java
@ComponentScan
public class TaskApplication {
  public static void main(String[] args) throws InterruptedException {
    try (AnnotationConfigApplicationContext appCtx =
                  new AnnotationConfigApplicationContext(TaskApplication.class)) {
      appCtx.getBean(Runnable.class).run();
    }
  }
}
```

```java
@Component
public class HardcodedSupplier implements Supplier<String> {
  @Override
  public String get() {
    return "Hello world";
  }
}
```

Si plusieurs constructeurs sont présents dans une classe **component**, il faut utiliser l'annotation ***@Autowired*** pour indiquer à Spring celui qu'il doit utiliser.

Si le constructeur a plusieurs paramètres, Spring va essayer de résoudre les dépendances pour chaque paramètre. Il est possible d'ajouter les annotations ***@Value*** et ***@Qualifier*** pour chaque paramètre.

## Stéréotypes de composants

- ***@Component*** : stéréotype générique pour indiquer à Spring d'instancier un bean

- ***@Service*** : composant qui remplit une fonctionnalité centrale dans l'application
- ***@Repository*** : composant référentiel qui représente un mécanisme permettant de stocker et de rechercher une collection d'objets
- ***@Configuration*** : composant permettant de configurer le contexte d'application (en général, il contient des ***@Bean***)
- ***@Controller*** / ***@RestController*** : composant qui joue le rôle d'un contrôleur dans une architecture MVC pour une application Web / API Web

### Cas particulier de *@Configuration*

Considérons l'exemple suivant :

```java
@Configuration
public class TimeConfiguration {

  @Bean
  public LocalTime startTime() {
    return LocalTime.now();
  }

  @Bean
  public LocalTime endTime() {
     return startTime().plusMinutes(1);
  }
}
```

Ici, sans l'annotation ***@Configuration***, l'appel à ***startTime()*** fait dans la méthode de fabrique ***endTime()*** devrait créer une nouvelle instance de ***LocalTime***. Or, avec ***@Configuration***, c'est bien le bean ***startTime*** qui est retourné. 

## @Order

Lorsque plusieurs beans sont du même type, il est possible d'ordonner leur instanciation avec ***@Order***.

Exemple :

```java
@Component
@Order(1)
public class TacheDebut implements Runnable {

  @Override
  public void run() {
    System.out.println("je suis la tâche de début");
  }
}
```

```java
@Component
@Order(2)
public class TacheIntermediaire implements Runnable {

  @Override
  public void run() {
    System.out.println("je suis la tâche intermédiaire");
  }
}
```

```java
@Component
@Order(3)
public class TacheFin implements Runnable {

  @Override
  public void run() {
    System.out.println("je suis la tâche de fin");
  }
}
```

Ceci est particulièrement utile lors de la mise en place de **filtres de traitement** d'une requête HTTP.

## @DependsOn

Il est parfois nécessaire d'indiquer explicitement la dépendance d'un bean pour un autre bean quand cette dépendance existe implicitement. Ceci est fait via l'annotation ***@DependsOn***.

# Configuration d'une application

Le fait de dépendre d'un IoC container offre un avantage conséquent à notre application : elle devient facilement **configurable**.

En effet, Spring va pouvoir injecter des valeurs extraites des fichiers de configuration.

## Lancement d'une application Spring Boot

Spring Boot fournit notamment une classe nommée ***SpringApplication*** qui permet de bénéficier des mécanismes d'**auto-configuration**.

Voici un exemple de lancement d'une application Spring Boot :

```java
@SpringBootApplication
public class MyApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyApplication.class, args);
  }

}
```

## Spring Boot et le fichier application.properties

Ce fichier, situé dans ***src/main/resources*** (pour un projet Maven), permet de paramétrer le comportement par défaut de l'application. Selon les dépendances déclarées dans notre projet et les valeurs des propriétés dans ce fichier, Spring Boot va adapter la création du contexte d'application.

Pour plus d'informations sur les propriétés disponibles : https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

## Injection de propriétés avec @Value

Nous pouvons injecter des propriétés définies dans le fichier ***application.properties*** via l'annotation ***@Value***.

Exemple :

```properties
database.uri = jdbc:mariadb://localhost:3306/db
database.login = root
database.password = r00t
```

```java
@Component
public class SimpleConnectionProvider implements Supplier<Connection> {

  @Value("${database.uri}")
  private String databaseUri;
  @Value("${database.login}")
  private String login;
  @Value("${database.password}")
  private String password;
  private Connection connection;

  @PostConstruct
  public void openConnection() throws SQLException {
    connection = DriverManager.getConnection(databaseUri, login, password);
  }

  @PreDestroy
  public void closeConnection() throws SQLException {
    if(connection != null) {
      connection.close();
    }
  }

  @Override
  public Connection get() {
    return connection;
  }
}
```

Il est également possible d'injecter tout type d'objet, à la condition que la classe de l'objet possède un **constructeur** avec une ***String*** en paramètre ou bien une **méthode de fabrique** statique ***valueOf()*** avec une ***String*** en paramètre.

Exemple :

```properties
remote.server.url = http://localhost/access
```

```java
@Component
public class RemoteServerAccess {

  @Value("${remote.server.url}")
  private URL url;

}
```

Il est enfin possible de définir une valeur **par défaut** dans le cas où la propriété n'existe pas.

Exemple :

```java
  @Value("${remote.server.timeout : 1000}")
  private int timeout;
```

## Ajout de fichiers de propriétés avec @PropertySource

Il est possible de définir d'autres fichiers de propriétés que application.properties :

```java
@SpringBootApplication
@PropertySource({"classpath:config.properties", "file:config.properties"})
public class MyApplication {

  public static void main(String[] args) {
    SpringApplication.run(MyApplication.class, args);
  }

}
```

Les fichiers déclarés les plus à droite sont les plus prioritaires, ils surchargent les propriétés existantes. Le fichier application.properties est le plus prioritaire.

Il est également possible de surcharger une propriété via la ligne de commande :

```
$ java -jar myapplication.jar --remote.server.timeout=20000
```

## Variables d'environnement

Il est possible d'injecter des variables d'environnement. Elle sont prioritaires sur les propriétés.

Exemple :

```java
@Value("${USER}")
private String user;
```

## Le bean Environment

Il est possible d'injecter un bean nommé ***Environment*** qui permet de réaliser des traitements plus complexes sur les valeurs des propriétés de l'application.

Exemple :

```java
@Component
public class DemoProperty {

  @Autowired
  private Environment env;

  @PostConstruct
  public void display() {
    System.out.println(env.getProperty("database.login"));
  }

}
```

## Beans de priopriétés avec @ConfigurationProperties

Avec Spring Boot, il est possible de définir un bean contenant certaines propriétés grâce à l'annotation ***@ConfigurationProperties***.

Exemple :

```properties
database.uri = jdbc:mariadb://localhost:3306/db
database.login = root
database.password = r00t
```

```java
@Configuration
@ConfigurationProperties(prefix = "database")
public class DatabaseConfig {

  private String uri;
  private String login;
  private String password;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
```

## Bean Validation

Spring dispose d'une API permettant d'ajouter des **contraintes** sur les attributs d'une classe en détectant dès le lancement de l'application les valeurs qui ne sont pas conformes.

Il faut pour cela ajouter une dépendance : 

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Et utiliser l'annotation ***@Validated*** sur la classe instanciée par Spring.

Exemple :

```java
@Configuration
@ConfigurationProperties(prefix = "database")
@Validated
public class DatabaseConfig {

  @Pattern(regexp = "jdbc:.*", message = "Database JDBC URI must start with jdbc:")
  private String uri;
  @NotBlank(message = "login cannot be blank")
  private String login;
  @NotNull(message = "password is mandatory but can be left empty")
  private String password;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
```
