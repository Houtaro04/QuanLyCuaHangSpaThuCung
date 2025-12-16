package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import model.*;
import repository.*;

public class DataManager {
    // Khởi tạo các Repository
    private static UserRepository userRepo = new UserRepository();
    private static PetRepository petRepo = new PetRepository();
    private static ServiceRepository serviceRepo = new ServiceRepository();
    private static BookingRepository bookingRepo = new BookingRepository();

    // ========== USER METHODS ==========
    public static User checkLogin(String u, String p) {
        return userRepo.checkLogin(u, p);
    }
    
    public static void registerCustomer(String u, String p, String name) {
        userRepo.registerCustomer(u, p, name);
    }
    
    // ========== CREATE METHODS ==========
    public static void addService(String name, double price) {
        serviceRepo.addService(name, price);
    }
    
    public static void addPet(String name, String species, int age, String ownerUser) {
        petRepo.addPet(name, species, age, ownerUser);
    }
    
    public static void addBooking(String user, String pName, String sName, double price, Date date, String time) {
        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String finalDateTime = dateStr + " " + time;
        bookingRepo.addBooking(user, pName, sName, price, finalDateTime);
    }

    // ========== DELETE METHODS ==========
    public static void deletePet(int id) {
        petRepo.deletePet(id);
    }

    public static void deleteService(int id) {
        serviceRepo.deleteService(id);
    }

    // ========== UPDATE METHODS ==========
    public static void updateBookingStatus(int id, String newStatus) {
        bookingRepo.updateBookingStatus(id, newStatus);
    }
    
    public static Booking getBookingById(int id) {
        return bookingRepo.getBookingById(id);
    }
    
    // ========== READ/LOAD METHODS ==========
    public static void loadPetsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        List<Pet> pets;
        
        if (role.equals("ADMIN")) {
            pets = petRepo.getAllPets();
        } else {
            pets = petRepo.getPetsByOwner(viewerUser);
        }
        
        int stt = 1;
        for (Pet p : pets) {
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

    public static void loadServicesToTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<Service> services = serviceRepo.getAllServices();
        
        for (Service s : services) {
            model.addRow(new Object[]{
                s.id, 
                s.name, 
                String.format("%,.0f đ", s.price)
            });
        }
    }

    public static void loadBookingsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        List<Booking> bookings;
        
        if (role.equals("ADMIN")) {
            bookings = bookingRepo.getAllBookings();
        } else {
            bookings = bookingRepo.getBookingsByCustomer(viewerUser);
        }
        
        for (Booking b : bookings) {
            model.addRow(new Object[]{
                b.id, 
                b.customerUser, 
                b.petName, 
                b.serviceName, 
                String.format("%,.0f đ", b.price), 
                b.date, 
                b.status
            });
        }
    }

    public static Vector<Pet> getPetsByOwner(String ownerUser) {
        List<Pet> list = petRepo.getPetsByOwner(ownerUser);
        return new Vector<>(list);
    }

    public static void searchPetsToTable(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        List<Pet> pets = petRepo.searchPets(keyword);
        
        for (Pet p : pets) {
            model.addRow(new Object[]{
                p.id, 
                p.name, 
                p.species, 
                p.age + " tuổi", 
                p.ownerUsername
            });
        }
    }
    
    // ========== UTILITY METHODS ==========
    
    // Lấy tất cả services (dạng Vector cho JComboBox)
    public static Vector<Service> mockServices = new Vector<>(); // Giữ cho tương thích
    
    // Method này sẽ load services từ database
    public static void refreshServices() {
        mockServices.clear();
        mockServices.addAll(serviceRepo.getAllServices());
    }
    
    // Gọi khi app khởi động để load services
    static {
        refreshServices();
    }
    
    // Thống kê
    public static int getTotalPets() {
        return petRepo.getAllPets().size();
    }
    
    public static int getTotalServices() {
        return serviceRepo.countServices();
    }
    
    public static int getPendingBookings() {
        return bookingRepo.countBookingsByStatus("Chờ duyệt");
    }
    
    public static int getCompletedBookings() {
        return bookingRepo.countBookingsByStatus("Đã Xong");
    }
    
    public static double getTotalRevenue() {
        return bookingRepo.getTotalRevenue();
    }
}