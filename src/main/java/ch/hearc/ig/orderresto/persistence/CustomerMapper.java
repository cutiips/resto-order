package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.exceptions.CustomerPersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerMapper extends BaseMapper<Customer> {

    public void insert(Customer customer) throws CustomerPersistenceException {
        String query = "INSERT INTO CLIENT (telephone, email, nom, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_sociale, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            CustomerUtils.setPreparedStatementForCustomer(stmt, customer);
            stmt.executeUpdate();
            addToCache(customer.getId(), customer); // Ajoute au cache
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de l'insertion du client", e);
        }
    }

    public Customer read(String email) throws CustomerPersistenceException {
        String query = "SELECT * FROM CLIENT WHERE email = ?";
        Customer customer = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer = CustomerUtils.mapCustomerFromResultSet(rs);
                if (customer != null) {
                    addToCache(customer.getId(), customer);
                }            }
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de la lecture du client", e);
        }

        return customer;
    }

    public Customer researchById(Long id) throws CustomerPersistenceException {
        Optional<Customer> cachedCustomer = findInCache(id);
        if (cachedCustomer.isPresent()) {
            return cachedCustomer.get();
        }

        String query = "SELECT * FROM CLIENT WHERE numero = ?";
        Customer customer = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer = CustomerUtils.mapCustomerFromResultSet(rs);
                if (customer != null) {
                    addToCache(id, customer);
                }
            }
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de la recherche du client par ID", e);
        }

        return customer;
    }

    public void update(Customer customer) throws CustomerPersistenceException {
        String query = "UPDATE CLIENT SET telephone = ?, nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ?, est_une_femme = ?, prenom = ?, forme_sociale = ? WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            CustomerUtils.setPreparedStatementForCustomer(stmt, customer);
            stmt.setString(11, customer.getEmail());
            stmt.executeUpdate();
            updateInCache(customer.getId(), customer); // Met à jour le cache
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de la mise à jour du client", e);
        }
    }

    public void delete(Long id) throws CustomerPersistenceException {
        String query = "DELETE FROM CLIENT WHERE numero = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            removeFromCache(id);
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de la suppression du client", e);
        }
    }
}
