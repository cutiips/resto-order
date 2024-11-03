package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerMapperTests {

    private static Connection conn;
    private CustomerMapper customerMapper;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        conn = ConnectionManager.getConnection();
    }

    @BeforeEach
    public void setUp() {
        customerMapper = new CustomerMapper();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (conn != null) {
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false);
            }
            conn.rollback(); // Revenir en arrière après chaque test
        }
    }

    @AfterAll
    public static void tearDownClass() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testInsertPrivateCustomer() {
        Address address = new Address("CH", "2000", "Neuchâtel", "Street", "2");
        PrivateCustomer customer = new PrivateCustomer(null, "123456789", "test@test.com", address, "O", "Jonathan", "Curty");

        assertDoesNotThrow(() -> customerMapper.insert(customer, conn));
        assertNotNull(customer.getId(), "Customer ID should be generated and set");
    }

    @Test
    public void testInsertOrganizationCustomer() {
        Address address = new Address("CH", "3000", "Berne", "Avenue", "2");
        OrganizationCustomer customer = new OrganizationCustomer(null, "987654321", "org@test.com", address, "Test SA", "SA");

        assertDoesNotThrow(() -> customerMapper.insert(customer, conn));
        assertNotNull(customer.getId(), "Customer ID should be generated and set");
    }

    @Test
    public void testInvalidPostalCode() {
        Address address = new Address("CH", "20000", "Genève", "Rue", "2"); // Code postal invalide (5 caractères)
        PrivateCustomer customer = new PrivateCustomer(null, "543216789", "invalid@test.com", address, "N", "Invalid", "Postal");

        CustomerPersistenceException exception = assertThrows(CustomerPersistenceException.class, () -> customerMapper.insert(customer, conn));
        assertTrue(exception.getCause() instanceof SQLException, "Expected a SQLException as the cause");
        String expectedMessage = "ORA-12899";
        String actualMessage = exception.getCause().getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Expected ORA-12899 error due to invalid postal code length");
    }


    @Test
    public void testReadPrivateCustomer() throws CustomerPersistenceException {
        Address address = new Address("CH", "1000", "Lausanne", "Route", "1");
        PrivateCustomer customer = new PrivateCustomer(null, "1122334455", "read@test.com", address, "O", "Read", "Customer");
        customerMapper.insert(customer, conn);

        Customer readCustomer = customerMapper.findByEmail("read@test.com", conn);
        assertNotNull(readCustomer, "Customer should be retrievable by email");
        assertEquals(customer.getEmail(), readCustomer.getEmail(), "Email should match");
    }

    @Test
    public void testReadOrganizationCustomer() throws CustomerPersistenceException {
        Address address = new Address("CH", "1000", "Lausanne", "Route", "1");
        OrganizationCustomer customer = new OrganizationCustomer(null, "5566778899", "org@gmail.com", address, "Test SA", "SA");
        customerMapper.insert(customer, conn);

        Customer readCustomer = customerMapper.findByEmail("org@gmail.com", conn);
        assertNotNull(readCustomer, "Customer should be retrievable by email");
        assertEquals(customer.getEmail(), readCustomer.getEmail(), "Email should match");
    }

    @Test
    public void testDeleteCustomer() throws CustomerPersistenceException {
        Address address = new Address("CH", "1000", "Lausanne", "Route", "1");
        PrivateCustomer customer = new PrivateCustomer(null, "5566778899", "delete@test.com", address, "N", "Delete", "Me");
        customerMapper.insert(customer, conn);

        assertDoesNotThrow(() -> customerMapper.delete(customer.getId(), conn));
        Customer deletedCustomer = customerMapper.findByEmail("delete@test.com", conn);
        assertNull(deletedCustomer, "Customer should no longer be retrievable after deletion");
    }

    @Test
    public void testUpdateCustomer() throws CustomerPersistenceException {
        Address address = new Address("CH", "1000", "Lausanne", "Route", "1");
        PrivateCustomer customer = new PrivateCustomer(null, "5566778899", "test-update@test.gmail", address, "N", "Update", "Me");
        customerMapper.insert(customer, conn);

        System.out.println("01 avant le read"+customer.getAddress().getPostalCode());

        Customer updatedCustomer = customerMapper.findByEmail("test-update@test.gmail", conn);


        System.out.println("02 après le read"+updatedCustomer.getAddress().getPostalCode());
        updatedCustomer.setPhoneNumber("123456789");
        updatedCustomer.getAddress().setStreet("Avenue");
        updatedCustomer.getAddress().setStreetNumber("2");

        System.out.println("03"+updatedCustomer.getAddress().getPostalCode());

        assertDoesNotThrow(() -> customerMapper.update(updatedCustomer, conn));
        Customer fetchedCustomer = customerMapper.findByEmail("test-update@test.gmail", conn);
        assertEquals("123456789", fetchedCustomer.getPhone(), "Phone number should be updated");
        assertEquals("Avenue", fetchedCustomer.getAddress().getStreet(), "Street should be updated");
        assertEquals("2", fetchedCustomer.getAddress().getStreetNumber(), "Street number should be updated");

    }
}
