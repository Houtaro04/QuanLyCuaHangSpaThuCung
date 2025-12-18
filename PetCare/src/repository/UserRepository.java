package repository;

import database.DatabaseConnection;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    
    // ==========================================================
    // 1. ĐĂNG NHẬP
    // ==========================================================
    public User checkLogin(String username, String password) {
        // Lưu ý: Đảm bảo tên bảng trong database là [Users] hoặc [User]
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
                    rs.getString("Phone"),  // Lấy SĐT
                    rs.getString("Email")   // Lấy Email
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi checkLogin: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // ==========================================================
    // 2. ĐĂNG KÝ KHÁCH HÀNG MỚI
    // ==========================================================
    public boolean registerCustomer(String username, String password, String fullName, String phone, String email) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (isUsernameExists(username)) {
            System.out.println("Username đã tồn tại: " + username);
            return false;
        }
        
        // 2. Thực hiện Insert
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
    
    // ==========================================================
    // 3. TIỆN ÍCH: KIỂM TRA TỒN TẠI
    // ==========================================================
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
    
    // ==========================================================
    // 4. LẤY DANH SÁCH (CHO ADMIN)
    // ==========================================================
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        // Sắp xếp theo Role để Admin lên đầu, hoặc theo Username
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
                    rs.getString("Email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllUsers: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    
    // ==========================================================
    // 5. CẬP NHẬT THÔNG TIN CÁ NHÂN (MỚI - QUAN TRỌNG)
    // ==========================================================
    // Hàm này được gọi từ giao diện "Thông tin cá nhân" để cập nhật SĐT, Email
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

    // Hàm update cũ (có đổi pass) - giữ lại nếu cần dùng sau này
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
    
    // ==========================================================
    // 6. XÓA USER
    // ==========================================================
    public boolean deleteUser(String username) {
        // Không cho phép xóa ADMIN
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
    // [MỚI] Hàm cập nhật mật khẩu (Dùng cho cả Đổi MK và Quên MK)
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

    // [MỚI] Kiểm tra xem Username và Email có khớp nhau không (Dùng cho Quên MK)
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