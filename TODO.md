1. Couche Service (service)

   Gestion des Exceptions :
   Renommez les exceptions pour qu'elles soient spécifiques à chaque service (RestaurantPersistenceException pour les méthodes relatives aux restaurants, etc.).
   Uniformisez la vérification de conn != null avant de faire un rollback ou d'autres opérations sur la connexion.

   Inversion de Contrôle (IoC) :
   Utilisez l'injection de dépendance pour le RestaurantMapper et les autres mappers. Cela rendra vos classes de service plus flexibles et faciles à tester.

   Transactions :
   Déplacez la gestion des transactions dans la couche service et assurez-vous de bien utiliser try-with-resources pour garantir la fermeture des connexions.

   Modularité :
   Séparez la logique de transaction et la logique métier. Évitez de mélanger des aspects bas niveau (connexion à la base de données) avec des aspects métier.

2. Couche de Persistance (persistence)

   Gestion des Identifiants :
   Vérifiez le type d'identifiant retourné par la base de données (oracle.sql.ROWID). Assurez-vous que les types sont compatibles avec votre code Java. Utilisez des objets ou des méthodes spécifiques pour convertir le type correctement.

   Reutilisation du Code :
   Uniformisez l'utilisation de AddressUtils dans les différentes classes mappers. Évitez toute redondance dans la manipulation des adresses.
   Suivez le design pattern DAO pour garder les classes mapper centrées uniquement sur les opérations CRUD.

   Exceptions Personnalisées :
   Ajoutez plus de détails aux exceptions lancées dans les classes de persistance, comme le message d'erreur personnalisé et la cause (SQLException).

3. Couche de Présentation (presentation)

   Séparation des Responsabilités :
   Assurez-vous que la couche présentation reste simple et ne contient que de la logique relative à l'affichage ou aux interactions utilisateur. Déplacez toute logique métier dans la couche service.
   En ce qui concerne les classes CLI, évitez la redondance dans la saisie d'adresse. Recherchez des moyens de partager le code entre les classes d'entrée pour différentes entités (par exemple, Customer, Restaurant).

4. Couche Utilitaire et Exceptions (utils, exceptions)

   Gestion des Connexions :
   Dans ConnectionManager, envisagez d'améliorer la gestion des connexions en utilisant try-with-resources directement pour réduire les risques de fuite de ressources.

   Exceptions Personnalisées :
   Créez des exceptions spécifiques à chaque couche. Par exemple, ServiceException pour la couche service, PersistenceException pour la couche persistance, etc., afin de mieux isoler les erreurs.

5. Tests (src/test)

   Mocking et Isolation :
   Utilisez systématiquement des mocks pour la couche persistance (RestaurantMapper et autres) dans vos tests de service pour éviter les dépendances directes avec la base de données.
   Réduisez l'utilisation des connexions réelles dans les tests unitaires. Envisagez d'utiliser un framework de base de données en mémoire (comme H2) pour vos tests d'intégration.

   Couverture des Tests :
   Augmentez la couverture des tests, en particulier pour les cas d'erreurs (exceptions). Assurez-vous de tester chaque méthode dans chaque couche (service, persistance, etc.).
   Utilisez des noms de méthode de test descriptifs pour indiquer ce qui est testé, quel est le cas d'utilisation et quel est le résultat attendu.
   Ajoutez des tests pour vérifier que les exceptions sont correctement lancées dans les situations appropriées.

   Indépendance des Tests :
   Utilisez @AfterEach pour réinitialiser les ressources entre les tests et vous assurer que chaque test est indépendant et ne dépend pas de l'état laissé par un autre test.

6. Structure Générale et Organisation des Dossiers

   Injection de Dépendances :
   Remplacez les instanciations directes des mappers par des injections de dépendances, ce qui facilitera le test unitaire de vos services et rendra votre code plus flexible.

   Documentation :
   Assurez-vous d'ajouter des javadocs sur toutes les classes et méthodes publiques pour expliquer leur rôle, leurs paramètres et leurs retours. Cela facilitera la compréhension et la maintenance du code.

   Tests d'Intégration :
   Envisagez d'ajouter des tests d'intégration dans un dossier spécifique (src/integration-test) pour tester les interactions entre la couche service et persistance, en utilisant la vraie base de données ou une base en mémoire.