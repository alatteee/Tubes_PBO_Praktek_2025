package manager;

import model.Customer;
import dao.CustomerDAO;
import java.util.List;

/**
 * Kelas yang menangani Business Logic untuk entitas Customer (Repository Layer).
 */
public class CustomerManager {

    private final CustomerDAO customerDAO;

    /**
     * Konstruktor yang menerima CustomerDAO (Dependency Injection).
     */
    public CustomerManager(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    /**
     * Mendaftarkan pelanggan baru setelah memvalidasi datanya (BR-05).
     * @param name Nama pelanggan.
     * @param phone Nomor telepon pelanggan.
     * @return Objek Customer yang telah disimpan (dengan ID dari DB).
     * @throws IllegalArgumentException jika data tidak valid.
     * @throws Exception jika terjadi error persistensi (DAO).
     */
    public Customer registerCustomer(String name, String phone) throws Exception {
        // ID diisi null/ sementara, karena DAO yang akan mengisi ID dari DB
        Customer newCustomer = new Customer(null, name, phone); 
        
        if (!newCustomer.isValid()) {
            throw new IllegalArgumentException("Data pelanggan tidak valid. Nama tidak boleh kosong dan format nomor telepon salah.");
        }
        
        return customerDAO.save(newCustomer);
    }

    /**
     * Mengambil data pelanggan berdasarkan ID.
     */
    public Customer getCustomerById(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID tidak boleh kosong.");
        }
        return customerDAO.findById(id);
    }

    /**
     * Mengambil semua daftar pelanggan. (Generic Programming: List<Customer>)
     */
    public List<Customer> getAllCustomers() throws Exception {
        return customerDAO.findAll();
    }
}