# Spring Data Access

Les applications d'entreprise ont la plupart du temps la nécessité d'interagir avec un système de base de données pour lire ou modifier des données.

Ce sont notamment les classes de type **DAO** (Data Access Object) qui en ont la responsabilité. Par exemple, en Java, les classes DAO peuvent utiliser l'API **JDBC** ou bien l'API **JPA**.

Le module **Spring Data Access** cherche à simplifier l'intégration et l'implémentation des interactions avec les bases de données.

## @Repository

Parmi les stéréotypes définis par Spring, celui qui désigne une classe qui sert de point d'accès à un système de base de données est ***@Repository***.

Exemple :

```java
@Repository
public class UserDao {

  public void save(User user) {
    // ...
  }

  public User getById(long id) {
    // ...
  }
}
```

## L'interface Repository

Spring Data s'organise autour de la notion de *repository* et fournit une interface générique : ***Repository<T, ID>***.

T correspond au type de l'objet géré par le repository, et ID correspond au type de la clé d'un objet.

L'interface **CrudRepository<T, ID>** hérite de ***Repository<T, ID>*** et fournit un ensemble d'opérations élémentaires pour la manipulation des objets.

## Intégration de JPA

### Avec Spring Boot

Il faut ajouter la dépendance suivante :

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Ainsi qu'une dépendance vers un pilote de base de données JDBC : (le numéro de version est fourni par Spring Boot)

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
</dependency>
```

Contrairement à ce qu'impose la norme JPA, il n'y pas besoin de fichier ***persistence.xml*** et toute la configuration peut se faire via le fichier ***application.properties*** :

```properties
spring.datasource.url = jdbc:mysql://mydb:3306/my_database
spring.datasource.username = root
spring.datasource.password = root
spring.datasource.driver-class-name = com.mysql.jdbc.Driver
spring.jpa.show-sql = true
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5Dialect
```

### Sans Spring Boot

C'est plus compliqué à faire.

### EntityManager

Pour une application Spring qui utilise JPA, il est possible d'injecter une instance d'***EntityManager*** dans un repository grâce aux annotations ***@Autowired***, ***@Inject*** ou ***@PersistenceContext*** :

```java
@Repository
public class UserDao {

  @PersistenceContext
  private EntityManager entityManager;

  public void save(User user) {
    // ...
  }

  public User getById(long id) {
    // ...
  }
}
```

Spring s'occupe alors de gérer la création et la fermeture de l'objet ***EntityManager***.

## Intégration de JDBC

### JdbcTemplate

Spring Data Access fournit la classe ***JdbcTemplate*** pour encapsuler les appels JDBC (qui est *thread-safe*). Cette classe réalise :

- l'encapsulation des appels à ***Statement*** et ***PreparedStatement***
- une aide pour la création d'objets à partir d'un ***ResultSet***
- la traduction d'une éventuelle ***SQLException*** dans la hiérarchie uniformisée des exceptions de Spring Data Access

La classe ***JdbcTemplate*** se construit à partir d'une ***DataSource***. L'implémentation recommandée est de construire une instance de JdbcTemplate au moment de l'injection de la DataSource :

```java
@Repository
public class UserDao {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  // ...

}
```

 Il suffit alors de définir la DataSource dans le contexte de déploiement Spring de l'application.

Exemple d'utilisation de ***JdbcTemplate*** :

```java
@Repository
public class UserDao {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public int getUserCount() {
    return jdbcTemplate.queryForObject("select count(1) from User", Integer.class);
  }

  public User getUserById(long id) {
    return jdbcTemplate.queryForObject("select * from User where id = ?", new UserRowMapper(), id);
  }

  public List<User> getAll() {
    return jdbcTemplate.query("select * from User", new UserRowMapper());
  }

  private final class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User user = new User();
      user.setId(rs.getLong("id"));
      user.setNom(rs.getString("nom"));
      return user;
    }
  }
}
```

### SimpleJdbcInsert

La classe ***SimpleJdbcInsert*** facilite la génération de requêtes d'insertion :

```java
@Repository
public class UserDao {

  private JdbcTemplate jdbcTemplate;
  private SimpleJdbcInsert simpleJdbcInsert;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("User");
  }

  public void save(User user) {
    Map<String,Object> params = new HashMap<String, Object>();
    params.put("name", user.getName());

    simpleJdbcInsert.execute(params);
  }

  // ...
}
```

### Avec Spring Boot

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
</dependency>
```

```properties
spring.datasource.url = jdbc:mysql://mydb:3306/my_database
spring.datasource.username = root
spring.datasource.password = root
spring.datasource.driver-class-name = com.mysql.jdbc.Driver
```

### Sans Spring Boot

C'est plus compliqué car il faut intégrer la ***DataSource*** dans le contexte d'application.

Il faut également les dépendances suivantes :

```xml
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-jdbc</artifactId>
  <version>5.3.1</version>
</dependency>
```

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.21</version>
</dependency>
```

Exemple d'intégration d'une DataSource :

```java
@Configuration
public class DatabaseConfiguration {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Bean
  public DataSource dataSource() {
    return new DriverManagerDataSource(url, username, password);
  }
}
```

### Uniformité de la hiérarchie des exceptions

De nombreuses exceptions peuvent être lancées depuis les différentes librairies et API utilisées. Par exemple, JDBC utilise des ***SQLException*** (*checked*) et JPA des ***PersistenceException*** (*unchecked*).

Spring Data Access propose une hiérarchie unique d'exceptions qui encapsulent ces différentes exceptions, elles héritent alors toutes de ***DataAccessException*** et sont des exceptions *unchecked* (comme ***RuntimeException***).

Il est possible de désactiver cette uniformisation avec l'ajout de la propriété suivante (avec Spring Boot) :

```properties
spring.dao.exceptiontranslation.enabled = false
```

# Spring Data JPA

**Spring Data JPA** est le module de Spring Data qui permet d'interagir avec une **base de données relationnelle** en représentation les objets du modèle métier sous la forme **d'entités** **JPA**.

 Il fournit l'interface **CrudRepository<T, ID>** qui hérite de ***Repository<T, ID>*** et fournit un ensemble de méthodes plus spécifiquement adaptées pour interagir avec une base de données relationnelle.

Exemple de *repository* :

```java
public interface UserRepository extends JpaRepository<User, Long> {
}
```

## Injection des repositories

A l'initialisation du contexte d'application, Spring Data JPA va rechercher à partir du package de base toutes les interfaces héritant de ***Repository<T, ID>***, et créer un **bean d'implémentation** portant le même nom que l'interface.

Il suffit alors d'injecter un bean du même type que l'interface pour y avoir accès :

```java
@Repository
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Transactional
  public void doSomething(long id) {
    long nbUser = userRepository.count();
    boolean exists = userRepository.existsById(id);

    // ..
  }
}
```

## Ajout de méthodes dans une interface de repository
