import manager.CustomerManager;
import model.Customer;

public class TestCustomerInsert {
    public static void main(String[] args) {
        try {
            CustomerManager cm = new CustomerManager();
            Customer c = cm.registerCustomer("Tes Supabase", "081234567890");
            System.out.println("Customer tersimpan dengan ID: " + c.getCustomerId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
