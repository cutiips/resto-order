package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.business.Address;
// TODO : ajouter un dossier Utils

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CustomerMapper extends BaseMapper{

    public void insert(Customer customer) throws SQLException {
        String query = "INSERT INTO CLIENT (email, telephone, nom, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_sociale, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPhone());

            AddressUtils.setPreparedStatementAddress(stmt, customer.getAddress(), 4);

            if (customer instanceof PrivateCustomer) {
                PrivateCustomer privateCustomer = (PrivateCustomer) customer;
                stmt.setString(3, privateCustomer.getLastName()); // Nom de famille
                stmt.setString(9, privateCustomer.getGender());
                stmt.setString(10, privateCustomer.getFirstName());
                stmt.setNull(11, java.sql.Types.VARCHAR); // Forme sociale pour client privé
                stmt.setString(12, "P"); // Type pour client privé
            }

            else if (customer instanceof OrganizationCustomer) {
                OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
                stmt.setString(3, organizationCustomer.getName()); // Nom de l'organisation
                stmt.setNull(9, java.sql.Types.VARCHAR); // Sexe pour client organisation
                stmt.setNull(10, java.sql.Types.VARCHAR); // Prénom pour client organisation
                stmt.setString(11, organizationCustomer.getLegalForm()); // Forme sociale
                stmt.setString(12, "O"); // Type pour client organisation
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du client : " + e.getMessage());
            throw e;
        }
    }

    public Customer read(String email) throws SQLException {
        String query = "SELECT * FROM CLIENT WHERE email = ?";
        Customer customer = null;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");
                String telephone = rs.getString("telephone");
                String nom = rs.getString("nom");
                String codePostal = rs.getString("code_postal");
                String localite = rs.getString("localite");
                String rue = rs.getString("rue");
                String numRue = rs.getString("num_rue");
                String pays = rs.getString("pays");
                String estUneFemme = rs.getString("est_une_femme");
                String prenom = rs.getString("prenom");
                String formeSociale = rs.getString("forme_sociale");

                Address address = new Address(codePostal, localite, rue, numRue, pays); // Assumez que vous avez un constructeur approprié

                if ("P".equals(type)) {
                    customer = new PrivateCustomer(null, telephone, email, address, estUneFemme, prenom, nom);
                } else if ("O".equals(type)) {
                    customer = new OrganizationCustomer(null, telephone, email, address, nom, formeSociale);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la lecture du client : " + e.getMessage());
            throw e;
        }

        return customer;
    }

    // UPDATE
    public void update(Customer customer) throws SQLException {
        String query = "UPDATE CLIENT SET telephone = ?, nom = ?, code_postal = ?, localite = ?, rue = ?, num_rue = ?, pays = ?, est_une_femme = ?, prenom = ?, forme_sociale = ? WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getPhone());
            Address address = customer.getAddress();
            AddressUtils.setPreparedStatementAddress(stmt, address, 3);

            if (customer instanceof PrivateCustomer) {
                PrivateCustomer privateCustomer = (PrivateCustomer) customer;
                stmt.setString(2, privateCustomer.getLastName());
                stmt.setString(8, privateCustomer.getGender());
                stmt.setString(9, privateCustomer.getFirstName());
                stmt.setNull(10, java.sql.Types.VARCHAR); // Forme sociale pour client privé
            } else if (customer instanceof OrganizationCustomer) {
                OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
                stmt.setString(2, organizationCustomer.getName());
                stmt.setNull(8, java.sql.Types.VARCHAR); // Sexe pour client organisation
                stmt.setNull(9, java.sql.Types.VARCHAR); // Prénom pour client organisation
                stmt.setString(10, organizationCustomer.getLegalForm()); // Forme sociale
            }

            stmt.setString(11, customer.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du client : " + e.getMessage());
            throw e;
        }
    }

    // DELETE
    public void delete(String email) throws SQLException {
        String query = "DELETE FROM CLIENT WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client : " + e.getMessage());
            throw e;
        }
    }
}
