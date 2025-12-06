package strategy.service;

import model.ServiceOrder;

public interface ServiceStrategy {
    String getName();
    double calculatePrice(ServiceOrder order);
    void processService(ServiceOrder order); 
}
