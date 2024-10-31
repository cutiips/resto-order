package ch.hearc.ig.orderresto.persistence.mappers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.ig.orderresto.business.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}