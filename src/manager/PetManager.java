package manager;

import model.Pet;
import dao.PetDAO;
import factory.PetFactory;
import java.util.List;

/**
 * Kelas yang menangani Business Logic untuk entitas Pet.
 * Bertanggung jawab untuk pembuatan Pet (via Factory) dan interaksi data (via DAO).
 */
public class PetManager {

    private final PetDAO petDAO;

    /**
     * Konstruktor yang menerima PetDAO (Dependency Injection).
     */
    public PetManager(PetDAO petDAO) {
        this.petDAO = petDAO;
    }

    /**
     * Mendaftarkan hewan peliharaan baru.
     * Menggunakan PetFactory untuk membuat objek yang spesifik.
     * @param type Jenis hewan ("Cat", "Dog", "Rabbit").
     * @param name Nama hewan.
     * @param age Umur hewan.
     * @param ownerId ID pemilik (Foreign Key).
     * @return Objek Pet yang telah disimpan (dengan ID dari DB jika DAO mengubah ID).
     * @throws IllegalArgumentException jika data Pet atau tipe tidak valid (dari PetFactory/Pet constructor).
     * @throws Exception jika terjadi error persistensi (DAO).
     */
    public Pet registerPet(String type, String name, int age, String ownerId) throws Exception {
        // 1. Creation Logic: Menggunakan PetFactory (Design Pattern)
        Pet newPet = PetFactory.createPet(type, name, age, ownerId);
        
        // 2. Persistency Logic: Menyimpan via DAO
        return petDAO.save(newPet);
    }

    /**
     * Mengambil daftar hewan milik seorang Customer.
     * (Generic Programming: List<Pet>)
     */
    public List<Pet> getPetsByCustomer(String ownerId) throws Exception {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pemilik tidak boleh kosong untuk mencari hewan.");
        }
        return petDAO.findByCustomer(ownerId);
    }
    
    /**
     * Mengambil data Pet berdasarkan ID.
     */
    public Pet getPetById(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong.");
        }
        return petDAO.findById(id);
    }
    
    // Catatan: PetManager juga bisa punya method updateStatus(Pet pet, String newStatus), 
    // tapi ini biasanya digabungkan ke OrderManager karena terikat dengan ServiceOrder.
}