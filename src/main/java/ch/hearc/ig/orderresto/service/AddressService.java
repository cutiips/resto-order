package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Address;

public class AddressService {

    public Address createAddress(String countryCode, String postalCode, String locality, String street, String streetNumber) {
        return new Address(
                countryCode,
                postalCode,
                locality,
                street,
                streetNumber.isEmpty() ? null : streetNumber
        );
    }
}
