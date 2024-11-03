package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.persistence.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.ProductMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// TODO : implémenter ProductPersistenceException
public class ProductService {
    //private final ProductMapper productMapper = new ProductMapper();

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

    public Product getProductById(Long id) throws ProductPersistenceException {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            return productMapper.read(id, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public boolean addProduct(Product product) throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            productMapper.insert(product, conn);
            conn.commit();

            System.out.println("Product added successfully!");
            return true;
        } catch (ProductPersistenceException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            throw e; // Propager l'exception au lieu de la gérer
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            throw new ProductPersistenceException("Error while adding product", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }



    public List<Product> getProductsByRestaurantId(Long restaurantId) throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            return productMapper.getProductsByRestaurantId(restaurantId, conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public boolean updateProduct(Product product) throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            productMapper.update(product, conn);
            conn.commit();

            System.out.println("Product updated successfully!");
            return true;
        } catch (ProductPersistenceException e) {
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error while updating product: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public boolean deleteProduct(Long id) throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            productMapper.delete(id, conn);
            conn.commit();

            System.out.println("Product deleted successfully!");
            return true;
        } catch (ProductPersistenceException e) {
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error while deleting product: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public List<Product> getAllProducts() throws ProductPersistenceException {
        Connection conn = null;

        try {
            conn = ConnectionManager.getConnection();
            return productMapper.findAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }


}






