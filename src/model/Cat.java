package model;

/**
 * Merepresentasikan jenis hewan Kucing, mewarisi dari kelas Pet.
 */
public class Cat extends Pet {

    // Harga dasar layanan (Base Price) untuk Kucing
    private static final double BASE_PRICE = 60000.0;

    /**
     * Konstruktor untuk Cat.
     * Memanggil konstruktor superclass Pet.
     * @param petId ID unik hewan.
     * @param name Nama kucing.
     * @param age Umur kucing.
     * @param ownerId ID pemilik.
     */
    public Cat(String petId, String name, int age, String ownerId) {
        super(petId, name, age, ownerId);

        // VALIDASI TAMBAHAN (opsional tapi aman)
        if (BASE_PRICE <= 0) {
            throw new IllegalStateException("Base price untuk Cat tidak boleh 0 atau negatif.");
        }
    }

    /**
     * Mengimplementasikan harga dasar layanan untuk Kucing.
     * @return Harga dasar layanan.
     */
    @Override
    public double getBasePrice() {
        // Tambahan pengecekan saat runtime (untuk berjaga-jaga)
        if (BASE_PRICE <= 0) {
            throw new IllegalStateException("Base price untuk Cat tidak valid (0 atau negatif).");
        }
        return BASE_PRICE;
    }
}
