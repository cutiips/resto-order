package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.ProductMapper;
import ch.hearc.ig.orderresto.persistence.RestaurantMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List ;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ProductCLI extends AbstractCLI {

    private final ProductMapper productMapper = new ProductMapper();

    public void run(Restaurant restaurant) throws SQLException {
        this.ln("======================================================");
        this.ln("Gestion des produits pour le restaurant : " + restaurant.getName());
        this.ln("0. Retour au menu principal");
        this.ln("1. Ajouter un nouveau produit");
        this.ln("2. Consulter un produit existant");
        this.ln("3. Mettre à jour un produit");
        this.ln("4. Supprimer un produit");
        this.ln("5. Voir tous les produits");
        int userChoice = this.readIntFromUser(5);
        this.handleUserChoice(userChoice, restaurant);
    }

    private void handleUserChoice(int userChoice, Restaurant restaurant) throws SQLException {
        switch (userChoice) {
            case 0:
                return;
            case 1:
                addProduct(restaurant);
                break;
            case 2:
                displayProductsForRestaurant(restaurant);
                break;
            case 3:
                updateProduct(restaurant);
                break;
            case 4:
                deleteProduct();
                break;
            default:
                this.ln("Choix non valide, veuillez réessayer.");
        }
        this.run(restaurant);
    }

    public void addProduct(Restaurant restaurant) throws SQLException {
        this.ln("Ajouter un nouveau produit - nom du produit : ");
        String name = this.readStringFromUser();

        this.ln("Prix unitaire : ");
        BigDecimal unitPrice = new BigDecimal(this.readStringFromUser());

        this.ln("Description du produit : ");
        String description = this.readStringFromUser();

        Product product = new Product(null, name, unitPrice, description, restaurant);

        productMapper.insert(product);
        this.ln("Produit ajouté avec succès !");
    }

    public void updateProduct(Restaurant restaurant) throws SQLException {
        this.ln("Voici la liste des produits (ID et Nom) :");
        displayProductsForRestaurant(restaurant);  // Affichage des ID et noms des produits

        this.ln("Entrez l'ID du produit à mettre à jour : ");
        Long id = this.readLongFromUser();
        Product existingProduct = productMapper.findById(id);
        if (existingProduct == null) {
            this.ln("Produit non trouvé.");
            return;
        }

        this.ln("Nouveau nom (actuel : " + existingProduct.getName() + ", appuyez sur Entrée pour garder inchangé) : ");
        String newName = this.readStringFromUser();
        if (newName.isEmpty()) {
            newName = existingProduct.getName();
        }

        this.ln("Nouveau prix unitaire (actuel : " + existingProduct.getUnitPrice() + ", appuyez sur Entrée pour garder inchangé) : ");
        String newPriceStr = this.readStringFromUser();
        BigDecimal newUnitPrice = newPriceStr.isEmpty() ? existingProduct.getUnitPrice() : new BigDecimal(newPriceStr);

        this.ln("Nouvelle description (actuelle : " + existingProduct.getDescription() + ", appuyez sur Entrée pour garder inchangé) : ");
        String newDescription = this.readStringFromUser();
        if (newDescription.isEmpty()) {
            newDescription = existingProduct.getDescription();
        }

        Product updatedProduct = new Product(id, newName, newUnitPrice, newDescription, restaurant);
        productMapper.update(updatedProduct);
        this.ln("Produit mis à jour avec succès !");
    }

    public void deleteProduct() throws SQLException {
        this.ln("Entrez l'ID du produit à supprimer : ");
        Long id = this.readLongFromUser();
        productMapper.delete(id);
        this.ln("Produit supprimé avec succès !");
    }


    /**
     * 🎛️ Affiche tous les produits associés à un restaurant.
     * @param restaurant Le restaurant pour lequel afficher les produits.
     */
    public Product displayProductsForRestaurant(Restaurant restaurant) {
        this.ln("Produits disponibles chez " + restaurant.getName() + ":");

        try {
            // Récupération des produits associés au restaurant
            List<Product> products = productMapper.findByRestaurant(restaurant);

            if (products.isEmpty()) {
                this.ln("Aucun produit trouvé pour ce restaurant.");
                return null;
            }

            // Affichage de chaque produit avec un index pour le choix de l'utilisateur
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                this.ln(String.format("%d. %s - %.2f CHF (%s)", i, product.getName(), product.getUnitPrice(), product.getDescription()));
            }

            // Lecture de l'index choisi par l'utilisateur
            int index = this.readIntFromUser(products.size() - 1);
            return products.get(index); // Retourne le produit sélectionné

        } catch (SQLException e) {
            this.ln("Erreur lors de la récupération des produits : " + e.getMessage());
            return null;
        }
    }

    /**
     * 📝 Affiche les détails d'un produit.
     * @param product Le produit à afficher.
     */
    private void displayProduct(Product product) {
        this.ln(String.format("Nom: %s, Prix: %.2f CHF, Description: %s",
                product.getName(),
                product.getUnitPrice(),
                product.getDescription()));
    }


    /* public Product getRestaurantProduct(Restaurant restaurant) {
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
     */
}
