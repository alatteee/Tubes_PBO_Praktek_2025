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

    //   SERVICE STRATEGY MAPPING
    private ServiceStrategy mapServiceStrategy(String serviceType) {

        if (serviceType == null || serviceType.isBlank()) {
            throw new IllegalArgumentException("Kolom service_type pada database tidak boleh NULL.");
        }

        String type = serviceType.trim().toLowerCase();

        if (type.contains("groom")) return new GroomingService();
        if (type.contains("board")) return new BoardingService();
        if (type.contains("med"))   return new MedicalService();

        throw new IllegalArgumentException("Service type tidak dikenali: " + serviceType);
    }

    //   MAP RESULTSET KE ServiceOrder (CORE FIX)
    private ServiceOrder mapRowToOrder(ResultSet rs) throws SQLException {

        String id = rs.getString("id");
        String petId = rs.getString("pet_id");
        String serviceType = rs.getString("service_type");
        Timestamp entryTs = rs.getTimestamp("entry_time");
        Timestamp exitTs = rs.getTimestamp("exit_time");
        String status = rs.getString("status");
        double totalCost = rs.getDouble("total_cost");

        // ===== VALIDASI ENTRY TIME =====
        if (entryTs == null) {
            throw new IllegalArgumentException("entry_time NULL pada order id = " + id);
        }

        // ===== PET =====
        Pet pet = petDAO.findById(petId);
        if (pet == null) {
            throw new IllegalArgumentException("Pet dengan ID " + petId + " tidak ditemukan.");
        }

        // ===== CUSTOMER =====
        Customer customer = customerDAO.findById(pet.getOwnerId());
        if (customer == null) {
            throw new IllegalArgumentException("Owner untuk pet " + petId + " tidak ditemukan.");
        }

        // ===== SERVICE (Tidak boleh null) =====
        ServiceStrategy service = mapServiceStrategy(serviceType);

        // ===== WAKTU =====
        LocalDateTime entryTime = entryTs.toLocalDateTime();
        LocalDateTime exitTime = (exitTs != null) ? exitTs.toLocalDateTime() : entryTime;

        // ===== BANGUN ORDER (VALIDATION DI SERVICEORDER AKTIF) =====
        ServiceOrder order = new ServiceOrder(id, pet, customer, service, entryTime, exitTime);

        order.setStatusFromDb(status);
        order.setTotalCost(totalCost);

        return order;
    }

    //   INSERT ORDER
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

    //   GET BY ID
    @Override
    public ServiceOrder findById(String id) {
        String sql = "SELECT * FROM service_orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                try {
                    return mapRowToOrder(rs);
                } catch (Exception e) {
                    throw new RuntimeException("Gagal memetakan service order: " + e.getMessage(), e);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari service order dengan id: " + id, e);
        }
    }

    //   GET ACTIVE ORDERS
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
                try {
                    result.add(mapRowToOrder(rs));
                } catch (Exception e) {
                    throw new RuntimeException("Gagal memetakan active order: " + e.getMessage(), e);
                }
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil active orders.", e);
        }
    }

    //   GET ORDERS BY PET
    @Override
    public List<ServiceOrder> findByPet(String petId) {

        String sql = "SELECT * FROM service_orders WHERE pet_id = ? ORDER BY entry_time";

        List<ServiceOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, petId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                try {
                    result.add(mapRowToOrder(rs));
                } catch (Exception e) {
                    throw new RuntimeException("Gagal memetakan order untuk pet: " + petId, e);
                }
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil order untuk pet: " + petId, e);
        }
    }

    //   UPDATE STATUS
    @Override
    public boolean updateStatus(String orderId, String newStatus) {

        String sql = "UPDATE service_orders SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, orderId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengupdate status order: " + orderId, e);
        }
    }

    //   GET FINISHED ORDERS
    @Override
    public List<ServiceOrder> findFinishedOrders() {

        String sql = "SELECT * FROM service_orders WHERE status = 'Selesai' ORDER BY entry_time";

        List<ServiceOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                try {
                    result.add(mapRowToOrder(rs));
                } catch (Exception e) {
                    throw new RuntimeException("Gagal memetakan finished order.", e);
                }
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil daftar finished orders.", e);
        }
    }
}
