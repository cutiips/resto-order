package ch.hearc.ig.orderresto.presentation.cli;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.presentation.AbstractCLI;
import ch.hearc.ig.orderresto.service.exceptions.CustomerServiceException;


import java.sql.SQLException;

public class MainCLI extends AbstractCLI {
    public void run() throws SQLException, ProductPersistenceException, RestaurantPersistenceException, CustomerPersistenceException, CustomerServiceException {
        this.ln("======================================================");
        this.ln("Que voulez-vous faire ?");
        this.ln("0. Quitter l'application");
        this.ln("1. Faire une nouvelle commande");
        this.ln("2. Consulter une commande");
        this.ln("3. Gérer les restaurants");
        int userChoice = this.readIntFromUser(3);
        this.handleUserChoice(userChoice);
    }

    private void handleUserChoice(int userChoice) throws SQLException, ProductPersistenceException, RestaurantPersistenceException, CustomerPersistenceException, CustomerServiceException {
        if (userChoice == 0) {
            this.ln("Good bye!");
            return;
        }
        OrderCLI orderCLI = new OrderCLI();
        // TODO : remplacer FakeDb par la base de données
        if (userChoice == 1) {
            Order newOrder = orderCLI.createNewOrder();
        } else if (userChoice == 2) {
            Order existingOrder = orderCLI.selectOrder();
            if (existingOrder != null) {
                orderCLI.displayOrder(existingOrder);
            }
        } else if (userChoice == 3) {
            RestaurantCLI restaurantCLI = new RestaurantCLI();
            restaurantCLI.run();
        }
    }

}
