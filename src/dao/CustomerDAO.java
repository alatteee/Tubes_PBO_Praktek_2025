package dao;

import model.Customer;
import java.util.List;

/**
 * Kontrak untuk operasi data persistensi (CRUD) Customer.
 * Diimplementasikan oleh Anggota 3.
 */
public interface CustomerDAO {
    Customer save(Customer customer) throws Exception;
    Customer findById(String id) throws Exception;
    List<Customer> findAll() throws Exception;
    Customer update(Customer customer) throws Exception;
    boolean delete(String id) throws Exception;
}