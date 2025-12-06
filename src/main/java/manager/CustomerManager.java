package manager;

import java.util.List;

import dao.CustomerDAO;
import dao.JdbcCustomerDAO;
import model.Customer;


public class CustomerManager {

    private final CustomerDAO customerDAO;

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

        // ========== VALIDASI INPUT DASAR ==========
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama pelanggan tidak boleh kosong.");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Nomor telepon pelanggan tidak boleh kosong.");
        }

        // ========== GENERATE ID ==========
        String id = generateCustomerId();

        Customer newCustomer = new Customer(id, name, phone);

        // ========== CEK VALIDITAS MODEL (BR-05) ==========
        if (!newCustomer.isValid()) {
            throw new IllegalArgumentException(
                    "Data pelanggan tidak valid. Nama tidak boleh kosong dan format nomor telepon harus benar."
            );
        }

        // ========== CEK DUPLIKAT CUSTOMER (BR-05: kombinasi nama + nomor telepon tidak boleh sama) ==========
        List<Customer> all = customerDAO.findAll();
        for (Customer c : all) {
            if (c.getName().equalsIgnoreCase(name.trim())
                    && c.getPhone().equals(phone.trim())) {
                throw new IllegalArgumentException(
                        "Customer dengan nama dan nomor telepon yang sama sudah terdaftar."
                );
            }
        }

        // ========== SIMPAN KE DATABASE ==========
        try {
            return customerDAO.save(newCustomer);
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan customer ke database: " + e.getMessage(), e);
        }
    }

    // Generate ID customer pendek: CUST01, CUST02, ..., CUST100 ...
    private String generateCustomerId() {

        List<Customer> all;
        try {
            all = customerDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil data customer dari database.", e);
        }

        int max = 0;

        for (Customer c : all) {
            String id = c.getCustomerId();
            if (id == null) continue;

            String lower = id.toLowerCase();
            if (lower.startsWith("cust")) {
                String numPart = id.substring(4);
                try {
                    int n = Integer.parseInt(numPart);
                    if (n > max) max = n;
                } catch (NumberFormatException ignore) {
                    // Abaikan ID lama yang formatnya tidak konsisten
                }
            }
        }

        int next = max + 1;

        String numStr = (next < 100)
                ? String.format("%02d", next)
                : String.valueOf(next);

        return "cust" + numStr;
    }

    public Customer getCustomerById(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID tidak boleh kosong.");
        }

        Customer found;
        try {
            found = customerDAO.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil data customer dari database.", e);
        }

        if (found == null) {
            throw new IllegalArgumentException("Customer dengan ID tersebut tidak ditemukan.");
        }

        return found;
    }

    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil daftar customer dari database.", e);
        }
    }
}
