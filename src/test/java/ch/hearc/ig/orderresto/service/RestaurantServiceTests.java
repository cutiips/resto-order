package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.RestaurantMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RestaurantServiceTests {

    private static Connection conn;
    private RestaurantService restaurantService;
    private RestaurantMapper restaurantMapperMock;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
    }

    @BeforeEach
    public void setUp() {
        restaurantMapperMock = Mockito.mock(RestaurantMapper.class);
        restaurantService = new RestaurantService(restaurantMapperMock);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        conn.rollback();
    }

    @AfterAll
    public static void tearDownClass() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testCreateRestaurant() {
        Restaurant restaurant = new Restaurant(null, "Test Restaurant", null);

        assertDoesNotThrow(() -> {
            boolean result = restaurantService.addRestaurant(restaurant);
            assertTrue(result, "Restaurant should be created successfully");
        });
    }

    @Test
    public void testCreateRestaurantWithSQLException() throws RestaurantPersistenceException {
        Restaurant restaurant = new Restaurant(null, "Test Restaurant", null);

        // Simuler une exception lors de l'insertion
        doThrow(new RestaurantPersistenceException("Simulated persistence exception", new SQLException("Simulated SQL cause")))
                .when(restaurantMapperMock).insert(any(Restaurant.class), any(Connection.class));

        // Vérifier qu'une exception est lancée
        assertThrows(RestaurantPersistenceException.class, () -> restaurantService.addRestaurant(restaurant));
    }

    @Test
    public void testCreateRestaurantRollbackOnFailure() throws RestaurantPersistenceException {
        Restaurant restaurant = new Restaurant(null, "Test Restaurant", null);

        // Simuler une exception lors de l'insertion
        doThrow(new RestaurantPersistenceException("Simulated persistence exception", new SQLException("Simulated SQL cause")))
                .when(restaurantMapperMock).insert(any(Restaurant.class), any(Connection.class));

        boolean result = false;
        try {
            result = restaurantService.addRestaurant(restaurant);
        } catch (RestaurantPersistenceException | ProductPersistenceException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Vérifier que la méthode retourne 'false' en cas d'échec
        assertFalse(result, "Restaurant creation should fail and rollback");
    }



    @Test
    public void testGetRestaurantById() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        try {
            when(restaurantMapperMock.read(anyLong(), any(Connection.class))).thenReturn(restaurant);
            Restaurant retrievedRestaurant = restaurantService.getRestaurantById(1L);
            assertNotNull(retrievedRestaurant, "Restaurant should be retrieved successfully");
            assertEquals(1L, retrievedRestaurant.getId(), "Restaurant ID should match");
        } catch (RestaurantPersistenceException | ProductPersistenceException e) {
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateRestaurant() {
        Restaurant restaurant = new Restaurant(1L, "Updated Restaurant", null);
        assertDoesNotThrow(() -> {
            boolean result = restaurantService.updateRestaurant(restaurant);
            assertTrue(result, "Restaurant should be updated successfully");
        });
    }

    @Test
    public void testDeleteRestaurant() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        assertDoesNotThrow(() -> {
            boolean result = restaurantService.deleteRestaurant(restaurant.getId());
            assertTrue(result, "Restaurant should be deleted successfully");
        });
    }

    @Test
    public void testFindAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        assertNotNull(restaurants, "Restaurants should be retrieved successfully");
        assertFalse(restaurants.isEmpty(), "There should be at least one restaurant retrieved");
    }


}
