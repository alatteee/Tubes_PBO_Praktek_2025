package model;

/**
 * Merepresentasikan jenis hewan Kelinci, mewarisi dari kelas Pet.
 */
public class Rabbit extends Pet {

    // Harga dasar layanan (Base Price) untuk Kelinci
    private static final double BASE_PRICE = 40000.0;

    /**
     * Konstruktor untuk Rabbit.
     * Memanggil konstruktor superclass Pet.
     * @param petId ID unik hewan.
     * @param name Nama kelinci.
     * @param age Umur kelinci.
     * @param ownerId ID pemilik.
     */
    public Rabbit(String petId, String name, int age, String ownerId) {
        super(petId, name, age, ownerId);

        // VALIDASI BASE PRICE
        if (BASE_PRICE <= 0) {
            throw new IllegalStateException("Base price untuk Rabbit tidak boleh 0 atau negatif.");
        }
    }

    /**
     * Mengimplementasikan harga dasar layanan untuk Kelinci.
     * @return Harga dasar layanan.
     */
    @Override
    public double getBasePrice() {
        // Proteksi runtime
        if (BASE_PRICE <= 0) {
            throw new IllegalStateException("Base price untuk Rabbit tidak valid (0 atau negatif).");
        }
        return BASE_PRICE;
    }
}
