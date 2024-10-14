package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;

import java.util.ArrayList;
import java.util.List ;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductCLI extends AbstractCLI {

    public Product getRestaurantProduct(Restaurant restaurant) {
        this.ln(String.format("Bienvenue chez %s. Choisissez un de nos produits:", restaurant.getName()));

        // Récupérer la liste des produits du restaurant
        List<Product> products = new ArrayList<>(restaurant.getProductsCatalog());

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            this.ln(String.format("%d. %s - %.2f CHF (%s)", i, product.getName(), product.getUnitPrice(), product.getDescription()));
        }

        int index = this.readIntFromUser(products.size() - 1);  // Demande à l'utilisateur de choisir un produit
        return products.get(index);  // Retourne le produit sélectionné
    }
}
