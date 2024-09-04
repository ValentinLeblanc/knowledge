## Serveur d’application

Un serveur d’application fournit un environnement d’exécution qui permet de déployer et d’exécuter une application Java EE.

## Conteneur de Servlets : TOMCAT

=> exécute des Servlets (application Web Java)

Un serveur d’application peut abriter plusieurs applications d’où la nécessité d’avoir un contexte racine de déploiement pour chaque application. (dans Eclipse, il s’agit du nom du projet)

## Packaging d’une application

Une application est packagée sous la forme d’un WAR (Web Archive) => il s’agit d’une archive ZIP.

**META-INF**
Contient des meta-informations sur le livrable (auteur, nom, date…)

**WEB-INF**
C’est la section privée de l’application : il contient le code et les fichiers de configuration. => non accessible depuis l’extérieur

**WEB-INF/web.xml**
Le fichier de déploiement : fournit des informations au serveur d’application au démarrage de l’application

**WEB-INF/classes**
Ce dossier contient les fichiers Java compilés (.class)

**WEB-INF/lib**
Ce dossier contient les bibliothèques Java (.jar) externes nécessaires à l’application. Attention : le serveur d’application fournit déjà de nombreuses bibliothèques qu’il ne faut pas ajouter dans ce dossier

## Les Servlets

Il s’agit un composant Web de Java EE. Elles permettent de traiter une requête entrante sur un serveur et de générer une réponse dynamique. Le plus souvent, elles permettent de traiter des **requêtes HTTP**.

Il s’agit d’une API définie par la spécification JSR-000369.

Le serveur d’application a besoin de savoir pour quelle URL cette servlet sera responsable de traiter et fournir une réponse 

=> annotation **@WebServlet(‘/path’)**

<u>Attention</u> : l’url complète de la servlet sera celle du contexte racine de l’application + celle définie dans le path de la servlet.

Une servlet peut également être définie dans le fichier **web.xml**.

## Le conteneur Web

Un serveur Java EE fournit un conteneur Web (ou conteneur de Servlets) qui a la charge d’instancier, initialiser et détruire les servlets d’une application. C’est également lui qui instancie **HttpServletRequest** et les **HttpServletReponse**.

Une seule instance de Servlet sera utilisée par le serveur d’application pour générer plusieurs requêtes, il faut donc veiller aux accès concurrents de l’exécution d’une Servlet. (thread-safe)

## Les attributs d’une application Web

Il est possible de partager de l’information dans un conteneur Web entre les différents composants. L’information partagée est appelée un attribut.

- <u>portée de requête</u> : information partagée par toutes les Servlets d’une même requête. Atribut accessible depuis HttpServletRequest
- <u>portée de session</u> : l’attribut est disponible pour toutes les requêtes (et leurs Servlets) émises par un même client dans un laps de temps restreint. Attribut accessible depuis HttpServletRequest::getSession()
- <u>portée d’application</u> : l’attribut est disponible à tout moment à l’ensemble des Servlets de l’application Web. Attribut accessible depuis HttpServlet::getServletContext()

## MVC et RequestDispatcher

**MVC** : Modèle d’architecture pour guider la conception d’applications qui nécessitent une interaction de l’utilisateur avec le système.

• <u>Modèle</u> : définit les données applicatives ainsi que les logiques de traitement propres à l’application (objet Java)
• <u>Vue</u> : représentation graphique des données et de l’interface utilisateur (JSP)
• <u>Contrôleur</u> : gère les interactions utilisateur et la mise à jour des vues après la modification des données (Servlet)

Le **RequestDispatcher** est un objet fourni par le conteneur Web. Il permet d’inclure ou de déléguer des traitements lors de la prise en charge d’une requête HTTP. Il est accessible depuis le ServletContext : **servletContext.getRequestDispatcher(‘/path’)**

On peut ensuite appeler la méthode **include** (pour inclure le résultat d’une autre servlet dans la réponse) ou la méthode **forward** (pour déléguer le traitement à une autre servlet).

=> Ceci permet de chainer les traitements d’une requête via l’exécution de plusieurs Servlets (exemple : une première Servlet permet de valider les paramètres, une deuxième permet de générer la réponse, une troisième servlet permet de valider la réponse).

## Web listeners et filtres

Les listeners et les filtres Web sont d’autres composants gérés par le conteneur Web.

<u>**• Listeners Web**</u>

Annotation **@WebListener** (ou fichier **web.xml**) sur une classe Java qui implémente une des interfaces suivantes :

• ServletContextListener
• ServletContextAttributeListener
• ServletRequestListener
• ServletRequestAttributeListener
• HttpSessionListener
• HttpSessionAttributeListener

**<u>• Filtres Web</u>**

Annotation **@WebFilter(‘path’)** (ou fichier **web.xml**) sur une classe qui implémente l’interface Filter.
=> Attention, l’ordre des filtres n’est pas garanti via l’annotation **@WebFilter**

## Les API Web avec JAX-RS

**JAX-RS** est une API pour implémenter des API Web (Web Services RESTful), basées sur HTTP.

JAX-RS 2.x est défini par la **JSR 339**. Il existe plusieurs implémentations de cette spécification, l’implémentation de référence est **Jersey**. Il y a aussi RestEasy et Apache CFX.

Dans le Web, ce qui est désigné par une URI est appelé une ressource. Chaque ressource peut mettre à disposition plusieurs méthodes pour permettant d’interagir avec elle : GET, HEAD, PUT, DELETE, POST, OPTIONS, TRACE, CONNECT.Les implémentations de la JAX-RS ne sont pas intégrées dans Tomcat, il faut donc les ajouter manuellement. Exemple : dépendances vers Jersey et définition d’une Servlet spécifique à Jersey.
Voir https://gayerie.dev/docs/jakartaee/javaee_web/jaxrs.html pour plus d’informations sur JAX-RS.

## Sécurisation d’une application Web

Des contraintes de sécurité peuvent être déclarées dans le fichier web.xml. 
Exemple :  

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"       
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"       
        xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee        
        http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"       
        version="3.1">
    <security-constraint>  
        <web-resource-collection>  
            <web-resource-name>Zone privée</web-resource-name>   
            <url-pattern>/private/*</url-pattern> 
        </web-resource-collection>    
        <auth-constraint>   
        	<role-name>admin</role-name>
        </auth-constraint>   
        <user-data-constraint>  
     	   <transport-guarantee>CONFIDENTIAL</transport-guarantee>  
        </user-data-constraint> 
	</security-constraint>  

    <login-config>   
        <auth-method>BASIC</auth-method>  
        <realm-name>Zone avec accès restreint</realm-name> 
	</login-config> 
        
    <security-role> 
   		<role-name>admin</role-name>
    </security-role> 

</web-app> 
```

## Authentification dans Tomcat

Tomcat utilise la nition de royaumes (**Realms**) pour définir des zones sécurisées. Cela permet de configurer différentes stratégies pour valider l’authentification d’un utilisateur. 
Par exemple, en recherchant un couple login/mot de passe dans une **BDD**, en interrogeant un serveur **LDAP**, ou alors en consultant un **fichier de configuration**. 
Il est également possible de fournir son propre mécanisme d’authentification en fournissant une bibliothèque Java conforme au standard **JAAS** (Java Authentification and Authorization Service).

Par défaut, un serveur Tomcat est configuré pour consulter un fichier de configuration appelé **tomcat-users.xml**.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<tomcat-users version="1.0"
              xmlns="http://tomcat.apache.org/xml"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd">
  <role rolename="admin"/>
  <user username="user" password="azerty" roles="admin"/>
</tomcat-users>
```



## Configuration HTTPS dans Tomcat

Pour activer le connecteur Https dans Tomcat, il faut disposer d’un certificat serveur. (on peut en générer un avec l’outil Java keytool qui sera stocké dans un fichier sécurisé appelé keystore).