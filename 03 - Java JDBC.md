# <u>**JDBC (Java DataBase Connectivity)**</u>

## Connection à une base de données

**JDBC** est l’API standard pour interagir avec des bases de données relationnelles depuis une application Java.

- **try-with-resources**

Depuis Java 7, il est conseillé d’utiliser ***try-with-resources*** lors de l’utilisation de l’interface ***AutoCloseable*** (notamment la classe ***java.sql.Connection***)

JDBC est une interface et ne fournit aucune implémentation de l’accès aux données. Il faut donc fournir un pilote pour chaque SGBDR (sous forme de fichier .*jar*) et l’ajouter dans le *classpath* de l’application (exemple : Oracle DB, MySQL, PostgreSQL, Apache Derby…)

- ​	**Création d’une connection**

Une connexion est effectuée via une instance de la classe ***java.sql.Connection***. 

Il faut dans un premier temps enregistrer un pilote JDBC en utilisant la classe ***java.sql.DriverManager*** puis créer une connexion en renseignant l’url de la base de données.

<u>Exemple :</u>

```java
DriverManager.registerDriver(new com.mysql.jdbc.Driver());
// Connexion à la base myschema sur la machine localhost
// en utilisant le login "username" et le password "password"
Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/myschema",
                                                    "username", "password");
```

- **Les requêtes SQL**

L’interface *Connection* permet de créer des *Statements*. 

Il y a 3 types de Statements : 

- *<u>Statement</u>* : permet d’exécuter une requête SQL et d’en connaître le résultat

  <u>Exemple :</u> 

  ```java
  java.sql.Statement stmt = connection.createStatement();
  
  // méthode la plus générique d'un statement. Retourne true si la requête SQL
  // exécutée est un select (c'est-à-dire si la requête produit un résultat)
  stmt.execute("insert into myTable (col1, col2) values ('value1', 'value1')");
  
  // méthode spécialisée pour l'exécution d'un select. Cette méthode retourne
  // un ResultSet (voir plus loin)
  stmt.executeQuery("select col1, col2 from myTable");
  
  // méthode spécialisée pour toutes les requêtes qui ne sont pas de type select.
  // Contrairement à ce que son nom indique, on peut l'utiliser pour des requêtes
  // DDL (create table, drop table, ...) et pour toutes requêtes DML (insert, update, delete).
  stmt.executeUpdate("insert into myTable (col1, col2) values ('value1', 'value1')");
  ```

​		Un *Statement* renvoie un objet Java *ResultSet* qui peut être utilisé comme un Iterateur et qui peut retourner directement des objets Java.

- *<u>PreparedStatement</u>* : comme un Statement mais permet de paramétrer la requête pour des raisons de performances et de se prémunir des failles de sécurité par injection SQL

  <u>Exemple :</u> 

  ```java
  String request = "insert into films (titre, date_sortie, duree) values (?, ?, ?)";
  
  try (java.sql.PreparedStatement pstmt = connection.prepareStatement(request)) {
  
    pstmt.setString(1, "live JDBC");
    pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis()));
    pstmt.setInt(3, 120);
  
    pstmt.executeUpdate();
  }
  ```

  <u>Il offre 3 avantages :</u>

  - il permet de convertir directement les types Java en types SQL pour les entrées
  - il permet d'améliorer les performances si on désire exécuter plusieurs fois la même requête avec des paramètres différents (avec notamment le mode *batch*)
  - il empêche les failles de sécurité de type <u>*injection SQL*</u>

- *<u>CallableStatement</u>* : permet d’exécuter des procédures stockées sur le SGBDR, avec paramètres en entrée et paramètres en sortie

​		Il permet d'appeler des procédures ou des fonctions stockées.

## Les transactions

Une transaction est définie par le respect de quatre propriétés désignées par l'acronyme ACID

- A pour **atomicité**

  Soit opérations qui composent une transaction sont soit toutes réalisées avec succès, soit 	aucune

- C pour **cohérence**

  Une transaction garantit qu'elle fait passer le système d'un état valide vers un autre état valide

- I pour **isolation**

  Deux transactions sont isolées l'une de l'autre : leur exécution simultanée produit le même résultat que leur exécution successive

- D pour **durabilité**

  Les modifications qu'une transaction apporte au système sont conservées durablement

=> L'**auto-commit** est souvent activé par défaut.

<u>Exemple :</u>

```java
// si nécessaire on force la désactivation de l'auto commit
connection.setAutoCommit(false);
boolean transactionOk = false;

try {

  // on ajoute un produit avec une quantité donnée dans la facture
  String requeteAjoutProduit =
            "insert into ligne_facture (facture_id, produit_id, quantite) values (?, ?, ?)";

  try (PreparedStatement pstmt = connection.prepareStatement(requeteAjoutProduit)) {
    pstmt.setString(1, factureId);
    pstmt.setString(2, produitId);
    pstmt.setLong(3, quantite);

    pstmt.executeUpdate();
  }

  // on déstocke la quantité de produit qui a été ajoutée dans la facture
  String requeteDestockeProduit =
            "update stock_produit set quantite = (quantite - ?) where produit_id = ?";

  try (PreparedStatement pstmt = connection.prepareStatement(requeteDestockeProduit)) {
    pstmt.setLong(1, quantite);
    pstmt.setString(2, produitId);

    pstmt.executeUpdate();
  }

  transactionOk = true;
}
finally {
  // L'utilisation d'une transaction dans cet exemple permet d'éviter d'aboutir à
  // des états incohérents si un problème survient pendant l'exécution du code.
  // Par exemple, si le code ne parvient pas à exécuter la seconde requête SQL
  // (bug logiciel, perte de la connexion avec la base de données, ...) alors
  // une quantité d'un produit aura été ajoutée dans une facture sans avoir été
  // déstockée. Ceci est clairement un état incohérent du système. Dans ce cas,
  // on effectue un rollback de la transaction pour annuler l'insertion dans
  // la table ligne_facture.
  if (transactionOk) {
    connection.commit();
  }
  else {
    connection.rollback();
  }
}
```

## JDBC dans une application Web

Dans une serveur d'application, il ne faut pas utiliser le **DriverManager** mais une **DataSource**.

Il faut configurer cette DataSource dans le serveur avec des paramètres comme l'URL et des identifiants de connexion. Elle est gérée comme une ressource par le serveur, il est donc possible d'y accéder via son nom et une interface Java (comme toutes les ressources d'une serveur Java EE).

L'accès aux ressources du serveur se fait via l'API  **JNDI** (*Java Naming and Directory Interface*). Elle permet notamment de se connecter à des annuaires LDAP et chaque serveur dispose de sa propre implémentation d'annuaire pour la gestion de ses ressources.

Une ressource est stockée dans l'arborescence dont le chemin commence par **java:/comp/env**.

<u>Exemple :</u>

```java
// javax.naming.InitialContext désigne le contexte racine de l'annuaire.
// Un annuaire JDNI est constitué d'instances de javax.naming.Context
// (qui sont l'équivalent des répertoires dans un système de fichiers).
Context envContext = InitialContext.doLookup("java:/comp/env");

// On récupère la source de données dans le contexte java:/comp/env
DataSource dataSource = (DataSource) envContext.lookup("nomDeLaDataSource");
```

Le contexte JNDI **java:/comp/env** est un contexte particulier, il désigne l'ensemble de composants Java EE disponibles dans l'environnement (env) du composant Java EE (comp) courant (ici, l'application Web). 

Il est également possible de d'injecter directement la DataSource via l'annotation **@Resource** :

```java
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {

  @Resource(name = "nomDeLaDataSource")
  private DataSource dataSource;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                          throws ServletException, IOException {

    try (Connection connection = dataSource.getConnection()) {
      // ...
    }

  }

}
```

### Déclaration de la DataSource dans le fichier web.xml

```xml
<resource-ref>
  <res-ref-name>nomDeLaDataSource</res-ref-name>
  <res-type>javax.sql.DataSource</res-type>
</resource-ref>
```