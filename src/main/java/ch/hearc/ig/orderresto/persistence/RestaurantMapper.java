package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.exceptions.RestaurantPersistenceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

/**
 * 🚀 Responsable du mapping des entités "Restaurant" et "Address" dans la db
 * Cette classe gère la connexion à la base de données et l'insertion des restaurants et adresses
 */
public class RestaurantMapper extends BaseMapper {
    private final ProductMapper productMapper = new ProductMapper();
    /**
     * 🚀 Insertion d'un restaurant directement dans la table RESTAURANT (avec l'adresse incluse dans les colonnes du restaurant
     * 💡 PAS DE MAJ DU SCRIPTS
     * @param restaurant Le restaurant est inséré avec son adresse.
     * @throws SQLException Si une erreur se produit lors de l'insertion dans la base de données.
     */
    public void insert(Restaurant restaurant) throws RestaurantPersistenceException {
        String query = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, restaurant.getName());

            AddressUtils.setPreparedStatementAddress(stmt, restaurant.getAddress(), 2);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de l'insertion du restaurant : ", e);
        }
    }

    /**
     * 🔍 Récupère un restaurant par son identifiant.
     * @param id L'identifiant du restaurant à récupérer.
     * @return Le restaurant correspondant à l'ID fourni, ou null s'il n'est pas trouvé.
     * @throws SQLException Si une erreur survient lors de la requête SQL.
     */
    public Restaurant findById(Long id) throws RestaurantPersistenceException {
        String query = "SELECT nom, code_postal, localite, rue, num_rue, pays FROM RESTAURANT WHERE numero = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Address address = AddressUtils.createAddressFromResultSet(rs);
                return new Restaurant(id, rs.getString("nom"), address);
            }
        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de la récupération du restaurant : ", e);
        }
        return null;  // 404
    }

    /**
     * 🔄 Met à jour les informations d'un restaurant dans la base de données.
     * @param restaurant Le restaurant avec les nouvelles informations à mettre à jour.
     * @throws SQLException Si une erreur survient lors de la mise à jour des données.
     */
    public void update(Restaurant restaurant) throws RestaurantPersistenceException {
        String query = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, restaurant.getName());
            System.out.println("update - restaurant.getName(): " + restaurant.getName());

            AddressUtils.setPreparedStatementAddress(stmt, restaurant.getAddress(), 2);
            stmt.setLong(7, restaurant.getId());


            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de la mise à jour du restaurant : ", e);
        }
    }

    /**
     * 🗑️ Supprime un restaurant de la base de données en fonction de son identifiant.
     * @param id L'identifiant du restaurant à supprimer.
     * @throws SQLException Si une erreur survient lors de la suppression.
     */
    public void delete(Long id) throws RestaurantPersistenceException {
        String query = "DELETE FROM RESTAURANT WHERE numero = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de la suppression du restaurant : ", e);
        }
    }

    /**
     * 📋 Récupère tous les restaurants présents dans la base de données.
     * @return Une liste contenant tous les restaurants.
     * @throws SQLException Si une erreur survient lors de la récupération des données.
     */
    public List<Restaurant> findAll() throws RestaurantPersistenceException {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT numero, nom, code_postal, localite, rue, num_rue, pays FROM RESTAURANT";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Address address = AddressUtils.createAddressFromResultSet(rs);
                Restaurant restaurant = new Restaurant(
                        rs.getLong("numero"),
                        rs.getString("nom"),
                        address
                );
                restaurants.add(restaurant);
            }
        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de la récupération des restaurants : ", e);
        }
        return restaurants;
    }

}
