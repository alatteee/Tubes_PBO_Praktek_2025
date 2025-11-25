package manager;

import dao.JdbcServiceOrderDAO;
import dao.ServiceOrderDAO;
import model.Customer;
import model.Pet;
import model.ServiceOrder;
import strategy.service.ServiceStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * OrderManager:
 * - membuat ServiceOrder baru
 * - mengecek apakah Pet masih aktif dalam layanan
 * - mengubah status order (start, finish)
 * - mengambil daftar order aktif / selesai
 */
public class OrderManager {

    private final ServiceOrderDAO orderDAO;

    public OrderManager() {
        this.orderDAO = new JdbcServiceOrderDAO();
    }

    /**
     * Cek apakah Pet punya order aktif (Menunggu / Sedang dikerjakan)
     */
    public boolean isPetActive(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Pet tidak boleh null.");
        }

        List<ServiceOrder> orders;
        try {
            orders = orderDAO.findByPet(pet.getPetId());
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil order pet dari database.", e);
        }

        for (ServiceOrder order : orders) {
            String st = order.getStatus();
            if ("Menunggu".equalsIgnoreCase(st) || 
                "Sedang dikerjakan".equalsIgnoreCase(st)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Membuat order baru.
     */
    public ServiceOrder createOrder(Pet pet,
                                    Customer customer,
                                    ServiceStrategy service,
                                    LocalDateTime entryTime,
                                    LocalDateTime exitTime) {

        // ===== VALIDASI INPUT =====
        if (pet == null) throw new IllegalArgumentException("Pet tidak boleh null.");
        if (customer == null) throw new IllegalArgumentException("Customer tidak boleh null.");
        if (service == null) throw new IllegalArgumentException("Service tidak boleh null.");

        if (entryTime == null) {
            entryTime = LocalDateTime.now();
        }
        if (exitTime == null) {
            throw new IllegalArgumentException("Exit time tidak boleh null.");
        }
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time tidak boleh sebelum entry time.");
        }

        // ===== CEK apakah pet masih aktif =====
        if (isPetActive(pet)) {
            throw new IllegalStateException("Pet masih memiliki layanan aktif.");
        }

        String orderId = "ORD-" + UUID.randomUUID();

        ServiceOrder order;
        try {
            order = new ServiceOrder(orderId, pet, customer, service, entryTime, exitTime);
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat order: " + e.getMessage(), e);
        }

        // ===== HITUNG TOTAL =====
        try {
            order.calculateTotal();
        } catch (Exception e) {
            throw new RuntimeException("Gagal menghitung total biaya: " + e.getMessage(), e);
        }

        // ===== SIMPAN KE DATABASE =====
        try {
            orderDAO.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan order ke database.", e);
        }

        return order;
    }

    public void updateStatus(String orderId, String newStatus) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong.");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Status baru tidak boleh kosong.");
        }

        // DAO update status
        try {
            orderDAO.updateStatus(orderId, newStatus);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengubah status order.", e);
        }
    }

    public ServiceOrder getOrderById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong.");
        }

        ServiceOrder order;
        try {
            order = orderDAO.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil order dari database.", e);
        }

        if (order == null) {
            throw new IllegalArgumentException("Order dengan ID tersebut tidak ditemukan.");
        }

        return order;
    }

    public List<ServiceOrder> getActiveOrders() {
        try {
            return orderDAO.findActiveOrders();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil daftar order aktif.", e);
        }
    }

    public List<ServiceOrder> getFinishedOrders() {
        try {
            return orderDAO.findFinishedOrders();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil daftar order yang selesai.", e);
        }
    }
}
