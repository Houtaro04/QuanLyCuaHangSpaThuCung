package model;

public class Booking {
    public int id; 
    public String customerUser, petName, serviceName, status, date; 
    public double price;
    
    public Booking(int id, String cUser, String pName, String sName, double pr, String st, String d) {
        this.id = id; customerUser = cUser; petName = pName; serviceName = sName; price = pr; status = st; date = d;
    }
}