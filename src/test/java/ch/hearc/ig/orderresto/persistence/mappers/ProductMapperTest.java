package ch.hearc.ig.orderresto.persistence.mappers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProductMapperTest {

    private ProductMapper productMapper;
    private Connection mockConnection;
    private PreparedStatement mockStatement;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        productMapper = new ProductMapper(); // Initialize the mapper
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

        // Créer une instance de ProductMapper utilisant la connexion mockée
        productMapper = new ProductMapper() {
            @Override
            protected Connection getConnection() {
                return mockConnection;
            }
        };
    }

    @Test
    public void testInsertProduct() throws Exception {
        // Créer un produit factice avec des valeurs nécessaires
        Restaurant mockRestaurant = Mockito.mock(Restaurant.class);
        when(mockRestaurant.getId()).thenReturn(1L);
        Product mockProduct = new Product(null, "Produit Test", BigDecimal.valueOf(10.5), "Description Test", mockRestaurant);

        // Insérer le produit
        productMapper.insert(mockProduct);

        // Vérifier que la requête a été exécutée
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockConnection, times(1)).prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS));

        // Vérifier que l'ID est bien défini
        assertNotNull(mockProduct.getId(), "L'ID du produit ne devrait pas être nul après l'insertion.");
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Créer un produit factice
        Restaurant mockRestaurant = Mockito.mock(Restaurant.class);
        when(mockRestaurant.getId()).thenReturn(1L);
        Product mockProduct = new Product(1L, "Produit Test", BigDecimal.valueOf(15.0), "Description Mise à Jour", mockRestaurant);

        // Mettre à jour le produit
        productMapper.update(mockProduct);

        // Vérifier que la requête de mise à jour a été exécutée
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Supprimer un produit par ID
        productMapper.delete(1L);

        // Vérifier que la requête de suppression a été exécutée
        verify(mockStatement, times(1)).setLong(1, 1L); // Vérifie que l'ID a été défini sur le PreparedStatement
        verify(mockStatement, times(1)).executeUpdate(); // Vérifie que la requête a été exécutée
        verify(mockConnection, times(1)).prepareStatement(anyString());
    }

    @Test
    public void testIdentityMapPreventsMultipleQueries() throws Exception {
        // Mock d'un produit avec un ID
        Restaurant mockRestaurant = Mockito.mock(Restaurant.class);
        Product mockProduct = new Product(1L, "Produit Test", BigDecimal.valueOf(10.5), "Description Test", mockRestaurant);

        // Ajouter le produit au cache
        productMapper.addToCache(mockProduct.getId(), mockProduct);

        // Tenter de lire le produit par ID, en s'attendant à ce qu'il provienne du cache au lieu d'exécuter une requête
        Product retrievedProduct = productMapper.findById(1L);

        // Vérifier que le produit récupéré correspond bien à celui du cache
        assertEquals(mockProduct, retrievedProduct, "Le produit récupéré devrait être le même que celui en cache.");

        // Vérifier qu'aucune interaction avec la base de données n'a eu lieu, car l'objet devrait être récupéré depuis le cache
        verify(mockConnection, times(0)).prepareStatement(anyString());
        verify(mockStatement, times(0)).executeQuery();
    }
}
