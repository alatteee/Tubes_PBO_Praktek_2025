package manager;

import dao.PetDAO;
import dao.JdbcPetDAO;
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
        this.petDAO = new JdbcPetDAO();
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

        // ===== VALIDASI INPUT DASAR =====
        if (owner == null) {
            throw new IllegalArgumentException("Owner tidak boleh null.");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Jenis hewan tidak boleh kosong.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nama hewan tidak boleh kosong.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Umur hewan tidak boleh negatif.");
        }

        // ===== CEK PET DUPLIKAT (optional tapi aman untuk BR-33) =====
        try {
            List<Pet> pets = petDAO.findByCustomer(owner.getCustomerId());
            for (Pet p : pets) {
                if (p.getName().equalsIgnoreCase(name.trim())) {
                    // NOTE: Nama pet boleh sama jika pemilik berbeda,
                    // tapi tidak boleh sama dalam 1 customer (good practice)
                    throw new IllegalArgumentException(
                        "Nama hewan sudah digunakan oleh customer ini."
                    );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Gagal memeriksa duplikasi nama hewan di database.", e);
        }

        // ===== BUAT PET (PAKAI FACTORY) =====
        Pet pet;
        try {
            pet = PetFactory.createPet(type, name, age, owner.getCustomerId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Gagal membuat objek Pet: " + e.getMessage(), e);
        }

        // ===== PASTIKAN STATUS AWAL VALID =====
        if (pet.getStatus() == null || pet.getStatus().isBlank()) {
            pet.setStatus("Tidak dalam layanan");
        }

        // ===== SIMPAN KE DATABASE =====
        try {
            return petDAO.save(pet);
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan data Pet ke database: " + e.getMessage(), e);
        }
    }

    /**
     * Ambil Pet berdasarkan ID.
     */
    public Pet getPetById(String petId) {

        if (petId == null || petId.isBlank()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong.");
        }

        Pet pet;
        try {
            pet = petDAO.findById(petId);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil data Pet dari database.", e);
        }

        if (pet == null) {
            throw new IllegalArgumentException("Pet dengan ID tersebut tidak ditemukan.");
        }

        return pet;
    }

    /**
     * Ambil semua Pet (untuk tabel GUI).
     */
    public List<Pet> getAllPets() {
        try {
            return petDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil daftar semua Pet.", e);
        }
    }

    /**
     * Ambil daftar Pet milik Customer tertentu.
     */
    public List<Pet> getPetsByCustomer(Customer owner) {

        if (owner == null) {
            throw new IllegalArgumentException("Owner tidak boleh null.");
        }

        try {
            return petDAO.findByCustomer(owner.getCustomerId());
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil daftar Pet milik customer.", e);
        }
    }

    /**
     * Update Pet (status, nama, atau data lainnya jika perlu).
     * Dipakai untuk checkout: status pet → "Tidak dalam layanan"
     */
    public Pet update(Pet pet) {

        if (pet == null) {
            throw new IllegalArgumentException("Pet tidak boleh null.");
        }

        try {
            return petDAO.update(pet);
        } catch (Exception e) {
            throw new RuntimeException("Gagal memperbarui data Pet di database.", e);
        }
    }
}
