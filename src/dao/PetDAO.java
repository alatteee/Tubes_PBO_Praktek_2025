package dao;

import model.Customer;
import model.Pet;
import java.util.List;

/**
 * Kontrak untuk operasi data persistensi (CRUD) Pet.
 * Diimplementasikan oleh Anggota 3.
 */
public interface PetDAO {
    Pet save(Pet pet) throws Exception;
    Pet findById(String id) throws Exception;
    // Method untuk mengambil hewan berdasarkan pemiliknya (Customer)
    List<Pet> findByCustomer(String ownerId) throws Exception; 
    Pet update(Pet pet) throws Exception;
    boolean delete(String id) throws Exception;
}