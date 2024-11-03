package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.mappers.CustomerMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import ch.hearc.ig.orderresto.service.exceptions.CustomerServiceException;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerService {

    private final CustomerMapper customerMapper;

    public CustomerService() {
        this.customerMapper = new CustomerMapper();
    }

    public void addCustomer(Customer customer) throws CustomerServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            customerMapper.insert(customer, conn);
            conn.commit();

            System.out.println("Client added successfully!");
        } catch (SQLException | CustomerPersistenceException e) {
            throw new CustomerServiceException("Failed to add customer", e);
        }
    }


    public Customer getExistingCustomer(String email) throws CustomerServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return customerMapper.findByEmail(email, conn);
        } catch (SQLException | CustomerPersistenceException e) {
            throw new CustomerServiceException("Failed to get customer by email", e);
        }
    }

    public Customer getCustomerById(Long id, Connection conn) throws CustomerPersistenceException {
        return customerMapper.read(id, conn);
    }

    public void updateCustomer(Customer customer) throws CustomerServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            customerMapper.update(customer, conn);
            conn.commit();

            System.out.println("Client updated successfully!");
        } catch (SQLException | CustomerPersistenceException e) {
            throw new CustomerServiceException("Failed to update customer", e);
        }
    }


    public void deleteCustomer(Customer customer) throws CustomerServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            customerMapper.delete(customer.getId(), conn);
            conn.commit();

            System.out.println("Client deleted successfully!");
        } catch (SQLException | CustomerPersistenceException e) {
            throw new CustomerServiceException("Failed to delete customer", e);
        }
    }

}
