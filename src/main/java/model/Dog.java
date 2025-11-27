package model;

/**
 * Merepresentasikan jenis hewan Anjing, mewarisi dari kelas Pet.
 */
public class Dog extends Pet {

    // Harga dasar layanan (Base Price) untuk Anjing
    private static final double BASE_PRICE = 75000.0;

    /**
     * Konstruktor untuk Dog.
     * Memanggil konstruktor superclass Pet.
     * @param petId ID unik hewan.
     * @param name Nama anjing.
     * @param age Umur anjing.
     * @param ownerId ID pemilik.
     */
    public Dog(String petId, String name, int age, String ownerId) {
        super(petId, name, age, ownerId);

        // VALIDASI BASE PRICE
        if (BASE_PRICE <= 0) {
            throw new IllegalStateException("Base price untuk Dog tidak boleh 0 atau negatif.");
        }
    }

    /**
     * Mengimplementasikan harga dasar layanan untuk Anjing.
     * @return Harga dasar layanan.
     */
    @Override
    public double getBasePrice() {
        // Proteksi tambahan saat runtime
        if (BASE_PRICE <= 0) {
            throw new IllegalStateException("Base price untuk Dog tidak valid (0 atau negatif).");
        }
        return BASE_PRICE;
    }
}
