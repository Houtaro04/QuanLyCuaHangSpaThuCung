package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlserver://HOANGVIET;databaseName=PetShopDB;integratedSecurity=true;encrypt=false;trustServerCertificate=true;";

    private static Connection connection = null;

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

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("✓ Kết nối database thành công!");
            }
        } catch (SQLException e) {
            System.err.println("✗ Lỗi kết nối database!");
            System.err.println("  Kiểm tra:");
            System.err.println("  1. Tên server HOUTARO\\SQLEXPRESS đúng chưa?");
            System.err.println("  2. Đã có file mssql-jdbc_auth.dll chưa?");
            System.err.println("  3. Service 'SQL Server Browser' đã bật chưa?");
            e.printStackTrace();
        }
        return connection;
    }

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

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}