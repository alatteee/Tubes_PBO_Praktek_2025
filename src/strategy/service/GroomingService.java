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
        return PRICE;
    }

    @Override
    public void processService(ServiceOrder order) {
        System.out.println("Proses grooming untuk " + order.getPet().getName() + "...");
    }
}
