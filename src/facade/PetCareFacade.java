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

import strategy.service.ServiceStrategy;
import strategy.service.GroomingService;
import strategy.service.BoardingService;
import strategy.service.MedicalService;

import strategy.payment.PaymentStrategy;

/**
 * Facade untuk menyederhanakan akses dari GUI ke layer bisnis.
 * GUI hanya berurusan dengan PetCareFacade, tidak langsung ke Manager/DAO.
 */
public class PetCareFacade {

    private final CustomerManager customerManager;
    private final PetManager petManager;
    private final OrderManager orderManager;

    public PetCareFacade() {
        this.customerManager = new CustomerManager();
        this.petManager = new PetManager();
        this.orderManager = new OrderManager();
    }

    // ===================== CUSTOMER =====================

    public Customer registerCustomer(String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama customer tidak boleh kosong");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong");
        }
        return customerManager.registerCustomer(name, phone);
    }

    public Customer getCustomerById(String id) {
        return customerManager.getCustomerById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerManager.getAllCustomers();
    }

    // ===================== PET =====================

    public Pet registerPet(String ownerId, String type, String name, int age) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID tidak boleh kosong");
        }

        Customer owner = customerManager.getCustomerById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Customer dengan ID " + ownerId + " tidak ditemukan");
        }

        return petManager.registerPet(type, name, age, owner);
    }

    public Pet getPetById(String petId) {
        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong");
        }
        return petManager.getPetById(petId);
    }

    public List<Pet> getPetsByCustomer(String ownerId) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID tidak boleh kosong");
        }

        Customer owner = customerManager.getCustomerById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Customer dengan ID " + ownerId + " tidak ditemukan");
        }

        return petManager.getPetsByCustomer(owner);
    }

    // ===================== SERVICE ORDER =====================

    public ServiceOrder createServiceOrder(String petId,
                                           String serviceType,
                                           LocalDateTime entry,
                                           LocalDateTime exit) {

        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong");
        }
        if (serviceType == null || serviceType.isBlank()) {
            throw new IllegalArgumentException("Service type tidak boleh kosong");
        }
        if (entry == null) {
            entry = LocalDateTime.now();
        }

        Pet pet = petManager.getPetById(petId);
        if (pet == null) {
            throw new IllegalArgumentException("Pet dengan ID " + petId + " tidak ditemukan");
        }

        Customer owner = customerManager.getCustomerById(pet.getOwnerId());
        if (owner == null) {
            throw new IllegalStateException("Owner untuk pet ini tidak ditemukan di database.");
        }

        ServiceStrategy serviceStrategy = createServiceStrategy(serviceType);

        return orderManager.createOrder(pet, owner, serviceStrategy, entry, exit);
    }

    public void startService(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        orderManager.updateStatus(orderId, "Sedang dikerjakan");
    }

    public void finishService(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        orderManager.updateStatus(orderId, "Selesai");
    }

    public List<ServiceOrder> getActiveOrders() {
        return orderManager.getActiveOrders();
    }

    public List<ServiceOrder> getFinishedOrders() {
        return orderManager.getFinishedOrders();
    }

    // ===================== CHECKOUT (Phase 2, tanpa ReceiptDAO) =====================

    public Receipt checkout(String orderId, PaymentStrategy paymentStrategy) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        if (paymentStrategy == null) {
            throw new IllegalArgumentException("Payment strategy tidak boleh null");
        }

        ServiceOrder order = orderManager.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order dengan ID " + orderId + " tidak ditemukan");
        }

        String currentStatus = order.getStatus();
        if (!"Selesai".equalsIgnoreCase(currentStatus)) {
            throw new IllegalStateException(
                    "Order dengan ID " + orderId + " belum selesai. Status saat ini: " + currentStatus);
        }

        double amount = order.getTotalCost();
        boolean paid = paymentStrategy.pay(amount);
        if (!paid) {
            throw new IllegalStateException("Pembayaran gagal diproses");
        }

        // Update status order → "Sudah diambil"
        orderManager.updateStatus(orderId, "Sudah diambil");

        // Update status pet → "Tidak dalam layanan"
        Pet pet = order.getPet();
        if (pet != null) {
            pet.setStatus("Tidak dalam layanan");
            petManager.update(pet);
        }

        // Generate receipt (belum disimpan ke DB, itu Phase 3)
        String txId = generateTransactionId(orderId);
        LocalDateTime now = LocalDateTime.now();

        Receipt receipt = new Receipt(txId, now, order, paymentStrategy, null);

        // Simpan "PDF" ke file
        receipt.saveToPDF();

        return receipt;
    }

    // ===================== Helper =====================

    private ServiceStrategy createServiceStrategy(String serviceType) {
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type tidak boleh null");
        }

        String type = serviceType.trim().toLowerCase();
        switch (type) {
            case "grooming":
                return new GroomingService();
            case "boarding":
                return new BoardingService();
            case "medical":
                return new MedicalService();
            default:
                throw new IllegalArgumentException("Service type tidak dikenal: " + serviceType);
        }
    }

    private String generateTransactionId(String orderId) {
        return "TRX-" + orderId + "-" + System.currentTimeMillis();
    }

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
