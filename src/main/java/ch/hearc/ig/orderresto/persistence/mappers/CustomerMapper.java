package ch.hearc.ig.orderresto.persistence.mappers;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.persistence.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.persistence.utils.CustomerUtils;

import java.sql.*;
import java.util.Optional;

public class CustomerMapper extends BaseMapper<Customer> {
    public void insert(Customer customer, Connection conn) throws CustomerPersistenceException {
        String query = "INSERT INTO CLIENT (telephone, email, nom, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_sociale, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, new String[]{"NUMERO"})) {
            CustomerUtils.setPreparedStatementForCustomer(stmt, customer, CustomerUtils.QueryType.INSERT);
            stmt.executeUpdate();

            // Récupérer l'ID généré par la base de données
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long generatedId = generatedKeys.getLong(1);
                    if (generatedId == null) {
                        throw new SQLException("ID généré est nul après l'insertion du client.");
                    }
                    customer.setId(generatedId);
                } else {
                    throw new SQLException("Échec de l'insertion du client, aucun ID généré.");
                }
            }


            // Ajouter au cache avec l'ID mis à jour
            addToCache(customer.getId(), customer);
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du client: " + e.getMessage());
            e.printStackTrace();
            throw new CustomerPersistenceException("Erreur lors de l'insertion du client", e);
        }


    }


    public Customer read(String email, Connection conn) throws CustomerPersistenceException {
        String query = "SELECT * FROM CLIENT WHERE email = ?";
        Customer customer = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

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

    public Customer findById(Long id, Connection conn) throws CustomerPersistenceException {
        Optional<Customer> cachedCustomer = findInCache(id);
        if (cachedCustomer.isPresent()) {
            System.out.println("Customer found in cache: " + id);
            return cachedCustomer.get();
        }

        String query = "SELECT * FROM CLIENT WHERE numero = ?";
        Customer customer = null;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer = CustomerUtils.mapCustomerFromResultSet(rs);
                if (customer != null && customer.getId() != null) {
                    addToCache(customer.getId(), customer);
                }

            }
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de la recherche du client par ID", e);
        }

        return customer;
    }



    public void update(Customer customer, Connection conn) throws CustomerPersistenceException {
        String query = "UPDATE CLIENT SET telephone = ?, nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ?, est_une_femme = ?, prenom = ?, forme_sociale = ? WHERE email = ?";



        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            CustomerUtils.setPreparedStatementForCustomer(stmt, customer, CustomerUtils.QueryType.UPDATE);
            stmt.executeUpdate();
            updateInCache(customer.getId(), customer);
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de la mise à jour du client", e);
        }
    }

    public void delete(Long id, Connection conn) throws CustomerPersistenceException {
        String query = "DELETE FROM CLIENT WHERE numero = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            removeFromCache(id);
        } catch (SQLException e) {
            throw new CustomerPersistenceException("Erreur lors de la suppression du client", e);
        }
    }
}
