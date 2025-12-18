package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // --- CÁC DÒNG CẦN SỬA ---
    // 1. Sửa DB_URL:
    //    - Thay localhost:1433 bằng HOUTARO\\SQLEXPRESS (Lưu ý: Phải dùng 2 dấu gạch chéo \\ để Java hiểu là 1 ký tự \)
    //    - Thêm integratedSecurity=true để dùng tài khoản Windows
    private static final String DB_URL = "jdbc:sqlserver://HOUTARO\\SQLEXPRESS;databaseName=PetShopDB;integratedSecurity=true;encrypt=false;trustServerCertificate=true;";

    private static Connection connection = null;
    
    // Load JDBC Driver
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("✓ JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ SQL Server JDBC Driver không tìm thấy!");
            System.err.println("  Vui lòng thêm mssql-jdbc.jar vào classpath");
            e.printStackTrace();
        }
    }
    
    // Lấy kết nối
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Khi dùng integratedSecurity, DriverManager sẽ tự tìm file .dll để xác thực với Windows
                connection = DriverManager.getConnection(DB_URL); 
                // Lưu ý: Có thể bỏ USER, PASS ở hàm getConnection khi dùng URL trên, 
                // hoặc để nguyên DriverManager.getConnection(DB_URL, USER, PASS) cũng không sao vì USER/PASS rỗng.
                System.out.println("✓ Kết nối database thành công!");
            }
        } catch (SQLException e) {
            System.err.println("✗ Lỗi kết nối database!");
            System.err.println("  Kiểm tra:");
            System.err.println("  1. Tên server HOUTARO\\SQLEXPRESS đúng chưa?");
            System.err.println("  2. Đã có file mssql-jdbc_auth.dll chưa?"); // Quan trọng nhất
            System.err.println("  3. Service 'SQL Server Browser' đã bật chưa?");
            e.printStackTrace();
        }
        return connection;
    }
    
    // Đóng kết nối
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Đã đóng kết nối database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Test kết nối
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}