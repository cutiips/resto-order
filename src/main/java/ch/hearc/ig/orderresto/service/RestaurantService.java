package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Restaurant;

import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;

import java.sql.Connection;
import java.util.List;

public class RestaurantService {
    public Restaurant getRestaurantById(Long id) throws ProductPersistenceException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            RestaurantMapper restaurantMapper = new RestaurantMapper();
            return restaurantMapper.findById(id, conn);
        } catch (Exception e) {
            throw new ProductPersistenceException("Erreur lors de la récupération du restaurant avec ID: " + id, e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public void addRestaurant(Restaurant restaurant) throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            RestaurantMapper restaurantMapper = new RestaurantMapper();
            restaurantMapper.insert(restaurant, conn);
            conn.commit();

            System.out.println("Restaurant added successfully!");
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while adding restaurant: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public void updateRestaurant(Restaurant restaurant) throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            RestaurantMapper restaurantMapper = new RestaurantMapper();
            restaurantMapper.update(restaurant, conn);
            conn.commit();

            System.out.println("Restaurant updated successfully!");
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while updating restaurant: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public void deleteRestaurant(Long id) {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            RestaurantMapper restaurantMapper = new RestaurantMapper();
            restaurantMapper.delete(id, conn);
            conn.commit();

            System.out.println("Restaurant deleted successfully!");
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while deleting restaurant: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public List<Restaurant> getAllRestaurants() {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            RestaurantMapper restaurantMapper = new RestaurantMapper();
            return restaurantMapper.findAll(conn);
        } catch (Exception e) {
            System.err.println("Error while getting all restaurants: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }
}
