package se.experis.vipscase;


import java.sql.*;
import java.util.ArrayList;

public class Database {


    public Database(){

    }

    /**
     *
     * @param conn, The connection provided from connectToDb()
     * @param query, String of queries
     * @return Returns an ArrayList of objects, containing the response from the database
     */

    ArrayList<Object[]> retrieveQuery(Connection conn, String query) {
        ArrayList<Object[]> results = new ArrayList<Object[]>();

        
        PreparedStatement pst = null;
        boolean isResult = false;
        boolean gotResult = false;
        try {
            System.out.println();
            pst = conn.prepareStatement(query);
            isResult = pst.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            do {
                assert pst != null;
                try (ResultSet rs = pst.getResultSet()) {

                    while (rs.next()) {
                        gotResult = true;
                        int columns = rs.getMetaData().getColumnCount();
                        Object[] arr = new Object[columns];
                        for (int i = 0; i < columns; i++) {
                            arr[i] = rs.getObject(i+1);
                        }
                        results.add(arr);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if(gotResult){
                        try {
                            isResult = pst.getMoreResults();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }


            } while (isResult);
        }
        return results;

    }

    /**
     *
     * @param conn, The connection provided from connectToDb()
     * @param cname, User's name
     * @param cpass, -||- password
     * @param mail, -||- mail
     * @param lname, -||- lastname
     * @param sname, -||- street
     * @param pcode, -||- postcode
     * @param city, -||- city
     * @param byear, -||- birthyear-month-day
     */
    void insertQuery(Connection conn, String cname, String cpass, String mail, String lname, String sname, int pcode, String city, int byear) {
        String insertQ = "INSERT INTO customers (customername, customerpass, email, lastname, streetname, postcode, city, birthyear) VALUES (" +
                "'"+cname + "', '"+ cpass +"', '" + mail + "', '" + lname +"', '" +
                    sname + "', '"+ pcode +"', '" + city + "', '" + byear + "')";


        try {
            PreparedStatement pst = conn.prepareStatement(insertQ);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    Connection connectToDb() {
        System.out.println("Connecting..");
        String url = "jdbc:postgresql://localhost:5432/vipscase";
        String user = "postgres";
        String pass = "root";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, pass);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (conn != null) {
            System.out.println("Connected!");
        } else {
            System.out.println("Connection failed..");
        }
        return conn;

    }


}
