package model;

/**
 * Kelas abstrak yang merepresentasikan Hewan Peliharaan.
 * Berfungsi sebagai superclass untuk Cat, Dog, dan Rabbit.
 */
public abstract class Pet {

    // Field inti harus bisa di-set ulang saat load dari database (TIDAK final).
    protected String petId;
    protected String name;
    protected int age;
    protected String ownerId; // Foreign Key ke Customer

    // Field status bisa berubah (mutable).
    protected String status = "Tidak dalam layanan";

    /**
     * Konstruktor untuk Pet.
     * @param petId ID unik hewan.
     * @param name Nama hewan.
     * @param age Umur hewan (harus >= 0).
     * @param ownerId ID pelanggan pemilik hewan (harus diisi).
     * @throws IllegalArgumentException jika umur negatif atau ownerId tidak valid.
     */
    public Pet(String petId, String name, int age, String ownerId) {
        if (age < 0) {
            throw new IllegalArgumentException("Umur hewan tidak boleh negatif.");
        }
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Setiap hewan harus memiliki ID pemilik.");
        }

        this.petId = petId;
        this.name = name;
        this.age = age;
        this.ownerId = ownerId;
    }

    // --- ABSTRACT METHOD ---
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

    // --- SETTERS (Diperlukan untuk JDBC DAO) ---

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        if (age < 0) throw new IllegalArgumentException("Umur hewan tidak boleh negatif.");
        this.age = age;
    }

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
