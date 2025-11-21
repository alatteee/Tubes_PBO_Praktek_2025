package model;

/**
 * Kelas abstrak yang merepresentasikan Hewan Peliharaan.
 * Berfungsi sebagai superclass untuk Cat, Dog, dan Rabbit.
 */
public abstract class Pet {

    // Field-field data inti yang bersifat immutable (final).
    private final String petId;
    private final String name;
    private final int age;
    private final String ownerId; // Foreign Key ke Customer

    // Field status bisa berubah (mutable), tidak menggunakan 'final'.
    private String status = "Tidak dalam layanan";

    /**
     * Konstruktor untuk Pet.
     * @param petId ID unik hewan.
     * @param name Nama hewan.
     * @param age Umur hewan (harus >= 0).
     * @param ownerId ID pelanggan pemilik hewan (harus diisi).
     * @throws IllegalArgumentException jika umur negatif atau ownerId tidak valid.
     */
    public Pet(String petId, String name, int age, String ownerId) {
        // Exception Handling (BR-04)
        if (age < 0) {
            throw new IllegalArgumentException("Umur hewan tidak boleh negatif.");
        }
        // Validasi kepemilikan (BR-02)
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Setiap hewan harus memiliki ID pemilik.");
        }
        
        this.petId = petId;
        this.name = name;
        this.age = age;
        this.ownerId = ownerId;
    }

    // --- ABSTRACT METHOD (BR-15) ---
    
    /**
     * Mendapatkan harga dasar untuk layanan berdasarkan jenis hewan.
     * Harus diimplementasikan oleh subclass (Cat, Dog, Rabbit).
     * @return Harga dasar layanan dalam double.
     */
    public abstract double getBasePrice();

    // --- GETTERS ---
    
    public String getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getStatus() {
        return status;
    }

    // --- SETTER STATUS (Digunakan oleh OrderManager/Facade saat layanan dimulai/selesai) ---
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Pet{" +
                "id='" + petId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", ownerId='" + ownerId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}