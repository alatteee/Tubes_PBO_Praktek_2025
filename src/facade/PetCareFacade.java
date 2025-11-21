// package facade;

import java.time.LocalDateTime;
import java.util.List;

import manager.CustomerManager;
import manager.PetManager;
import manager.OrderManager;
import dao.ReceiptDAO;

import model.Customer;
import model.Pet;
import model.ServiceOrder;
import model.Receipt;

import strategy.service.ServiceStrategy;
import strategy.service.GroomingService;
import strategy.service.BoardingService;
import strategy.service.MedicalService;

import strategy.payment.PaymentStrategy;

public class PetCareFacade {

    private final CustomerManager customerManager;
    private final PetManager petManager;
    private final OrderManager orderManager;
    private final ReceiptDAO receiptDAO;

    public PetCareFacade() {
        // ASUMSI: Manager dan DAO punya konstruktor default.
        // Kalau di projectmu beda (misal butuh DAO di constructor),
        // silakan sesuaikan bagian ini.
        this.customerManager = new CustomerManager();
        this.petManager = new PetManager();
        this.orderManager = new OrderManager();
        this.receiptDAO = new ReceiptDAO();
    }

    /**
     * Register customer baru.
     */
    public Customer registerCustomer(String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama customer tidak boleh kosong");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong");
        }

        // ASUMSI: CustomerManager punya method registerCustomer(name, phone)
        return customerManager.registerCustomer(name, phone);
    }

    /**
     * Register pet baru untuk owner tertentu.
     */
    public Pet registerPet(String ownerId, String type, String name, int age) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID tidak boleh kosong");
        }

        // ASUMSI: CustomerManager punya getCustomerById(id)
        Customer owner = customerManager.getCustomerById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Customer dengan ID " + ownerId + " tidak ditemukan");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama hewan tidak boleh kosong");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Umur hewan tidak boleh negatif");
        }

        // ASUMSI: PetManager.registerPet(type, name, age, owner) sudah ada
        return petManager.registerPet(type, name, age, owner);
    }

    /**
     * Membuat ServiceOrder baru untuk pet tertentu dengan jenis layanan tertentu.
     */
    public ServiceOrder createServiceOrder(String petId,
                                           String serviceType,
                                           LocalDateTime entry,
                                           LocalDateTime exit) {
        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong");
        }
        if (entry == null) {
            throw new IllegalArgumentException("Entry time tidak boleh null");
        }

        // ASUMSI: PetManager.getPetById(id) ada
        Pet pet = petManager.getPetById(petId);
        if (pet == null) {
            throw new IllegalArgumentException("Pet dengan ID " + petId + " tidak ditemukan");
        }

        Customer owner = pet.getOwner(); // ASUMSI: Pet punya getOwner()
        if (owner == null) {
            throw new IllegalStateException("Pet belum memiliki owner");
        }

        ServiceStrategy serviceStrategy = createServiceStrategy(serviceType);

        // ASUMSI: OrderManager.createOrder(pet, service, entry, exit) ada.
        // Kalau di kawanmu parameternya termasuk Customer, sesuaikan:
        // createOrder(pet, owner, serviceStrategy, entry, exit)
        return orderManager.createOrder(pet, serviceStrategy, entry, exit);
    }

    /**
     * Mengubah status order menjadi "Dalam Proses".
     */
    public void startService(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }

        // ASUMSI: OrderManager.updateStatus(orderId, status) ada
        orderManager.updateStatus(orderId, "Dalam Proses");
    }

    /**
     * Mengubah status order menjadi "Selesai".
     */
    public void finishService(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }

        orderManager.updateStatus(orderId, "Selesai");
    }

    /**
     * Mengembalikan list order yang masih aktif (belum diambil).
     */
    public List<ServiceOrder> getActiveOrders() {
        // ASUMSI: OrderManager.getActiveOrders() ada
        return orderManager.getActiveOrders();
    }

    /**
     * Checkout: proses pembayaran + update status order & pet + generate Receipt.
     */
    public Receipt checkout(String orderId, PaymentStrategy paymentStrategy) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        if (paymentStrategy == null) {
            throw new IllegalArgumentException("Payment strategy tidak boleh null");
        }

        // ASUMSI: OrderManager.getOrderById(id) ada
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
            // ASUMSI: PetManager punya method update(Pet) atau sejenisnya
            // Sesuaikan dengan method yang benar di proyekmu:
            petManager.update(pet); // TODO: kalau di projectmu beda, ganti nama method ini
        }

        // Buat Receipt
        String trxId = generateTransactionId(orderId);
        LocalDateTime trxTime = LocalDateTime.now();

        Receipt receipt = new Receipt(trxId, trxTime, order, paymentStrategy);

        // Simpan ke DB (opsional tapi disarankan)
        // ASUMSI: ReceiptDAO.save(Receipt) ada
        receiptDAO.save(receipt);

        // Simpan PDF/text receipt ke file sistem (opsional)
        try {
            receipt.saveToPDF("receipts");
        } catch (Exception e) {
            // Di sini jangan terlalu agresif: kalau gagal simpan file,
            // tetap return receipt tapi log error.
            e.printStackTrace();
        }

        return receipt;
    }

    /**
     * Helper untuk mapping nama layanan dari GUI ke objek ServiceStrategy.
     */
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

    /**
     * Generate ID transaksi sederhana.
     * Kalau kamu punya util/IDGenerator, boleh ganti pakai itu.
     */
    private String generateTransactionId(String orderId) {
        return "TRX-" + orderId + "-" + System.currentTimeMillis();
    }

    // Getter untuk Manager kalau sewaktu-waktu diperlukan GUI / test
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
