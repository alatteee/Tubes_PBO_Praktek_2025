import manager.CustomerManager;
import manager.PetManager;
import model.Customer;
import model.Pet;

public class TestPetInsert {
    public static void main(String[] args) {
        try {
            CustomerManager cm = new CustomerManager();
            Customer owner = cm.registerCustomer("Owner Pet", "0899999999");

            PetManager pm = new PetManager();
            Pet pet = pm.registerPet("Cat", "Mimi", 2, owner);

            System.out.println("Pet tersimpan dengan ID: " + pet.getPetId()
                    + " milik customer: " + owner.getCustomerId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
