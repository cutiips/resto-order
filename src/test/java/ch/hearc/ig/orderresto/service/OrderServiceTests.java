package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.persistence.mappers.OrderMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTests {

    private static Connection conn;
    private OrderService orderService;
    private OrderMapper orderMapperMock;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        //conn = ConnectionManager.getConnection();
        conn = Mockito.mock(Connection.class);
        conn.setAutoCommit(false);
    }

    @BeforeEach
    public void setUp() {
        orderMapperMock = Mockito.mock(OrderMapper.class);
        orderService = new OrderService(orderMapperMock);

//        try (Connection conn = ConnectionManager.getConnection()) {
//            conn.createStatement().executeUpdate("DELETE FROM COMMANDE");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);//        }
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
    public void testCreateOrder() {
        PrivateCustomer customer = new PrivateCustomer(1L, "123456789", "test@example.com", null, "N", "John", "Doe");
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());

        assertDoesNotThrow(() -> {
            boolean result = orderService.createOrder(order);
            assertTrue(result, "Order should be created successfully");
        });
    }

    @Test
    public void testCreateOrderWithSQLException() {
        PrivateCustomer customer = new PrivateCustomer(1L, "123456789", "test@example.com", null, "N", "John", "Doe");
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());

        try {
            doThrow(new SQLException("Simulated SQL exception")).when(orderMapperMock).insert(any(Order.class), any(Connection.class));
            boolean result = orderService.createOrder(order);
            assertFalse(result, "Order creation should fail and rollback should be executed");
        } catch (SQLException e) {
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }


    @Test
    public void testCreateOrderRollbackOnFailure() {
        PrivateCustomer customer = new PrivateCustomer(1L, "123456789", "test@example.com", null, "N", "Jane", "Doe");
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());

        try {
            doThrow(new SQLException("Simulated SQL exception")).when(orderMapperMock).insert(any(Order.class), any(Connection.class));
            boolean result = orderService.createOrder(order);
            assertFalse(result, "Order creation should fail and rollback");
        } catch (SQLException e) {
            // Si une exception est lancée, vérifier que la transaction est bien annulée
            assertTrue(e instanceof SQLException, "Expected SQLException during order creation");
        }
    }

    @Test
    public void testGetOrderById() {
        Order order = new Order(1L, null, null, false, LocalDateTime.now());
        try {
            when(orderMapperMock.read(anyLong(), any(Connection.class))).thenReturn(order);
            Order retrievedOrder = orderService.getOrderById(1L);
            assertNotNull(retrievedOrder, "Order should be retrieved successfully");
            assertEquals(1L, retrievedOrder.getId(), "Order ID should match");
        } catch (SQLException e) {
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateOrder() {
        PrivateCustomer customer = new PrivateCustomer(1L, "123456789", "test@example.com", null, "N", "Jane", "Doe");
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", null);
        Order order = new Order(1L, customer, restaurant, false, LocalDateTime.now()); // Initialiser tous les champs

        assertDoesNotThrow(() -> {
            boolean result = orderService.updateOrder(order);
            assertTrue(result, "Order should be updated successfully");
        });
    }


    @Test
    public void testDeleteOrder() {
        Order order = new Order(1L, null, null, false, LocalDateTime.now());
        assertDoesNotThrow(() -> {
            boolean result = orderService.deleteOrder(order);
            assertTrue(result, "Order should be deleted successfully");
        });
    }

    @Test
    public void testFindOrdersByCustomer() {
        PrivateCustomer customer = new PrivateCustomer(1L, "123456789", "test@example.com", null, "N", "John", "Doe");
        try {
            when(orderMapperMock.findOrdersByCustomer(any(PrivateCustomer.class), any(Connection.class))).thenReturn(List.of(new Order(1L, customer, null, false, LocalDateTime.now())));
            List<Order> orders = orderService.findOrdersByCustomer(customer);
            assertNotNull(orders, "Orders should be retrieved successfully");
            assertEquals(1, orders.size(), "There should be one order retrieved");
        } catch (SQLException e) {
            fail("Unexpected exception during setup: " + e.getMessage());
        }
    }
}