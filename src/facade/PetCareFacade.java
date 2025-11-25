package facade;

import java.time.LocalDateTime;
import java.util.List;

import dao.JdbcReceiptDAO;
import dao.ReceiptDAO;
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
    private final ReceiptDAO receiptDAO;

    public PetCareFacade() {
        this.customerManager = new CustomerManager();
        this.petManager = new PetManager();
        this.orderManager = new OrderManager();
        this.receiptDAO = new JdbcReceiptDAO();
    }

    // ===================== CUSTOMER =====================

    public Customer registerCustomer(String name, String phone) throws Exception {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama customer tidak boleh kosong");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong");
        }
        
        try {
            return customerManager.registerCustomer(name, phone);
        } catch (IllegalArgumentException e) {
            // Menangkap Business Rule Validation (contoh: Duplikat Customer)
            throw new Exception("Gagal mendaftar customer: " + e.getMessage());
        } catch (RuntimeException e) {
            // Menangkap kegagalan Database/Infrastructure
            throw new Exception("Gagal mendaftar customer. Terjadi masalah database.");
        }
    }

    public Customer getCustomerById(String id) throws Exception {
        try {
            return customerManager.getCustomerById(id);
        } catch (IllegalArgumentException e) {
            // Menangkap ID tidak ditemukan
            throw new Exception("Gagal mencari customer: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal mencari customer. Terjadi masalah database.");
        }
    }

    public List<Customer> getAllCustomers() throws Exception {
        try {
            return customerManager.getAllCustomers();
        } catch (RuntimeException e) {
            throw new Exception("Gagal mengambil daftar customer. Terjadi masalah database.");
        }
    }

    // ===================== PET =====================

    public Pet registerPet(String ownerId, String type, String name, int age) throws Exception {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID tidak boleh kosong");
        }

        try {
            Customer owner = customerManager.getCustomerById(ownerId);
            if (owner == null) {
                throw new IllegalArgumentException("Customer dengan ID " + ownerId + " tidak ditemukan");
            }
    
            return petManager.registerPet(type, name, age, owner);
        } catch (IllegalArgumentException e) {
            // Menangkap ID owner tidak ditemukan atau error validasi pet
            throw new Exception("Gagal mendaftar pet: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal mendaftar pet. Terjadi masalah database.");
        }
    }

    public Pet getPetById(String petId) throws Exception {
        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong");
        }

        try {
            return petManager.getPetById(petId);
        } catch (IllegalArgumentException e) {
            // Menangkap ID pet tidak ditemukan
            throw new Exception("Gagal mencari pet: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal mencari pet. Terjadi masalah database.");
        }
    }

    public List<Pet> getPetsByCustomer(String ownerId) throws Exception {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID tidak boleh kosong");
        }

        try {
            Customer owner = customerManager.getCustomerById(ownerId);
            if (owner == null) {
                throw new IllegalArgumentException("Customer dengan ID " + ownerId + " tidak ditemukan");
            }
    
            return petManager.getPetsByCustomer(owner);
        } catch (IllegalArgumentException e) {
            throw new Exception("Gagal mengambil daftar pet: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal mengambil daftar pet. Terjadi masalah database.");
        }
    }

    // ===================== SERVICE ORDER =====================

    public ServiceOrder createServiceOrder(String petId,
                                           String serviceType,
                                           LocalDateTime entry,
                                           LocalDateTime exit) throws Exception {

        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong");
        }
        if (serviceType == null || serviceType.isBlank()) {
            throw new IllegalArgumentException("Service type tidak boleh kosong");
        }
        if (entry == null) {
            entry = LocalDateTime.now();
        }

        try {
            Pet pet = petManager.getPetById(petId);
            if (pet == null) {
                throw new IllegalArgumentException("Pet dengan ID " + petId + " tidak ditemukan");
            }
    
            Customer owner = customerManager.getCustomerById(pet.getOwnerId());
            if (owner == null) {
                // IllegalStateException karena data DB tidak konsisten
                throw new IllegalStateException("Owner untuk pet ini tidak ditemukan di database.");
            }
    
            ServiceStrategy serviceStrategy = createServiceStrategy(serviceType);
    
            // Buat order baru
            ServiceOrder order = orderManager.createOrder(pet, owner, serviceStrategy, entry, exit);
    
            // Tandai pet sedang dalam proses layanan (status awal)
            pet.setStatus("Menunggu layanan");
            petManager.update(pet);
    
            return order;
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Menangkap Business Rule dari Manager/Facade
            throw new Exception("Gagal membuat order: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal membuat order. Terjadi masalah database.");
        }
    }

    public void startService(String orderId) throws Exception {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }

        try {
            // Update status order
            orderManager.updateStatus(orderId, "Sedang dikerjakan");
    
            // Update status pet juga
            ServiceOrder order = orderManager.getOrderById(orderId);
            if (order != null && order.getPet() != null) {
                Pet pet = order.getPet();
                pet.setStatus("Sedang dikerjakan");
                petManager.update(pet);
            }
        } catch (IllegalArgumentException e) {
            // Menangkap Order ID tidak ditemukan
            throw new Exception("Gagal memulai layanan: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal memulai layanan. Terjadi masalah database.");
        }
    }

    public void finishService(String orderId) throws Exception {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }

        try {
            // Update status order
            orderManager.updateStatus(orderId, "Selesai");
    
            // Pet sudah selesai layanan, tinggal menunggu checkout
            ServiceOrder order = orderManager.getOrderById(orderId);
            if (order != null && order.getPet() != null) {
                Pet pet = order.getPet();
                pet.setStatus("Menunggu checkout");
                petManager.update(pet);
            }
        } catch (IllegalArgumentException e) {
            // Menangkap Order ID tidak ditemukan
            throw new Exception("Gagal menyelesaikan layanan: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal menyelesaikan layanan. Terjadi masalah database.");
        }
    }

    public List<ServiceOrder> getActiveOrders() throws Exception {
        try {
            return orderManager.getActiveOrders();
        } catch (RuntimeException e) {
            throw new Exception("Gagal mengambil daftar order aktif. Terjadi masalah database.");
        }
    }

    public List<ServiceOrder> getFinishedOrders() throws Exception {
        try {
            return orderManager.getFinishedOrders();
        } catch (RuntimeException e) {
            throw new Exception("Gagal mengambil daftar order selesai. Terjadi masalah database.");
        }
    }

    // ===================== CHECKOUT (Phase 3, dengan ReceiptDAO) =====================

    public Receipt checkout(String orderId, PaymentStrategy paymentStrategy) throws Exception {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong");
        }
        if (paymentStrategy == null) {
            throw new IllegalArgumentException("Payment strategy tidak boleh null");
        }

        try {
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
    
            // Generate receipt
            String txId = generateTransactionId(orderId);
            LocalDateTime now = LocalDateTime.now();
    
            Receipt receipt = new Receipt(txId, now, order, paymentStrategy, null);
    
            // Simpan file PDF
            receipt.saveToPDF();
    
            // Simpan metadata ke database (Supabase)
            receiptDAO.save(receipt);
    
            return receipt;

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Menangkap Order ID tidak ditemukan / Status Order salah / Pembayaran gagal
            throw new Exception("Gagal melakukan checkout: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new Exception("Gagal melakukan checkout. Terjadi masalah database.");
        }
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
        // Contoh: TRX-1768956123456
        return "TRX-" + System.currentTimeMillis();
    }

    // ===================== Getter untuk GUI / Testing =====================

    public CustomerManager getCustomerManager() {
        return customerManager;
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public ReceiptDAO getReceiptDAO() {
        return receiptDAO;
    }
}