package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Restaurant;

import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RestaurantService {

    private RestaurantMapper restaurantMapper;

    public RestaurantService() {
        this.restaurantMapper = new RestaurantMapper();
    }

    public RestaurantService(RestaurantMapper restaurantMapper) {
        this.restaurantMapper = restaurantMapper;
    }

    public Restaurant getRestaurantById(Long id) throws ProductPersistenceException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            return restaurantMapper.read(id, conn);
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

    public boolean addRestaurant(Restaurant restaurant) throws ProductPersistenceException, RestaurantPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            // Insertion du restaurant
            restaurantMapper.insert(restaurant, conn);

            // Commit de la transaction
            conn.commit();

            System.out.println("Restaurant ajouté avec succès !");
            return true;
        } catch (RestaurantPersistenceException | SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur pendant le rollback de la transaction : " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur lors de l'ajout du restaurant : " + e.getMessage());
            // Lancer une exception personnalisée si nécessaire
            throw new RestaurantPersistenceException("Erreur pendant l'ajout du restaurant", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Erreur lors de la fermeture de la connexion : " + closeEx.getMessage());
                }
            }
        }
    }



    public boolean updateRestaurant(Restaurant restaurant) throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            restaurantMapper.update(restaurant, conn);
            conn.commit();

            System.out.println("Restaurant updated successfully!");
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while updating restaurant: " + e.getMessage());
            return false;
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

    public boolean deleteRestaurant(Long id) {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            restaurantMapper.delete(id, conn);
            conn.commit();

            System.out.println("Restaurant deleted successfully!");
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while deleting restaurant: " + e.getMessage());
            return false;
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
