package model;

public class User {
    public String username, password, role, fullName;
    public User(String u, String p, String r, String name) { 
        username = u; password = p; role = r; fullName = name;
    }
}