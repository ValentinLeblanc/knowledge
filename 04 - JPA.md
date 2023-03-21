# <u>**JPA (Java Persistence API)**</u>

L'API JDBC présente quelques inconvénients :

- verbeux et répétitif
- la gestion de nombreuses ressources (Connection, Statement, ResultSet)
- il n'offre que la possibilité d'échanger avec une base de données

Les **ORM** sont des frameworks qui permettent de créer une correspondance entre un modèle Objet et un modèle relationnel de base de données.

Java fournit une API standard pour l'utilisation d'un ORM : **JPA (Java Persistence API)**.

Il existe plusieurs implémentations de la JPA, notamment **Hibernate**. Elles utilisent toutes JDBC, il y a donc une notion de <u>pilote</u>, de <u>DataSource</u> et d'<u>URL</u> pour configurer l'accès aux bases de données.

## Les Entités JPA

JPA permet de définir des entités : il s'agit d'une instance d'une classe qui sera persistance et désigné par l'annotation **@Entity**. Chaque entité doit disposer d'un ou plusieurs attributs portant l'annotation **@Id**, il s'agit de clé primaire de la table associée.

<u>Exemple avec annotations :</u>

```java
@Entity
@Table(name="individu")
public class Individu {

    @Id
    @Column(name="individuId")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(length=30, nullable=false)
    private String nom;

    @Basic
    @Column(length=30, nullable=false)
    private String prenom;

    @Transient
    private Integer age;

    @Temporal(TemporalType.DATE)
    private Calendar dateNaissance;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable=false)
    private Calendar dateCreation;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    private byte[] image;
```

Cette entité JPA correspond à la table SQL suivante :

```sql
CREATE TABLE `individu` (
  `individuId` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(30) NOT NULL,
  `prenom` varchar(30) NOT NULL,
  `dateNaissance` DATE,
  `dateCreation` TIMESTAMP,
  `image` BLOB,
  PRIMARY KEY (`individuId`)
);
```

## Configuration de la JPA

Il faut ajouter en dépendance du projet l'implémentation choisie pour la JPA (ex : **Hibernate**).

```xml
<dependency>
  <groupId>javax.xml.bind</groupId>
  <artifactId>jaxb-api</artifactId>
  <version>2.3.1</version>
 </dependency>

<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-core</artifactId>
  <version>5.4.9.Final</version>
</dependency>
```

Il faut ensuite fournir un fichier de déploiement **persistence.xml** dans le répertoire **META-INF**.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">
  <persistence-unit name="monUniteDePersistance">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    <properties>
      <property name="javax.persistence.jdbc.driver" value="com.mysql.jdbc.Driver" />
      <property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/database" />
      <property name="javax.persistence.jdbc.user" value="root" />
      <property name="javax.persistence.jdbc.password" value="root" />
      <property name="hibernate.dialect" value="org.hibernate.dialect.MySQLDialect" />
      <property name="hibernate.show_sql" value="true" />
      <property name="hibernate.format_sql" value="true" />
    </properties>
  </persistence-unit>
</persistence>
```

## L'EntityManager

Les annotations sont exploitées par l'interface **EntityManager**.

```java
// on spécifie le nom de l'unité de persistence en paramètre
EntityManagerFactory emf = Persistence.createEntityManagerFactory("monUniteDePersistance");

EntityManager entityManager = emf.createEntityManager();
```

L'EntityManager fournit 6 méthodes pour modifer, charger ou supprimer des entités :

 - **find** : recherche une entité via sa clé primaire (utilise un cache)
 - **persist** : enregistre l'entité en BDD et positionne la valeur de la clé primaire
 - **merge** : permet d'enregistrer une entité en BDD avec une clé primaire non gérée par l'EntityManager
 - **detach** : annule la gestion d'une entité par l'EntityManager
 - **refresh** : annule toutes les modifications faites sur l'entité (dans la transaction) et recharge son état à partir de la BDD
 - **remove** : supprime une entité de la BDD

Il prend en charge la <u>relation avec la base de données</u> et la <u>génération des requêtes SQL</u>.

```java
EntityManager entityManager = ... // nous faisons l'hypothèse que nous disposons d'une instance
Individu individu = new Individu();
individu.setPrenom("John");
individu.setNom("Smith");

// Demande d'insertion dans la base de données
entityManager.persist(individu);

// Demande de chargement d'une entité.
// Le second paramètre correspond à la valeur de la clé de l'entité recherchée.
individu = entityManager.find(Individu.class, individu.getId());

// Demande de suppression (delete)
entityManager.remove(individu);
```

Pour les opérations qui modifient une entité (ex : **persist** ou **remove**), l'appel doit se faire dans une **transaction**.

```java
EntityManager entityManager = ... // nous faisons l'hypothèse que nous disposons d'une instance

entityManager.getTransaction().begin();
boolean transactionOk = false;
try {
// ..

transactionOk = true;
}
finally {
    if(transactionOk) {
        entityManager.getTransaction().commit();
    }
    else {
        entityManager.getTransaction().rollback();
    }
}
```

**<u>Attention :</u>** cet exemple ci-dessus ne fonctionne pas dans un serveur Java EE qui utilise l'API de gestion des transactions JTA.

## Les requêtes JPA

Pour effectuer des requêtes SQL plus élaborées, plusieurs API de l'EntityManager sont disponibles.

### Les requêtes SQL natives

```java
List<Individu> individus = null;
individus = entityManager.createNativeQuery("select * from individu", Individu.class)
                         .getResultList();
```

```java
int ageMax = 25;
List<Individu> individus = null;
individus = entityManager
              .createNativeQuery("select * from individu where age <= ?", Individu.class)
              .setParameter(1, ageMax)
              .getResultList();
```

```java
long result = (Long) entityManager
                   .createNativeQuery("select count(1) from individu")
                   .getSingleResult();
```

```java
long individuId = 1;
// Cette requête nécessite une transaction active
entityManager.createNativeQuery("delete from individu where individuId = ?")
             .setParameter(1, individuId)
             .executeUpdate();
```

### Les requêtes JPQL

```java
List<Individu> individus = null;
individus = entityManager.createQuery("select i from Individu i", Individu.class)
                         .getResultList();
```

```java
int ageMax = 25;
List<Individu> individus = null;
individus = entityManager
                .createQuery("select i from Individu i where i.age <= :ageMax", Individu.class)
                .setParameter("ageMax", ageMax)
                .getResultList();
```

```java
long result = (Long) entityManager.createQuery("select count(i) from Individu i")
                           .getSingleResult();
```

```java
long individuId = 1;
// Cette requête nécessite une transaction active
entityManager.createQuery("delete from Individu i where i.id = :id")
             .setParameter("id", individuId)
             .executeUpdate();
```

#### Par programmation

```java
CriteriaBuilder builder = entityManager.getCriteriaBuilder();

CriteriaQuery<Individu> query = builder.createQuery(Individu.class);
Root<Individu> i = query.from(Individu.class);
query.select(i);

List<Individu> individus = entityManager.createQuery(query).getResultList();
```

```java
int ageMax = 25;

CriteriaBuilder builder = entityManager.getCriteriaBuilder();

CriteriaQuery<Individu> query = builder.createQuery(Individu.class);
Root<Individu> i = query.from(Individu.class);
query.select(i);
query.where(builder.lessThanOrEqualTo(i.get("age").as(int.class), ageMax));

List<Individu> individus = entityManager.createQuery(query).getResultList();
```

```java
CriteriaBuilder builder = entityManager.getCriteriaBuilder();

CriteriaQuery<Long> query = builder.createQuery(Long.class);
Root<Individu> i = query.from(Individu.class);
query.select(builder.count(i));

long result = entityManager.createQuery(query).getSingleResult();
```

### Les requêtes nommées

```java
@Entity
@NamedQueries({
  @NamedQuery(name="findIndividuByNom", query="select i from Individu i where i.nom = :nom"),
  @NamedQuery(name="deleteIndividuByNom", query="delete from Individu i where i.nom = :nom"),
  @NamedQuery(name="deleteAllIndividus", query="delete from Individu i")
})
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nom;

  // getters et setters omis

}
```

```java
Individu individu = entityManager.createNamedQuery("findIndividuByNom", Individu.class)
                                 .setParameter("nom", "David Gayerie")
                                 .getSingleResult();
```

```java
entityManager.getTransaction().begin();
entityManager.createNamedQuery("deleteIndividuByNom")
             .setParameter("nom", "David Gayerie")
             .executeUpdate();
entityManager.getTransaction().commit();
```

## Les relations avec JPA

### La relation 1:1

On utilise l'annotation **@OneToOne** :

```java
@Entity
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "abonnement_fk")
  private Abonnement abonnement;

  // getters et setters omis ...
}
```

Pour JPA, la table **Individu** contient donc une colonne qui est une clé étrangère contenant la clé d'un **Abonnement**. Par défaut, JPA s'attend à ce que cette colonne se nomme **ABONNEMENT_ID**, mais il est possible de changer ce nom grâce à l'annotation **@JoinColumn**.

### La relation n:1

On utilise l'annotation **@ManyToOne** :

```java
@Entity
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Societe societe;

  // getters et setters omis ...
}
```

Même remarque concernant l'annotation **@JoinColumn**.

Plutôt que par une colonne, il est possible d'indiquer à la JPA qu'elle doit passer par une table d'association pour établir la relation entre les deux entités avec l'annotation **@JoinTable** :

```java
@Entity
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  // déclaration d'une table d'association
  @JoinTable(name = "societe_individu",
             joinColumns = @JoinColumn(name = "individu_id"),
             inverseJoinColumns = @JoinColumn(name = "societe_id"))
  private Societe societe;

  // getters et setters omis ...
}
```

### La relation 1:n

On utilise l'annotation **@OneToMany** :

Cette annotation ne peut être utilisée qu'avec une collection d'éléments.

```java
@Entity
public class Societe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany
  private List<Individu> employes = new ArrayList<>();

  // getters et setters omis ...
}
```

Pour JPA, la table **Individu** contient donc une colonne qui est une clé étrangère contenant la clé d'une **Societe**. Par défaut, JPA s'attend à ce que cette colonne se nomme **SOCIETE_ID**, mais il est possible de changer ce nom grâce à l'annotation **@JoinColumn**.

 Il est possible d'indiquer à la JPA qu'elle doit passer par une table d'association pour établir la relation entre les deux entités avec l'annotation **@JoinTable** :

```java
@Entity
public class Societe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany
  @JoinTable(name = "societe_individu",
             joinColumns = @JoinColumn(name = "societe_id"),
             inverseJoinColumns = @JoinColumn(name = "individu_id"))
  private List<Individu> employes = new ArrayList<>();

  // getters et setters omis ...
}
```

### La relation n:n

On utilise l'annotation **@ManyToMany** :

Cette annotation ne peut être utilisée qu'avec une collection d'éléments.

```java
@Entity
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToMany
  @JoinTable(name = "individu_competence",
             joinColumns = @JoinColumn(name = "individu_id"),
             inverseJoinColumns = @JoinColumn(name = "competence_id"))
  private List<Competence> competences = new ArrayList<>();

  // getters et setters omis ...
}
```

L'annotation **@ManyToMany** implique qu'il existe une **table d'association**. 

### Les relations bi-directionnelles

Il est parfois nécessaire qu'un lien entre deux objets soit navigable dans les deux sens. Il faut utiliser l'annotation **@MappedBy** :

```java
@Entity
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToMany
  @JoinTable(name = "individu_competence",
             joinColumns = @JoinColumn(name = "individu_id"),
             inverseJoinColumns = @JoinColumn(name = "competence_id"))
  private List<Competence> competences = new ArrayList<>();

  // getters et setters omis ...
}
```

```java
@Entity
public class Competence {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToMany(mappedBy = "competences")
  private List<Individu> individus = new ArrayList<>();

  // getters et setters omis ...
}
```

Cette annotation permet d'éviter la redondance d'information.

### La propagation en cascade

Avec JPA, il est possible de définir la portée des modifications effectuées sur une entité sur ses entités liées. Les annotations spécifiant une relation possèdent un attribut **cascade** permettant de cibler les opérations concernées par les modifications :

	- **ALL**
	- **DETACH**
	- **MERGE**
	- **PERSIST**
	- **REFRESH**
	- **REMOVE**

```java
@Entity
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  private Societe societe;

  // getters et setters omis ...
}
```

Ici, on précise que l'instance de **Societe** doit être enregistrée en BDD (si nécessaire) au moment où l'instance d'**Individu** sera elle-même enregistrée. Même chose pour un appel au **merge** sur une instance d'**Individu**.

### Fetch lazy ou fetch eager

Lorsque JPA doit charge une entité depuis la BDD, la question est de savoir quelles informations doivent être chargées. Quels attributs de l'entité doivent être chargés (**fetch**) ?

- **eager** : l'information doit être chargée systématiquement lorsque l'entité est chargée (par défaut pour **@Basic**, **@OneToOne**, **@ManyToOne**)
- **lazy** : l'information ne sera chargée qu'à la demande (par défaut pour **@OneToMany** et **@ManyToMany**)

```java
@Entity
public class Individu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Societe societe;

  /*
   * Stocke la photo d'identité sous une forme binaire. Comme l'information peut être
   * volumineuse, on déclare un fetch lazy pour ne déclencher le chargement qu'à l'appel
   * de getPhoto(), c'est-à-dire quand l'application en a vraiment besoin.
   */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  private byte[] photo;

  // getters et setters omis ...
}
```
