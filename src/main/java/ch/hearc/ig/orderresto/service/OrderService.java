package ch.hearc.ig.orderresto.service;

import ch.hearc.ig.orderresto.business.Customer;
import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.persistence.mappers.OrderMapper;
import ch.hearc.ig.orderresto.persistence.utils.ConnectionManager;
import ch.hearc.ig.orderresto.service.exceptions.OrderServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderMapper orderMapper;

    public OrderService() {
        this.orderMapper = new OrderMapper();
    }

    public OrderService(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public boolean createOrder(Order order) throws OrderServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            orderMapper.insert(order, conn);
            conn.commit();

            System.out.println("Order created successfully!");
            return true;
        } catch (SQLException e) {
            throw new OrderServiceException("Failed to create order", e);
        }
    }

    public List<Order> findOrdersByCustomer(Customer customer) throws OrderServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return orderMapper.findOrdersByCustomer(customer, conn);
        } catch (SQLException e) {
            throw new OrderServiceException("Failed to find orders by customer", e);
        }
    }

    public Order getOrderById(Long id) throws OrderServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            return orderMapper.read(id, conn);
        } catch (SQLException e) {
            throw new OrderServiceException("Failed to get order by id", e);
        }
    }

    @Deprecated
    public List<Order> getAllOrders() {
        try (Connection conn = ConnectionManager.getConnection()) {
            return orderMapper.findAll(conn);
        } catch (SQLException e) {
            System.err.println("Error while finding orders: " + e.getMessage());
            return null;
        }
    }

    public boolean updateOrder(Order order) throws OrderServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            orderMapper.update(order, conn);
            conn.commit();

            System.out.println("Order updated successfully!");
            return true;
        } catch (SQLException e) {
            throw new OrderServiceException("Failed to update order", e);
        }
    }

    public boolean deleteOrder(Order order) throws OrderServiceException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            orderMapper.delete(order.getId(), conn);
            conn.commit();

            System.out.println("Order deleted successfully!");
            return true;
        } catch (SQLException e) {
            throw new OrderServiceException("Failed to delete order", e);
        }
    }
}
