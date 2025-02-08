package mypack;

import java.util.ArrayList;

public class Apartment {
    private int id;
    private int rooms;
    private float square;
    private String address;
    private String district;
    private int price;

    private ArrayList<String> updated = new ArrayList<>();

    public Apartment(int rooms, float square, String address, String district, int price) {
        this.rooms = rooms;
        this.square = square;
        this.address = address;
        this.district = district;
        this.price = price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public void setSquare(float square) {
        this.square = square;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setPrice(int price) {
        if (this.price != price) {
            this.price = price;
            updated.add("price");
        }
    }

    public ArrayList<String> getUpdated() {
        return updated;
    }

    public int getId() {
        return id;
    }

    public int getRooms() {
        return rooms;
    }

    public float getSquare() {
        return square;
    }

    public String getAddress() {
        return address;
    }

    public String getDistrict() {
        return district;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format(
            "%d : %d room(s) in area of %.2f sq.m : %s, %s : price = %d",
            id, rooms, square, address, district, price
        );
    }
}
