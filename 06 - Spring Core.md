# Introduction

Spring est un **framework** largement utilisé dans la communauté Java. Il accélère le **développement** d'applications d'entreprise. Il se présente comme une **alternative** au modèle d'architecture proposé par la plate-forme **J2EE** du début des années 2000.

J2EE est initialement basé sur des serveurs d'applications qui déploient et gèrent des composants (**Servlet**, **EJB**...) fournis par les développeurs au sein de conteneurs logiciels, et qui doivent être conformes à une spécification technique pour qu'ils puissent être pris en charge par le serveur. Les services tels que la **sécurité**, l'accès aux **bases de données**, la gestion des **transactions**... sont gérés par le serveur d'application. Le développeur J2EE doit donc connaître les API nécessaire à la conformité de ses composants, qui sont complexes. Ainsi, la **virtualisation**, le **cloud computing** et les architectures **microservices** tendent à mettre en inadéquation J2EE et sa plate-forme avec les problématiques actuelles.

**Spring Framework** propose de bâtir des applications qui **embarque** elle-même les **services** dont elle a besoin. Il offre des solutions déjà existantes aux problématiques techniques évoquées et il est possible de s'affranchir des services non nécessaires.

L'idée centrale du Spring Framework est de n'imposer aucune norme de développement ni aucune contrainte technique sur la façon de coder. Il se veut non-intrusif tout en se basant sur le principe de <u>l'inversion de contrôle</u> (**IoC**) et sur la <u>programmation orientée aspect</u> (**AOP**). Il met en œuvre des **Design Patterns** pour fournir un environnement le plus souple possible.

# L'inversion de contrôle

L'inversion de contrôle est un **patron d'architecture** qui fonctionne selon le principe que le **flot d'exécution** d'un logiciel n'est plus sous le **contrôle** direct de l'application mais du **framework** ou de la couche logicielle sous-jacente. Un framework de ce type fournit une ossature, une charpente à mon application sur laquelle va reposer le code spécifique du développeur. 

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

L'API Spring Framework définit l'interface ***ApplicationContext*** pour laquelle plusieurs implémentations existent et donc plusieurs façons de définir un contexte d'application également. L'une d'entre elles est la classe ***AnnotationConfigApplicationContext*** qui permet d'utiliser des **annotations** sur les classes de l'application pour indiquer au conteneur IoC comment définir les objets.

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

Spring va créer une instance de la classe ***TimeApplication***, puis il va chercher des annotations particulières sur cet objet. La méthode ***maintenant()*** possède l'annotation ***@Bean*** : Spring va donc l'appeler et l'objet ***LocalTime*** qu'elle retourne sera placé dans le contexte d'application sous le nom ***maintenant***.

Il est possible de donner plusieurs noms à un même **bean** : 

```java
@Bean(name = {"maintenant", "now", "ahora", "jetzt"})
public LocalTime getLocalTime() {
  return LocalTime.now();
}
```
