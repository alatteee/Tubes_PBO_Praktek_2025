package model;

public class Dog extends Pet {
    
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
    }

    /**
     * Mengimplementasikan harga dasar layanan untuk Anjing.
     * @return Harga dasar layanan.
     */
    @Override
    public double getBasePrice() {
        return BASE_PRICE;
    }
}