package strategy.service;

import model.ServiceOrder;

public class GroomingService implements ServiceStrategy {

    private static final double PRICE = 50000;

    @Override
    public String getName() {
        return "Grooming";
    }

    @Override
    public double calculatePrice(ServiceOrder order) {

        // === EXCEPTION HANDLING DITAMBAHKAN ===
        if (order == null) {
            throw new IllegalArgumentException("Order tidak boleh null untuk menghitung harga grooming.");
        }

        return PRICE;
    }

    @Override
    public void processService(ServiceOrder order) {

        // === EXCEPTION HANDLING DITAMBAHKAN ===
        if (order == null) {
            throw new IllegalArgumentException("Order tidak boleh null saat memproses grooming.");
        }
        if (order.getPet() == null) {
            throw new IllegalStateException("Pet tidak boleh null saat memproses grooming.");
        }
        if (order.getPet().getName() == null) {
            throw new IllegalStateException("Nama pet tidak boleh null saat proses grooming.");
        }

        System.out.println("Proses grooming untuk " + order.getPet().getName() + "...");
    }
}
