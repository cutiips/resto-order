package ch.hearc.ig.orderresto.persistence.utils;

import ch.hearc.ig.orderresto.business.*;

import java.sql.*;

public class CustomerUtils {

    public static Customer mapCustomerFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("numero");
        String type = rs.getString("type");
        String email = rs.getString("email");
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

        Address address = new Address(codePostal, localite, rue, numRue, pays);

        if ("P".equals(type)) {
            return new PrivateCustomer(id, telephone, email, address, estUneFemme, prenom, nom);
        } else if ("O".equals(type)) {
            return new OrganizationCustomer(id, telephone, email, address, nom, formeSociale);
        }
        return null;
    }

    public static void setPreparedStatementForCustomer(PreparedStatement stmt, Customer customer) throws SQLException {
        stmt.setString(1, customer.getPhone());
        stmt.setString(2, customer.getEmail());

        AddressUtils.setPreparedStatementAddress(stmt, customer.getAddress(), 3);

        if (customer instanceof PrivateCustomer) {
            PrivateCustomer privateCustomer = (PrivateCustomer) customer;
            stmt.setString(4, privateCustomer.getLastName());
            stmt.setString(8, privateCustomer.getGender());
            stmt.setString(9, privateCustomer.getFirstName());
            stmt.setNull(10, java.sql.Types.VARCHAR); // Forme sociale
            stmt.setString(11, "P");
        } else if (customer instanceof OrganizationCustomer) {
            OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
            stmt.setString(4, organizationCustomer.getName());
            stmt.setNull(8, java.sql.Types.VARCHAR); // Sexe
            stmt.setNull(9, java.sql.Types.VARCHAR); // Prénom
            stmt.setString(10, organizationCustomer.getLegalForm()); // Forme sociale
            stmt.setString(11, "O");
        }
    }
}
