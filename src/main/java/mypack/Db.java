package mypack;

import java.sql.*;
import java.util.ArrayList;

public class Db {
    static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/mytest?serverTimezone=Europe/Kiev";
    static final String DB_USER = "mytest";
    static final String DB_PASSWORD = "mytest";
    static final String DB_TABNAME = "apartment";

    private Connection conn;

    public void initDb() throws SQLException {
        conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS " + DB_TABNAME);
            st.execute(
                "CREATE TABLE " + DB_TABNAME + " ("
                    + "id SERIAL PRIMARY KEY NOT NULL,"
                    + "rooms NUMERIC(4) NOT NULL,"
                    + "square NUMERIC (10, 2) NOT NULL,"
                    + "price NUMERIC(10) NOT NULL,"
                    + "address VARCHAR(255) NOT NULL,"
                    + "district VARCHAR(255) NOT NULL)"
            );
            System.out.println("Table " + DB_TABNAME + " created successfully");
        } finally {
            st.close();
        }
    }

    public ArrayList<Apartment> getApartments(int rooms, float price) throws SQLException {
        ArrayList<Apartment> apartList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        if (rooms > 0)  sb.append(" AND rooms = " + rooms);
        if (price > 0)  sb.append(" AND price <= " + price);
        if (sb.length() > 0) {
            sb.delete(0, 4).insert(0, " WHERE");
        }

        String sql = "SELECT * FROM " + DB_TABNAME + sb;

        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                apartList.add(getApartmentFromResultSet(rs));
            }

        } catch (SQLException ex) {
            System.out.println("can't select apartments");
        } finally {
            if (rs != null)   rs.close();
            if (pst != null)   pst.close();
        }

        return apartList;
    }

    public Apartment getApartmentById(int id) throws SQLException {
        Apartment apart = null;
        String sql = "SELECT * FROM " + DB_TABNAME + " WHERE id = ?";

        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();

            if (rs.next())
                apart = getApartmentFromResultSet(rs);
        } catch (SQLException ex) {
            System.out.println("can't select apartment with id " + id);
        } finally {
            if (rs != null)   rs.close();
            if (pst != null)   pst.close();
        }

        return apart;
    }

    public void createApartment(Apartment apart) throws SQLException {
        String sql = "INSERT INTO " + DB_TABNAME
            + "(rooms, square, price, address, district)"
            + " VALUES (?,?,?,?,?)";
        String[] generatedColumns = {"id"};
        PreparedStatement pst = conn.prepareStatement(sql, generatedColumns);
        try {
            pst.setInt(1, apart.getRooms());
            pst.setFloat(2, apart.getSquare());
            pst.setInt(3, apart.getPrice());
            pst.setString(4, apart.getAddress());
            pst.setString(5, apart.getDistrict());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                if (id > 0)     apart.setId(id);
            }
        } finally {
            pst.close();
        }
    }

    public boolean updateApartment(Apartment apart) throws SQLException {
        ArrayList<String> updates = apart.getUpdated();
        if (updates.isEmpty()) return false;

        StringBuilder sb = new StringBuilder();
        for (String property : updates) {
            sb.append(", " + property + " = ?");
        }
        sb.delete(0, 2).append(" WHERE id = ?");
        String sql = "UPDATE " + DB_TABNAME + " SET " + sb;

        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            for (int idx = 0; idx < updates.size(); idx++) {
                switch (updates.get(idx)) {
                    case "price":
                        pst.setInt(idx + 1, apart.getPrice());
                        break;
                    case "rooms":
                        pst.setInt(idx + 1, apart.getRooms());
                        break;
                    case "square":
                        pst.setFloat(idx + 1, apart.getSquare());
                        break;
                    case "address":
                        pst.setString(idx + 1, apart.getAddress());
                        break;
                    case "district":
                        pst.setString(idx + 1, apart.getDistrict());
                        break;
                }
                pst.setInt(updates.size() + 1, apart.getId());
                pst.executeUpdate();
            }

            return true;
        } catch (SQLException ex) {
            System.out.println("can't update apartment with id " + apart.getId());
        } finally {
            if (pst != null)   pst.close();
        }

        return false;
    }

    public void deleteApartment(int id) throws SQLException {
        String sql = "DELETE FROM " + DB_TABNAME + " WHERE id = ?";
        PreparedStatement pst = null;

        try {
            pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();
        } finally {
            if (pst != null)   pst.close();
        }
    }

    public void closeConnection() {
        try {
            if (conn != null) conn.close();
        } catch (Exception ex) {
            System.out.println("Error closing connection: " + ex);
        }
    }

    protected Apartment getApartmentFromResultSet(ResultSet rs) throws SQLException {
        Apartment apart = new Apartment(
                rs.getInt("rooms"),
                rs.getFloat("square"),
                rs.getString("address"),
                rs.getString("district"),
                rs.getInt("price")
        );
        apart.setId(rs.getInt("id"));

        return apart;
    }
}
