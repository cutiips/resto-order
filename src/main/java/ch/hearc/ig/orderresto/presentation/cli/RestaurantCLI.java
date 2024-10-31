package ch.hearc.ig.orderresto.presentation.cli;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.mappers.RestaurantMapper;
import ch.hearc.ig.orderresto.presentation.AbstractCLI;

import java.sql.SQLException;
import java.util.List;

/**
 * 🚀 Gère les interactions CLI pour les opérations CRUD des restaurants.
 * Permet d'ajouter, consulter, mettre à jour, supprimer et afficher les restaurants.
 */
public class RestaurantCLI extends AbstractCLI {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();

    /**
     * 🎛️ Démarre le menu de gestion des restaurants.
     * Affiche les options et permet à l'utilisateur de sélectionner une action.
     */
    public void run() throws SQLException, RestaurantPersistenceException {
        this.ln("======================================================");
        this.ln("Gestion des restaurants");
        this.ln("0. Retour au menu principal");
        this.ln("1. Ajouter un nouveau restaurant");
        this.ln("2. Consulter un restaurant existant");
        this.ln("3. Mettre à jour un restaurant");
        this.ln("4. Supprimer un restaurant");
        this.ln("5. Voir tous les restaurants");
        int userChoice = this.readIntFromUser(5);
        this.handleUserChoice(userChoice);
    }

    /**
     * 🎯 Gère le choix de l'utilisateur pour le menu des restaurants.
     * Exécute l'action correspondante en fonction du choix.
     * @param userChoice Choix de l'utilisateur (de 0 à 5).
     */
    private void handleUserChoice(int userChoice) throws SQLException, RestaurantPersistenceException {
        switch (userChoice) {
            case 0:
                return;
            case 1:
                addRestaurant();
                break;
            case 2:
                displayRestaurantIdsAndNames();
                break;
            case 3:
                updateRestaurant();
                break;
            case 4:
                deleteRestaurant();
                break;
            case 5:
                displayAllRestaurants();
                break;
            default:
                this.ln("Choix non valide, veuillez réessayer.");
        }
        this.run();
    }

    /**
     * ➕ Ajoute un nouveau restaurant avec son adresse.
     * Demande les informations du restaurant à l'utilisateur.
     */
    private void addRestaurant() {
        this.ln("Ajouter un nouveau restaurant - nom du restaurant : ");
        String name = this.readStringFromUser();

        readAddressFromUser();
        Address address = readAddressFromUser();

        Restaurant restaurant = new Restaurant(null, name, address);

        try {
            restaurantMapper.insert(restaurant);
            this.ln("Restaurant ajouté avec succès !");
        } catch (RestaurantPersistenceException e) {
            this.ln("Erreur lors de l'insertion du restaurant : " + e.getMessage());
        }
    }

    /**
     * 🔄 Met à jour un restaurant existant.
     * Demande l'ID du restaurant à mettre à jour, puis les nouvelles informations.
     * Si une entrée est vide, conserve l'ancienne valeur.
     */
    private void updateRestaurant() throws SQLException, RestaurantPersistenceException {
        this.ln("Voici la liste des restaurants (ID et Nom) :");
        displayRestaurantIdsAndNames();  // Affichage uniquement des ID et noms des restaurants

        this.ln("Entrez l'ID du restaurant à mettre à jour : ");
        Long id = this.readLongFromUser();
        try {
            Restaurant existingRestaurant = restaurantMapper.findById(id);
            if (existingRestaurant == null) {
                this.ln("Restaurant non trouvé.");
                return;
            }

            this.ln("Nouveau nom (actuel : " + existingRestaurant.getName() + ", appuyez sur Entrée pour garder inchangé) : ");
            String newName = this.readStringFromUserAllowEmpty();
            if (newName == null || newName.isEmpty()) {
                newName = existingRestaurant.getName();
            }

            this.ln("Nouveau code postal (actuel : " + existingRestaurant.getAddress().getPostalCode() + ", appuyez sur Entrée pour garder inchangé) : ");
            String newPostalCode = this.readStringFromUserAllowEmpty();
            if (newPostalCode.isEmpty()) {
                newPostalCode = existingRestaurant.getAddress().getPostalCode();
            }

            this.ln("Nouveau code pays (actuel : " + existingRestaurant.getAddress().getCountryCode() + ", appuyez sur Entrée pour garder inchangé) : ");
            String newCountryCode = this.readStringFromUserAllowEmpty();
            this.ln("newCountryCode - update: " + newCountryCode);
            if (newCountryCode.isEmpty()) {
                newCountryCode = existingRestaurant.getAddress().getCountryCode();
                this.ln("newCountryCode - default: " + newCountryCode);
            }

            this.ln("Nouvelle localité (actuel : " + existingRestaurant.getAddress().getLocality() + ", appuyez sur Entrée pour garder inchangé) : ");
            String newLocality = this.readStringFromUserAllowEmpty();
            if (newLocality.isEmpty()) {
                newLocality = existingRestaurant.getAddress().getLocality();
            }

            this.ln("Nouvelle rue (actuel : " + existingRestaurant.getAddress().getStreet() + ", appuyez sur Entrée pour garder inchangé) : ");
            String newStreet = this.readStringFromUserAllowEmpty();
            if (newStreet.isEmpty()) {
                newStreet = existingRestaurant.getAddress().getStreet();
            }

            this.ln("Nouveau numéro de rue (actuel : " + existingRestaurant.getAddress().getStreetNumber() + ", appuyez sur Entrée pour garder inchangé) : ");
            String newStreetNumber = this.readStringFromUserAllowEmpty();
            if (newStreetNumber.isEmpty()) {
                newStreetNumber = existingRestaurant.getAddress().getStreetNumber();
            }

            Address updatedAddress = new Address(
                    newCountryCode,
                    newPostalCode,
                    newLocality,
                    newStreet,
                    newStreetNumber
            );
            Restaurant updatedRestaurant = new Restaurant(id, newName, updatedAddress);
            restaurantMapper.update(updatedRestaurant);

            manageRestaurantProducts(updatedRestaurant);
            this.ln("Restaurant mis à jour avec succès !");

        } catch (SQLException | ProductPersistenceException e) {
            this.ln("Erreur lors de la mise à jour du restaurant : " + e.getMessage());
        }
    }

    private void manageRestaurantProducts(Restaurant restaurant) throws SQLException, ProductPersistenceException {
        ProductCLI productCLI = new ProductCLI();

        while (true) {
            this.ln("\nGestion des produits pour le restaurant " + restaurant.getName());
            this.ln("1. Ajouter un nouveau produit");
            this.ln("2. Mettre à jour un produit existant");
            this.ln("3. Supprimer un produit");
            this.ln("0. Retourner");

            int choice = this.readIntFromUser(3);

            switch (choice) {
                case 1:
                    productCLI.addProduct(restaurant);
                    break;
                case 2:
                    productCLI.updateProduct(restaurant);
                    break;
                case 3:
                    productCLI.deleteProduct();
                    break;
                case 0:
                    return; // Quitte le menu de gestion des produits
                default:
                    this.ln("Choix non valide, veuillez réessayer.");
            }
        }
    }

    /**
     * 🗑️ Supprime un restaurant par son ID.
     * Affiche la liste des restaurants avec leurs ID pour que l'utilisateur puisse choisir.
     */
    private void deleteRestaurant() throws SQLException, RestaurantPersistenceException {
        this.ln("Voici la liste des restaurants (ID et Nom) :");
        displayRestaurantIdsAndNames();

        this.ln("Entrez l'ID du restaurant à supprimer : ");
        Long id = this.readLongFromUser();
        restaurantMapper.delete(id);
        this.ln("Restaurant supprimé avec succès !");

    }

    /**
     * 📋 Affiche la liste complète des restaurants avec leurs informations.
     * Utilise la méthode displayRestaurant pour afficher chaque restaurant.
     */
    private void displayAllRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantMapper.findAll();
            if (restaurants.isEmpty()) {
                this.ln("Aucun restaurant trouvé.");
            } else {
                for (Restaurant restaurant : restaurants) {
                    displayRestaurant(restaurant);
                }
            }
        } catch (RestaurantPersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 📄 Affiche uniquement les IDs et les noms des restaurants.
     * Utile pour la mise à jour ou la suppression des restaurants.
     */
    public Restaurant displayRestaurantIdsAndNames() throws RestaurantPersistenceException {
        this.ln("Choisissez un restaurant:");
        List<Restaurant> allRestaurants = restaurantMapper.findAll();
        for (int i = 0 ; i < allRestaurants.size() ; i++) {
            Restaurant restaurant = (Restaurant) allRestaurants.get(i);
            this.ln(String.format("%d. %s.", i, restaurant.getName()));
        }
        int index = this.readIntFromUser(allRestaurants.size() - 1);
        return (Restaurant) allRestaurants.get(index);
//        try {
//            List<Restaurant> restaurants = restaurantMapper.findAll();
//            if (restaurants.isEmpty()) {
//                this.ln("Aucun restaurant trouvé.");
//            } else {
//                for (Restaurant restaurant : restaurants) {
//                    this.ln(String.format("ID: %d, Nom: %s", restaurant.getId(), restaurant.getName()));
//                }
//            }
//        } catch (SQLException e) {
//            this.ln("Erreur lors de la récupération des restaurants : " + e.getMessage());
//        }
    }

    @Deprecated
    public Restaurant getExistingRestaurant() {
        this.ln("Choisissez un restaurant:");
        Object[] allRestaurants = FakeDb.getRestaurants().toArray();
        for (int i = 0 ; i < allRestaurants.length ; i++) {
            Restaurant restaurant = (Restaurant) allRestaurants[i];
            this.ln(String.format("%d. %s.", i, restaurant.getName()));
        }
        int index = this.readIntFromUser(allRestaurants.length - 1);
        return (Restaurant) allRestaurants[index];
    }

    /**
     * 📝 Affiche les détails complets d'un restaurant.
     * @param restaurant Le restaurant à afficher.
     */
    private void displayRestaurant(Restaurant restaurant) {
        this.ln(String.format("Nom: %s, Adresse: %s %s, %s %s",
                restaurant.getName(),
                restaurant.getAddress().getStreet(),
                restaurant.getAddress().getStreetNumber(),
                restaurant.getAddress().getPostalCode(),
                restaurant.getAddress().getLocality()));
    }


}