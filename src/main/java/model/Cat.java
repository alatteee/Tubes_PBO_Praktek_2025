package model;

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
    }

    // Mengimplementasikan harga dasar layanan untuk Kucing.@return
    @Override
    public double getBasePrice() {
        return BASE_PRICE;
    }
}
