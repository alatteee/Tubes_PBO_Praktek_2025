package factory;

import java.util.List;

import dao.JdbcPetDAO;
import model.Cat;
import model.Dog;
import model.Rabbit;
import model.Pet;


public class PetFactory {
    public static Pet createPet(String type, String name, int age, String ownerId) {
        String id = generatePetId(type);

        String t = type.toLowerCase();
        switch (t) {
            case "cat":
                return new Cat(id, name, age, ownerId);
            case "dog":
                return new Dog(id, name, age, ownerId);
            case "rabbit":
                return new Rabbit(id, name, age, ownerId);
            default:
                return new Pet(id, name, age, ownerId) {
                    @Override
                    public double getBasePrice() {
                        return 0;
                    }
                };
        }
    }


    private static String generatePetId(String type) {
        String prefix;

        switch (type.toLowerCase()) {
            case "cat"    -> prefix = "cat";
            case "dog"    -> prefix = "dog";
            case "rabbit" -> prefix = "rab";
            default       -> prefix = "pet";
        }

        JdbcPetDAO dao = new JdbcPetDAO();
        List<Pet> allPets = dao.findAll();

        int max = 0;
        for (Pet p : allPets) {
            String id = p.getPetId();   
            if (id == null) continue;

            String lower = id.toLowerCase();
            if (lower.startsWith(prefix)) {
                String numPart = id.substring(prefix.length()); 
                try {
                    int n = Integer.parseInt(numPart);
                    if (n > max) max = n;
                } catch (NumberFormatException ignore) {
                    // abaikan ID lama yang formatnya beda
                }
            }
        }

        int next = max + 1;
        String numStr = (next < 100)
                ? String.format("%02d", next)   // 01..99
                : String.valueOf(next);         // 100, 101, dst

        return prefix + numStr;   // cat01, dog01, rab01, pet01, ...
    }

}
