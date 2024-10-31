package ch.hearc.ig.orderresto.persistence.mappers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class RestaurantMapperTest {

    private RestaurantMapper restaurantMapper;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws Exception {
        // Créer des mocks pour les objets JDBC
        mockConnection = Mockito.mock(Connection.class);
        mockStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        // Configurer les mocks pour retourner un PreparedStatement quand on prépare une requête
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        // Configurer les données retournées par le ResultSet
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("numero")).thenReturn(1L);
        when(mockResultSet.getString("nom")).thenReturn("Restaurant Test");

        // Créer une instance de RestaurantMapper utilisant la connexion mockée
        restaurantMapper = new RestaurantMapper() {
            @Override
            protected Connection getConnection() {
                return mockConnection;
            }
        };
    }

    @Test
    public void testIdentityMapPreventsMultipleQueries() throws Exception {
        // Premier appel - devrait effectuer une requête vers la base de données
        Restaurant restaurant1 = restaurantMapper.findById(1L);
        verify(mockConnection, times(1)).prepareStatement(anyString());

        // Deuxième appel avec le même ID - devrait récupérer depuis l'IdentityMap sans nouvelle requête
        Restaurant restaurant2 = restaurantMapper.findById(1L);
        verify(mockConnection, times(1)).prepareStatement(anyString());

        // Vérifier que les deux objets sont identiques (même référence) car récupérés depuis le cache
        assertSame(restaurant1, restaurant2, "L'IdentityMap ne fonctionne pas correctement, les objets devraient être identiques");
    }

    @Test
    public void testUpdateRestaurant() throws Exception {
        // Créer un restaurant factice
        Restaurant mockRestaurant = Mockito.mock(Restaurant.class);
        Address mockAddress = Mockito.mock(Address.class);
        when(mockRestaurant.getName()).thenReturn("Restaurant Test");
        when(mockRestaurant.getAddress()).thenReturn(mockAddress);
        when(mockRestaurant.getId()).thenReturn(1L);

        // Mettre à jour le restaurant
        restaurantMapper.update(mockRestaurant);

        // Vérifier que la requête de mise à jour a été exécutée
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }

    @Test
    public void testDeleteRestaurant() throws Exception {
        // Supprimer un restaurant par ID
        restaurantMapper.delete(1L);

        // Vérifier que la requête de suppression a été exécutée
        verify(mockStatement, times(1)).setLong(1, 1L); // Vérifie que l'ID a été défini sur le PreparedStatement
        verify(mockStatement, times(1)).executeUpdate(); // Vérifie que la requête a été exécutée
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }

    @Test
    public void testFindAllRestaurants() throws Exception {
        // Configurer le mock pour retourner plusieurs restaurants
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getLong("numero")).thenReturn(1L, 2L);
        when(mockResultSet.getString("nom")).thenReturn("Restaurant Test 1", "Restaurant Test 2");

        // Appeler la méthode findAll
        List<Restaurant> restaurants = restaurantMapper.findAll();

        // Vérifier le nombre de restaurants récupérés
        assertEquals(2, restaurants.size(), "Le nombre de restaurants récupérés est incorrect");

        // Vérifier que la requête a été exécutée
        verify(mockStatement, times(1)).executeQuery();
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }
}
