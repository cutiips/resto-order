package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ProductMapperTests {

    private static Connection conn;
    private ProductMapper productMapper;
    private RestaurantMapper restaurantMapper;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
    }

    @BeforeEach
    public void setUp() {
        productMapper = new ProductMapper();
        restaurantMapper = new RestaurantMapper();
    }

    @AfterEach
    public void tearDown() {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertProduct() throws SQLException, RestaurantPersistenceException, ProductPersistenceException {
        // Arrange
        Address address = new Address("CH", "1000", "Lausanne", "Rue", "1");
        Restaurant restaurant = new Restaurant(null, "Test Resto", address);
        restaurantMapper.insert(restaurant, conn);

        Product product = new Product(null, "Pizza", new BigDecimal("18.00"), "Delicious pizza", restaurant);

        // Act & Assert
        assertDoesNotThrow(() -> productMapper.insert(product, conn));
        assertNotNull(product.getId(), "Product ID should be generated and set");
    }

    @Test
    public void testReadProduct() throws SQLException, RestaurantPersistenceException, ProductPersistenceException {
        // Arrange
        Address address = new Address("CH", "1000", "Lausanne", "Rue", "1");
        Restaurant restaurant = new Restaurant(null, "Read Resto", address);
        restaurantMapper.insert(restaurant, conn);

        Product product = new Product(null, "Burger", new BigDecimal("12.00"), "Tasty burger", restaurant);
        productMapper.insert(product, conn);

        // Act
        Product readProduct = productMapper.read(product.getId(), conn);

        // Assert
        assertNotNull(readProduct, "Product should be retrievable by ID");
        assertEquals(product.getId(), readProduct.getId(), "Product ID should match");
        assertEquals(product.getName(), readProduct.getName(), "Product name should match");
    }

    @Test
    public void testUpdateProduct() throws SQLException, RestaurantPersistenceException, ProductPersistenceException {
        // Arrange
        Address address = new Address("CH", "1000", "Lausanne", "Rue", "1");
        Restaurant restaurant = new Restaurant(null, "Update Resto", address);
        restaurantMapper.insert(restaurant, conn);

        Product product = new Product(null, "Pasta", new BigDecimal("10.00"), "Delicious pasta", restaurant);
        productMapper.insert(product, conn);

        // Act
        product.setName("Updated Pasta");
        product.setUnitPrice(new BigDecimal("11.00"));
        assertDoesNotThrow(() -> productMapper.update(product, conn));

        Product updatedProduct = productMapper.read(product.getId(), conn);

        // Assert
        assertNotNull(updatedProduct, "Updated product should be retrievable by ID");
        assertEquals("Updated Pasta", updatedProduct.getName(), "Product name should be updated");
        assertEquals(new BigDecimal("11.00"), updatedProduct.getUnitPrice(), "Product price should be updated");
    }

    @Test
    public void testDeleteProduct() throws SQLException, RestaurantPersistenceException, ProductPersistenceException {
        // Arrange
        Address address = new Address("CH", "1000", "Lausanne", "Rue", "1");
        Restaurant restaurant = new Restaurant(null, "Delete Resto", address);
        restaurantMapper.insert(restaurant, conn);

        Product product = new Product(null, "Salad", new BigDecimal("8.00"), "Fresh salad", restaurant);
        productMapper.insert(product, conn);

        // Act & Assert
        assertDoesNotThrow(() -> productMapper.delete(product.getId(), conn));
        Product deletedProduct = productMapper.read(product.getId(), conn);
        assertNull(deletedProduct, "Product should no longer be retrievable after deletion");
    }
}
