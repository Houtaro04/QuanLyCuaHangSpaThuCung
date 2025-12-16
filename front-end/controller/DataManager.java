package controller;

import model.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class DataManager {
    public static Vector<User> mockUsers = new Vector<>();
    public static Vector<Pet> mockPets = new Vector<>();
    public static Vector<Service> mockServices = new Vector<>();
    public static Vector<Booking> mockBookings = new Vector<>();

    private static int petIdCounter = 0, serviceIdCounter = 0, bookingIdCounter = 0;

    static {
        mockUsers.add(new User("admin", "123", "ADMIN", "Quản Lý Cửa Hàng"));
        mockUsers.add(new User("khach", "123", "CUSTOMER", "Nguyễn Văn Khách"));

        addService("Tắm gội thơm tho", 150000);
        addService("Cắt tỉa tạo kiểu", 250000);
        addService("Combo VIP (Tắm+Cắt)", 350000);
        addService("Tiêm phòng", 100000);
        
        addPet("Mimi", "Mèo Anh", 2, "khach");
        addPet("LuLu", "Poodle", 4, "khach");
    }

    public static User checkLogin(String u, String p) {
        for (User user : mockUsers) if (user.username.equals(u) && user.password.equals(p)) return user;
        return null;
    }
    public static void registerCustomer(String u, String p, String name) { mockUsers.add(new User(u, p, "CUSTOMER", name)); }
    
    // --- CREATE ---
    public static void addService(String name, double price) { mockServices.add(new Service(++serviceIdCounter, name, price)); }
    public static void addPet(String name, String species, int age, String ownerUser) { mockPets.add(new Pet(++petIdCounter, name, species, age, ownerUser)); }
    public static void addBooking(String user, String pName, String sName, double price, Date date, String time) {
        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String finalDateTime = dateStr + " " + time;
        mockBookings.add(new Booking(++bookingIdCounter, user, pName, sName, price, "Chờ duyệt", finalDateTime));
    }

    // --- DELETE/UPDATE ---
    public static void deletePet(int id) {
        mockPets.removeIf(p -> p.id == id);
    }

    public static void deleteService(int id) {
        mockServices.removeIf(s -> s.id == id);
    }

    public static void updateBookingStatus(int id, String newStatus) {
        for(Booking b : mockBookings) if(b.id == id) { b.status = newStatus; break; }
    }
    
    public static Booking getBookingById(int id) {
        for(Booking b : mockBookings) if(b.id == id) return b;
        return null;
    }
    
    // --- READ/LOAD ---
    public static void loadPetsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        int stt = 1; 
        for (Pet p : mockPets) {
            if (role.equals("ADMIN") || p.ownerUsername.equals(viewerUser)) {
                model.addRow(new Object[]{
                    stt++,           // STT
                    p.name,          // Tên
                    p.species,       // Loài
                    p.age + " tuổi", // Tuổi
                    p.ownerUsername, // Chủ
                    p.id             // ID ẨN
                });
            }
        }
    }

    public static void loadServicesToTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Service s : mockServices) model.addRow(new Object[]{s.id, s.name, String.format("%,.0f đ", s.price)});
    }

    public static void loadBookingsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        for (Booking b : mockBookings) if (role.equals("ADMIN") || b.customerUser.equals(viewerUser)) 
            model.addRow(new Object[]{b.id, b.customerUser, b.petName, b.serviceName, String.format("%,.0f đ", b.price), b.date, b.status});
    }

    public static Vector<Pet> getPetsByOwner(String ownerUser) {
        Vector<Pet> list = new Vector<>();
        for(Pet p : mockPets) if(p.ownerUsername.equals(ownerUser)) list.add(p);
        return list;
    }

    public static void searchPetsToTable(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        String key = keyword.toLowerCase();
        for (Pet p : mockPets) {
            if (p.name.toLowerCase().contains(key) || 
                p.species.toLowerCase().contains(key) || 
                p.ownerUsername.toLowerCase().contains(key)) {
                model.addRow(new Object[]{p.id, p.name, p.species, p.age + " tuổi", p.ownerUsername});
            }
        }
    }
}