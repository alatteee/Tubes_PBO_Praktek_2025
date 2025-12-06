package model;
public class Rabbit extends Pet {

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
    }

    /**
     * Mengimplementasikan harga dasar layanan untuk Kelinci.
     * @return Harga dasar layanan.
     */
    @Override
    public double getBasePrice() {
        return BASE_PRICE;
    }
}