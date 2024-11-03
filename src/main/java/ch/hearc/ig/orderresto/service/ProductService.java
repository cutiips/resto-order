package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.ProductMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import ch.hearc.ig.orderresto.service.exceptions.ProductServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductMapper productMapper;

    public ProductService() {
        this.productMapper = new ProductMapper();
    }

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public Product getProductById(Long id, Connection conn) throws ProductPersistenceException {
        return productMapper.read(id, conn);
    }

    public Product getProductById(Long id) throws ProductServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return productMapper.read(id, conn);
        } catch (SQLException | ProductPersistenceException e) {
            throw new ProductServiceException("Error while getting product by id", e);
        }
    }

    public boolean addProduct(Product product) throws ProductPersistenceException, ProductServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            productMapper.insert(product, conn);
            conn.commit();

            System.out.println("Product added successfully!");
            return true;
        } catch (SQLException e) {
            throw new ProductServiceException("Failed to add product", e);
        }
    }

    public List<Product> getProductsByRestaurantId(Long restaurantId) throws ProductPersistenceException, ProductServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return productMapper.getProductsByRestaurantId(restaurantId, conn);
        } catch (SQLException e) {
            throw new ProductServiceException("Failed to get products by restaurant id", e);
        }
    }

    public boolean updateProduct(Product product) throws ProductServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            productMapper.update(product, conn);
            conn.commit();

            System.out.println("Product updated successfully!");
            return true;
        } catch (SQLException | ProductPersistenceException e) {
            throw new ProductServiceException("Failed to update product", e);
        }
    }

    public boolean deleteProduct(Long id) throws ProductServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            productMapper.delete(id, conn);
            conn.commit();

            System.out.println("Product deleted successfully!");
            return true;
        } catch (SQLException | ProductPersistenceException e) {
            throw new ProductServiceException("Failed to delete product", e);
        }
    }

    public List<Product> getAllProducts() throws ProductServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return productMapper.findAll(conn);
        } catch (SQLException | ProductPersistenceException e) {
            throw new ProductServiceException("Failed to get all products", e);
        }
    }
}






