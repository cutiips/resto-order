package ch.hearc.ig.orderresto.persistence.service;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import ch.hearc.ig.orderresto.service.CustomerService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerServiceTests {

    private static Connection conn;
    private CustomerService customerService;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
    }

    @BeforeEach
    public void setUp() {
        customerService = new CustomerService();
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
    public void testInsertPrivateCustomer() throws CustomerPersistenceException {
        // Arrange
        Address address = new Address("CH", "2000", "Neuchâtel", "Rue", "1");
        PrivateCustomer customer = new PrivateCustomer(null, "123456789", "private@test.com", address, "O", "John", "Doe");

        // Act & Assert
        assertDoesNotThrow(() -> customerService.addCustomer(customer));
        assertNotNull(customer.getId(), "Customer ID should be generated and set");
    }

    @Test
    public void testInsertOrganizationCustomer() throws CustomerPersistenceException {
        // Arrange
        Address address = new Address("CH", "3000", "Berne", "Avenue", "10");
        OrganizationCustomer customer = new OrganizationCustomer(null, "987654321", "org@test.com", address, "Test SA", "SA");

        // Act & Assert
        assertDoesNotThrow(() -> customerService.addCustomer(customer));
        assertNotNull(customer.getId(), "Customer ID should be generated and set");
    }

    @Test
    public void testGetCustomerById() throws CustomerPersistenceException {
        // Arrange
        Address address = new Address("CH", "1000", "Lausanne", "Route", "5");
        PrivateCustomer customer = new PrivateCustomer(null, "1122334455", "getcustomer@test.com", address, "N", "Alice", "Smith");
        customerService.addCustomer(customer);

        // Act
        PrivateCustomer fetchedCustomer = (PrivateCustomer) customerService.getCustomerById(customer.getId(), conn);

        // Assert
        assertNotNull(fetchedCustomer, "Customer should be retrievable by ID");
        assertEquals(customer.getEmail(), fetchedCustomer.getEmail(), "Emails should match");
    }

    @Test
    public void testUpdateCustomer() throws CustomerPersistenceException {
        // Arrange
        Address address = new Address("CH", "4000", "Bâle", "Rue Centrale", "3");
        PrivateCustomer customer = new PrivateCustomer(1L, "9988776655", "update2@test.com", address, "O", "Bob", "Marley");
        customerService.addCustomer(customer);

        System.out.println("Step 1 : " + customer.getFirstName());

        // Update customer details
        customer.setFirstName("Robert");

        System.out.println("Step 2 : " + customer.getFirstName());
        System.out.println("Step 3 : " + customer.getPhoneNumber());
        customer.setPhoneNumber("111222333");
        System.out.println("Step 4 : " + customer.getPhoneNumber());

        // Act & Assert
        assertDoesNotThrow(() -> customerService.updateCustomer(customer));
        PrivateCustomer updatedCustomer = (PrivateCustomer) customerService.getCustomerById(customer.getId(), conn);
        assertEquals("Robert", updatedCustomer.getFirstName(), "First name should be updated");
        assertEquals("111222333", updatedCustomer.getPhoneNumber(), "Phone number should be updated");
    }

    @Test
    public void testDeleteCustomer() throws CustomerPersistenceException {
        // Arrange
        Address address = new Address("CH", "5000", "Aarau", "Place", "7");
        PrivateCustomer customer = new PrivateCustomer(null, "5544332211", "delete@test.com", address, "N", "Charlie", "Chaplin");
        customerService.addCustomer(customer);

        // Act & Assert
        assertDoesNotThrow(() -> customerService.deleteCustomer(customer));
        assertNull(customerService.getCustomerById(customer.getId(), conn), "Customer should no longer be retrievable after deletion");
    }
}
