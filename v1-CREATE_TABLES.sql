-- DROP SEQUENCE SEQ_RESTAURANT;
-- DROP SEQUENCE SEQ_CLIENT;
-- DROP SEQUENCE SEQ_COMMANDE;
-- DROP SEQUENCE SEQ_PRODUIT;

-- DROP TABLE PRODUIT_COMMANDE CASCADE CONSTRAINTS;
-- DROP TABLE COMMANDE CASCADE CONSTRAINTS;
-- DROP TABLE CLIENT CASCADE CONSTRAINTS;
-- DROP TABLE PRODUIT CASCADE CONSTRAINTS;
-- DROP TABLE RESTAURANT CASCADE CONSTRAINTS;
-- DROP TABLE ADDRESS CASCADE CONSTRAINTS;

-- Création de la table RESTAURANT
CREATE TABLE RESTAURANT (
        numero number(9) NOT NULL,
        nom varchar2(255) NOT NULL,
        fk_address number(9) NOT NULL, -- Clé étrangère vers ADDRESS
        PRIMARY KEY (numero),
        FOREIGN KEY (fk_address) REFERENCES ADDRESS(code_postal) -- Référence à l'adresse
);

-- Création de la table ADDRESS
CREATE TABLE ADDRESS (
     codePays char(2) NOT NULL, -- countryCode
     code_postal char(4) NOT NULL, -- postalCode
     localite varchar2(255) NOT NULL, -- locality
     rue varchar2(255) NOT NULL, -- street
     num_rue varchar2(4), -- streetNumber
     PRIMARY KEY (code_postal, codePays) -- Clé primaire composée pour garantir l'unicité
);

-- Création de la table PRODUIT
CREATE TABLE PRODUIT (
     numero number(9) NOT NULL,
     fk_resto number(9) NOT NULL,
     prix_unitaire number(9,2) NOT NULL,
     nom varchar2(255) NOT NULL,
     description varchar2(255) NOT NULL,
     PRIMARY KEY (numero),
     FOREIGN KEY (fk_resto) REFERENCES RESTAURANT(numero) ON DELETE CASCADE
);

-- Création de la table CLIENT
CREATE TABLE CLIENT (
    numero number(9) NOT NULL,
    email varchar2(255) NOT NULL,
    telephone varchar2(255) NOT NULL,
    nom varchar2(255) NOT NULL,
    fk_address number(9) NOT NULL, -- Clé étrangère vers ADDRESS
    est_une_femme CHAR(1),
    prenom varchar2(255),
    forme_sociale varchar2(5),
    type char(1) NOT NULL,
    PRIMARY KEY (numero),
    FOREIGN KEY (fk_address) REFERENCES ADDRESS(code_postal) -- Référence à l'adresse
);

ALTER TABLE CLIENT ADD CONSTRAINT CK_TYPE CHECK (type IN ('P', 'O'));
ALTER TABLE CLIENT ADD CONSTRAINT CK_FORME_SOCIALE CHECK ((type = 'P' AND forme_sociale IS NULL) OR (type = 'O' AND forme_sociale IN ('SA', 'F', 'A')));
ALTER TABLE CLIENT ADD CONSTRAINT CK_PRENOM CHECK ((type = 'P' AND prenom IS NOT NULL) OR (type = 'O' AND prenom IS NULL));
ALTER TABLE CLIENT ADD CONSTRAINT CK_EST_UNE_FEMME CHECK ((type = 'P' AND est_une_femme IN('O', 'N')) OR (type = 'O' AND est_une_femme IS NULL));

-- Création de la table COMMANDE
CREATE TABLE COMMANDE (
      numero number(9) NOT NULL,
      fk_client number(9) NOT NULL,
      fk_resto number(9) NOT NULL,
      a_emporter char(1) NOT NULL,
      quand date NOT NULL,
      PRIMARY KEY (numero),
      FOREIGN KEY (fk_client) REFERENCES CLIENT(numero) ON DELETE CASCADE,
      FOREIGN KEY (fk_resto) REFERENCES RESTAURANT(numero)
);

ALTER TABLE COMMANDE ADD CONSTRAINT CK_A_EMPORTER CHECK (a_emporter IN('O', 'N'));

-- Création de la table PRODUIT_COMMANDE
CREATE TABLE PRODUIT_COMMANDE (
              fk_commande number(9) NOT NULL,
              fk_produit number(9) NOT NULL,
              PRIMARY KEY (fk_commande, fk_produit),
              FOREIGN KEY (fk_commande) REFERENCES COMMANDE(numero) ON DELETE CASCADE,
              FOREIGN KEY (fk_produit) REFERENCES PRODUIT(numero) ON DELETE CASCADE
);

-- Création des séquences
CREATE SEQUENCE SEQ_RESTAURANT;
CREATE SEQUENCE SEQ_CLIENT;
CREATE SEQUENCE SEQ_COMMANDE;
CREATE SEQUENCE SEQ_PRODUIT;

-- Création des triggers pour l'insertion automatique des ID
CREATE OR REPLACE TRIGGER TR_BI_RESTAURANT
BEFORE INSERT ON RESTAURANT
FOR EACH ROW
BEGIN
IF :NEW.NUMERO IS NULL THEN
:NEW.NUMERO := SEQ_RESTAURANT.NEXTVAL;
END IF;
END;
/

CREATE OR REPLACE TRIGGER TR_BI_CLIENT
BEFORE INSERT ON CLIENT
FOR EACH ROW
BEGIN
IF :NEW.NUMERO IS NULL THEN
:NEW.NUMERO := SEQ_CLIENT.NEXTVAL;
END IF;
END;
/

CREATE OR REPLACE TRIGGER TR_BI_COMMANDE
BEFORE INSERT ON COMMANDE
FOR EACH ROW
BEGIN
IF :NEW.NUMERO IS NULL THEN
:NEW.NUMERO := SEQ_COMMANDE.NEXTVAL;
END IF;
END;
/

CREATE OR REPLACE TRIGGER TR_BI_PRODUIT
BEFORE INSERT ON PRODUIT
FOR EACH ROW
BEGIN
IF :NEW.NUMERO IS NULL THEN
:NEW.NUMERO := SEQ_PRODUIT.NEXTVAL;
END IF;
END;
/
