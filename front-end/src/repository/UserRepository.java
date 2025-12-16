package repository;

import database.DatabaseConnection;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    
    // Đăng nhập
    public User checkLogin(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("fullName")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi checkLogin: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Đăng ký khách hàng mới
    public boolean registerCustomer(String username, String password, String fullName) {
        // Kiểm tra username đã tồn tại chưa
        if (isUsernameExists(username)) {
            System.out.println("Username đã tồn tại!");
            return false;
        }
        
        String sql = "INSERT INTO Users (username, password, role, fullName) VALUES (?, ?, 'CUSTOMER', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi registerCustomer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Kiểm tra username đã tồn tại chưa
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi isUsernameExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy tất cả users (cho admin)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY createdAt DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("fullName")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    // Cập nhật thông tin user
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET password = ?, fullName = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.password);
            stmt.setString(2, user.fullName);
            stmt.setString(3, user.username);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa user
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM Users WHERE username = ? AND role != 'ADMIN'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi deleteUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}