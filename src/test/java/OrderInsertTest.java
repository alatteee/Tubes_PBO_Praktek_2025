import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import manager.CustomerManager;
import manager.PetManager;
import manager.OrderManager;
import model.Customer;
import model.Pet;
import model.ServiceOrder;
import strategy.service.GroomingService;
import strategy.service.ServiceStrategy;

public class OrderInsertTest {

    @Test
    void testCreateOrder_success() {
        CustomerManager cm = new CustomerManager();
        PetManager pm = new PetManager();
        OrderManager om = new OrderManager();

        String customerName = "Order Customer " + System.currentTimeMillis();
        String phone        = "08" + System.currentTimeMillis();
        String petName      = "Kuro-" + System.currentTimeMillis();

        Customer customer = cm.registerCustomer(customerName, phone);
        assertNotNull(customer);
        assertNotNull(customer.getCustomerId());

        Pet pet = pm.registerPet("Cat", petName, 3, customer);
        assertNotNull(pet);
        assertNotNull(pet.getPetId());

        ServiceStrategy service = new GroomingService();

        ServiceOrder order = om.createOrder(
                pet,
                customer,
                service,
                LocalDateTime.now(),
                null
        );

        assertNotNull(order, "Order tidak boleh null");
        assertNotNull(order.getOrderId(), "ID order harus terisi");
    }
}
