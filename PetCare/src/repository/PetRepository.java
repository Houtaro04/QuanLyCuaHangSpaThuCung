package repository;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Pet;

public class PetRepository {

    public boolean addPet(String name, String species, int age, String ownerUsername) {
        String sql = "INSERT INTO Pets (name, species, age, ownerUsername) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, species);
            stmt.setInt(3, age);
            stmt.setString(4, ownerUsername);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi addPet: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM Pets ORDER BY createdAt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pets.add(new Pet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("species"),
                        rs.getInt("age"),
                        rs.getString("ownerUsername")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllPets: " + e.getMessage());
            e.printStackTrace();
        }
        return pets;
    }

    public List<Pet> getPetsByOwner(String ownerUsername) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM Pets WHERE ownerUsername = ? ORDER BY createdAt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ownerUsername);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pets.add(new Pet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("species"),
                        rs.getInt("age"),
                        rs.getString("ownerUsername")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getPetsByOwner: " + e.getMessage());
            e.printStackTrace();
        }
        return pets;
    }

    public Pet getPetById(int id) {
        String sql = "SELECT * FROM Pets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Pet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("species"),
                        rs.getInt("age"),
                        rs.getString("ownerUsername"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getPetById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Pet> searchPets(String keyword) {
        List<Pet> pets = new ArrayList<>();
        String sql = "SELECT * FROM Pets WHERE name LIKE ? OR species LIKE ? OR ownerUsername LIKE ? ORDER BY createdAt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                pets.add(new Pet(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("species"),
                        rs.getInt("age"),
                        rs.getString("ownerUsername")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchPets: " + e.getMessage());
            e.printStackTrace();
        }
        return pets;
    }

    public boolean updatePet(Pet pet) {
        String sql = "UPDATE Pets SET name = ?, species = ?, age = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pet.name);
            stmt.setString(2, pet.species);
            stmt.setInt(3, pet.age);
            stmt.setInt(4, pet.id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updatePet: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePet(int id) {
        String sql = "DELETE FROM Pets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi deletePet: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int countPetsByOwner(String ownerUsername) {
        String sql = "SELECT COUNT(*) FROM Pets WHERE ownerUsername = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ownerUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi countPetsByOwner: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}