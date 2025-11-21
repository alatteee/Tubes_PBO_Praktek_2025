package manager;

import model.Pet;
import model.ServiceOrder;
import strategy.service.ServiceStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    private final List<ServiceOrder> orders = new ArrayList<>();

    public ServiceOrder createOrder(Pet pet, ServiceStrategy service,
                                    LocalDateTime entry, LocalDateTime exit) {

        if (isPetActive(pet)) {
            throw new IllegalStateException("Pet masih memiliki layanan aktif");
        }
        if (exit.isBefore(entry)) {
            throw new IllegalArgumentException("Exit time tidak boleh sebelum entry time");
        }

        String id = "ORD-" + System.currentTimeMillis();

        ServiceOrder order = new ServiceOrder(
                id,
                pet,
                pet.getOwner(),   // pastikan Pet punya getOwner()
                service,
                entry,
                exit
        );

        orders.add(order);
        return order;
    }

    public void updateStatus(String orderId, String newStatus) {
        ServiceOrder order = findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order tidak ditemukan");
        }
        order.updateStatus(newStatus);
    }

    public boolean isPetActive(Pet pet) {
        return orders.stream()
                .anyMatch(o -> o.getPet().equals(pet)
                        && !"Sudah diambil".equalsIgnoreCase(o.getStatus()));
    }

    /** Semua order yang masih aktif (belum diambil) */
    public List<ServiceOrder> getActiveOrders() {
        return orders.stream()
                .filter(o -> !"Sudah diambil".equalsIgnoreCase(o.getStatus()))
                .toList();
    }

    /** ✅ Tambahan: order yang sudah SELESAI, dipakai di tab Checkout */
    public List<ServiceOrder> getFinishedOrders() {
        return orders.stream()
                .filter(o -> "Selesai".equalsIgnoreCase(o.getStatus()))
                .toList();
    }

    /** ✅ Tambahan: dipakai PetCareFacade.checkout() */
    public ServiceOrder getOrderById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        return findById(id);
    }

    // Helper private
    private ServiceOrder findById(String id) {
        return orders.stream()
                .filter(o -> o.getOrderId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
