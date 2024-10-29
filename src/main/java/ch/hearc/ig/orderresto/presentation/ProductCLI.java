package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.ProductMapper;

import java.util.List ;


public class ProductCLI extends AbstractCLI {
    public Product getRestaurantProduct(Restaurant restaurant) throws ProductPersistenceException {
        this.ln(String.format("Bienvenue chez %s. Choisissez un de nos produits:", restaurant.getName()));

        ProductMapper productMapper = new ProductMapper();
        // récupérer les produits du restaurant
        List<Product> products = productMapper.findProductsByRestaurant(restaurant);

        if (products.isEmpty()) {
            this.ln("Aucun produit disponible pour ce restaurant.");
            return null;
        }

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            this.ln(String.format("%d. %s - %.2f CHF (%s)", i, product.getName(), product.getUnitPrice(), product.getDescription()));
        }

        int index = this.readIntFromUser(products.size() - 1);
        return products.get(index);
    }
}
