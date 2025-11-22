package dao;

import model.Customer;
import model.Pet;
import model.ServiceOrder;
import strategy.service.BoardingService;
import strategy.service.GroomingService;
import strategy.service.MedicalService;
import strategy.service.ServiceStrategy;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcServiceOrderDAO implements ServiceOrderDAO {

    private final PetDAO petDAO = new JdbcPetDAO();
    private final CustomerDAO customerDAO = new JdbcCustomerDAO();

    private ServiceStrategy mapServiceStrategy(String serviceType) {
        if (serviceType == null) return null;
        switch (serviceType.toLowerCase()) {
            case "grooming" -> { return new GroomingService(); }
            case "boarding" -> { return new BoardingService(); }
            case "medical"  -> { return new MedicalService(); }
            default -> { return null; }
        }
    }

    private ServiceOrder mapRowToOrder(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String petId = rs.getString("pet_id");
        String serviceType = rs.getString("service_type");
        Timestamp entryTs = rs.getTimestamp("entry_time");
        Timestamp exitTs = rs.getTimestamp("exit_time");
        String status = rs.getString("status");
        double totalCost = rs.getDouble("total_cost");

        Pet pet = petDAO.findById(petId);
        Customer customer = customerDAO.findById(pet.getOwnerId());

        ServiceStrategy service = mapServiceStrategy(serviceType);
        LocalDateTime entryTime = entryTs != null ? entryTs.toLocalDateTime() : null;
        LocalDateTime exitTime = exitTs != null ? exitTs.toLocalDateTime() : null;

        ServiceOrder order = new ServiceOrder(id, pet, customer, service, entryTime, exitTime);
        order.setStatusFromDb(status);
        order.setTotalCost(totalCost);

        return order;
    }

    @Override
    public ServiceOrder save(ServiceOrder order) {
        String sql = """
                INSERT INTO service_orders
                (id, pet_id, service_type, entry_time, exit_time, status, total_cost)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getOrderId());
            ps.setString(2, order.getPet().getPetId());
            ps.setString(3, order.getService().getName());
            ps.setTimestamp(4, Timestamp.valueOf(order.getEntryTime()));
            if (order.getExitTime() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(order.getExitTime()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.setString(6, order.getStatus());
            ps.setDouble(7, order.getTotalCost());

            ps.executeUpdate();
            return order;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyimpan service order: " + e.getMessage(), e);
        }
    }

    @Override
    public ServiceOrder findById(String id) {
        String sql = "SELECT * FROM service_orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToOrder(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari service order dengan id: " + id, e);
        }
    }

    @Override
    public List<ServiceOrder> findActiveOrders() {
        String sql = """
                SELECT * FROM service_orders
                WHERE status = 'Menunggu' OR status = 'Sedang dikerjakan'
                ORDER BY entry_time
                """;

        List<ServiceOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToOrder(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil active orders", e);
        }
    }

    @Override
    public List<ServiceOrder> findByPet(String petId) {
        String sql = "SELECT * FROM service_orders WHERE pet_id = ? ORDER BY entry_time";

        List<ServiceOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, petId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapRowToOrder(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil order untuk pet: " + petId, e);
        }
    }

    @Override
    public boolean updateStatus(String orderId, String newStatus) {
        String sql = "UPDATE service_orders SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengupdate status order: " + orderId, e);
        }
    }

    @Override
    public List<ServiceOrder> findFinishedOrders() {
        String sql = "SELECT * FROM service_orders WHERE status = 'Selesai' ORDER BY entry_time";

        List<ServiceOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToOrder(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil finished orders", e);
        }
    }
}
