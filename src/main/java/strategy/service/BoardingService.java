package strategy.service;

import model.ServiceOrder;
import java.time.Duration;

public class BoardingService implements ServiceStrategy {

    private static final double PRICE_PER_DAY = 40000;

    @Override
    public String getName() {
        return "Boarding";
    }

    @Override
    public double calculatePrice(ServiceOrder order) {

        // ==== EXCEPTION HANDLING DITAMBAHKAN ====
        if (order == null) {
            throw new IllegalArgumentException("Order tidak boleh null untuk menghitung harga boarding.");
        }
        if (order.getEntryTime() == null || order.getExitTime() == null) {
            throw new IllegalArgumentException("Entry/Exit time tidak boleh null pada boarding service.");
        }
        if (PRICE_PER_DAY <= 0) {
            throw new IllegalStateException("Harga per hari boarding tidak valid (0 atau negatif).");
        }

        // === VALIDASI BUSINESS RULE YANG SUDAH ADA ===
        if (order.getExitTime().isBefore(order.getEntryTime())) {
            throw new IllegalArgumentException("Exit time tidak boleh sebelum entry time.");
        }

        long days = Duration.between(order.getEntryTime(), order.getExitTime()).toDays();
        if (days <= 0) days = 1;  // minimal 1 hari

        return days * PRICE_PER_DAY;
    }

    @Override
    public void processService(ServiceOrder order) {

        // ==== EXCEPTION HANDLING DITAMBAHKAN ====
        if (order == null) {
            throw new IllegalArgumentException("Order tidak boleh null saat memproses boarding.");
        }
        if (order.getPet() == null) {
            throw new IllegalStateException("Pet tidak boleh null ketika boarding.");
        }

        System.out.println("Hewan sedang boarding...");
    }
}
