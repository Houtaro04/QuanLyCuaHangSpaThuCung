package test;

import database.DatabaseConnection;
import repository.*;
import model.*;
import java.util.List;
import java.io.File;

public class TestConnection {
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   KIỂM TRA KẾT NỐI DATABASE");
        System.out.println("=================================\n");
        
        // In ra thư mục hiện tại để debug
        System.out.println("Thư mục làm việc hiện tại: " + new File(".").getAbsolutePath());

        // 1. Test kết nối cơ bản
        System.out.println("1. Kiểm tra kết nối database...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("   ✓ Kết nối thành công!\n");
        } else {
            System.out.println("   ✗ Kết nối thất bại!");
            System.out.println("   HÃY KIỂM TRA LẠI: ");
            System.out.println("   1. File config.properties đã đúng IP chưa?");
            System.out.println("   2. SQL Server đã bật TCP/IP và cổng 1433 chưa?");
            System.out.println("   3. Tường lửa (Firewall) đã mở cổng 1433 chưa?");
            return; // Dừng luôn nếu không có mạng
        }
        
        // Các phần test sau giữ nguyên, chỉ chạy khi có kết nối
        try {
            // 2. Test UserRepository
            System.out.println("2. Kiểm tra UserRepository...");
            UserRepository userRepo = new UserRepository();
            List<User> users = userRepo.getAllUsers();
            System.out.println("   ✓ Tổng số users: " + users.size());

            // 3. Test PetRepository
            System.out.println("3. Kiểm tra PetRepository...");
            PetRepository petRepo = new PetRepository();
            List<Pet> pets = petRepo.getAllPets();
            System.out.println("   ✓ Tổng số thú cưng: " + pets.size());
            
            // ... (Các phần test khác giữ nguyên) ...
            
        } catch (Exception e) {
            System.out.println("✗ Có lỗi xảy ra trong quá trình truy vấn dữ liệu:");
            e.printStackTrace();
        }
        
        System.out.println("\n=================================");
        System.out.println("  HOÀN THÀNH KIỂM TRA!");
        System.out.println("=================================");
        
        DatabaseConnection.closeConnection();
    }
}