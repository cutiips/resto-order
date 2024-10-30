package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.business.Product;
import ch.hearc.ig.orderresto.business.Restaurant;
import ch.hearc.ig.orderresto.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.exceptions.RestaurantPersistenceException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderCLI extends AbstractCLI {

    public Order createNewOrder() throws ProductPersistenceException, SQLException, RestaurantPersistenceException {

        this.ln("======================================================");
        Restaurant restaurant = (new RestaurantCLI()).displayRestaurantIdsAndNames();

        ProductCLI productCLI = new ProductCLI();
        Product selectedProduct = productCLI.getRestaurantProduct(restaurant);

        if (selectedProduct != null) {
            System.out.println("Produit sélectionné : " + selectedProduct.getName());
        }

        CustomerCLI customerCLI = new CustomerCLI();
        Customer customer = customerCLI.getExistingCustomer();

        if (customer == null) {
            this.ln("Client non trouvé. Création d'un nouveau client...");
            customerCLI.addCustomer();
            customer = customerCLI.getExistingCustomer();
        }

        // Possible improvements:
        // - ask whether it's a takeAway order or not?
        // - Ask user for multiple products?
        Order order = new Order(null, customer, restaurant, false, LocalDateTime.now());
        order.addProduct(selectedProduct);

        // Actually place the order (this could/should be in a different method?)
        selectedProduct.addOrder(order);
        restaurant.addOrder(order);
        customer.addOrder(order);

        this.ln("Merci pour votre commande!");

        return order;
    }

    public Order selectOrder() throws CustomerPersistenceException {
        Customer customer = (new CustomerCLI()).getExistingCustomer();
        Object[] orders = customer.getOrders().toArray();
        if (orders.length == 0) {
            this.ln(String.format("Désolé, il n'y a aucune commande pour %s", customer.getEmail()));
            return null;
        }
        this.ln("Choisissez une commande:");
        for (int i = 0 ; i < orders.length ; i++) {
            Order order = (Order) orders[i];
            LocalDateTime when = order.getWhen();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
            this.ln(String.format("%d. %.2f, le %s chez %s.", i, order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        }
        int index = this.readIntFromUser(orders.length - 1);
        return (Order) orders[index];
    }

    public void displayOrder(Order order) {
        LocalDateTime when = order.getWhen();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy à hh:mm");
        this.ln(String.format("Commande %.2f, le %s chez %s.:", order.getTotalAmount(), when.format(formatter), order.getRestaurant().getName()));
        int index = 1;
        for (Product product: order.getProducts()) {
            this.ln(String.format("%d. %s", index, product));
            index++;
        }
    }
}
