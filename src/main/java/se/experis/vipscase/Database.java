package se.experis.vipscase;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
public class Database {


    public Database(){

    }

    /**
     *
     * @param conn, The connection provided from connectToDb()
     * @param query, String of queries
     * @return Returns an ArrayList of objects, containing the response from the database
     */

    public ArrayList<Object[]> retrieveQuery(Connection conn, String query) {
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
        closeConnect(conn);
        return results;

    }

    /**
     *  @param conn, The connection provided from connectToDb()
     * @param cname, User's name
     * @param cpass, -||- password
     * @param mail, -||- mail
     * @param lname, -||- lastname
     * @param sname, -||- street
     * @param pcode, -||- postcode
     * @param city, -||- city
     * @param byear, -||- birthyear-month-day
     * @param byear
     */
    public void insertQuery(Connection conn, String cname, String cpass, String mail, String lname, String sname, int pcode, String city, String byear) {
        cpass = hashStuff(cpass);

        String insertQ = "INSERT INTO customers (customername, customerpass, email, lastname, streetname, postcode, city, birthyear) VALUES (" +
                "'"+cname + "', '"+ cpass +"', '" + mail + "', '" + lname +"', '" +
                    sname + "', '"+ pcode +"', '" + city + "', '" + byear + "')";

        System.out.println(insertQ);


        try {
            PreparedStatement pst = conn.prepareStatement(insertQ);
            pst.execute();
            closeConnect(conn);

            } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    public int addOrder(Connection conn, int customer_id) {
        String insertQ = "INSERT INTO orders (customer_id) VALUES ('"+ customer_id + "')";
        System.out.println(insertQ);
        int order_id = 0;
        try {
            PreparedStatement pst = conn.prepareStatement(insertQ, Statement.RETURN_GENERATED_KEYS);
            pst.execute();

            ResultSet getId = pst.getGeneratedKeys();
            if(getId.next()){
                System.out.println("the newly inserted id: " + getId.getInt(1));
                order_id = getId.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order_id;
    }

    public void addOrderDetails(Connection conn, int order_id, ArrayList product_id, String status) {

        try {
            for (Object prod_id : product_id) {
                String insertQ = "INSERT INTO order_details (order_id, product_id, status) " +
                        "VALUES (" + "'"+ order_id + "', '"+ prod_id +"', '" + status + "')";
                PreparedStatement pst = conn.prepareStatement(insertQ);
                pst.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
  
    public Connection connectToDb() {
        System.out.println("Connecting..");
        String url = "jdbc:postgresql://localhost:5432/vipstest";
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

    public void closeConnect(Connection conn) {
        try {
            conn.close();
            System.out.println("Connection closed..");
        } catch (SQLException e) {
            System.out.println("Connection not closed..");
            e.printStackTrace();
        }
    }

    public String hashStuff(String pass)  {
        String hashtext = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(pass.getBytes());

            BigInteger bigInt = new BigInteger(1, messageDigest);

            hashtext = bigInt.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashtext;

    }



}
