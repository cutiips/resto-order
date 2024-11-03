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
    private final RestaurantMapper restaurantMapper = new RestaurantMapper();

    private Product extractProductFromResultSet(ResultSet rs, Connection conn) throws ProductPersistenceException {
        try {
            Long productId = rs.getLong("numero");
            String name = rs.getString("nom");
            BigDecimal price = rs.getBigDecimal("prix_unitaire");
            String description = rs.getString("description");
            Long restaurantId = rs.getLong("fk_resto");
            Restaurant restaurant = getRestaurantById(restaurantId, conn);

            return new Product(productId, name, price, description, restaurant);

        } catch (SQLException | RestaurantPersistenceException e) {
            throw new ProductPersistenceException("Erreur lors de l'extraction du produit du ResultSet", e);
        }
    }

    // Méthode pour trouver un produit par ID avec utilisation de l'IdentityMap
    public Product read(Long id, Connection conn) throws ProductPersistenceException {
        // Vérifie si le produit est déjà en cache
        Optional<Product> cachedProduct = findInCache(id);
        if (cachedProduct.isPresent()) {
            return cachedProduct.get();
        }

        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit WHERE numero = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();



            if (rs.next()) {
                Product product = extractProductFromResultSet(rs, conn);
                addToCache(id, product); // Ajoute le produit au cache
                return product;
            }
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération du produit avec ID: " + id, e);
        }
        return null;
    }

    // Méthode pour insérer un produit et l'ajouter dans le cache
    public void insert(Product product, Connection conn) throws ProductPersistenceException {
        String sql = "INSERT INTO Produit (nom, prix_unitaire, description, fk_resto) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql, new String[]{"NUMERO"})) {
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
    public void update(Product product, Connection conn) throws ProductPersistenceException {
        String sql = "UPDATE Produit SET nom = ?, prix_unitaire = ?, description = ?, fk_resto = ? WHERE numero = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
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
    public void delete(Long id, Connection conn) throws ProductPersistenceException {
        String sql = "DELETE FROM Produit WHERE numero = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
            removeFromCache(id); // Supprime le produit du cache
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la suppression du produit avec ID: " + id, e);
        }
    }

    // Méthode pour récupérer un restaurant par ID
    private Restaurant getRestaurantById(Long restaurantId, Connection conn) throws RestaurantPersistenceException {
        return restaurantMapper.read(restaurantId, conn);
    }

    // Méthode pour récupérer tous les produits d'un restaurant par ID avec ajout au cache
    //TODO : implémenter une verification dans le cache findInCache() pour voir si certains produits sont déjà présents - évite de les ajouter plusieurs fois
    public List<Product> getProductsByRestaurantId(Long restaurantId, Connection conn) throws ProductPersistenceException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit WHERE fk_resto = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, restaurantId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = extractProductFromResultSet(rs, conn);
                products.add(product);
                addToCache(product.getId(), product); // Ajoute chaque produit au cache
            }
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération des produits pour le restaurant avec ID: " + restaurantId, e);
        }
        return products;
    }

    // Méthode pour récupérer tous les produits
    public List<Product> findAll(Connection conn) throws ProductPersistenceException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = extractProductFromResultSet(rs, conn);
                products.add(product);
                addToCache(product.getId(), product); // Ajoute chaque produit au cache
            }
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération de tous les produits", e);
        }
        return products;
    }
}
