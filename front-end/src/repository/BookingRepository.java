package repository;

import database.DatabaseConnection;
import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    
    // Thêm booking mới
    public boolean addBooking(String customerUser, String petName, String serviceName, 
                             double price, String appointmentDate) {
        String sql = "INSERT INTO Bookings (customerUser, petName, serviceName, price, status, appointmentDate) " +
                     "VALUES (?, ?, ?, ?, N'Chờ duyệt', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerUser);
            stmt.setString(2, petName);
            stmt.setString(3, serviceName);
            stmt.setDouble(4, price);
            stmt.setString(5, appointmentDate);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi addBooking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy tất cả bookings
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Bookings ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getString("customerUser"),
                    rs.getString("petName"),
                    rs.getString("serviceName"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("appointmentDate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllBookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    // Lấy bookings theo khách hàng
    public List<Booking> getBookingsByCustomer(String customerUser) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Bookings WHERE customerUser = ? ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerUser);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getString("customerUser"),
                    rs.getString("petName"),
                    rs.getString("serviceName"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("appointmentDate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getBookingsByCustomer: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    // Lấy booking theo ID
    public Booking getBookingById(int id) {
        String sql = "SELECT * FROM Bookings WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Booking(
                    rs.getInt("id"),
                    rs.getString("customerUser"),
                    rs.getString("petName"),
                    rs.getString("serviceName"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("appointmentDate")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getBookingById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Cập nhật trạng thái booking
    public boolean updateBookingStatus(int id, String newStatus) {
        String sql = "UPDATE Bookings SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateBookingStatus: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Cập nhật booking
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE Bookings SET petName = ?, serviceName = ?, price = ?, " +
                     "status = ?, appointmentDate = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, booking.petName);
            stmt.setString(2, booking.serviceName);
            stmt.setDouble(3, booking.price);
            stmt.setString(4, booking.status);
            stmt.setString(5, booking.date);
            stmt.setInt(6, booking.id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi updateBooking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Xóa booking
    public boolean deleteBooking(int id) {
        String sql = "DELETE FROM Bookings WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi deleteBooking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy bookings theo trạng thái
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Bookings WHERE status = ? ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getString("customerUser"),
                    rs.getString("petName"),
                    rs.getString("serviceName"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("appointmentDate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getBookingsByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    // Đếm bookings theo trạng thái
    public int countBookingsByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Bookings WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi countBookingsByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Tính tổng doanh thu
    public double getTotalRevenue() {
        String sql = "SELECT SUM(price) FROM Bookings WHERE status = N'Đã Xong'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getTotalRevenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}