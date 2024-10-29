package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.exceptions.ProductPersistenceException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductMapper extends BaseMapper {
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException, ProductPersistenceException {
        Long productId = rs.getLong("numero");
        String name = rs.getString("nom");
        BigDecimal price = rs.getBigDecimal("prix_unitaire");
        String description = rs.getString("description");
        Long restaurantId = rs.getLong("fk_resto");
        Restaurant restaurant = getRestaurantById(restaurantId);

        return new Product(productId, name, price, description, restaurant);
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
                product = extractProductFromResultSet(rs);
            }
        } catch (SQLException | ProductPersistenceException e) {
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
                products.add(extractProductFromResultSet(rs));
            }

        } catch (ProductPersistenceException | SQLException e) {
            throw new RuntimeException(e);
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

    /**
     * 🍽️ Récupère un restaurant par son identifiant.
     *
     * @param restaurantId L'identifiant du restaurant à récupérer
     * @return Le restaurant correspondant à l'ID fourni
     * @throws ProductPersistenceException Si une erreur survient lors de la requête SQL
     */
    private Restaurant getRestaurantById(Long restaurantId) throws ProductPersistenceException {
        RestaurantMapper restaurantMapper = new RestaurantMapper();
        try {
            return restaurantMapper.findById(restaurantId);  // Appelle le RestaurantMapper pour récupérer le Restaurant
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération du restaurant avec ID: " + restaurantId, e);
        }
    }

    /**
     * 📦 Récupère tous les produits d'un restaurant par son identifiant.
     *
     * @param restaurantId L'identifiant du restaurant dont on veut récupérer les produits
     * @return Une liste de produits associés au restaurant, ou une liste vide si aucun produit n'est trouvé
     * @throws ProductPersistenceException Si une erreur survient lors de la requête SQL
     */
    public List<Product> getProductsByRestaurantId(Long restaurantId) throws ProductPersistenceException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT numero, nom, prix_unitaire, description, fk_resto FROM Produit WHERE fk_resto = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, restaurantId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Long productId = rs.getLong("numero");
                String name = rs.getString("nom");
                BigDecimal price = rs.getBigDecimal("prix_unitaire");
                String description = rs.getString("description");

                Restaurant restaurant = getRestaurantById(restaurantId);  // Récupérer le restaurant avec l'ID

                products.add(new Product(productId, name, price, description, restaurant));
            }
        } catch (SQLException e) {
            throw new ProductPersistenceException("Erreur lors de la récupération des produits pour le restaurant avec ID: " + restaurantId, e);
        }
        return products;
    }

    /**
     * 🥗 Récupère tous les produits d'un restaurant donné.
     *
     * @param restaurant Le restaurant dont on veut récupérer les produits
     * @return Une liste de produits associés au restaurant, ou une liste vide si aucun produit n'est trouvé
     * @throws ProductPersistenceException Si une erreur survient lors de la requête SQL
     */
    public List<Product> findProductsByRestaurant(Restaurant restaurant) throws ProductPersistenceException {
        return getProductsByRestaurantId(restaurant.getId());
    }
}
