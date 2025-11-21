package manager;

import java.util.List;

import dao.PetDAO;
import factory.PetFactory;
import model.Customer;
import model.Pet;

public class PetManager {

    private final PetDAO petDAO;
    private final PetFactory petFactory;

    public PetManager() {
        this.petDAO = new PetDAO();
        this.petFactory = new PetFactory();
    }

    /**
     * Register pet baru untuk owner tertentu.
     * type: "Cat", "Dog", "Rabbit"
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

        // Gunakan Factory sesuai class diagram
        Pet pet = petFactory.createPet(type, name, age, owner);

        // Status awal seharusnya "Tidak dalam layanan", tapi kalau belum di-set, kita pastikan di sini
        if (pet.getStatus() == null || pet.getStatus().isBlank()) {
            pet.setStatus("Tidak dalam layanan");
        }

        return petDAO.save(pet);
    }

    /**
     * Ambil pet berdasarkan ID (dipakai di Facade saat createServiceOrder).
     */
    public Pet getPetById(String petId) {
        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong");
        }
        return petDAO.findById(petId);
    }

    /**
     * Ambil semua pet (untuk tabel).
     * Kalau kamu cuma butuh per customer, bisa pakai getPetsByCustomer saja.
     */
    public List<Pet> getAllPets() {
        return petDAO.findAll();
    }

    /**
     * Ambil semua pet milik owner tertentu.
     */
    public List<Pet> getPetsByCustomer(Customer owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner tidak boleh null");
        }
        return petDAO.findByCustomer(owner);
    }

    /**
     * Update data pet (termasuk status).
     * Dipakai misalnya saat checkout untuk mengubah status menjadi "Tidak dalam layanan".
     */
    public Pet update(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Pet tidak boleh null");
        }
        return petDAO.update(pet);
    }
}
