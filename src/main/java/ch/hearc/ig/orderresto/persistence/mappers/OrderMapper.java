package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.service.CustomerService;
import ch.hearc.ig.orderresto.service.ProductService;
import ch.hearc.ig.orderresto.service.RestaurantService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderMapper {
    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();

    // Méthode pour trouver une commande par ID
    public Order read(Long id, Connection conn) throws SQLException {
        Order order = null;
        String sql = "SELECT numero, fk_client, fk_resto, a_emporter, quand FROM Commande WHERE numero = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                order = mapRowToOrder(rs, conn);
            }
        } catch (RestaurantPersistenceException | CustomerPersistenceException | ProductPersistenceException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    // Méthode pour récupérer toutes les commandes
    public List<Order> findAll(Connection conn) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT numero, fk_client, fk_resto, a_emporter, quand FROM Commande";

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                orders.add(mapRowToOrder(rs, conn));
            }
        } catch (RestaurantPersistenceException | CustomerPersistenceException | ProductPersistenceException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    // Méthode pour insérer une nouvelle commande
    public void insert(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO Commande (numero, fk_client, fk_resto, a_emporter, quand) VALUES (SEQ_COMMANDE.NEXTVAL, ?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql, new String[]{"numero"})) { // Spécifie la colonne de l'ID généré

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

            insertOrderProducts(order, conn); // Associer les produits à la commande après l'insertion
        }
    }


    // Méthode pour supprimer une commande
    public void delete(Long id, Connection conn) throws SQLException {
        deleteOrderProducts(id, conn); // Supprime d'abord les produits associés

        String sql = "DELETE FROM Commande WHERE numero = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Mappe une ligne de ResultSet vers un objet Order
    private Order mapRowToOrder(ResultSet rs, Connection conn) throws SQLException, RestaurantPersistenceException, CustomerPersistenceException, ProductPersistenceException {
        Long orderId = rs.getLong("numero");

        Long customerId = rs.getLong("fk_client");
        Customer customer = customerService.getCustomerById(customerId, conn);

        Long restaurantId = rs.getLong("fk_resto");
        Restaurant restaurant = new RestaurantService().getRestaurantById(restaurantId);

        Boolean takeAway = "Y".equalsIgnoreCase(rs.getString("a_emporter"));
        LocalDateTime when = rs.getTimestamp("quand").toLocalDateTime();

        Order order = new Order(orderId, customer, restaurant, takeAway, when);

        // Charger les produits associés à la commande
        order.getProducts().addAll(findProductsByOrderId(orderId, conn));

        return order;
    }

    // Méthode pour insérer des produits associés à une commande dans la table Produit_Commande
    private void insertOrderProducts(Order order, Connection conn) throws SQLException {
        String sql = "INSERT INTO Produit_Commande (fk_produit, fk_commande) VALUES (?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            for (Product product : order.getProducts()) {
                statement.setLong(1, product.getId());
                statement.setLong(2, order.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    // Supprime les produits associés à une commande dans la table Produit_Commande
    private void deleteOrderProducts(Long orderId, Connection conn) throws SQLException {
        String sql = "DELETE FROM Produit_Commande WHERE fk_commande = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            statement.executeUpdate();
        }
    }

    // Récupère les produits associés à une commande
    private Set<Product> findProductsByOrderId(Long orderId, Connection conn) throws SQLException {
        Set<Product> products = new HashSet<>();
        String sql = "SELECT fk_produit FROM Produit_Commande WHERE fk_commande = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Long productId = rs.getLong("fk_produit");
                products.add(productService.getProductById(productId, conn));
            }
        } catch (ProductPersistenceException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public List<Order> findOrdersByCustomer(Customer customer, Connection conn) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT numero, fk_client, fk_resto, a_emporter, quand FROM Commande WHERE fk_client = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, customer.getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                orders.add(mapRowToOrder(rs, conn));
            }
        } catch (RestaurantPersistenceException | CustomerPersistenceException | ProductPersistenceException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    // Update: Update an existing order
    public void update(Order order, Connection conn) throws SQLException {
        String sql = "UPDATE Commande SET fk_client = ?, fk_resto = ?, a_emporter = ?, quand = ? WHERE numero = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, order.getCustomer().getId());
            statement.setLong(2, order.getRestaurant().getId());
            statement.setString(3, order.getTakeAway() ? "O" : "N");
            statement.setTimestamp(4, Timestamp.valueOf(order.getWhen()));
            statement.setLong(5, order.getId());
            statement.executeUpdate();

            deleteOrderProducts(order.getId(), conn);
            insertOrderProducts(order, conn);
        }
    }
}
