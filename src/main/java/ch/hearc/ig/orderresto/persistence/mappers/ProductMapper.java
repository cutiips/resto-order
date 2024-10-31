package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductMapper extends BaseMapper<Product> {

    private Product extractProductFromResultSet(ResultSet rs) throws ProductPersistenceException {
        try {
            Long productId = rs.getLong("numero");
            String name = rs.getString("nom");
            BigDecimal price = rs.getBigDecimal("prix_unitaire");
            String description = rs.getString("description");
            Long restaurantId = rs.getLong("fk_resto");
            Restaurant restaurant = getRestaurantById(restaurantId);

            return new Product(productId, name, price, description, restaurant);

        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de l'extraction du produit du ResultSet", e);
        }
    }

    // Méthode pour trouver un produit par ID avec utilisation de l'IdentityMap
    public Product findById(Long id) throws ProductPersistenceException {
        // Vérifie si le produit est déjà en cache
        Optional<Product> cachedProduct = findInCache(id);
        if (cachedProduct.isPresent()) {
            return cachedProduct.get();
        }

        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit WHERE numero = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                addToCache(id, product); // Ajoute le produit au cache
                return product;
            }
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération du produit avec ID: " + id, e);
        }
        return null;
    }

    // Méthode pour insérer un produit et l'ajouter dans le cache
    public void insert(Product product) throws ProductPersistenceException {
        String sql = "INSERT INTO Produit (nom, prix_unitaire, description, fk_resto) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getUnitPrice());
            statement.setString(3, product.getDescription());
            statement.setLong(4, product.getRestaurant().getId());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getLong(1));
                addToCache(product.getId(), product); // Ajoute le produit au cache
            }
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de l'insertion du produit: " + product, e);
        }
    }

    // Méthode pour mettre à jour un produit et mettre à jour le cache
    public void update(Product product) throws ProductPersistenceException {
        String sql = "UPDATE Produit SET nom = ?, prix_unitaire = ?, description = ?, fk_resto = ? WHERE numero = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getUnitPrice());
            statement.setString(3, product.getDescription());
            statement.setLong(4, product.getRestaurant().getId());
            statement.setLong(5, product.getId());

            statement.executeUpdate();
            updateInCache(product.getId(), product); // Met à jour le produit dans le cache
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la mise à jour du produit: " + product, e);
        }
    }

    // Méthode pour supprimer un produit et le retirer du cache
    public void delete(Long id) throws ProductPersistenceException {
        String sql = "DELETE FROM Produit WHERE numero = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
            removeFromCache(id); // Supprime le produit du cache
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la suppression du produit avec ID: " + id, e);
        }
    }

    // Méthode pour récupérer un restaurant par ID
    private Restaurant getRestaurantById(Long restaurantId) throws ProductPersistenceException {
        RestaurantMapper restaurantMapper = new RestaurantMapper();
        try {
            return restaurantMapper.findById(restaurantId);
        } catch (RestaurantPersistenceException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération du restaurant avec ID: " + restaurantId, e);
        }
    }

    // Méthode pour récupérer tous les produits d'un restaurant par ID avec ajout au cache
    public List<Product> getProductsByRestaurantId(Long restaurantId) throws ProductPersistenceException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit WHERE fk_resto = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, restaurantId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                products.add(product);
                addToCache(product.getId(), product); // Ajoute chaque produit au cache
            }
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération des produits pour le restaurant avec ID: " + restaurantId, e);
        }
        return products;
    }
}