# Le protocole HTTP

L'**Hypertext Transfer Protocol** (HTTP) est un protocole de la couche application.

Il est le fondement de l'échange de données pour le Web. HTTP/1.1 est **orienté texte** : les machines envoient des données au format texte sur le réseau.

## Structure des messages

### Requête HTTP

Structure d'une **requête** HTTP :

```
[méthode] [ressource cible] HTTP/1.1
[Nom de l'en-tête]: [Valeur de l'en-tête]
...
[ligne vide]
[corps de message]
```

<u>Exemple :</u>

```http
GET /html/rfc7230 HTTP/1.1
Host: tools.ietf.org
```

Ceci est équivalent à la requête GET sur l'URI https://tools.ietf.org/html/rfc7230

### Réponse HTTP

Structure d'une **réponse** HTTP :

```
HTTP/1.1 [code statut] [message]
[Nom de l'en-tête]: [Valeur de l'en-tête]
...
[ligne vide]
[corps de message]
```

<u>Exemple :</u>

```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=utf-8
Content-Length: 420

<html>
...
</html>
```

## Les codes de statut

Il y a plusieurs familles de codes de statut HTTP :

- **1xx** réponse informationnelle
- **2xx** succès
- **3xx** redirection
- **4xx** erreur du client
- **5xx** erreur du serveur

## Les méthodes HTTP

Les méthodes HTTP désignent le type d'opération que le client désire réaliser.

- **GET** : demande au serveur une représentation d'une ressource identifiée par l'URI
- **HEAD** : comme un GET sauf que le réponse ne contient que les HEADERS
- **PUT** : Crée ou met à jour l'état d'une ressource identifiée par l'URI
- **DELETE** : Détruit l'association de l'URI avec l'état de la ressource
- **POST** : la sémantique de cette méthode est plus compliquée car elle est utilisable dans différentes situations :
  - fournir un formulaire à un processus de traitement
  - poster un message dans système de centralisation d'articles
  - créer une nouvelle ressource qui sera identifiée par le serveur
  - ajouter des informations à la représentation d'une ressource 

- **OPTIONS** : permet d'obtenir les options de communication (exemple : les méthodes autorisées pour l'URI) : le serveur retourne ces informations dans l'en-tête **Allow** de la réponse

## Propriétés des méthodes HTTP

Les méthodes HTTP sont classées selon 3 propriétés :

- **Safety** : le client ne s'attend à aucune modification de l'état du serveur
- **Idempotent** : l'effet obtenu est le même que la méthode soit exécutée 1 ou N fois
- **Cacheable** : la réponse peut être stockée par le client ou un proxy pour une utilisation ultérieure

|         | Safety  | Idempotent | Cacheable |
| ------- | ------- | ---------- | --------- |
| GET     | **oui** | **oui**    | **oui**   |
| HEAD    | **oui** | **oui**    | **oui**   |
| PUT     | non     | **oui**    | non       |
| PATCH   | non     | non        | non       |
| DELETE  | non     | **oui**    | non       |
| POST    | non     | non        | **oui**   |
| OPTIONS | **oui** | **oui**    | non       |

## Les en-têtes HTTP

Les en-têtes (**HEADERS**) de requête et de réponse permettent d'enrichir le contexte de la requête ou de la réponse. Ils sont insensibles à la casse et peuvent être envoyés dans n'importe quel ordre. Voici quelques exemples :

- **Host** (requête) : il est le seul obligatoire en HTTP/1.1, il contient le nom et le port du serveur
- **Content-Type** (requête et réponse) : permet d'identifier le format du contenu du corps du message (si présent) sous forme de **type MIME**
- **Content-Length** (requête et réponse) : permet de communiquer la taille en octets du corps du message

## La gestion du cache
