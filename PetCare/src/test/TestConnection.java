package test;

import database.DatabaseConnection;
import repository.*;
import model.*;
import java.util.List;

public class TestConnection {
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   KIỂM TRA KẾT NỐI DATABASE");
        System.out.println("=================================\n");
        
        // 1. Test kết nối cơ bản
        System.out.println("1. Kiểm tra kết nối database...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("   ✓ Kết nối thành công!\n");
        } else {
            System.out.println("   ✗ Kết nối thất bại!");
            System.out.println("   Vui lòng kiểm tra:");
            System.out.println("   - SQL Server đang chạy");
            System.out.println("   - Connection string đúng");
            System.out.println("   - Username/Password đúng");
            return;
        }
        
        // 2. Test UserRepository
        System.out.println("2. Kiểm tra UserRepository...");
        UserRepository userRepo = new UserRepository();
        List<User> users = userRepo.getAllUsers();
        System.out.println("   ✓ Tổng số users: " + users.size());
        for (User u : users) {
            System.out.println("     - " + u.username + " (" + u.role + ") - " + u.fullName);
        }
        System.out.println();
        
        // 3. Test PetRepository
        System.out.println("3. Kiểm tra PetRepository...");
        PetRepository petRepo = new PetRepository();
        List<Pet> pets = petRepo.getAllPets();
        System.out.println("   ✓ Tổng số thú cưng: " + pets.size());
        for (Pet p : pets) {
            System.out.println("     - " + p.name + " (" + p.species + ", " + p.age + " tuổi) - Chủ: " + p.ownerUsername);
        }
        System.out.println();
        
        // 4. Test ServiceRepository
        System.out.println("4. Kiểm tra ServiceRepository...");
        ServiceRepository serviceRepo = new ServiceRepository();
        List<Service> services = serviceRepo.getAllServices();
        System.out.println("   ✓ Tổng số dịch vụ: " + services.size());
        for (Service s : services) {
            System.out.println("     - " + s.name + " - " + String.format("%,.0f đ", s.price));
        }
        System.out.println();
        
        // 5. Test BookingRepository
        System.out.println("5. Kiểm tra BookingRepository...");
        BookingRepository bookingRepo = new BookingRepository();
        List<Booking> bookings = bookingRepo.getAllBookings();
        System.out.println("   ✓ Tổng số bookings: " + bookings.size());
        
        int pending = bookingRepo.countBookingsByStatus("Chờ duyệt");
        int approved = bookingRepo.countBookingsByStatus("Đã Duyệt");
        int completed = bookingRepo.countBookingsByStatus("Đã Xong");
        
        System.out.println("     - Chờ duyệt: " + pending);
        System.out.println("     - Đã duyệt: " + approved);
        System.out.println("     - Đã xong: " + completed);
        System.out.println();
        
        // 6. Test Login
        System.out.println("6. Test chức năng đăng nhập...");
        User admin = userRepo.checkLogin("admin", "123");
        if (admin != null) {
            System.out.println("   ✓ Đăng nhập admin thành công!");
            System.out.println("     - Username: " + admin.username);
            System.out.println("     - Role: " + admin.role);
            System.out.println("     - Full Name: " + admin.fullName);
        } else {
            System.out.println("   ✗ Đăng nhập admin thất bại!");
        }
        System.out.println();
        
        User customer = userRepo.checkLogin("khach", "123");
        if (customer != null) {
            System.out.println("   ✓ Đăng nhập customer thành công!");
            System.out.println("     - Username: " + customer.username);
            System.out.println("     - Role: " + customer.role);
            System.out.println("     - Full Name: " + customer.fullName);
        } else {
            System.out.println("   ✗ Đăng nhập customer thất bại!");
        }
        System.out.println();
        
        // 7. Thống kê
        System.out.println("7. Thống kê tổng quan...");
        double revenue = bookingRepo.getTotalRevenue();
        System.out.println("   ✓ Tổng doanh thu: " + String.format("%,.0f VND", revenue));
        System.out.println("   ✓ Tổng pets: " + petRepo.getAllPets().size());
        System.out.println("   ✓ Tổng services: " + serviceRepo.countServices());
        System.out.println("   ✓ Tổng bookings: " + bookingRepo.getAllBookings().size());
        System.out.println();
        
        // 8. Test CRUD operations
        System.out.println("8. Test CRUD operations...");
        
        // Test thêm pet
        System.out.println("   - Test thêm pet...");
        boolean addResult = petRepo.addPet("TestPet", "Test Species", 1, "khach");
        if (addResult) {
            System.out.println("     ✓ Thêm pet thành công!");
                
            // Test search
            List<Pet> searchResult = petRepo.searchPets("TestPet");
            if (!searchResult.isEmpty()) {
                System.out.println("     ✓ Tìm kiếm pet thành công!");
                Pet testPet = searchResult.get(0);
                
                // Test delete
                boolean deleteResult = petRepo.deletePet(testPet.id);
                if (deleteResult) {
                    System.out.println("     ✓ Xóa pet thành công!");
                } else {
                    System.out.println("     ✗ Xóa pet thất bại!");
                }
            }
        } else {
            System.out.println("     ✗ Thêm pet thất bại!");
        }
        System.out.println();
        
        System.out.println("=================================");
        System.out.println("  HOÀN THÀNH KIỂM TRA!");
        System.out.println("=================================");
        
        // Đóng kết nối
        DatabaseConnection.closeConnection();
    }
}