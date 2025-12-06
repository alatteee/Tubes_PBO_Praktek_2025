package dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCustomerDAO implements CustomerDAO {

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (id, name, phone) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getUncheckedConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getPhone());

            ps.executeUpdate();
            return customer;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyimpan customer: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer findById(String id) {
        String sql = "SELECT id, name, phone FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getUncheckedConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phone")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal menemukan customer dengan id: " + id, e);
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT id, name, phone FROM customers ORDER BY id ASC";

        List<Customer> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getUncheckedConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phone")
                );
                result.add(c);
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil semua customer", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getUncheckedConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus customer dengan id: " + id, e);
        }
    }

    @Override
    public Customer update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, phone = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getUncheckedConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getCustomerId());

            int rows = ps.executeUpdate();
            return rows > 0 ? customer : null;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengupdate customer dengan id: " + customer.getCustomerId(), e);
        }
    }
}