package dao;

import model.Pet;
import java.util.List;

/**
 * Kontrak untuk operasi data persistensi (CRUD) Pet.
 * Diimplementasikan oleh Anggota 3.
 */
public interface PetDAO {

    Pet save(Pet pet);

    Pet findById(String id);

    // Ambil semua pet (untuk tabel GUI)
    List<Pet> findAll();

    // Ambil hewan berdasarkan ID pemiliknya (Customer.id)
    List<Pet> findByCustomer(String ownerId);

    Pet update(Pet pet);

    boolean delete(String id);
}
