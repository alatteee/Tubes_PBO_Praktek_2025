package model;

public abstract class Pet {

    protected String petId;
    protected String name;
    protected int age;
    protected String ownerId; // Foreign Key ke Customer

    // Status default
    protected String status = "Tidak dalam layanan";

    /**
     * Konstruktor untuk Pet.
     * @param petId ID unik hewan.
     * @param name Nama hewan.
     * @param age Umur hewan (>= 0).
     * @param ownerId ID pelanggan pemilik hewan.
     * @throws IllegalArgumentException jika data tidak valid.
     */
    public Pet(String petId, String name, int age, String ownerId) {

        if (petId == null || petId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama hewan tidak boleh kosong.");
        }

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

    // --- SETTERS (Perlu untuk JDBC DAO) ---
    public void setPetId(String petId) {
        if (petId == null || petId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet ID tidak boleh kosong.");
        }
        this.petId = petId;
    }

    public void setOwnerId(String ownerId) {
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Owner ID tidak boleh kosong.");
        }
        this.ownerId = ownerId;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama hewan tidak boleh kosong.");
        }
        this.name = name;
    }

    public void setAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Umur hewan tidak boleh negatif.");
        }
        this.age = age;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status hewan tidak boleh kosong.");
        }
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
