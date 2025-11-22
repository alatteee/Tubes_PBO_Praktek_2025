package model;

import java.time.LocalDateTime;
import strategy.service.ServiceStrategy;

public class ServiceOrder {
    private String orderId;
    private Pet pet;
    private Customer customer;
    private ServiceStrategy service;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String status = "Menunggu";
    private double totalCost;

    public ServiceOrder(String orderId, Pet pet, Customer customer,
                        ServiceStrategy service,
                        LocalDateTime entryTime, LocalDateTime exitTime) {
        this.orderId = orderId;
        this.pet = pet;
        this.customer = customer;
        this.service = service;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    // Status: Menunggu -> Sedang dikerjakan -> Selesai -> Sudah diambil
    public void updateStatus(String newStatus) {
        if (!isValidStatusTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                "Status tidak valid dari " + this.status + " ke " + newStatus
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

    public double calculateTotal() {
        this.totalCost = service.calculatePrice(this);
        return this.totalCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    // dipakai DAO ketika load dari DB
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    // setter khusus untuk load dari DB (tanpa validasi transition)
    public void setStatusFromDb(String status) {
        this.status = status;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public Pet getPet() { return pet; }
    public Customer getCustomer() { return customer; }
    public String getStatus() { return status; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public ServiceStrategy getService() { return service; }
}
