package manager;

import java.util.List;

import dao.CustomerDAO;
import model.Customer;

public class CustomerManager {

    private final CustomerDAO customerDAO;

    public CustomerManager() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Register customer baru (buat ID + validasi basic).
     */
    public Customer registerCustomer(String name, String phone) {
        // Validasi sederhana, validasi lebih detail boleh di Customer.isValid()
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong");
        }

        // Generate ID sederhana (kalau kalian punya IDGenerator sendiri, pakai itu)
        String id = "CUST-" + System.currentTimeMillis();

        Customer customer = new Customer(id, name, phone);

        if (!customer.isValid()) {
            throw new IllegalArgumentException("Data customer tidak valid");
        }

        // Simpan ke DB lewat DAO
        return customerDAO.save(customer);
    }

    /**
     * Ambil customer berdasarkan ID.
     */
    public Customer getCustomerById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Customer ID tidak boleh kosong");
        }
        return customerDAO.findById(id);
    }

    /**
     * Ambil semua customer untuk ditampilkan di tabel.
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
}
