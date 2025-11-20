package strategy.service;

import model.ServiceOrder;

public class MedicalService implements ServiceStrategy {

    private static final double BASE_PRICE = 70000;
    private double medicineCost = 0;

    public MedicalService() {}

    public MedicalService(double medicineCost) {
        this.medicineCost = medicineCost;
    }

    @Override
    public String getName() {
        return "Medical Treatment";
    }

    @Override
    public double calculatePrice(ServiceOrder order) {
        return BASE_PRICE + medicineCost;
    }

    @Override
    public void processService(ServiceOrder order) {
        System.out.println("Pemeriksaan medis sedang dilakukan...");
    }
}
