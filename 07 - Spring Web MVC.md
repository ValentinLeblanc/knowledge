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
