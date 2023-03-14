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

