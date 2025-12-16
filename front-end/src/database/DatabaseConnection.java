package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Thay đổi thông tin kết nối theo SQL Server của bạn
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=PetShopDB;encrypt=false;trustServerCertificate=true";
    private static final String USER = "sa"; // Thay username của bạn
    private static final String PASS = "123456"; // Thay password của bạn
    
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
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("✓ Kết nối database thành công!");
            }
        } catch (SQLException e) {
            System.err.println("✗ Lỗi kết nối database!");
            System.err.println("  Kiểm tra:");
            System.err.println("  1. SQL Server đang chạy");
            System.err.println("  2. Database PetShopDB đã được tạo");
            System.err.println("  3. Username/Password đúng");
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