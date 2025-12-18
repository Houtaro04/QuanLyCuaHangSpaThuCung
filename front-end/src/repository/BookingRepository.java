package repository;

import database.DatabaseConnection;
import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    
    // 1. Thêm booking mới
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
    
    // 2. Lấy TẤT CẢ bookings (Cho Admin) -> Sắp xếp ID Tăng dần
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        // QUAN TRỌNG: ORDER BY id ASC (Bé -> Lớn)
        String sql = "SELECT * FROM Bookings ORDER BY id ASC"; 
        
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
    
    // 3. Lấy bookings theo khách hàng (Cho Lịch sử đơn hàng) -> Sắp xếp ID Tăng dần
    public List<Booking> getBookingsByCustomer(String customerUser) {
        List<Booking> bookings = new ArrayList<>();
        // QUAN TRỌNG: ORDER BY id ASC (Bé -> Lớn)
        String sql = "SELECT * FROM Bookings WHERE customerUser = ? ORDER BY id ASC";
        
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
    
    // 4. Lấy booking theo ID
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
    
    // 5. Cập nhật trạng thái booking
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
    
    // 6. Cập nhật booking (nếu cần)
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
    
    // 7. Xóa booking
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
    
    // 8. Lấy bookings theo trạng thái -> Sắp xếp ID Tăng dần
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        // QUAN TRỌNG: ORDER BY id ASC (Bé -> Lớn)
        String sql = "SELECT * FROM Bookings WHERE status = ? ORDER BY id ASC";
        
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
    
    // 9. Đếm và Thống kê (Giữ nguyên)
    public int countBookingsByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Bookings WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getTotalRevenue() {
        String sql = "SELECT SUM(price) FROM Bookings WHERE status = N'Đã Xong'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}