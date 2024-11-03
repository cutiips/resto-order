# instruction
1. Ajouter un fichier config.properties dans le dossier resources avec les configurations suivantes:
```
db.url=_remplacer par l'url_
db.username=_username_
db.password=_password_
```
2. Lancer les scripts dans la base de données
3. Lancer l'application
   ⚠️ les tests ne doivent pas être lancés sur la même db (une db différente est nécessaire pour lancer les tests), sinon il risque d'y avoir des insertions ou des modifications de données dans la db

# tests
Exécuter les tests unitaires et d'intégration
```
mvn test
```
