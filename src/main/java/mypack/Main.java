package mypack;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    static Db dbObj = null;

    public static void main(String[] args) {
        try {
            dbObj = new Db();
            dbObj.initDb();
        } catch (Exception ex) {
            if (dbObj != null)  dbObj.closeConnection();
            return;
        }

        System.out.print("\n");
        addApartment(4, 100,  "1 Chreschatyk str, Kyiv", "Center", 100000);
        addApartment(3, 70,  "1 Bandery ave, Kyiv", "Pochayna", 80000);
        addApartment(1, 30.8f,  "1 Bazhana ave, Kyiv", "Osokorky", 50000);
        addApartment(1, 28.4f,  "1 Hlushkova ave, Kyiv", "Teremky", 45000);

        System.out.print("\n");
        showApartments(1);

        System.out.print("\n");
        updateApartmentPrice(1, 98000);
        updateApartmentPrice(10, 10000);

        System.out.print("\n");
        removeApartment(2);

        System.out.print("\n");
        showApartments(0);
    }

    private static void addApartment(int rooms, float square, String address, String district, int price) {
        Apartment apart = new Apartment(rooms, square, address, district, price);
        try {
            dbObj.createApartment(apart);
            System.out.println("Created : " + apart);
        } catch (SQLException ex) {
            System.out.println("Creation failed : " + apart + " : " + ex.getMessage());
        }
    }

    private static void updateApartmentPrice(int id, int price) {
        try {
            Apartment apart = dbObj.getApartmentById(id);
            if (apart == null) {
                System.out.println("Apartment with id " + id + " not found");
                return;
            }

            if (price != apart.getPrice()) {
                int origPrice = apart.getPrice();
                apart.setPrice(price);

                if (dbObj.updateApartment(apart)) {
                    System.out.println("Updated price = " + origPrice + " for " + apart);
                } else {
                    System.out.println("Price update failed for " + apart);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Apartment not found. id = : " + id + " : " + ex.getMessage());
        }
    }

    private static void removeApartment(int id) {
        try {
            Apartment apart = dbObj.getApartmentById(id);
            if (apart == null) {
                System.out.println("Apartment with id " + id + " not found");
                return;
            }
            dbObj.deleteApartment(id);
            System.out.println("Apartment removed " + apart);
        } catch (SQLException ex) {
            System.out.println("Apartment not found. id = : " + id + " : " + ex.getMessage());
        }
    }

    private static void showApartments(int rooms) {
        try {
            ArrayList<Apartment> apartList = dbObj.getApartments(rooms, 0);
            if (apartList.isEmpty())  System.out.println("No apartments found " + (rooms > 0 ? "with rooms = " + rooms : ""));
            else System.out.println("Found " + apartList.size() + " apartments " + (rooms > 0 ? "with rooms = " + rooms : ""));

            for (Apartment apart : apartList) {
                System.out.println(apart);
            }
        } catch (SQLException ex) {
            System.out.println("Selection failed : " + ex.getMessage());
        }

    }
}
