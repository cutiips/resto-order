Voici un fichier `.md` qui explique le fonctionnement des tests et leur objectif.

---

# Documentation des Tests - Mapper & IdentityMap

## Objectif des Tests

Ces tests visent à vérifier le bon fonctionnement des mappers (`CustomerMapper`, `ProductMapper`, `RestaurantMapper`) en s’assurant que :
1. Les opérations de persistance (insertion, mise à jour, suppression) sur les entités fonctionnent comme attendu.
2. Le cache (`IdentityMap`) est utilisé correctement, c’est-à-dire qu’une seule requête est émise pour chaque entité unique, en limitant les requêtes supplémentaires en cas de récupération répétée d’une même entité.

## Structure des Tests

Les tests sont séparés par classe de mapper pour tester chaque entité (Customer, Product, Restaurant) indépendamment. Voici les fichiers de tests principaux :
- `CustomerMapperTest`
- `ProductMapperTest`
- `RestaurantMapperTest`

### Préparation des Tests

Chaque fichier de test utilise des mocks pour simuler la connexion à la base de données. Les objets JDBC (`Connection`, `PreparedStatement`, `ResultSet`) sont simulés avec **Mockito** pour contrôler leur comportement et vérifier que chaque opération interagit correctement avec la base de données.

Chaque test suit généralement la structure suivante :
1. **Préparation** : Initialiser les mocks et les configurations.
2. **Exécution** : Appeler la méthode de la classe Mapper.
3. **Vérification** : Utiliser `verify()` pour s’assurer qu’une seule requête est émise par entité grâce au cache (`IdentityMap`).

## Détails des Tests

### CustomerMapperTest

Les tests de `CustomerMapper` vérifient les fonctionnalités de base (insertion, mise à jour, suppression) et l’utilisation de l’`IdentityMap` pour éviter les requêtes redondantes.

- **testInsertCustomer** : Teste l'insertion d'un client et vérifie que l'ID est correctement défini après l'insertion.
- **testUpdateCustomer** : Teste la mise à jour d'un client et vérifie qu'une seule requête est exécutée.
- **testDeleteCustomer** : Teste la suppression d'un client et s'assure qu'une seule requête est exécutée.
- **testIdentityMapPreventsMultipleQueries** : Vérifie que l'`IdentityMap` empêche les requêtes redondantes pour une même entité. Le test ajoute un client au cache, puis récupère le client par ID sans déclencher de requête SQL supplémentaire.

### ProductMapperTest

Les tests de `ProductMapper` sont similaires à ceux de `CustomerMapper` et incluent les fonctionnalités de base ainsi que le test de l’`IdentityMap`.

- **testInsertProduct** : Teste l'insertion d'un produit, en s’assurant que l'ID est défini après insertion et qu'une seule requête est exécutée.
- **testUpdateProduct** : Teste la mise à jour d'un produit en vérifiant qu'une seule requête est exécutée.
- **testDeleteProduct** : Teste la suppression d'un produit.
- **testIdentityMapPreventsMultipleQueries** : Teste le bon fonctionnement de l’`IdentityMap` en s’assurant qu’aucune requête supplémentaire n'est émise après la mise en cache d'un produit.

### RestaurantMapperTest

Les tests de `RestaurantMapper` incluent également des vérifications sur l'`IdentityMap` pour le cache.

- **testIdentityMapPreventsMultipleQueries** : Teste l’utilisation de l’`IdentityMap` en vérifiant qu’une seule requête est émise pour charger un restaurant, même si la méthode `findById` est appelée plusieurs fois. Le test vérifie également que le même objet (référence) est retourné pour le même ID.

## Résumé

Ces tests permettent de s’assurer que :
- Les mappers effectuent correctement les opérations de base (CRUD) en utilisant des interactions JDBC simulées.
- Une seule requête est émise pour chaque entité unique grâce au cache (`IdentityMap`), réduisant ainsi le nombre d’interactions avec la base de données et optimisant les performances de récupération des données.

## Instructions d'Exécution

1. Installer les dépendances nécessaires (par exemple, Mockito).
2. Exécuter les tests avec `mvn test` ou directement dans l'IDE.

--- 

En suivant cette documentation, toute personne peut comprendre la logique des tests et les exécuter pour vérifier que l'`IdentityMap` et les opérations CRUD fonctionnent comme prévu dans chaque mapper.