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
        stmt.setString(4, customer.getAddress().getPostalCode());

        stmt.setString(5, customer.getAddress().getLocality());
        stmt.setString(6, customer.getAddress().getStreet());
        stmt.setString(7, customer.getAddress().getStreetNumber());
        stmt.setString(8, customer.getAddress().getCountryCode());

        if (customer instanceof PrivateCustomer) {
            PrivateCustomer privateCustomer = (PrivateCustomer) customer;
            stmt.setString(3, privateCustomer.getLastName());
            stmt.setString(9, "O".equalsIgnoreCase(privateCustomer.getGender()) ? "O" : "N");
            stmt.setString(10, privateCustomer.getFirstName());
            stmt.setNull(11, java.sql.Types.VARCHAR); // Forme sociale est null pour PrivateCustomer
            stmt.setString(12, "P");
        } else if (customer instanceof OrganizationCustomer) {
            OrganizationCustomer organizationCustomer = (OrganizationCustomer) customer;
            stmt.setString(3, organizationCustomer.getName());
            stmt.setNull(9, java.sql.Types.VARCHAR); // Sexe est null pour OrganizationCustomer
            stmt.setNull(10, java.sql.Types.VARCHAR); // Prénom est null pour OrganizationCustomer
            stmt.setString(11, organizationCustomer.getLegalForm());
            stmt.setString(12, "O");
        }
    }



}