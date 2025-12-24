package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import model.*;
import repository.*;

public class DataManager {
    private static UserRepository userRepo = new UserRepository();
    private static PetRepository petRepo = new PetRepository();
    private static ServiceRepository serviceRepo = new ServiceRepository();
    private static BookingRepository bookingRepo = new BookingRepository();

    public static Vector<Booking> mockBookings = new Vector<>();
    public static Vector<Service> mockServices = new Vector<>();

    static {
        refreshCache();
    }

    public static void refreshCache() {
        mockBookings.clear();
        mockBookings.addAll(bookingRepo.getAllBookings());

        mockServices.clear();
        mockServices.addAll(serviceRepo.getAllServices());
    }

    public static User checkLogin(String u, String p) {
        return userRepo.checkLogin(u, p);
    }

    public static void registerCustomer(String u, String p, String name, String phone, String email) {
        userRepo.registerCustomer(u, p, name, phone, email);
    }

    public static void updateCustomerInfo(String username, String fullName, String phone, String email) {
        userRepo.updateCustomerInfo(username, fullName, phone, email);
    }

    public static void loadCustomersToTable(DefaultTableModel model) {
        model.setRowCount(0);
        List<User> users = userRepo.getAllUsers();
        for (User u : users) {
            if ("CUSTOMER".equalsIgnoreCase(u.role)) {
                model.addRow(new Object[] { u.username, u.fullName, u.phone, u.email });
            }
        }
    }

    public static void addService(String name, double price) {
        serviceRepo.addService(name, price);
        refreshCache();
    }

    public static void updateService(int id, String name, double price) {
        serviceRepo.updateService(id, name, price);
        refreshCache();
    }

    public static void deleteService(int id) {
        serviceRepo.deleteService(id);
        refreshCache();
    }

    public static void loadServicesToTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Service s : mockServices) {
            model.addRow(new Object[] { s.id, s.name, String.format("%,.0f đ", s.price) });
        }
    }

    public static void addPet(String name, String species, int age, String ownerUser) {
        petRepo.addPet(name, species, age, ownerUser);
    }

    public static void deletePet(int id) {
        petRepo.deletePet(id);
    }

    public static void loadPetsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        List<Pet> pets;
        if (role.equals("ADMIN"))
            pets = petRepo.getAllPets();
        else
            pets = petRepo.getPetsByOwner(viewerUser);

        int stt = 1;
        for (Pet p : pets) {
            model.addRow(new Object[] { stt++, p.name, p.species, p.age + " tuổi", p.ownerUsername, p.id });
        }
    }

    public static Vector<Pet> getPetsByOwner(String ownerUser) {
        return new Vector<>(petRepo.getPetsByOwner(ownerUser));
    }

    public static void searchPetsToTable(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        for (Pet p : petRepo.searchPets(keyword)) {
            model.addRow(new Object[] { p.id, p.name, p.species, p.age + " tuổi", p.ownerUsername });
        }
    }

    public static void addBooking(String user, String pName, String sName, double price, Date date, String time) {
        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
        String finalDateTime = dateStr + " " + time;
        bookingRepo.addBooking(user, pName, sName, price, finalDateTime);
        refreshCache();
    }

    public static void updateBookingStatus(int id, String newStatus) {
        bookingRepo.updateBookingStatus(id, newStatus);
        refreshCache();
    }

    public static Booking getBookingById(int id) {
        return bookingRepo.getBookingById(id);
    }

    public static void loadBookingsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        List<Booking> bookings;
        if (role.equals("ADMIN"))
            bookings = bookingRepo.getAllBookings();
        else
            bookings = bookingRepo.getBookingsByCustomer(viewerUser);

        for (Booking b : bookings) {
            model.addRow(new Object[] {
                    b.id, b.customerUser, b.petName, b.serviceName,
                    String.format("%,.0f đ", b.price), b.date, b.status
            });
        }
    }

    public static boolean updatePassword(String username, String newPassword) {
        return userRepo.updatePassword(username, newPassword);
    }

    public static boolean verifyUserEmail(String username, String email) {
        return userRepo.verifyUserEmail(username, email);
    }
}