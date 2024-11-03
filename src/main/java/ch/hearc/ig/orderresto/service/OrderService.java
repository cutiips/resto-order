package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.mappers.OrderMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

//TODO : implémenter OrderServiceException
public class OrderService {


    // ajout d'un constructeur
    // avant il y avait juste la déclaration de la variable, soit :
    // private final OrderMapper orderMapper = new OrderMapper();
    private final OrderMapper orderMapper;

    public OrderService() {
        this.orderMapper = new OrderMapper();
    }

    // Nouveau constructeur pour les tests
    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public boolean createOrder(Order order) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            orderMapper.insert(order, conn);
            conn.commit();

            System.out.println("Order created successfully!");
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while creating order: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public List<Order> findOrdersByCustomer(Customer customer) {
        Connection conn = null;
        List<Order> orders = null;
        try {
            conn = ConnectionManager.getConnection();
            orders = orderMapper.findOrdersByCustomer(customer, conn);
        } catch (SQLException e) {
            System.err.println("Error while finding orders: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
        return orders;
    }

    public Order getOrderById(Long id) {
        Connection conn = null;
        Order order = null;
        try {
            conn = ConnectionManager.getConnection();
            order = orderMapper.read(id, conn);
        } catch (SQLException e) {
            System.err.println("Error while finding order: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
        return order;
    }

    public List<Order> getAllOrders() {
        Connection conn = null;
        List<Order> orders = null;
        try {
            conn = ConnectionManager.getConnection();
            orders = orderMapper.findAll(conn);
        } catch (SQLException e) {
            System.err.println("Error while finding orders: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
        return orders;
    }

    public boolean updateOrder(Order order) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            orderMapper.update(order, conn);
            conn.commit();

            System.out.println("Order updated successfully!");
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while updating order: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    public boolean deleteOrder(Order order) {
        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);

            orderMapper.delete(order.getId(), conn);
            conn.commit();

            System.out.println("Order deleted successfully!");
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during transaction rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Error while deleting order: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                }
            }
        }
    }
}
