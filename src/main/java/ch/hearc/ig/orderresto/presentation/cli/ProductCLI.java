package ch.hearc.ig.orderresto.presentation.cli;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.ProductMapper;
import ch.hearc.ig.orderresto.presentation.AbstractCLI;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List ;


public class ProductCLI extends AbstractCLI {

    private final ProductMapper productMapper = new ProductMapper();

    private Product selectProductFromList(List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            this.ln(String.format("%d. %s - %.2f CHF (%s)", i, product.getName(), product.getUnitPrice(), product.getDescription()));
        }

        int index = this.readIntFromUser(products.size() - 1);
        return products.get(index);
    }

    public void run(Restaurant restaurant) throws SQLException, ProductPersistenceException {
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

    private void handleUserChoice(int userChoice, Restaurant restaurant) throws SQLException, ProductPersistenceException {
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

    public void addProduct(Restaurant restaurant) throws SQLException, ProductPersistenceException {
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

    public void updateProduct(Restaurant restaurant) throws SQLException, ProductPersistenceException {
        this.ln("Voici la liste des produits (ID et Nom) :");

        // Récupérer les produits associés au restaurant
        List<Product> products = productMapper.getProductsByRestaurantId(restaurant.getId());

        if (products.isEmpty()) {
            this.ln("Aucun produit trouvé pour ce restaurant.");
            return;
        }

        // Afficher la liste des produits pour que l’utilisateur puisse sélectionner un produit par son ID
        for (Product product : products) {
            this.ln("ID: " + product.getId() + " - Nom: " + product.getName());
        }

        this.ln("Entrez l'ID du produit à mettre à jour : ");
        Long id = this.readLongFromUser();

        // Vérifier si le produit existe
        Product existingProduct = productMapper.findById(id);
        if (existingProduct == null || !existingProduct.getRestaurant().getId().equals(restaurant.getId())) {
            this.ln("Produit non trouvé ou n'appartient pas à ce restaurant.");
            return;
        }

        // Demander à l'utilisateur les nouvelles valeurs pour les champs, en conservant les valeurs existantes si rien n'est entré
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

        // Créer un nouvel objet Product avec les valeurs mises à jour et l’ID du produit existant
        Product updatedProduct = new Product(id, newName, newUnitPrice, newDescription, restaurant);

        // Mettre à jour le produit dans la base de données
        productMapper.update(updatedProduct);
        this.ln("Produit mis à jour avec succès !");
    }


    public void deleteProduct() throws SQLException, ProductPersistenceException {
        this.ln("Entrez l'ID du produit à supprimer : ");
        Long id = this.readLongFromUser();
        productMapper.delete(id);
        this.ln("Produit supprimé avec succès !");
    }


    /**
     * 🎛️ Affiche tous les produits associés à un restaurant.
     *
     * @param restaurant Le restaurant pour lequel afficher les produits.
     * @return
     */
    public Product displayProductsForRestaurant(Restaurant restaurant) {
        this.ln("Produits disponibles chez " + restaurant.getName() + ":");

        try {
            // Récupération des produits associés au restaurant
            List<Product> products = productMapper.getProductsByRestaurantId(restaurant.getId());

            if (products.isEmpty()) {
                this.ln("Aucun produit trouvé pour ce restaurant.");
                return null;
            }

            selectProductFromList(products);

        } catch (ProductPersistenceException e) {
            throw new RuntimeException(e);
        }
        return null;
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
    }    public Product getRestaurantProduct(Restaurant restaurant) throws ProductPersistenceException {
        this.ln(String.format("Bienvenue chez %s. Choisissez un de nos produits:", restaurant.getName()));

        ProductMapper productMapper = new ProductMapper();
        // récupérer les produits du restaurant
        List<Product> products = productMapper.getProductsByRestaurantId(restaurant.getId());

        if (products.isEmpty()) {
            this.ln("Aucun produit disponible pour ce restaurant.");
            return null;
        }

        return selectProductFromList(products);

    }
}
