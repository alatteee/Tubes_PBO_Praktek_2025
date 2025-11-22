package dao;

import model.Cat;
import model.Dog;
import model.Pet;
import model.Rabbit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcPetDAO implements PetDAO {

    private Pet mapRowToPet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String type = rs.getString("type");
        int age = rs.getInt("age");
        String status = rs.getString("status");
        String ownerId = rs.getString("owner_id");

        Pet pet;
        switch (type.toLowerCase()) {
            case "cat":
                pet = new Cat(id, name, age, ownerId);
                break;
            case "dog":
                pet = new Dog(id, name, age, ownerId);
                break;
            case "rabbit":
                pet = new Rabbit(id, name, age, ownerId);
                break;
            default:
                throw new IllegalArgumentException("Unknown pet type: " + type);
        }

        // set status dari DB
        if (status != null && !status.isBlank()) {
            pet.setStatus(status);
        }
        return pet;
    }

    @Override
    public Pet save(Pet pet) {
        String sql = "INSERT INTO pets (id, name, type, age, status, owner_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pet.getPetId());
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getClass().getSimpleName()); // "Cat", "Dog", "Rabbit"
            ps.setInt(4, pet.getAge());
            ps.setString(5, pet.getStatus());
            ps.setString(6, pet.getOwnerId());

            ps.executeUpdate();
            return pet;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menyimpan pet: " + e.getMessage(), e);
        }
    }

    @Override
    public Pet findById(String id) {
        String sql = "SELECT id, name, type, age, status, owner_id FROM pets WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToPet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mencari pet dengan id: " + id, e);
        }
    }

    @Override
    public List<Pet> findAll() {
        String sql = "SELECT id, name, type, age, status, owner_id FROM pets ORDER BY name";

        List<Pet> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToPet(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil semua pet", e);
        }
    }

    @Override
    public List<Pet> findByCustomer(String ownerId) {
        String sql = "SELECT id, name, type, age, status, owner_id FROM pets WHERE owner_id = ? ORDER BY name";

        List<Pet> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ownerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapRowToPet(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil pet milik owner: " + ownerId, e);
        }
    }

    @Override
    public Pet update(Pet pet) {
        String sql = "UPDATE pets SET name = ?, type = ?, age = ?, status = ?, owner_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pet.getName());
            ps.setString(2, pet.getClass().getSimpleName());
            ps.setInt(3, pet.getAge());
            ps.setString(4, pet.getStatus());
            ps.setString(5, pet.getOwnerId());
            ps.setString(6, pet.getPetId());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                return pet;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengupdate pet dengan id: " + pet.getPetId(), e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM pets WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus pet dengan id: " + id, e);
        }
    }
}
