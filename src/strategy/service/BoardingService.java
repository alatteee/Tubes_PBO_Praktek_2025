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
        if (order.getExitTime().isBefore(order.getEntryTime())) {
            throw new IllegalArgumentException("Exit time tidak boleh sebelum entry time");
        }

        long days = Duration.between(order.getEntryTime(), order.getExitTime()).toDays();
        if (days <= 0) days = 1;  // minimal 1 hari

        return days * PRICE_PER_DAY;
    }

    @Override
    public void processService(ServiceOrder order) {
        System.out.println("Hewan sedang boarding...");
    }
}
