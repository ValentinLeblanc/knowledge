## Introduction

Dans le domaine des réseaux, un protocole est un ensemble de règles permettant de formater et de traiter les données. Cela permet aux ordinateurs d'un réseau de communiquer entre eux, indépendamment des logiciels et du matériel qu'ils utilisent.

## Modèle OSI

Chaque protocole appartient à une couche du modèle **OSI** (Open Systems Interconnections).

Ce modèle est une représentation abstraite du fonctionnement de l'internet. Il contient 7 couches, chacune représentant une catégorie différente de fonctions de mise en réseau.



- 7 : <u>Application Layer</u> => Interaction Homme - Machine, applications qui accèdent au réseau
  - HTTP
  - HTTPS
  - SMTP
  - FTP

- 6: <u>Presentation Layer</u> => Formatage et cryptage de la donnée
  - ASCII
  - Unicode
  - MIME

- 5 : <u>Session Layer</u> => Maintient les connexions et est responsable du contrôle des ports et des sessions
- 4 : <u>Transport Layer</u> => Fait transiter la donnée en utilisant des protocoles tels que TCP et UDP
  - TCP
  - UDP
- 3 : <u>Network Layer</u> => Décide quel chemin physique la donnée va emprunter
  - IP
  - ICMP
  - IGMP
- 2 : <u>Datalink Layer</u> => Définit le format de la donnée sur le réseau
  - Ethernet
  - Wi-Fi
  - Bluetooth
- 1 : <u>Physical Layer</u> => Transmet les données bianires brutes sur le support réseau
  - signal électronique
  - signal radio
  - signal laser

