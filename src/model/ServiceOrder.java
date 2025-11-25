package model;

import java.time.LocalDateTime;
import strategy.service.ServiceStrategy;

/**
 * Representasi Order Layanan dalam Pet Care System.
 * Menerapkan validasi sesuai Business Rules.
 */
public class ServiceOrder {

    private final String orderId;
    private final Pet pet;
    private final Customer customer;
    private final ServiceStrategy service;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;

    private String status = "Menunggu";   // default
    private double totalCost;

    /**
     * Konstruktor dengan validasi data sesuai BR.
     */
    public ServiceOrder(String orderId, Pet pet, Customer customer,
                        ServiceStrategy service,
                        LocalDateTime entryTime, LocalDateTime exitTime) {

        // -----------------------------
        // VALIDASI WAJIB (EXCEPTION HANDLING)
        // -----------------------------
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID tidak boleh kosong.");
        }
        if (pet == null) {
            throw new IllegalArgumentException("Pet pada ServiceOrder tidak boleh null.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer dalam ServiceOrder tidak boleh null.");
        }
        if (service == null) {
            throw new IllegalArgumentException("Service (layanan) tidak boleh null.");
        }
        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time tidak boleh null.");
        }
        if (exitTime == null) {
            throw new IllegalArgumentException("Exit time tidak boleh null.");
        }

        // Masalah pada BoardingService (lama titip tidak valid)
        // Checking global — Manager juga akan cek lagi
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time tidak boleh sebelum entry time.");
        }

        // -----------------------------
        // ASSIGN FIELDS
        // -----------------------------
        this.orderId = orderId;
        this.pet = pet;
        this.customer = customer;
        this.service = service;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    // ===================================================================
    // STATUS MANAGEMENT — HARUS MENURUTI FLOW:
    // Menunggu → Sedang dikerjakan → Selesai → Sudah diambil
    // ===================================================================
    public void updateStatus(String newStatus) {
        if (!isValidStatusTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                "Perubahan status tidak valid: " + this.status + " → " + newStatus
            );
        }
        this.status = newStatus;
    }

    private boolean isValidStatusTransition(String current, String next) {
        return switch (current) {
            case "Menunggu" -> next.equals("Sedang dikerjakan");
            case "Sedang dikerjakan" -> next.equals("Selesai");
            case "Selesai" -> next.equals("Sudah diambil");
            default -> false;
        };
    }

    // ===================================================================
    // PERHITUNGAN BIAYA (via STRATEGY PATTERN)
    // ===================================================================
    public double calculateTotal() {
        try {
            this.totalCost = service.calculatePrice(this);
        } catch (Exception e) {
            throw new IllegalStateException("Gagal menghitung biaya layanan: " + e.getMessage());
        }
        return this.totalCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    // Setter untuk load dari DB (boleh tanpa validasi)
    public void setTotalCost(double totalCost) {
        if (totalCost < 0) {
            throw new IllegalArgumentException("Total cost tidak boleh negatif.");
        }
        this.totalCost = totalCost;
    }

    // Setter khusus load DB: boleh bypass rule
    public void setStatusFromDb(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status dari DB tidak boleh kosong/null.");
        }
        this.status = status;
    }

    // ===================================================================
    // GETTERS
    // ===================================================================
    public String getOrderId() { return orderId; }
    public Pet getPet() { return pet; }
    public Customer getCustomer() { return customer; }
    public String getStatus() { return status; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public ServiceStrategy getService() { return service; }

}
