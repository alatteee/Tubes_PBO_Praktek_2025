import manager.CustomerManager;
import manager.PetManager;
import manager.OrderManager;
import model.Customer;
import model.Pet;
import model.ServiceOrder;
import strategy.service.GroomingService;
import strategy.service.ServiceStrategy;

import java.time.LocalDateTime;

public class TestOrderInsert {
    public static void main(String[] args) {
        try {
            CustomerManager cm = new CustomerManager();
            Customer customer = cm.registerCustomer("Order Customer", "0811111111");

            PetManager pm = new PetManager();
            Pet pet = pm.registerPet("Cat", "Kuro", 3, customer);

            ServiceStrategy service = new GroomingService();

            OrderManager om = new OrderManager();
            ServiceOrder order = om.createOrder(pet, customer, service,
                    LocalDateTime.now(), null);

            System.out.println("Order tersimpan dengan ID: " + order.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
