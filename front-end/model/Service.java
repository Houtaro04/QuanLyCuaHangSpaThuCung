package model;

public class Service {
    public int id; 
    public String name; 
    public double price;
    
    public Service(int id, String name, double price) { 
        this.id = id; this.name = name; this.price = price; 
    }
    @Override public String toString() { return name + " - " + String.format("%,.0f đ", price); }
}