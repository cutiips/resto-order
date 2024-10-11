package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.FakeDb;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;

import java.sql.SQLException;

public class RestaurantCLI extends AbstractCLI {

    private final RestaurantMapper restaurantMapper = new RestaurantMapper();

    public void run() {
        this.ln("======================================================");
        this.ln("Gestion des restaurants");
        this.ln("0. Retour au menu principal");
        this.ln("1. Ajouter un nouveau restaurant");
        this.ln("2. Consulter un restaurant existant");
        int userChoice = this.readIntFromUser(2);
        this.handleUserChoice(userChoice);
    }

    private void handleUserChoice(int userChoice) {
        switch (userChoice) {
            case 0:
                return;
            case 1:
                addRestaurant();
                break;
            case 2:
                Restaurant restaurant = getExistingRestaurant();
                if (restaurant != null) {
                    displayRestaurant(restaurant);
                }
                break;
            default:
                this.ln("Choix non valide, veuillez réessayer.");
        }
        this.run();
    }

    private void addRestaurant() {
        this.ln("Ajouter un nouveau restaurant - nom du restaurant : ");
        String name = this.readStringFromUser();


        this.ln("Code du pays : ");
        String countryCode = this.readStringFromUser();
        this.ln("Code postal : ");
        String postalCode = this.readStringFromUser();
        this.ln("Localité : ");
        String locality = this.readStringFromUser();
        this.ln("Rue : ");
        String street = this.readStringFromUser();
        this.ln("Numéro de rue : ");
        String streetNumber = this.readStringFromUser();

        Address address = new Address(countryCode, postalCode, locality, street, streetNumber);
        Restaurant restaurant = new Restaurant(null, name, address);

        try {
            restaurantMapper.insert(restaurant);
            this.ln("Restaurant ajouté avec succès !");
        } catch (SQLException e) {
            this.ln("Erreur lors de l'insertion du restaurant : " + e.getMessage());
        }
    }

    private void displayRestaurant(Restaurant restaurant) {
        this.ln(String.format("Nom: %s, Adresse: %s %s, %s %s",
                restaurant.getName(),
                restaurant.getAddress().getStreet(),
                restaurant.getAddress().getStreetNumber(),
                restaurant.getAddress().getPostalCode(),
                restaurant.getAddress().getLocality()));
    }


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
}
