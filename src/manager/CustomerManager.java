package manager;

import java.util.List;

import dao.CustomerDAO;
import model.Customer;

/**
 * Kelas yang menangani business logic untuk entitas Customer.
 * - Validasi data customer (BR-05)
 * - Generate ID customer
 * - Interaksi data melalui CustomerDAO (CRUD).
 */
public class CustomerManager {

    private final CustomerDAO customerDAO;

    /**
     * Konstruktor default.
     * Dipakai oleh PetCareFacade (tanpa dependency injection eksplisit).
     */
    public CustomerManager() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Mendaftarkan pelanggan baru setelah memvalidasi datanya (BR-05).
     *
     * @param name  Nama pelanggan.
     * @param phone Nomor telepon pelanggan.
     * @return Objek Customer yang telah disimpan.
     * @throws IllegalArgumentException jika data tidak valid.
     */
    public Customer registerCustomer(String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama pelanggan tidak boleh kosong.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon tidak boleh kosong.");
        }

        // Generate ID sederhana (boleh diganti IDGenerator kalau nanti ada)
        String id = "CUST-" + System.currentTimeMillis();

        // Buat objek Customer dan validasi format telepon melalui isValid()
        Customer newCustomer = new Customer(id, name, phone);

        if (!newCustomer.isValid()) {
            throw new IllegalArgumentException(
                    "Data pelanggan tidak valid. Nama tidak boleh kosong dan format nomor telepon salah.");
        }

        // Simpan ke DB via DAO
        return customerDAO.save(newCustomer);
    }

    /**
     * Mengambil data pelanggan berdasarkan ID.
     *
     * @param id ID customer.
     * @return Customer jika ditemukan, atau null jika tidak.
     * @throws IllegalArgumentException jika ID kosong.
     */
    public Customer getCustomerById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID tidak boleh kosong.");
        }
        return customerDAO.findById(id);
    }

    /**
     * Mengambil semua daftar pelanggan.
     * (Generic Programming: List<Customer>)
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
}
