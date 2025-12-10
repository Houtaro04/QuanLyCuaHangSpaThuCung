package model;

public class User {
    public String username, password, role, fullName;
    // THÊM: Các trường mới
    public String phone, email;

    // Cập nhật Constructor
    public User(String u, String p, String r, String name, String phone, String email) { 
        this.username = u; 
        this.password = p; 
        this.role = r; 
        this.fullName = name;
        this.phone = phone;
        this.email = email;
    }
}