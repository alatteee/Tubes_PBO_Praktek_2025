import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import manager.CustomerManager;
import manager.PetManager;
import model.Customer;
import model.Pet;

public class PetInsertTest {

    @Test
    void testRegisterPet_success() {
        CustomerManager cm = new CustomerManager();
        PetManager pm = new PetManager();

        String ownerName = "Owner Pet " + System.currentTimeMillis();
        String phone     = "08" + System.currentTimeMillis();
        String petName   = "Mimi-" + System.currentTimeMillis();

        Customer owner = cm.registerCustomer(ownerName, phone);
        assertNotNull(owner);
        assertNotNull(owner.getCustomerId());

        Pet pet = pm.registerPet("Cat", petName, 2, owner);

        assertNotNull(pet, "Pet tidak boleh null");
        assertNotNull(pet.getPetId(), "ID pet harus terisi");
        assertEquals(owner.getCustomerId(), pet.getOwnerId(), "Owner ID harus sesuai");
    }
}
