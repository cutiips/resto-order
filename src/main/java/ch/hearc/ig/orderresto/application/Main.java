package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.exceptions.CustomerPersistenceException;
import ch.hearc.ig.orderresto.exceptions.ProductPersistenceException;
import ch.hearc.ig.orderresto.exceptions.RestaurantPersistenceException;
import ch.hearc.ig.orderresto.presentation.MainCLI;

import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException, ProductPersistenceException, RestaurantPersistenceException, CustomerPersistenceException {

    (new MainCLI()).run();
  }
}
