package model;

public class Pet {
    public int id; 
    public String name, species; 
    public int age; 
    public String ownerUsername;
    
    public Pet(int id, String n, String s, int a, String oUser) {
        this.id = id; name = n; species = s; age = a; ownerUsername = oUser;
    }
    @Override public String toString() { return name + " (" + species + ")"; } 
}