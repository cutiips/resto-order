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
