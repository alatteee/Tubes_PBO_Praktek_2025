package manager;

import dao.PetDAO;
import factory.PetFactory;
import model.Customer;
import model.Pet;

import java.util.List;

/**
 * PetManager bertanggung jawab untuk:
 * - Validasi data Pet
 * - Pembuatan Pet melalui PetFactory (Factory Method Pattern)
 * - Interaksi data via PetDAO (CRUD)
 */
public class PetManager {

    private final PetDAO petDAO;
    private final PetFactory petFactory;

    /**
     * Konstruktor default – membuat PetDAO dan PetFactory sendiri
     * Cocok dipakai oleh PetCareFacade.
     */
    public PetManager() {
        this.petDAO = new PetDAO();
        this.petFactory = new PetFactory();
    }

    /**
     * Register pet baru (Cat/Dog/Rabbit)
     * @param type jenis hewan ("Cat", "Dog", "Rabbit")
     * @param name nama hewan
     * @param age umur hewan
     * @param owner obj Customer yang valid (sudah dicek di Facade)
     */
    public Pet registerPet(String type, String name, int age, Customer owner) {

        if (owner == null) {
            throw new IllegalArgumentException("Owner tidak boleh null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama hewan tidak boleh kosong");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Umur hewan tidak boleh negatif");
        }

        // Buat Pet sesuai Factory Method Pattern
        Pet pet = petFactory.createPet(type, name, age, owner);

        // Pastikan status awal benar
        if (pet.getStatus() == null || pet.getStatus().isBlank()) {
            pet.setStatus("Tidak dalam layanan");
        }

        // Simpan Pet lewat DAO
        return petDAO.save(pet);
    }

    /**
     * Ambil Pet berdasarkan ID.
     */
    public Pet getPetById(String petId) {
        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong");
        }
        return petDAO.findById(petId);
    }

    /**
     * Ambil semua Pet (untuk tabel GUI).
     */
    public List<Pet> getAllPets() {
        return petDAO.findAll();
    }

    /**
     * Ambil daftar Pet milik Customer tertentu.
     */
    public List<Pet> getPetsByCustomer(Customer owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner tidak boleh null");
        }
        return petDAO.findByCustomer(owner);
    }

    /**
     * Update Pet (status, nama, atau data lainnya jika perlu).
     * Dipakai untuk checkout: status pet → "Tidak dalam layanan"
     */
    public Pet update(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Pet tidak boleh null");
        }
        return petDAO.update(pet);
    }
}
