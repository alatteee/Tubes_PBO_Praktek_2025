package facade;

import java.time.LocalDateTime;
import java.util.List;

import manager.CustomerManager;
import manager.PetManager;
import manager.OrderManager;
import model.Customer;
import model.Pet;
import model.ServiceOrder;
import model.Receipt;
import strategy.payment.PaymentStrategy;

public class PetCareFacade {

    private CustomerManager customerManager;
    private PetManager petManager;
    private OrderManager orderManager;

    public PetCareFacade() {
        // Nanti bisa diubah kalau pakai dependency injection atau Singleton
        this.customerManager = new CustomerManager();
        this.petManager = new PetManager();
        this.orderManager = new OrderManager();
    }

    public Customer registerCustomer(String name, String phone) {
        // TODO: implementasi nanti (Phase 2)
        // return customerManager.registerCustomer(name, phone);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Pet registerPet(String ownerId, String type, String name, int age) {
        // TODO: ambil Customer, lalu delegasi ke PetManager.registerPet(...)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public ServiceOrder createServiceOrder(String petId,
                                           String serviceType,
                                           LocalDateTime entry,
                                           LocalDateTime exit) {
        // TODO: ambil Pet, pilih ServiceStrategy, lalu delegasi ke OrderManager.createOrder(...)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void startService(String orderId) {
        // TODO: delegasi ke orderManager.updateStatus(orderId, "Dalam Proses");
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void finishService(String orderId) {
        // TODO: delegasi ke orderManager.updateStatus(orderId, "Selesai");
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Receipt checkout(String orderId, PaymentStrategy paymentStrategy) {
        // TODO: ambil order, validasi status, proses pembayaran, buat Receipt, update status, simpan
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<ServiceOrder> getActiveOrders() {
        // TODO: delegasi ke orderManager.getActiveOrders();
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Getter untuk manager (kalau sewaktu-waktu dibutuhkan)
    public CustomerManager getCustomerManager() {
        return customerManager;
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }
}
