package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import java.sql.*;
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
            throw new RuntimeException("Database configuration error", ex); // except propagate
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    /**
     * 🚀 Insertion de l'adresse en premier pour récupérer l'ID du restaurant et passer celui-ci en param dans l'insert du restaurant
     * 💡 MAJ DU SCRIPT (v1-... .sql)
     * @param address L'adresse à insérer dans la base de données.
     * @return L'ID de l'adresse insérée, ou null si l'insertion a échoué
     * @throws SQLException Si une erreur se produit lors de l'insertion dans la base de données
     */
    private Long insertAddress(Address address) throws SQLException {
        String query = "INSERT INTO ADDRESS (codePays, code_postal, localite, rue, num_rue) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, address.getCountryCode());
            stmt.setString(2, address.getPostalCode());
            stmt.setString(3, address.getLocality());
            stmt.setString(4, address.getStreet());
            stmt.setString(5, address.getStreetNumber());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // return id address
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting address: " + e.getMessage());
            throw e; // Propagate the exception
        }
        return null;
    }


    /**
     * 🚀 Insertion d'un restaurant et de son adresse dans la db
     * 💡 MAJ DU SCRIPTS (v1-... .sql)
     * 📦 Si l'adresse n'existe pas, elle sera insérée d'abord, puis le restaurant sera ajouté
     * @param restaurant Le restaurant à insérer dans la base de données.
     * @throws SQLException Si une erreur se produit lors de l'insertion dans la base de données.
     */
    public void insertRestaurant(Restaurant restaurant) throws SQLException {
        Long addressId = insertAddress(restaurant.getAddress()); // address insert + recover ID

        if (addressId == null) {
            throw new SQLException("Failed to insert address, cannot insert restaurant."); // if address = null
        }

        String query = "INSERT INTO Restaurants (id, name, adress_id) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, restaurant.getId());
            stmt.setString(2, restaurant.getName());
            stmt.setLong(3, addressId); // use id address

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting restaurant: " + e.getMessage());
            throw e; // Propagate the exception
        }
    }

    /**
     * 🚀 Insertion d'un restaurant directement dans la table RESTAURANT (avec l'adresse incluse dans les colonnes du restaurant
     * 💡 PAS DE MAJ DU SCRIPTS
     * @param restaurant Le restaurant est inséré avec son adresse
     * @throws SQLException Si une erreur se produit lors de l'insertion dans la base de données.
     */
    public void insert(Restaurant restaurant) throws SQLException {
        String query = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, restaurant.getName());

            Address address = restaurant.getAddress();
            stmt.setString(2, address.getPostalCode());
            stmt.setString(3, address.getLocality());
            stmt.setString(4, address.getStreet());
            stmt.setString(5, address.getStreetNumber());
            stmt.setString(6, address.getCountryCode());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du restaurant : " + e.getMessage());
            throw e;  // propagate
        }
    }

}
