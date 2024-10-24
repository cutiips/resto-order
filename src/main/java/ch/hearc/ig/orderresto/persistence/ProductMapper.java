package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ProductMapper {
    private String url;
    private String username;
    private String password;

    private void loadProperties() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
        } catch (IOException ex) {
            System.err.println("Error loading properties: " + ex.getMessage());
            throw new RuntimeException("Database configuration error", ex);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Méthode pour trouver un produit par ID
    public Product findById(Long id) {
        Product product = null;
        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit WHERE numero = ?";
        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Récupérer les informations du produit
                Long productId = rs.getLong("numero");
                String name = rs.getString("nom");
                BigDecimal price = rs.getBigDecimal("prix_unitaire");
                String description = rs.getString("description");

                // Récupérer le restaurant associé au produit (par fk_resto)
                Long restaurantId = rs.getLong("fk_resto");
                Restaurant restaurant = findRestaurantById(restaurantId);  // Méthode à implémenter pour récupérer un Restaurant

                // Créer l'objet Product
                product = new Product(productId, name, price, description, restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }

    // Méthode pour récupérer tous les produits
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                // Récupérer les informations du produit
                Long productId = rs.getLong("numero");
                String name = rs.getString("nom");
                BigDecimal price = rs.getBigDecimal("prix_unitaire");
                String description = rs.getString("description");

                // Récupérer le restaurant associé
                Long restaurantId = rs.getLong("fk_resto");
                Restaurant restaurant = findRestaurantById(restaurantId);  // Méthode à implémenter

                // Créer et ajouter le produit à la liste
                products.add(new Product(productId, name, price, description, restaurant));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Méthode pour insérer un produit
    public void insert(Product product) {
        String sql = "INSERT INTO Produit (nom, prix_unitaire, description, fk_resto) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getUnitPrice());
            statement.setString(3, product.getDescription());
            statement.setLong(4, product.getRestaurant().getId()); // ID du restaurant

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour un produit existant
    public void update(Product product) {
        String sql = "UPDATE Produit SET nom = ?, prix_unitaire = ?, description = ?, fk_resto = ? WHERE numero = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getUnitPrice());
            statement.setString(3, product.getDescription());
            statement.setLong(4, product.getRestaurant().getId()); // ID du restaurant
            statement.setLong(5, product.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer un produit
    public void delete(Long id) {
        String sql = "DELETE FROM Produit WHERE numero = ?";

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode auxiliaire pour récupérer un restaurant par ID
    private Restaurant findRestaurantById(Long restaurantId) throws SQLException {
        RestaurantMapper restaurantMapper = new RestaurantMapper();
        return restaurantMapper.findById(restaurantId);  // Appelle le RestaurantMapper pour récupérer le Restaurant
    }
}
