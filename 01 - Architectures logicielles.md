L'architecture logicielle est **l'organisation** dans laquelle les différents **composants** de l'application vont être séparés et communiquer entre eux.

Le but ultime de l'architecture logicielle est de faciliter le **développement**, **l'évolution**, le **déploiement** et la **maintenance** d'un système.

# **<u>Architecture client-serveur</u>**

L’architecture client-serveur répartit les tâches entre les fournisseurs d’un service (Serveurs) et les consommateurs du service (Clients).

![image-20230307154441029](/home/valentin.leblanc/.config/Typora/typora-user-images/image-20230307154441029.png)

Ce type d’architecture standard comporte 3 parties :

<u>*• le Front-End :*</u> 
  il s’agit de la partie du logiciel qui interagit avec les utilisateurs

<u>*• le serveur d’application :*</u>
il s’agit du serveur où sont installés les modules logiciels de l’application

<u>*• le serveur de base de données :*</u>
  il contient les tables, les index et les données gérées par l’application

**Avantages :**
    • séparation entre le support (vue et matériel), le logiciel (fonctionnalités) et les données : ce qui permet de faire évoluer chaque partie indépendament des autres
    • seule la partie front-end doit être adaptée pour communiquer avec différents appareils
      
**Inconvénients :**
    • si tous les clients demandent simultanément des données au serveur, celui-ci peut être surchargé
    • si le serveur tombe en panne, aucun utilisateur ne peut utiliser le système