package ch.hearc.ig.orderresto.persistence.mappers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomerMapperTest {

    private CustomerMapper customerMapper;
    private Connection mockConnection;
    private PreparedStatement mockStatement;

    @BeforeEach
    public void setUp() throws Exception {
        // Créer des mocks pour les objets JDBC
        mockConnection = Mockito.mock(Connection.class);
        mockStatement = Mockito.mock(PreparedStatement.class);
        ResultSet mockResultSet = Mockito.mock(ResultSet.class);  // Converti en variable locale

        // Configurer les mocks pour retourner un PreparedStatement quand on prépare une requête
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement); // Ajoutez cette ligne pour les requêtes sans GENERATED_KEYS

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        // Créer une instance de CustomerMapper utilisant la connexion mockée
        customerMapper = new CustomerMapper() {
            @Override
            protected Connection getConnection() {
                return mockConnection;
            }
        };

    }

    @Test
    public void testIdentityMapPreventsMultipleQueries() throws Exception {
        // Mock the Customer object
        Customer mockCustomer = Mockito.mock(Customer.class);
        when(mockCustomer.getId()).thenReturn(1L);
        when(mockCustomer.getEmail()).thenReturn("test@example.com");

        // Add the customer to the cache
        customerMapper.addToCache(mockCustomer.getId(), mockCustomer);

        // Attempt to read the customer by ID, expecting it to come from the cache instead of executing a query
        Customer retrievedCustomer = customerMapper.researchById(1L);

        // Verify that the retrieved customer matches the cached one
        assertEquals(mockCustomer, retrievedCustomer, "The retrieved customer should be the same as the cached one.");

        // Verify that no database interaction occurred, as the object should have been retrieved from the cache
        verify(mockConnection, times(0)).prepareStatement(anyString());
        verify(mockStatement, times(0)).executeQuery();
    }


    @Test
    public void testInsertCustomer() throws Exception {
        // Créer un customer factice avec des valeurs nécessaires
        Customer mockCustomer = Mockito.mock(Customer.class);
        Address mockAddress = Mockito.mock(Address.class);
        when(mockCustomer.getAddress()).thenReturn(mockAddress);
        when(mockAddress.getPostalCode()).thenReturn("12345");
        when(mockCustomer.getPhone()).thenReturn("0123456789");
        when(mockCustomer.getEmail()).thenReturn("test@example.com");

        // Configurer le comportement du PreparedStatement
        doNothing().when(mockStatement).setString(anyInt(), anyString());

        // Insérer le customer
        customerMapper.insert(mockCustomer);

        // Vérifier que la requête a été exécutée
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockConnection, times(1)).prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS));

        // Vérifier que l'ID est bien défini
        assertNotNull(mockCustomer.getId(), "L'ID du client ne devrait pas être nul après l'insertion.");
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        // Créer un customer factice
        Customer mockCustomer = Mockito.mock(Customer.class);
        Address mockAddress = Mockito.mock(Address.class);
        when(mockCustomer.getAddress()).thenReturn(mockAddress);
        when(mockAddress.getPostalCode()).thenReturn("12345");
        when(mockCustomer.getPhone()).thenReturn("0123456789");
        when(mockCustomer.getEmail()).thenReturn("test@test.com");
        when(mockCustomer.getId()).thenReturn(1L);

        // Mettre à jour le customer
        customerMapper.update(mockCustomer);

        // Vérifier que la requête de mise à jour a été exécutée
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        // Supprimer un customer par ID
        customerMapper.delete(1L);

        // Vérifier que la requête de suppression a été exécutée
        verify(mockStatement, times(1)).setLong(1, 1L); // Vérifie que l'ID a été défini sur le PreparedStatement
        verify(mockStatement, times(1)).executeUpdate(); // Vérifie que la requête a été exécutée
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }
}
