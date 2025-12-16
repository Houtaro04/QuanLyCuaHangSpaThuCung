package repository;

import database.DatabaseConnection;
import model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {
    
    // Thêm dịch vụ mới
    public boolean addService(String name, double price) {
        String sql = "INSERT INTO Services (name, price) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi addService: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tất cả dịch vụ
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
            System.err.println("Lỗi getAllServices: " + e.getMessage());
            e.printStackTrace();
        }
        return services;
    }
    
    // Lấy dịch vụ theo ID
    public Service getServiceById(int id) {
        String sql = "SELECT * FROM Services WHERE id = ? AND isActive = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Service(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getServiceById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy dịch vụ theo tên
    public Service getServiceByName(String name) {
        String sql = "SELECT * FROM Services WHERE name = ? AND isActive = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Service(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getServiceByName: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Cập nhật dịch vụ
    public boolean updateService(Service service) {
        String sql = "UPDATE Services SET name = ?, price = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, service.name);
            stmt.setDouble(2, service.price);
            stmt.setInt(3, service.id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateService: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa dịch vụ (soft delete)
    public boolean deleteService(int id) {
        String sql = "UPDATE Services SET isActive = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi deleteService: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa dịch vụ thật (hard delete)
    public boolean hardDeleteService(int id) {
        String sql = "DELETE FROM Services WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi hardDeleteService: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Tìm kiếm dịch vụ
    public List<Service> searchServices(String keyword) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Services WHERE name LIKE ? AND isActive = 1 ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                services.add(new Service(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchServices: " + e.getMessage());
            e.printStackTrace();
        }
        return services;
    }
    
    // Đếm tổng số dịch vụ
    public int countServices() {
        String sql = "SELECT COUNT(*) FROM Services WHERE isActive = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi countServices: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}