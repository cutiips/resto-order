# resto-order
# Projet 1 - Exercice 1

## Créer le projet
Veuillez télécharger l’application `resto-order` disponible sur Cyberlearn et l’importer dans votre IDE. Lancez le script de création de la structure des données ainsi que le script d’insertion des données sur votre serveur Oracle.

- File -> New -> Project from Existing Sources
- Sélectionnez le fichier `pom.xml` dans le projet
- Confirmez en cliquant sur « OK »
- Note : la version de Java à utiliser est en principe Java 11, mais vous êtes libres d’utiliser des versions plus récentes si vous souhaitez bénéficier de fonctionnalités plus avancées.

## Présentation du projet

Voici quelques informations supplémentaires sur l'application :

À la racine de l'archive se trouvent deux scripts SQL :
- Le premier permet de **créer la structure** (Tables / Séquences / Triggers).
- Le second permet d'**insérer un jeu de données** de base.

L'autre élément présent dans l'archive est un projet **Maven** que vous pouvez importer dans votre IDE. Ce projet est parfaitement exécutable et a pour but d’accueillir les commandes de clients des restaurants de la région. Il est donc possible de passer commande, ou de consulter les commandes existantes.

Actuellement, le projet ne persiste aucune de ses données. Vous allez devoir implémenter complètement la **couche de persistance**. Cependant, pour que vous puissiez la prendre en main dans un premier temps, le projet de base contient une classe `FakeDb` où sont créées quelques instances d’objets métier par programmation. **Il faudra supprimer cette classe à terme** pour que toutes les données soient récupérées de la base de données.

Le projet Maven contient **quatre packages** :

- **Package `application`** : contient le main de l’application.
- **Package `business`** : contient toutes les classes métier du projet. Le diagramme de classes les présentant se trouve ci-dessous. Les classes sont des POJO classiques : elles contiennent uniquement leurs attributs, quelques constructeurs, et les getters et setters.
- **Package `persistence`** : contient une classe `FakeDb` qui fournit des fausses données à l'application, en attendant la connexion à la base de données.
- **Package `presentation`** : contient des classes qui fournissent une interface en ligne de commande (CLI). Seuls les cas d'utilisation principaux sont implémentés. Il n’est pas possible, par exemple, d’ajouter un restaurant ou un produit.

Cette application a pour but de vous permettre de mettre en pratique rapidement les concepts vus en cours. Certaines simplifications ont été apportées pour éviter des pertes de temps sur certains détails.

---

## Exercice 1

Vous devez persister le modèle de domaine **"restaurants"** dans la structure de données fournie. Utilisez le design pattern **Data Mapper** [Martin Fowler](http://martinfowler.com/eaaCatalog/dataMapper.html) en vous appuyant sur les classes disponibles sur Cyberlearn.

Les méthodes à implémenter incluent :
- **Ajout**
- **Suppression**
- **Mise à jour**
- **Recherche**

---

### Questions à résoudre [Issue #4]([https://github.com/votre-utilisateur/votre-repository/issues/X](https://github.com/cutiips/resto-order/issues/4))

À la fin de cet exercice, vous devrez être capables d’apporter des solutions aux questions suivantes :

- Comment gérer les connexions JDBC ?
- Comment générer les identifiants techniques (PK) et faire en sorte qu’ils soient présents dans les objets après leur création ?
- Comment gérer les relations ?
- Que faire dans le Data Mapper lors de la recherche du restaurant (rechercher uniquement le restaurant ? également ses commandes ? où s'arrêter ?) ?
- Doit-il y avoir des relations entre les différents Data Mappers ?
- Combien d'interactions (requêtes JDBC) sont effectuées avec la base de données dans votre code ?





---

Il n’y a pas toujours de réponses définitives à ces questions, mais des considérations à prendre en compte. Elles peuvent se révéler acceptables ou non selon les cas.

Ne tentez pas de tout gérer en même temps. Commencez simple (par exemple avec la classe `City`) et construisez progressivement.

Cet exercice est important, assez long, et représente les **fondations** pour les futures connaissances de ce cours.

---

## Modèle métier :
![Modèle métier](ModelMetier.png)

## Modèle de données :
![Modèle de données](ModeleDonnees.png)


