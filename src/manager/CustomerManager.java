package manager;

import java.util.List;

import dao.CustomerDAO;
import dao.JdbcCustomerDAO;
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
        this.customerDAO = new JdbcCustomerDAO();
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

        // Generate ID baru, pendek, misal: C-0123
        String id = generateCustomerId();

        Customer newCustomer = new Customer(id, name, phone);

        if (!newCustomer.isValid()) {
            throw new IllegalArgumentException(
                    "Data pelanggan tidak valid. Nama tidak boleh kosong dan format nomor telepon salah.");
        }

        return customerDAO.save(newCustomer);
    }

    /**
     * Generate ID customer pendek: C-0000 s/d C-9999.
     * Loop sampai ketemu ID yang belum dipakai (cek ke database).
     */
    private String generateCustomerId() {
        // Ambil semua customer dari DB
        List<Customer> all = customerDAO.findAll();
    
        int max = 0;
        for (Customer c : all) {
            String id = c.getCustomerId();   // contoh lama: CUST-..., contoh baru: cust01
            if (id == null) continue;
    
            String lower = id.toLowerCase();
            if (lower.startsWith("cust")) {
                String numPart = id.substring(4); // ambil setelah "cust"
                try {
                    int n = Integer.parseInt(numPart);
                    if (n > max) max = n;
                } catch (NumberFormatException ignore) {
                    // abaikan ID lama yang formatnya beda
                }
            }
        }
    
        int next = max + 1;
    
        // 01..99 pakai 2 digit, 100 ke atas tanpa padding
        String numStr = (next < 100)
                ? String.format("%02d", next)
                : String.valueOf(next);
    
        return "cust" + numStr;   // hasil: cust01, cust02, ..., cust100, ...
    }    

    public Customer getCustomerById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID tidak boleh kosong.");
        }
        return customerDAO.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
}
