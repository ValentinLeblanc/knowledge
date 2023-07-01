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

En complément des méthodes déjà présentes dans l'interface ***JpaRepository<T, ID>***, il est possible de créer d'autres méthodes qui, selon leur nom, permettront d'effectuer des requêtes sans avoir besoin de les implémenter, Spring Data JPA se chargera de fournir l'implémentation automatiquement. On appelle ces méthodes des **query methods**.

Exemple :

```java
public interface UserRepository extends JpaRepository<User, Long> {
  User getByLogin(String login);
  long countByEmail(String email);
  List<User> findByNameAndEmail(String name, String email);
  List<User> findByNameOrEmail(String name, String email);
}
```

Cette interface va engendrer les implémentations sous-jacentes suivantes :

```java

return entityManager.createQuery("select u from User u where u.login = :login", User.class)
                  .setParameter("login", login)
                  .getSingleResult();
```

```java
return (Long) entityManager.createQuery("select count(u) from User u where u.email = :email")
                         .setParameter("email", email)
                         .getSingleResult();
```

```java
return entityManager.createQuery("select u from User u where u.name = :name and u.email = :email", User.class)
                  .setParameter("name", name)
                  .setParameter("email", email)
                  .getResultList();

```

```java
return entityManager.createQuery("select u from User u where u.name = :name or u.email = :email", User.class)
                  .setParameter("name", name)
                  .setParameter("email", email)
                  .getResultList();
```

## Implémentation des méthodes de Repository

Il est parfois nécessaire d'implémenter soi-même une ou plusieurs méthodes de Repository.

Il faut alors créer une interface dédiée, par exemple ***UserCustomRepository*** :

```java
public interface UserCustomRepository {

  void doSomethingComplicatedWith(User u);

}
```

et faire hériter l'interface ***UserRepository*** initiale avec cette interface custom.

```java
public interface UserRepository extends UserCustomRepository, JpaRepository<User, Long>{

}
```

Spring Data JPA va alors injecter une classe Java portant le même nom que ***UserCustomRepository*** avec le suffixe ***Impl***.

Il ne <u>faut pas</u> ajouter ***@Component*** ou ***@Repository*** sur la classe d'implémentation.

```java
public class UserCustomRepositoryImpl implements UserCustomRepository {

  @PersistenceContext
  private EntityManager em;

  @Override
  public void doSomethingComplicatedWith(User u) {
    // ...
  }

}
```

# Spring transaction

C'est le module Spring dédié à la gestion des transactions.

- Il fournit une abstraction au dessus des différentes solutions du monde Java avec l'interface ***TransactionManager*** et plusieurs implémentations
- il se base sur l'**AOP**
- il permet une gestion déclarative des transactions

## La transaction

Une transaction est définie par le respect de 4 propriétés ACID :

- Atomicité

  la transaction garantit que l'ensemble des opérations qui la composent sont soit toutes réalisées avec succès soit aucune n'est conservée

- Cohérence

  la transaction garantit qu'elle fait passer le système d'un état valide vers un autre état valide

- Isolation

  Deux transactions réalisées exécutées simultanément produiront le même résultat qu'exécutées l'une après l'autre

- Durabilité

  la transaction garantit qu'après son exécution, les modifications qu'elle a apportées au système sont conservées durablement

**Démarcation transactionnelle** => **commit** ou **rollback**

