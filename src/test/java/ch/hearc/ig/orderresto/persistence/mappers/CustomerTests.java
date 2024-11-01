package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.ig.orderresto.business.*;
import ch.hearc.ig.orderresto.persistence.mappers.CustomerMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerTests {

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
    public void tearDown() {
        try {
            conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        try {
            conn.close();
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
        Address address = new Address("CH", "123456", "Genève", "Rue", "2"); // Invalid postal code length
        PrivateCustomer customer = new PrivateCustomer(null, "543216789", "invalid@test.com", address, "N", "Invalid", "Postal");

        Exception exception = assertThrows(SQLException.class, () -> customerMapper.insert(customer, conn));
        String expectedMessage = "ORA-12899";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Expected ORA-12899 error due to invalid postal code length");
    }

    @Test
    public void testReadPrivateCustomer() throws CustomerPersistenceException {
        Address address = new Address("CH", "1000", "Lausanne", "Route", "1");
        PrivateCustomer customer = new PrivateCustomer(null, "1122334455", "read@test.com", address, "O", "Read", "Customer");
        customerMapper.insert(customer, conn);

        Customer readCustomer = customerMapper.read("read@test.com", conn);
        assertNotNull(readCustomer, "Customer should be retrievable by email");
        assertEquals(customer.getEmail(), readCustomer.getEmail(), "Email should match");
    }

    @Test
    public void testReadOrganizationCustomer() throws CustomerPersistenceException {
        Address address = new Address("CH", "1000", "Lausanne", "Route", "1");
        OrganizationCustomer customer = new OrganizationCustomer(null, "5566778899", "org@gmail.com", address, "Test SA", "SA");
        customerMapper.insert(customer, conn);

        Customer readCustomer = customerMapper.read("org@gmail.com", conn);
        assertNotNull(readCustomer, "Customer should be retrievable by email");
        assertEquals(customer.getEmail(), readCustomer.getEmail(), "Email should match");
    }

    @Test
    public void testDeleteCustomer() throws CustomerPersistenceException {
        Address address = new Address("CH", "1000", "Lausanne", "Route", "1");
        PrivateCustomer customer = new PrivateCustomer(null, "5566778899", "delete@test.com", address, "N", "Delete", "Me");
        customerMapper.insert(customer, conn);

        assertDoesNotThrow(() -> customerMapper.delete(customer.getId(), conn));
        Customer deletedCustomer = customerMapper.read("delete@test.com", conn);
        assertNull(deletedCustomer, "Customer should no longer be retrievable after deletion");
    }
}
