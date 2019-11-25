package se.experis.vipscase;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;


public class Database {
    private boolean live = true;

    public Database(){

    }

    /**
     * Performs a query towards the database which expects a response with rows from the db
     * @param conn, The connection provided from connectToDb()
     * @param pst, The prepared query statement from loginUser()
     * @return Returns an ArrayList of objects, containing the response from the database
     */

    public ArrayList<Object[]> retrieveQuery(Connection conn, PreparedStatement pst) {

        ArrayList<Object[]> results = new ArrayList<Object[]>();

        boolean isResult = false;
        boolean gotResult = false;
        try {
            isResult = pst.execute();
        } catch (Exception e) {
            isResult = false;
            e.getMessage();
        } finally {
            while (isResult) {

                try {

                    if (pst.isClosed())
                    {
                        //Closed
                    } else {
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
                            e.getMessage();
                            break;
                        } finally {
                            if(gotResult){
                                try {
                                    isResult = pst.getMoreResults();
                                } catch (SQLException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }

                } catch (SQLException e) {
                    e.getMessage();
                }
            }
        }
        closeConnect(conn);
        return results;
    }

    /**
     * Inserts into the database
     * @param conn, The connection provided from connectToDb()
     * @param pst, The prepared statement from postOrder()
     * @return usrId, The users id
     */
    public int insertQuery(Connection conn, PreparedStatement pst) {

        int usrId = 0;
        try {
            pst.execute();
            ResultSet getUsrId = pst.getGeneratedKeys();
            if (getUsrId.next()) {
                usrId = getUsrId.getInt(1);
            }
            closeConnect(conn);
            } catch (SQLException e) {
            e.getMessage();
        }
        return usrId;
    }

    /**
     * Retrieves ID from a given user
     * @param conn, The connection provided from connectToDb()
     * @param pst, The prepared statement from postOrder()
     * @return order_id, The orders id
     */
    public int addOrder(Connection conn, PreparedStatement pst) {

        int order_id = 0;
        try {
            pst.execute();
            ResultSet getId = pst.getGeneratedKeys();
            if(getId.next()){
                order_id = getId.getInt(1);
            }

        } catch (SQLException e) {
            e.getMessage();
        }
        return order_id;
    }

    /**
     * Adds order details for a given user
     * @param conn, the connection
     * @param pst, the prepared statement
     */

    public void addOrderDetails(Connection conn, PreparedStatement pst) {

        try {
                pst.execute();
            } catch (SQLException ex) {
            ex.getMessage();
        }
        closeConnect(conn);
    }
  
    public Connection connectToDb() {
        System.out.println("Connecting..");
        String url, user, pass;
        if(live) {
            url = "jdbc:postgresql://ec2-54-228-252-67.eu-west-1.compute.amazonaws.com:5432/d8rvv37evbnavq";
            user = "ypmhwqfqxnnrvl";
            pass = "62d2cf241684da311d7fd46492f20218f6bfe43a4c4c473d20870e76d48bce8b";
        } else {
            url = "jdbc:postgresql://localhost:5432/vipscase";
            user = "postgres";
            pass = "root";
        }

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

    /**
     * Closes the open connection
     * @param conn, the connection
     */
    public void closeConnect(Connection conn) {
        try {
            conn.close();
            System.out.println("Connection closed..");
        } catch (SQLException e) {
            System.out.println("Connection not closed..");
            e.printStackTrace();
        }
    }

    /**
     * Hashes a string, pass, with sha-512
     * @param pass, the string to hash
     * @return hashtext, returns the hashed string
     */

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
