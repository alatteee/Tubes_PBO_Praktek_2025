import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import manager.CustomerManager;
import model.Customer;

public class CustomerInsertTest {

    @Test
    void testRegisterNewCustomer_success() {
        CustomerManager cm = new CustomerManager();

        // Data unik supaya tidak tabrakan dengan data lama di DB
        long suffix = System.currentTimeMillis();
        String name  = "JUnit User " + suffix;
        String phone = "08" + suffix;

        Customer c = assertDoesNotThrow(
                () -> cm.registerCustomer(name, phone),
                "Seharusnya pendaftaran customer baru tidak melempar exception"
        );

        assertNotNull(c, "Customer tidak boleh null");
        assertNotNull(c.getCustomerId(), "ID customer harus terisi");
        assertFalse(c.getCustomerId().isEmpty(), "ID customer tidak boleh kosong");
    }

    @Test
    void testRegisterDuplicateCustomer_throwsException() {
        CustomerManager cm = new CustomerManager();

        long suffix = System.currentTimeMillis();
        String name  = "JUnit Duplicate " + suffix;
        String phone = "08" + suffix;

        Customer first = cm.registerCustomer(name, phone);
        assertNotNull(first);

        assertThrows(
                IllegalArgumentException.class,
                () -> cm.registerCustomer(name, phone),
                "Seharusnya pendaftaran customer duplikat melempar IllegalArgumentException"
        );
    }
}
