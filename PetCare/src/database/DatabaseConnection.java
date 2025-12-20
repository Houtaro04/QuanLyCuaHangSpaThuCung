package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    // --- CÁC DÒNG CẦN SỬA ---
    // 1. Sửa DB_URL:
    //    - Thay localhost:1433 bằng HOUTARO\\SQLEXPRESS (Lưu ý: Phải dùng 2 dấu gạch chéo \\ để Java hiểu là 1 ký tự \)
    //    - Thêm integratedSecurity=true để dùng tài khoản Windows
    private static final String DB_URL = "jdbc:sqlserver://HOUTARO\\SQLEXPRESS;databaseName=PetSpaDB;integratedSecurity=true;encrypt=false;trustServerCertificate=true;";

    private static Connection connection = null;

    // Load JDBC Driver (Chỉ chạy 1 lần)
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Không tìm thấy driver SQL Server! Hãy kiểm tra thư mục libs.");
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties prop = new Properties();
                String configPath = null;
                boolean isLoaded = false;

                // --- LOGIC TÌM FILE CONFIG THÔNG MINH ---
                // Danh sách các chỗ có thể chứa file config
                String[] possiblePaths = {
                    "config.properties",                        // Ngay cạnh file chạy (Priority 1)
                    "src/config.properties",                    // Trong folder src (khi chạy VS Code)
                    "PetCare/config.properties",                // Trong folder con
                    "../config.properties"                      // Ở thư mục cha
                };

                for (String path : possiblePaths) {
                    File f = new File(path);
                    if (f.exists() && !f.isDirectory()) {
                        try (InputStream input = new FileInputStream(f)) {
                            prop.load(input);
                            configPath = f.getAbsolutePath();
                            isLoaded = true;
                            System.out.println("✓ Đã tìm thấy file cấu hình tại: " + configPath);
                            break; // Tìm thấy rồi thì dừng
                        } catch (IOException e) {
                            // Bỏ qua, thử đường dẫn tiếp theo
                        }
                    }
                }

                if (!isLoaded) {
                    System.err.println("⚠ LỖI CỰC KỲ NGHIÊM TRỌNG: Không tìm thấy file 'config.properties' ở đâu cả!");
                    System.err.println("   Đã tìm thử tại: " + String.join(", ", possiblePaths));
                    System.err.println("   Hãy copy file config.properties để cạnh file đang chạy!");
                    return null;
                }

                // --- KẾT NỐI ---
                String ip = prop.getProperty("db.ip");
                String port = prop.getProperty("db.port");
                String dbName = prop.getProperty("db.name");
                String user = prop.getProperty("db.user");
                String pass = prop.getProperty("db.password");

                // Check dữ liệu null
                if (ip == null || user == null || pass == null) {
                    System.err.println("⚠ Lỗi: File config bị thiếu thông tin (ip, user hoặc password).");
                    return null;
                }

                String connectionUrl = "jdbc:sqlserver://" + ip + ":" + port + 
                                       ";databaseName=" + dbName + 
                                       ";encrypt=true;trustServerCertificate=true;" + 
                                       "loginTimeout=10;"; // Timeout 10s để không đợi lâu nếu sai IP

                System.out.println("... Đang kết nối tới: " + ip + ":" + port);
                connection = DriverManager.getConnection(connectionUrl, user, pass);
                System.out.println("✓ Kết nối database thành công!");
            }
        } catch (SQLException e) {
            System.err.println("✗ Kết nối thất bại! Lỗi SQL:");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
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