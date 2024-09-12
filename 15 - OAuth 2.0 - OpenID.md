## OAuth 2.0

OAuth 2.0 est un mécanisme qui permet à une application d'avoir accès à des ressources d'une autre application sans avoir besoin de s'identifier sur celle-ci. L'utilisateur s'est alors lui-même identifié sur les deux applications et celles-ci se partagent des ressources via un Access Token (jeton signé).

Exemple :

- Alice (**Resource Owner**) est authentifiée sur une application Web nommée PictureAI (**OAuth Client**) qui modifie les photos de profil via une IA
- PictureAI propose d'accéder au Google Drive (**Resource Server**) de l'utilisateur pour récupérer ses photos
- Si l'utilisateur le souhaite, PictureAI demande à Google (**Authorization Server**) d'accéder à ses photos
- Google renvoie une URL qui demande à l'utilisateur s'il souhaite partager ses photos avec PictureAI (après une éventuelle authentification sur Google)
- Si l'utilisateur approuve, Google transmet à PictureAI un code d'autorisation à usage unique
- PictureAI utilise ce code pour demander un Access Token à Google (via une connexion sécurisée)
- Google transmet un Access Token, et PictureAI a alors accès aux photos avec ce jeton signé

OAuth 2.0 est donc un mécanisme qui permet **l'autorisation** de ressources.

## OpenID

OpenID Connect est un standard qui repose sur OAuth 2.0 mais qui propose en supplément le moyen pour application d'authentifier ses utilisateurs via une autre application. Cette dernière transmet un Token ID en plus de l'Access Token.

Le Token ID est un JWT qui contient des informations sur l'identité de la personne ainsi que d'autres métadonnées.

OpenID est un mécanisme qui ajoute une couche d'authentification à OAuth 2.0.