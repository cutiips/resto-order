package ch.hearc.ig.orderresto.persistence;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class CustomerMapper extends BaseMapper{

    public void insert(Customer customer) throws SQLException {
        String query = "INSERT INTO CLIENT (email, telephone, code_postal, localite, rue, num_rue, pays, est_une_femme, prenom, forme_social, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customer.getEmail());
            stmt.setString(2, customer.getPhone());

            AddressUtils.setPreparedStatementAddress(stmt, customer.getAddress(), 3);

            if (customer instanceof PrivateCustomer) {
                PrivateCustomer privateCustomer = (PrivateCustomer) customer;
                stmt.setString(8, privateCustomer.getGender());
                stmt.setString(9, privateCustomer.getFirstName());
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setString(11, "P");
            } else if (customer instanceof OrganizationCustomer) {
                OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
                stmt.setNull(8, java.sql.Types.VARCHAR);
                stmt.setNull(9, java.sql.Types.VARCHAR);
                stmt.setString(10, organizationCustomer.getLegalForm());
                stmt.setString(11, "O");
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du client : " + e.getMessage());
            throw e;
        }
    }

}
