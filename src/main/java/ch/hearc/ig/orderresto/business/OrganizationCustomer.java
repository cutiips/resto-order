package ch.hearc.ig.orderresto.business;

public class OrganizationCustomer extends Customer {

    private String name;
    private String legalForm;

    public OrganizationCustomer(Long id, String phone, String email, Address address, String name, String legalForm) {
        super(id, phone, email, address);
        this.name = name;
        this.legalForm = legalForm;
    }

    public String getName() {
        return name;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

}