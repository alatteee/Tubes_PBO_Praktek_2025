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
            throw new IllegalArgumentException("Pet tidak boleh null");
        }

        List<ServiceOrder> orders = orderDAO.findByPet(pet.getPetId());
        for (ServiceOrder order : orders) {
            String st = order.getStatus();
            if ("Menunggu".equalsIgnoreCase(st) || "Sedang dikerjakan".equalsIgnoreCase(st)) {
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

        if (pet == null) throw new IllegalArgumentException("Pet tidak boleh null");
        if (customer == null) throw new IllegalArgumentException("Customer tidak boleh null");
        if (service == null) throw new IllegalArgumentException("Service tidak boleh null");

        if (entryTime == null) {
            entryTime = LocalDateTime.now();
        }

        if (isPetActive(pet)) {
            throw new IllegalStateException("Pet masih memiliki layanan aktif.");
        }

        String orderId = "ORD-" + UUID.randomUUID();

        ServiceOrder order = new ServiceOrder(orderId, pet, customer, service, entryTime, exitTime);
        order.calculateTotal(); // hitung total pakai strategy

        // status awal "Menunggu" sudah default
        orderDAO.save(order);
        return order;
    }

    public void updateStatus(String orderId, String newStatus) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        orderDAO.updateStatus(orderId, newStatus);
    }

    public ServiceOrder getOrderById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        return orderDAO.findById(id);
    }

    public List<ServiceOrder> getActiveOrders() {
        return orderDAO.findActiveOrders();
    }

    public List<ServiceOrder> getFinishedOrders() {
        return orderDAO.findFinishedOrders();
    }
}
