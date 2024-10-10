package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductCLI extends AbstractCLI {

    public Product getRestaurantProduct(Restaurant restaurant) {
        this.ln(String.format("Bienvenue chez %s. Choisissez un de nos produits:", restaurant.getName()));
        Object[] products = restaurant.getProductsCatalog().toArray();
        for (int i = 0 ; i < products.length ; i++) {
            Product product = (Product) products[i];
            this.ln(String.format("%d. %s", i, product));
        }
        int index = this.readIntFromUser(products.length - 1);
        return (Product) products[index];
    }
}
