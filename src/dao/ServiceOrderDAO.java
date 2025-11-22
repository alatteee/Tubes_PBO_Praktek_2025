package dao;

import model.ServiceOrder;

import java.util.List;

public interface ServiceOrderDAO {

    ServiceOrder save(ServiceOrder order);

    ServiceOrder findById(String id);

    // Order dengan status aktif (Menunggu / Sedang dikerjakan)
    List<ServiceOrder> findActiveOrders();

    // Semua order milik pet tertentu
    List<ServiceOrder> findByPet(String petId);

    // Update status saja
    boolean updateStatus(String orderId, String newStatus);

    // Order yang sudah "Selesai" (dipakai di panel checkout)
    List<ServiceOrder> findFinishedOrders();
}
