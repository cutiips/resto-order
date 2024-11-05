# prérequis

- Version utilisée
    - JDK : 21
    - Maven : 3.6 ou supérieur

# instruction

1. Ajouter un fichier `config.properties` dans le dossier ressources (soit sous : src/main/resources/config.properties) avec les configurations suivantes:

```markdown
db.url=*remplacer par l'url*
db.username=*username*
db.password=*password*
```

1. Lancer les scripts dans la base de données 
2. Lancer l'application
3. Tests

   ⚠️ les tests sont lancés sur la même base de données, des modifications ou des suppressions des valeurs surviennent si vous lancer les tests.
   *nous avons fait le choix d'insérer dans la même db par manque de ressources (pas d'autres db à disposition).*

   *Les tests ont été crées pour pouvoir tester les mises en cache, nous avons enrichis ceux-ci pour tester directement d’autres aspects de l’application et pour découvrir les tests (n’avons jamais pratiqué cela dans le cadre de notre bachelor).*


# tests

Exécuter les tests unitaires et d'intégration

```
mvn test
```

[](https://www.notion.so/135af37e49fe8017ae1cf45450ac825b?pvs=21)