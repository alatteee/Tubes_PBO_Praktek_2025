package factory;

import model.Cat;
import model.Dog;
import model.Pet;
import model.Rabbit;
import java.util.UUID;

/**
 * Kelas utilitas yang bertanggung jawab untuk membuat objek Pet.
 * Menerapkan Design Pattern: Factory Method (Simple Factory).
 */
public class PetFactory {

    /**
     * Membuat objek Pet (Cat, Dog, atau Rabbit) berdasarkan tipe yang diberikan.
     * @param type Jenis hewan ("Cat", "Dog", "Rabbit").
     * @param name Nama hewan.
     * @param age Umur hewan.
     * @param ownerId ID pemilik hewan (Foreign Key).
     * @return Objek Pet yang spesifik.
     * @throws IllegalArgumentException jika jenis hewan tidak dikenali.
     */
    public static Pet createPet(String type, String name, int age, String ownerId) throws IllegalArgumentException {
        // Menggunakan UUID sebagai placeholder ID sementara sebelum disimpan ke DB oleh DAO.
        String tempId = UUID.randomUUID().toString();

        if (type == null) {
            throw new IllegalArgumentException("Jenis hewan tidak boleh kosong.");
        }

        switch (type.toLowerCase()) {
            case "cat":
                return new Cat(tempId, name, age, ownerId);
            case "dog":
                return new Dog(tempId, name, age, ownerId);
            case "rabbit":
                return new Rabbit(tempId, name, age, ownerId);
            default:
                // Exception Handling (BR-03)
                throw new IllegalArgumentException("Jenis hewan '" + type + "' tidak dikenali.");
        }
    }
}