package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.utils.AddressUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMapper extends BaseMapper<Restaurant> {

    private final ProductMapper productMapper = new ProductMapper();

    public void insert(Restaurant restaurant) throws RestaurantPersistenceException {
        String query = "INSERT INTO RESTAURANT (nom, code_postal, localite, rue, num_rue, pays) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, restaurant.getName());
            AddressUtils.setPreparedStatementAddress(stmt, restaurant.getAddress(), 2);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    restaurant.setId(generatedKeys.getLong(1));
                    addToCache(restaurant.getId(), restaurant); // Ajouter au cache après insertion
                }
            }

        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de l'insertion du restaurant : ", e);
        }
    }

    public Restaurant findById(Long id) throws RestaurantPersistenceException {
        // Vérifie si le restaurant est déjà dans le cache
        return findInCache(id).orElseGet(() -> {
            String query = "SELECT nom, code_postal, localite, rue, num_rue, pays FROM RESTAURANT WHERE numero = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Address address = AddressUtils.createAddressFromResultSet(rs);
                    Restaurant restaurant = new Restaurant(id, rs.getString("nom"), address);
                    addToCache(id, restaurant); // Ajouter au cache
                    return restaurant;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la récupération du restaurant", e);
            }
            return null;  // 404
        });
    }

    public void update(Restaurant restaurant) throws RestaurantPersistenceException {
        String query = "UPDATE RESTAURANT SET nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ? WHERE numero = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, restaurant.getName());
            AddressUtils.setPreparedStatementAddress(stmt, restaurant.getAddress(), 2);
            stmt.setLong(7, restaurant.getId());

            stmt.executeUpdate();
            updateInCache(restaurant.getId(), restaurant); // Mettre à jour dans le cache

        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de la mise à jour du restaurant : ", e);
        }
    }

    public void delete(Long id) throws RestaurantPersistenceException {
        String query = "DELETE FROM RESTAURANT WHERE numero = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            removeFromCache(id); // Supprimer du cache

        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de la suppression du restaurant : ", e);
        }
    }

    public List<Restaurant> findAll() throws RestaurantPersistenceException {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT numero, nom, code_postal, localite, rue, num_rue, pays FROM RESTAURANT";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("numero");

                // Vérifier si le restaurant est déjà dans le cache
                Restaurant restaurant = findInCache(id).orElse(null);
                if (restaurant == null) {
                    try {
                        Address address = AddressUtils.createAddressFromResultSet(rs);
                        restaurant = new Restaurant(id, rs.getString("nom"), address);
                        addToCache(id, restaurant);
                    } catch (SQLException e) {
                        throw new RestaurantPersistenceException("Erreur lors de la récupération des données de l'adresse ou du nom", e);
                    }
                }

                restaurants.add(restaurant);
            }

        } catch (SQLException e) {
            throw new RestaurantPersistenceException("Erreur lors de la récupération des restaurants : ", e);
        }
        return restaurants;
    }


}
