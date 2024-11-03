package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Restaurant;

import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import ch.hearc.ig.orderresto.service.exceptions.RestaurantServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RestaurantService {

    private final RestaurantMapper restaurantMapper;

    public RestaurantService() {
        this.restaurantMapper = new RestaurantMapper();
    }

    public RestaurantService(RestaurantMapper restaurantMapper) {
        this.restaurantMapper = restaurantMapper;
    }

    public Restaurant getRestaurantById(Long id) throws RestaurantServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return restaurantMapper.read(id, conn);
        } catch (SQLException | RestaurantPersistenceException e) {
            throw new RestaurantServiceException("Error while getting restaurant by id", e);
        }
    }

    public boolean addRestaurant(Restaurant restaurant) throws RestaurantServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            restaurantMapper.insert(restaurant, conn);
            conn.commit();

            System.out.println("Restaurant ajouté avec succès !");
            return true;
        } catch (SQLException | RestaurantPersistenceException e) {
            throw new RestaurantServiceException("Failed to add restaurant", e);
        }
    }

    public boolean updateRestaurant(Restaurant restaurant) throws RestaurantServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            restaurantMapper.update(restaurant, conn);
            conn.commit();

            System.out.println("Restaurant updated successfully!");
            return true;
        } catch (SQLException | RestaurantPersistenceException e) {
            throw new RestaurantServiceException("Failed to update restaurant", e);
        }
    }

    public boolean deleteRestaurant(Long id) throws RestaurantServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            restaurantMapper.delete(id, conn);
            conn.commit();

            System.out.println("Restaurant deleted successfully!");
            return true;
        } catch (SQLException | RestaurantPersistenceException e){
            throw new RestaurantServiceException("Failed to delete restaurant", e);
        }
    }

    public List<Restaurant> getAllRestaurants() throws RestaurantServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return restaurantMapper.findAll(conn);
        } catch (SQLException | RestaurantPersistenceException e) {
            throw new RestaurantServiceException("Failed to get all restaurants", e);
        }
    }
}
