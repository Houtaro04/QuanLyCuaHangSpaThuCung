package repository;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserRepository {

    public User checkLogin(String username, String password) {
        String sql = "SELECT * FROM [Users] WHERE Username = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("FullName"),
                        rs.getString("Phone"),
                        rs.getString("Email"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi checkLogin: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerCustomer(String username, String password, String fullName, String phone, String email) {
        if (isUsernameExists(username)) {
            System.out.println("Username đã tồn tại: " + username);
            return false;
        }

        String sql = "INSERT INTO [Users] (Username, Password, Role, FullName, Phone, Email) VALUES (?, ?, 'CUSTOMER', ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setString(4, phone);
            stmt.setString(5, email);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi registerCustomer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM [Users] WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [Users] ORDER BY Role ASC, Username ASC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("FullName"),
                        rs.getString("Phone"),
                        rs.getString("Email")));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateCustomerInfo(String username, String fullName, String phone, String email) {
        String sql = "UPDATE [Users] SET FullName = ?, Phone = ?, Email = ? WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, email);
            stmt.setString(4, username);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateCustomerInfo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE [Users] SET Password = ?, FullName = ?, Phone = ?, Email = ? WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.password);
            stmt.setString(2, user.fullName);
            stmt.setString(3, user.phone);
            stmt.setString(4, user.email);
            stmt.setString(5, user.username);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String username) {
        String sql = "DELETE FROM [Users] WHERE Username = ? AND Role != 'ADMIN'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE [Users] SET Password = ? WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, username);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyUserEmail(String username, String email) {
        String sql = "SELECT COUNT(*) FROM [Users] WHERE Username = ? AND Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}