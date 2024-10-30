package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Address;
import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.OrganizationCustomer;
import ch.hearc.ig.orderresto.business.PrivateCustomer;
import ch.hearc.ig.orderresto.persistence.CustomerMapper;
import ch.hearc.ig.orderresto.persistence.FakeDb;

import java.sql.SQLException;
import java.util.Objects;

public class CustomerCLI extends AbstractCLI {

    private final CustomerMapper customerMapper = new CustomerMapper();

    public void run() throws SQLException {
        this.ln("======================================================");
        this.ln("0. Retour au menu principal");
        this.ln("1. Nouveau client");
        this.ln("2. Client existant");
        int userChoice = this.readIntFromUser(2);
        this.handleUserChoice(userChoice);
    }

    private void handleUserChoice(int userChoice) {
        switch (userChoice) {
            case 0:
                return;
            case 1:
                this.addCustomer();
                break;
            case 2:
                Customer customer = this.getExistingCustomer();
                if (customer != null) {
                    this.ln("Client trouvé : ");
                }
                break;
        }
    }

    protected Customer createCustomer(boolean isPrivateCustomer) {
        this.ln("Quelle est votre adresse email?");
        String email = this.readEmailFromUser();
        this.ln("Quel est votre numéro de téléphone?");
        String phone = this.readStringFromUser();
        Address address = (new AddressCLI()).getNewAddress();

        if (isPrivateCustomer) {
            this.ln("Quel est votre prénom ?");
            String firstName = this.readStringFromUser();
            this.ln("Quel est votre nom ?");
            String lastName = this.readStringFromUser();
            this.ln("Êtes-vous un homme ou une femme (H/F) ?");
            String gender = this.readChoicesFromUser(new String[]{"H", "F"});
            gender = Objects.equals(gender, "H") ? "O" : "N"; // Simplified gender assignment

            return new PrivateCustomer(null, phone, email, address, gender, firstName, lastName);
        } else {
            this.ln("Quel est le nom de votre organisation ?");
            String name = this.readStringFromUser();
            this.ln(String.format("%s est une société anonyme (SA) ?, une association (A) ou une fondation (F) ?", name));
            String legalForm = this.readChoicesFromUser(new String[]{"SA", "A", "F"});

            return new OrganizationCustomer(null, phone, email, address, name, legalForm);
        }
    }

    public void addCustomer() {
        this.ln("Type de client : 1. Client Privé, 2. Organisation");
        int choice = this.readIntFromUser(1, 2);
        Customer customer = createCustomer(choice == 1);

        try {
            customerMapper.insert(customer);
            this.ln("Client ajouté avec succès !");
        } catch (SQLException e) {
            this.ln("Erreur lors de l'insertion du client : " + e.getMessage());
        }
    }

    public Customer getExistingCustomer(){
        this.ln("Quelle est votre addresse email?");
        String email = this.readEmailFromUser();

        try {
            return customerMapper.read(email);
        } catch (SQLException e) {
            this.ln("Erreur lors de la lecture du client : " + e.getMessage());
            return null;
        }
    }
}
