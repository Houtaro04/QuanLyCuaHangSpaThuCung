package repository;

import database.DatabaseConnection;
import model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {
    
    // Thêm dịch vụ mới
    public boolean addService(String name, double price) {
        String sql = "INSERT INTO Services (name, price, isActive) VALUES (?, ?, 1)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tất cả dịch vụ (Chỉ lấy isActive = 1)
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Services WHERE isActive = 1 ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                services.add(new Service(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
    
    // [MỚI] Hàm tiện ích: Update nhận tham số rời (để DataManager gọi)
    public boolean updateService(int id, String name, double price) {
        // Gọi lại hàm updateService(Object) ở dưới
        return updateService(new Service(id, name, price));
    }

    // Cập nhật dịch vụ (Nhận đối tượng Service)
    public boolean updateService(Service service) {
        String sql = "UPDATE Services SET name = ?, price = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.name);
            stmt.setDouble(2, service.price);
            stmt.setInt(3, service.id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa dịch vụ (Soft delete - Ẩn đi chứ không xóa mất)
    public boolean deleteService(int id) {
        String sql = "UPDATE Services SET isActive = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Đếm tổng số dịch vụ đang hoạt động
    public int countServices() {
        String sql = "SELECT COUNT(*) FROM Services WHERE isActive = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}