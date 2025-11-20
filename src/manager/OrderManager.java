package manager;

import model.Pet;
import model.ServiceOrder;
import strategy.service.ServiceStrategy;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

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

        ServiceOrder order = new ServiceOrder(id, pet, pet.getOwner(),
                service, entry, exit);

        orders.add(order);
        return order;
    }

    public void updateStatus(String orderId, String newStatus) {
        ServiceOrder order = findById(orderId);
        if (order == null) throw new IllegalArgumentException("Order tidak ditemukan");

        order.updateStatus(newStatus);
    }

    public boolean isPetActive(Pet pet) {
        return orders.stream()
                .anyMatch(o -> o.getPet().equals(pet)
                        && !o.getStatus().equals("Sudah diambil"));
    }

    public List<ServiceOrder> getActiveOrders() {
        return orders.stream()
                .filter(o -> !o.getStatus().equals("Sudah diambil"))
                .toList();
    }

    private ServiceOrder findById(String id) {
        return orders.stream()
                .filter(o -> o.getOrderId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
