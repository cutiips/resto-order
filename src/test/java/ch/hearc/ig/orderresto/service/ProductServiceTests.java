package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.ProductMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTests {

    private static Connection conn;
    private ProductService productService;
    private ProductMapper productMapperMock;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
    }

    @BeforeEach
    public void setUp() {
        productMapperMock = Mockito.mock(ProductMapper.class);
        productService = new ProductService(productMapperMock);
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
    public void testCreateProduct() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Product product = new Product(null, "Test Product", new BigDecimal("10.99"), "A product for testing", restaurant);

        assertDoesNotThrow(() -> {
            boolean result = productService.addProduct(product);
            assertTrue(result, "Product should be created successfully");
        });
    }

    @Test
    public void testCreateProductWithSQLException() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Product product = new Product(null, "Test Product", new BigDecimal("10.99"), "A product for testing", restaurant);

        try {
            // Simuler une ProductPersistenceException au lieu de RuntimeException
            doThrow(new ProductPersistenceException("Simulated persistence exception", new SQLException("Simulated SQL cause"))).when(productMapperMock).insert(any(Product.class), any(Connection.class));
            assertThrows(ProductPersistenceException.class, () -> productService.addProduct(product));
        } catch (ProductPersistenceException e) {
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }

    @Test
    public void testCreateProductRollbackOnFailure() throws ProductPersistenceException {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Product product = new Product(null, "Test Product", new BigDecimal("10.99"), "A product for testing", restaurant);

        // Simuler une ProductPersistenceException au lieu de RuntimeException
        doThrow(new ProductPersistenceException("Simulated persistence exception", new SQLException("Simulated SQL cause")))
                .when(productMapperMock).insert(any(Product.class), any(Connection.class));

        boolean result = false;
        try {
            result = productService.addProduct(product);
        } catch (ProductPersistenceException e) {
            // Ne pas relancer l'exception ici, car nous la testons
        } catch (ch.hearc.ig.orderresto.service.exceptions.ProductServiceException e) {
            throw new RuntimeException(e);
        }

        assertFalse(result, "Product creation should fail and rollback");
    }





    @Test
    public void testGetProductById() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Product product = new Product(1L, "Test Product", new BigDecimal("10.99"), "A product for testing", restaurant);
        try {
            when(productMapperMock.read(anyLong(), any(Connection.class))).thenReturn(product);
            Product retrievedProduct = productService.getProductById(1L);
            assertNotNull(retrievedProduct, "Product should be retrieved successfully");
            assertEquals(1L, retrievedProduct.getId(), "Product ID should match");
        } catch (ProductPersistenceException e) {
            fail("Unexpected exception during setup: " + e.getMessage());
        } catch (ch.hearc.ig.orderresto.service.exceptions.ProductServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdateProduct() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Product product = new Product(1L, "Updated Product", new BigDecimal("15.99"), "An updated product for testing", restaurant);
        assertDoesNotThrow(() -> {
            boolean result = productService.updateProduct(product);
            assertTrue(result, "Product should be updated successfully");
        });
    }

    @Test
    public void testDeleteProduct() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Product product = new Product(1L, "Test Product", new BigDecimal("10.99"), "A product for testing", restaurant);
        assertDoesNotThrow(() -> {
            boolean result = productService.deleteProduct(product.getId());
            assertTrue(result, "Product should be deleted successfully");
        });
    }

    @Test
    public void testFindAllProducts() {
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        try {
            when(productMapperMock.findAll(any(Connection.class))).thenReturn(List.of(new Product(1L, "Test Product", new BigDecimal("10.99"), "A product for testing", restaurant)));
            List<Product> products = productService.getAllProducts();
            assertNotNull(products, "Products should be retrieved successfully");
            assertEquals(1, products.size(), "There should be one product retrieved");
        } catch (ProductPersistenceException e) {
            throw new RuntimeException(e);
        } catch (ch.hearc.ig.orderresto.service.exceptions.ProductServiceException e) {
            throw new RuntimeException(e);
        }
    }
}
