package dao;

import model.Customer;
import java.util.List;

public interface CustomerDAO {
    Customer save(Customer customer);
    Customer findById(String id);
    List<Customer> findAll();
    boolean delete(String id);
    Customer update(Customer customer);
}
