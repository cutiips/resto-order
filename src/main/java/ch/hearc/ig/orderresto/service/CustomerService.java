package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.CustomerMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

//TODO : implémenter CustomerServiceException
public class CustomerService {

    //private final CustomerMapper customerMapper = new CustomerMapper();

    private final CustomerMapper customerMapper;

    public CustomerService() {
        this.customerMapper = new CustomerMapper();
    }

    public CustomerService(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public void addCustomer(Customer customer) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            customerMapper.insert(customer, conn);
            conn.commit();

            System.out.println("Client added successfully!");
        } catch (SQLException | CustomerPersistenceException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while adding customer: " + e.getMessage());
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

    public Customer getExistingCustomer(String email) {
        Connection conn = null;
        Customer customer = null;
        try {
            conn = ConnectionManager.getConnection();
            customer = customerMapper.findByEmail(email, conn);
        } catch (SQLException | CustomerPersistenceException e) {
            System.err.println("Error while reading customer: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
        return customer;
    }

    public Customer getCustomerById(Long id, Connection conn) throws CustomerPersistenceException {
        return customerMapper.read(id, conn);
    }

    public void updateCustomer(Customer customer) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            customerMapper.update(customer, conn);
            conn.commit();

            System.out.println("Client updated successfully!");
        } catch (SQLException | CustomerPersistenceException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while updating customer: " + e.getMessage());
            throw new RuntimeException("Failed to update customer", e);
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

    public void deleteCustomer(Customer customer) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            customerMapper.delete(customer.getId(), conn);
            conn.commit();

            System.out.println("Client deleted successfully!");
        } catch (SQLException | CustomerPersistenceException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while deleting customer: " + e.getMessage());
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
}
