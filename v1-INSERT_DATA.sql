SET DEFINE OFF;

-- Insertion des adresses (ajout de cette étape pour peupler la table ADDRESS)
INSERT INTO ADDRESS(codePays, code_postal, localite, rue, num_rue)
VALUES ('CH', '2000', 'Neuchâtel', 'Place de La Gare', '2');
INSERT INTO ADDRESS(codePays, code_postal, localite, rue, num_rue)
VALUES ('CH', '2000', 'Neuchâtel', 'Pl. Blaise-Cendrars', '5');
INSERT INTO ADDRESS(codePays, code_postal, localite, rue, num_rue)
VALUES ('CH', '2000', 'Neuchâtel', 'Espa. de l''Europe', '1/3');
COMMIT;

-- Insertion des restaurants avec les clés étrangères d'adresse
INSERT INTO RESTAURANT(nom, fk_address)
VALUES ('Alpes Et Lac', (SELECT code_postal FROM ADDRESS WHERE localite = 'Neuchâtel' AND rue = 'Place de La Gare' AND num_rue = '2' AND codePays = 'CH'));
INSERT INTO RESTAURANT(nom, fk_address)
VALUES ('Les Belgeries', (SELECT code_postal FROM ADDRESS WHERE localite = 'Neuchâtel' AND rue = 'Pl. Blaise-Cendrars' AND num_rue = '5' AND codePays = 'CH'));
INSERT INTO RESTAURANT(nom, fk_address)
VALUES ('Domino''s Pizza', (SELECT code_postal FROM ADDRESS WHERE localite = 'Neuchâtel' AND rue = 'Espa. de l''Europe' AND num_rue = '1/3' AND codePays = 'CH'));
COMMIT;

-- Insertion des produits
INSERT INTO PRODUIT(fk_resto, prix_unitaire, nom, description)
VALUES (1, 20, 'Tartare de chevreuil', 'De saison');
INSERT INTO PRODUIT(fk_resto, prix_unitaire, nom, description)
VALUES (2, 5, 'Frites mini', '150g de frites + sauce au choix');
INSERT INTO PRODUIT(fk_resto, prix_unitaire, nom, description)
VALUES (2, 7.5, 'Frites normales', '250g de frites + sauce au choix');
INSERT INTO PRODUIT(fk_resto, prix_unitaire, nom, description)
VALUES (3, 16, 'MARGHERITA', 'Sauce tomate, extra mozzarella (45% MG/ES)');
INSERT INTO PRODUIT(fk_resto, prix_unitaire, nom, description)
VALUES (3, 18, 'VÉGÉTARIENNE', 'Sauce tomate, mozzarella (45% MG/ES), champignons, poivrons, tomates cherry, olives, oignons rouges');
INSERT INTO PRODUIT(fk_resto, prix_unitaire, nom, description)
VALUES (3, 21, 'CHEESE & HAM', 'Sauce tomate, mozzarella (45% MG/ES), jambon (CH)');
COMMIT;

-- Insertion des clients avec référence à l'adresse
INSERT INTO CLIENT(type, email, est_une_femme, nom, prenom, forme_sociale, fk_address, telephone)
VALUES ('P', 'vincent.pazeller@he-arc.ch', 'N', 'Pazeller', 'Vincent', NULL,
        (SELECT code_postal FROM ADDRESS WHERE localite = 'Le Landeron' AND rue = 'Rue du test' AND num_rue = '2' AND codePays = 'CH'),
        '+41 76 000 00 00');

INSERT INTO CLIENT(type, email, est_une_femme, nom, prenom, forme_sociale, fk_address, telephone)
VALUES ('O', 'info@rhne.ch', NULL, 'Hôpital Pourtales', NULL, 'SA',
        (SELECT code_postal FROM ADDRESS WHERE localite = 'Neuchâtel' AND rue = 'Rue du test' AND num_rue = '5b' AND codePays = 'CH'),
        '+41 32 000 00 00');
COMMIT;

-- Insertion des villes
INSERT INTO VILLES(code_postal, nom_ville) VALUES ('2000', 'Neuchâtel');
COMMIT;

-- Remarque : La table RESTAURANTS n'existe plus dans la nouvelle structure, elle est remplacée par RESTAURANT
-- En utilisant le nom correct de la table, insérer d'autres restaurants si nécessaire.

-- Insertion des commentaires
INSERT INTO COMMENTAIRES(date_eval, commentaire, nom_utilisateur, fk_rest) VALUES (sysdate, 'Génial !', 'Toto', 1);
INSERT INTO COMMENTAIRES(date_eval, commentaire, nom_utilisateur, fk_rest) VALUES (sysdate, 'Très bon', 'Titi', 1);
INSERT INTO COMMENTAIRES(date_eval, commentaire, nom_utilisateur, fk_rest) VALUES (sysdate, 'Un régal !', 'Dupont', 2);
INSERT INTO COMMENTAIRES(date_eval, commentaire, nom_utilisateur, fk_rest) VALUES (sysdate, 'Rien à dire, le top !', 'Dupasquier', 2);
COMMIT;

-- Insertion des notes
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (4, 1, 1);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (5, 1, 2);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (4, 1, 3);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (4, 2, 1);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (4, 2, 2);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (4, 2, 3);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (5, 3, 1);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (5, 3, 2);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (5, 3, 3);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (5, 4, 1);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (5, 4, 2);
INSERT INTO NOTES(note, fk_comm, fk_crit) VALUES (5, 4, 3);
COMMIT;

-- Insertion des likes
INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES ('T', sysdate, '1.2.3.4', 1);
INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES ('T', sysdate, '1.2.3.5', 1);
INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES ('F', sysdate, '1.2.3.6', 1);
INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES ('T', sysdate, '1.2.3.7', 2);
INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES ('T', sysdate, '1.2.3.8', 2);
INSERT INTO LIKES(appreciation, date_eval, adresse_ip, fk_rest) VALUES ('T', sysdate, '1.2.3.9', 2);
COMMIT;
