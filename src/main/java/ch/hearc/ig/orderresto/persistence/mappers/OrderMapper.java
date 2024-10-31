package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.CustomerMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class OrderMapper {
    private String url;
    private String username;
    private String password;

    public OrderMapper() {
        loadProperties();
    }

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

    // Méthode pour trouver une commande par ID
    public Order findById(Long id) throws SQLException {
        Order order = null;
        String sql = "SELECT numero, fk_client, fk_resto, a_emporter, quand FROM Commande WHERE numero = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                order = mapRowToOrder(rs);
            }
        } catch (RestaurantPersistenceException e) {
            throw new RuntimeException(e);
        } catch (CustomerPersistenceException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    // Méthode pour récupérer toutes les commandes
    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT numero, fk_client, fk_resto, a_emporter, quand FROM Commande";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (RestaurantPersistenceException e) {
            throw new RuntimeException(e);
        } catch (CustomerPersistenceException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    // Méthode pour insérer une nouvelle commande
    public void insert(Order order) throws SQLException {
        String sql = "INSERT INTO Commande (numero, fk_client, fk_resto, a_emporter, quand) VALUES (SEQ_COMMANDE.NEXTVAL, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, new String[]{"numero"})) { // Spécifie la colonne de l'ID généré

            // Définition des paramètres
            statement.setLong(1, order.getCustomer().getId());
            statement.setLong(2, order.getRestaurant().getId());
            statement.setString(3, order.getTakeAway() ? "O" : "N");
            statement.setTimestamp(4, Timestamp.valueOf(order.getWhen()));

            // Exécuter l'insertion
            statement.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1)); // Assigne l'ID généré à la commande
                } else {
                    throw new SQLException("Échec de l'insertion de la commande, aucun ID généré.");
                }
            }

            insertOrderProducts(order); // Associer les produits à la commande après l'insertion
        }
    }


    // Méthode pour supprimer une commande
    public void delete(Long id) throws SQLException {
        deleteOrderProducts(id); // Supprime d'abord les produits associés

        String sql = "DELETE FROM Commande WHERE numero = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Mappe une ligne de ResultSet vers un objet Order
    private Order mapRowToOrder(ResultSet rs) throws SQLException, RestaurantPersistenceException, CustomerPersistenceException {
        Long orderId = rs.getLong("numero");

        Long customerId = rs.getLong("fk_client");
        Customer customer = new CustomerMapper().findById(customerId);

        Long restaurantId = rs.getLong("fk_resto");
        Restaurant restaurant = new RestaurantMapper().findById(restaurantId);

        Boolean takeAway = "Y".equalsIgnoreCase(rs.getString("a_emporter"));
        LocalDateTime when = rs.getTimestamp("quand").toLocalDateTime();

        Order order = new Order(orderId, customer, restaurant, takeAway, when);

        // Charger les produits associés à la commande
        order.getProducts().addAll(findProductsByOrderId(orderId));

        return order;
    }

    // Méthode pour insérer des produits associés à une commande dans la table Produit_Commande
    private void insertOrderProducts(Order order) throws SQLException {
        String sql = "INSERT INTO Produit_Commande (fk_produit, fk_commande) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Product product : order.getProducts()) {
                statement.setLong(1, product.getId());
                statement.setLong(2, order.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Supprime les produits associés à une commande dans la table Produit_Commande
    private void deleteOrderProducts(Long orderId) throws SQLException {
        String sql = "DELETE FROM Produit_Commande WHERE fk_commande = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            statement.executeUpdate();
        }
    }

    // Récupère les produits associés à une commande
    private Set<Product> findProductsByOrderId(Long orderId) throws SQLException {
        Set<Product> products = new HashSet<>();
        String sql = "SELECT fk_produit FROM Produit_Commande WHERE fk_commande = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            ResultSet rs = statement.executeQuery();

            ProductMapper productMapper = new ProductMapper();
            while (rs.next()) {
                Long productId = rs.getLong("fk_produit");
                products.add(productMapper.findById(productId));
            }
        } catch (ProductPersistenceException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public List<Order> findOrdersByCustomer(Customer customer) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT numero, fk_client, fk_resto, a_emporter, quand FROM Commande WHERE fk_client = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, customer.getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (RestaurantPersistenceException e) {
            throw new RuntimeException(e);
        } catch (CustomerPersistenceException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }
}
