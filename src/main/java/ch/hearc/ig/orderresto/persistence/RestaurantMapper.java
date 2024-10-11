package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

import java.sql.SQLException;

/**
 * 🚀 Responsable du mapping des entités "Restaurant" et "Address" dans la db
 * Cette classe gère la connexion à la base de données et l'insertion des restaurants et adresses
 */
public class RestaurantMapper {
    private String url;
    private String username;
    private String password;

    public RestaurantMapper() {
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

    /**
     * 🚀 Insertion d'un restaurant directement dans la table RESTAURANT (avec l'adresse incluse dans les colonnes du restaurant
     * 💡 PAS DE MAJ DU SCRIPTS
     * @param restaurant Le restaurant est inséré avec son adresse.
     * @throws SQLException Si une erreur se produit lors de l'insertion dans la base de données.
     */
    public void insert(Restaurant restaurant) throws SQLException {
        String query = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, restaurant.getName());

            address(restaurant, stmt);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du restaurant : " + e.getMessage());
            throw e;
        }
    }

    private void address(Restaurant restaurant, PreparedStatement stmt) throws SQLException {
        Address address = restaurant.getAddress();
        stmt.setString(2, address.getPostalCode());
        stmt.setString(3, address.getLocality());
        stmt.setString(4, address.getStreet());
        stmt.setString(5, address.getStreetNumber());
        stmt.setString(6, address.getCountryCode());
    }

    /**
     * 🔍 Récupère un restaurant par son identifiant.
     * @param id L'identifiant du restaurant à récupérer.
     * @return Le restaurant correspondant à l'ID fourni, ou null s'il n'est pas trouvé.
     * @throws SQLException Si une erreur survient lors de la requête SQL.
     */
    public Restaurant findById(Long id) throws SQLException {
        String query = "SELECT nom, code_postal, localite, rue, num_rue, pays FROM RESTAURANT WHERE numero = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Address address = new Address(
                        rs.getString("pays"),
                        rs.getString("code_postal"),
                        rs.getString("localite"),
                        rs.getString("rue"),
                        rs.getString("num_rue")
                );
                return new Restaurant(id, rs.getString("nom"), address);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du restaurant : " + e.getMessage());
            throw e;
        }
        return null;  // 404
    }

    /**
     * 🔄 Met à jour les informations d'un restaurant dans la base de données.
     * @param restaurant Le restaurant avec les nouvelles informations à mettre à jour.
     * @throws SQLException Si une erreur survient lors de la mise à jour des données.
     */
    public void update(Restaurant restaurant) throws SQLException {
        String query = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, restaurant.getName());
            System.out.println("update - restaurant.getName(): " + restaurant.getName());

            address(restaurant, stmt);
            stmt.setLong(7, restaurant.getId());


            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du restaurant : " + e.getMessage());
            throw e;
        }
    }

    /**
     * 🗑️ Supprime un restaurant de la base de données en fonction de son identifiant.
     * @param id L'identifiant du restaurant à supprimer.
     * @throws SQLException Si une erreur survient lors de la suppression.
     */
    public void delete(Long id) throws SQLException {
        String query = "DELETE FROM RESTAURANT WHERE numero = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du restaurant : " + e.getMessage());
            throw e;
        }
    }

    /**
     * 📋 Récupère tous les restaurants présents dans la base de données.
     * @return Une liste contenant tous les restaurants.
     * @throws SQLException Si une erreur survient lors de la récupération des données.
     */
    public List<Restaurant> findAll() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT numero, nom, code_postal, localite, rue, num_rue, pays FROM RESTAURANT";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Address address = new Address(
                        rs.getString("pays"),
                        rs.getString("code_postal"),
                        rs.getString("localite"),
                        rs.getString("rue"),
                        rs.getString("num_rue")
                );
                Restaurant restaurant = new Restaurant(
                        rs.getLong("numero"),
                        rs.getString("nom"),
                        address
                );
                restaurants.add(restaurant);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des restaurants : " + e.getMessage());
            throw e;
        }
        return restaurants;
    }

}
