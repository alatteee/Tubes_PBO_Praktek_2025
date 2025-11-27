package strategy.service;

import model.ServiceOrder;

public class MedicalService implements ServiceStrategy {

    private static final double BASE_PRICE = 70000;
    private double medicineCost = 0;

    public MedicalService() {}

    public MedicalService(double medicineCost) {

        // === EXCEPTION HANDLING TAMBAHAN ===
        if (medicineCost < 0) {
            throw new IllegalArgumentException("Biaya obat tidak boleh negatif.");
        }

        this.medicineCost = medicineCost;
    }

    @Override
    public String getName() {
        return "Medical Treatment";
    }

    @Override
    public double calculatePrice(ServiceOrder order) {

        // === EXCEPTION HANDLING TAMBAHAN ===
        if (order == null) {
            throw new IllegalArgumentException("Order tidak boleh null untuk menghitung harga medical service.");
        }
        if (BASE_PRICE <= 0) {
            throw new IllegalStateException("Base price medical service tidak valid (0 atau negatif).");
        }
        if (medicineCost < 0) {
            throw new IllegalStateException("Biaya obat tidak valid (negatif).");
        }

        return BASE_PRICE + medicineCost;
    }

    @Override
    public void processService(ServiceOrder order) {

        // === EXCEPTION HANDLING TAMBAHAN ===
        if (order == null) {
            throw new IllegalArgumentException("Order tidak boleh null saat memproses layanan medis.");
        }
        if (order.getPet() == null) {
            throw new IllegalStateException("Pet tidak boleh null saat layanan medis dijalankan.");
        }

        System.out.println("Pemeriksaan medis sedang dilakukan...");
    }
}
