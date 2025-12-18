package model;

public class User {
    public String username, password, role, fullName;
    // THÊM 2 TRƯỜNG MỚI
    public String phone, email;

    // Cập nhật Constructor để nhận thêm phone và email
    public User(String u, String p, String r, String name, String phone, String email) { 
        this.username = u; 
        this.password = p; 
        this.role = r; 
        this.fullName = name;
        this.phone = phone;
        this.email = email;
    }
}