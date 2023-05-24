# Spring Batch

## Présentation

Il s'agit d'une infrastructure qui permet de faire des développements par lots (batch) légère et complète. Il est utilisé lorsqu'un grand volume de données doit être traité de façon automatique, donc lorsqu'on doit exécuter un **Batch Processing**.

**Un programme Batch :**

- lit un grand nombre de données depuis une BDD, un fichier ou une queue
- traite les données
- écrit les données sous une forme modifiée

Spring Batch est l'un des rares Framework open source offrant une solution robuste pour ce type de processing.

**Exemple d'utilisations métier :**

- une entreprise doit effectuer le virement des salaires aux employés
- le traitement des bulletins de salaire à la fin de chaque mois
- envoie de courriels de communication de masse
- générer des rapports automatisés sur une base quotidienne, hebdo ou mensuelle
- exécution automatique d'un flux de travail sans intervention humaine
- soumettre un traitement par lots de façon périodique
- faire un traitement par lots de façon massive en parallèle

**Exemple d'utilisations techniques :**

- exécuter automatiquement des tests unitaires périodiquement
- effectuer des mises à jour automatiques de bases de données
- définir un système de file d'attente pour traiter un grand nombre de traitements
- être utilisé avec des API pour faire un contrôle d'intégrité du serveur ou de l'appli, générer des données factices...

## Architecture

C'est une architecture à 3 couches :

- **Application**

  contient tous les traitements par lots et le code personnalisé écrit par le développeur

- **Batch Core**

  c'est le noyau qui contient les classes d'exécution principales au lancement et au contrôle d'un lancement de batch (**JobLauncher**, **Job** et **Step**)

- **Batch Infrastructure**

  Application et Batch Core sont tous les deux construits sur une infrastructure commune. Elle contient des **Readers**, des **Writers** et des **services de traitement courants**

## Concepts fondamentaux

Un traitement par lot est défini par :

- un **Job** qui se compose de plusieurs **Step**
- chaque **Step** a un seul **ItemReader**, **ItemProcessor** et **ItemWriter**
- un **Job** est exécuté par un **JobLauncher**
- les métadonnées relatives aux traitements configurés et exécutés sont stockées dans un **JobRepository**

## Job

Un Job est une entité qui encapsule un processus de traitement par lots complet.

Comme avec les autres projets de Spring, un Job est associé à :

- un fichier de configuration XML

  OU

- une classe de configuration Java

Un **Job** est un conteneur pour les instances **Step**. Chaque Job peut être associé à plusieurs **JobInstance**, chacune étant définie de manière unique par ses **JobParameters**

Chaque exécution d'une **JobInstance** est appelée **JobExecution**.

Chaque JobExecution suit ce qui s'est passé lors d'une exécution comme les statuts actuels, de sortie, les heures de début, de fin...

## Step

C'est une phase spécifique indépendante d'un travail par lots.

Chaque **Step** utilise :

- un **ItemReader** permettant de lire et désérialiser les enregistrements Batch à partir d'une entrée
- un **ItemProcessor** permettant de traiter les items
- un **ItemWriter** permettant d'écrire les enregistrements traités par ItemProcesor dans une sortie quelconque

## StepConfiguration

Spring Batch utilise le modèle **Chunk-oriented processing** : les données sont lues une par une et des morceaux sont créés avant d'être écrits dans une transaction.

Un élément est lu par un **ItemReader**, confié à un **ItemProcessor** et agrégé.

Lorsque le nombre d'éléments agrégés est égal à l'intervalle de validation, l'élément complet est écrit par un **ItemWriter** puis la transaction est validée

## ExecutionContext

C'est un ensemble de paires de clé/valeur contenant des informations relatives à **StepExecution** et **JobExecution**.

## JobRepository

C'est un mécanisme qui rend toute persistance possible. Il fournit des opérations CRUD pour les instanciations de **JobLauncher**, **Job** et **Step**.

