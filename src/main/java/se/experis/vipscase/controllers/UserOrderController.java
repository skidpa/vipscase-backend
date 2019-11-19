package se.experis.vipscase.controllers;

import org.postgresql.jdbc.PreferQueryMode;
import org.springframework.web.bind.annotation.*;

import se.experis.vipscase.model.Order;
import se.experis.vipscase.model.User;
import se.experis.vipscase.model.Product;
import se.experis.vipscase.Database;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@CrossOrigin(
        allowCredentials = "true",
        allowedHeaders = "*",
        origins = {
                "http://localhost:3000",
                "https://pa-vips-front.herokuapp.com",
                "http://pa-vips-front.herokuapp.com"
        },
        maxAge = 3600
)
//@CrossOrigin(maxAge = 3600)
@RestController
public class UserOrderController {
    UserOrderController(){

    }



    /**
     * Endpoint which handles the insertion of orders into the database. Returns 201 (Created) as success
     * and 400 (Bad request) if errors occured.
     * @param response, to send back status to Client
     * @param order, to create an order and insert into the database
     *
     */


    @PostMapping("/order")
    @ResponseBody
    public void postOrder(HttpServletResponse response, @RequestBody Order order) {
        System.out.println("order: start");

        System.out.println(order.toString());
        Database db = new Database();
        Connection conn = db.connectToDb();
        //TODO: add atomic operations?
        String insertQ = "INSERT INTO orders (customer_id) VALUES (?)";
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(insertQ, Statement.RETURN_GENERATED_KEYS);
            pst.setObject(1, order.getCustomer_id());
            int order_id = db.addOrder(conn, pst);

            Database db2 = new Database();
            PreparedStatement pst2 = null;
            Connection conn2 = null;
            String insertQ2;

            for (int i = 0; i < order.getProduct_id().size(); i++) {
                conn2 = db2.connectToDb();
                insertQ2 = "INSERT INTO order_details (order_id, product_id, status) " +
                        "VALUES (?,?,?)";

                try {
                    pst2 = conn.prepareStatement(insertQ2);
                    pst2.setInt(1, order_id);
                    pst2.setInt(2,Integer.parseInt(order.getProduct_id().get(i).toString())); //????
                    pst2.setString(3, order.getStatus());
                    db.addOrderDetails(conn2, pst2);

                } catch (SQLException d) {
                    d.printStackTrace();
                }
            }
            response.setStatus(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(400);
        }
        System.out.println("order: done");
    }

    /**
     * Endpoint which handles the insertion of customers into the database. Returns 201 (Created) as success
     * and 400 (Bad request) if errors occured.
     * @param response, to send back status to Client
     * @param user, to create a user and insert into the database
     */
    @PostMapping("/register/user")
    @ResponseBody
    public int registerUser(HttpServletResponse response,@RequestBody User user) {
        System.out.println("register user: start");
        Database db = new Database();
        Connection conn = db.connectToDb();
        String cpass = db.hashStuff(user.getPassword());

        String insertQ = "INSERT INTO " +
                "customers (customername, customerpass, email, lastname, streetname, postcode, city, birthyear) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        int usrId = 0;
        try {
            PreparedStatement pst = conn.prepareStatement(insertQ, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getName());
            pst.setString(2, cpass);
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getLastname());
            pst.setString(5, user.getStreet());
            pst.setInt(6, user.getPostcode()); //?
            pst.setString(7, user.getCity());
            pst.setString(8, user.getBirthdate());
            usrId = db.insertQuery(conn, pst);
            response.setStatus(201);

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(400);

        }
        //Simon
        System.out.println("register user: done");
        return usrId;
    }


    /**
     * Endpoint which handles the insertion of products into the database. Returns 201 (Created) as success
     * and 400 (Bad request) if errors occured.
     * @param response, to send back status to Client
     * @param product, to create a product and insert into the database
     */

    @PostMapping("/addproduct")
    @ResponseBody
    public void addProduct(HttpServletResponse response, @RequestBody Product product) {
        Database db = new Database();
        Connection conn = db.connectToDb();
        String sql = "INSERT INTO products (productname, productdescription, instock, price) "
                + " VALUES (?,?,?,?)";

        ArrayList<Object[]> results = new ArrayList<>();
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, product.getProductname());
            pst.setString(2, product.getProductdescription());
            pst.setInt(3, product.getInstock());
            pst.setInt(4, product.getPrice());
            db.insertQuery(conn, pst);
            response.setStatus(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(400);
        }
    }


    /**
     * Endpoint which handles login for a user. Retrieves hashed password from database if email can be found
     * and compares the stored hash with a new hash for a given password. If the hashes match, user ID is retrieved
     * and a session is created, as well as a cookie containing that user's ID.
     * TODO: Change the cookie to only contain boolean values for logged in / logged out.
     *
     * @param response, to send back status to Client
     * @param request, to create a new session
     * @param user, to create a customer object which calls getEmail() and getPassword()
     */


    //@CrossOrigin(origins = "http://localhost:3000")

    @PostMapping("/login")
    @ResponseBody
    public void loginUser(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {

        Database db = new Database();
        ArrayList<Object[]> userCred = new ArrayList<>();
        String newHashed = "", dbPass = "";
        System.out.println("User id:");
        System.out.println(user.getId());
        System.out.println(user.toString());
        Connection conn = db.connectToDb();
        String sql = "SELECT customerpass FROM customers WHERE email= ?";
        try {

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user.getEmail());
            userCred = db.retrieveQuery(conn, pst);

            dbPass = Arrays.toString(userCred.get(0));
            dbPass = dbPass.substring(1, dbPass.length() -1);

            newHashed = db.hashStuff(user.getPassword());
            System.out.println("Try: newhashed: " + newHashed);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Connection conn2 = db.connectToDb();
        if(newHashed.equals(dbPass)) {
            System.out.println("Lösenord stämmer, kollar ID i password checken");
            String userSql = "SELECT id FROM customers WHERE email= ?";
            try {
                PreparedStatement pst2 = conn2.prepareStatement(userSql);
                pst2.setString(1, user.getEmail());
                userCred = db.retrieveQuery(conn2, pst2);

                String usrid = Arrays.toString(userCred.get(0));
                usrid = usrid.substring(1, usrid.length() -1);
                System.out.println("User id: " + usrid);


                //Sessions
                HttpSession sess = request.getSession();

                    System.out.println("New session created for user with static ID: " + usrid);
                    System.out.println("Session ID: " + sess.getId());
                    sess.setAttribute("Snus", usrid);
                    sess.setMaxInactiveInterval(15*60);

                    String uid = usrid.toString();
                    Cookie loginCookie = new Cookie("check", uid);
                    loginCookie.setHttpOnly(false);
                    loginCookie.setMaxAge(30*60*120);


                    //loginCookie.setDomain("localhost");
                    //loginCookie.setDomain("pa-vips-back.herokuapp.com");


                    response.addCookie(loginCookie);
                    response.setStatus(200);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Hacker be Gone!");
            response.setStatus(400);
        }
    }



    @GetMapping("/loggedin")
    public int isLoggedIn(HttpServletRequest req, HttpServletResponse resp){
        Object sess = null;
        int returnInt = 0;
        HttpSession retrievedSession = req.getSession();
        sess = retrievedSession.getAttribute("Snus");
        System.out.println("session stuff: " + sess.toString());

        if (sess != null) {
            //returnInt = 1;
            returnInt = Integer.parseInt(sess.toString());
            System.out.println("Active session found");

        } else {
            System.out.println("No session found, are you logged in?");
        }

        return returnInt;
    }

    /**
     *  Endpoint which retrieves every product from the database.
     * @param response, to send back status to Client
     * @return ArrayList<Object[]>, ArrayList of produts
     */
    //Returns every product from database
    @GetMapping("/products")
    public ArrayList<Object[]> getAllProduct(HttpServletResponse response) {
        Database db = new Database();
        String query = "SELECT * FROM products";
        Connection conn = db.connectToDb();
        ArrayList<Object[]> results = new ArrayList<>();
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            results = db.retrieveQuery(conn, pst);
            response.setStatus(200);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(400);
        }

        return results;
    }

    /**
     * Endpoint which generates 4 random items from the products table in database.
     * @param response, to send back status to Client
     * @return ArrayList<Object>, ArrayList of products
     */
    @GetMapping("/randomproducts")
    public ArrayList<ArrayList<Object[]>> getRandomProducts(HttpServletResponse response) {
        Database db = new Database();
        Connection conn = db.connectToDb();
        ArrayList<Object[]> nrOfRows;
        ArrayList<String> results = new ArrayList<>();
        String countQuery = "SELECT id FROM products GROUP BY id";
        String id_from_orders, newId;
        ArrayList<ArrayList<Object[]>> finalResults = new ArrayList<>();
        String finalQuery = "SELECT id, productname, productdescription, instock, price FROM products WHERE id = ?";
        PreparedStatement pst = null;
        int randInt = 0;
        try {
            PreparedStatement pst2 = conn.prepareStatement(countQuery);
            nrOfRows = db.retrieveQuery(conn, pst2);
            //id_from_orders = Arrays.toString(nrOfRows.get(0));

            for (int i = 0; i < 4; i++) {
                //System.out.println("Run: " + i);
                //System.out.println("1");
                conn = db.connectToDb();
                //System.out.println("2");
                Random r = new Random();
                //System.out.println("3");

                try {
                    randInt = r.nextInt(nrOfRows.size() - 1) + 1;
                    //System.out.println("Rand: ");
                    //System.out.println(randInt);
                    id_from_orders = Arrays.toString(nrOfRows.get(randInt));
                    newId = id_from_orders.substring(1, id_from_orders.length() - 1);
                    results.add(newId);

                    pst = conn.prepareStatement(finalQuery);
                    pst.setInt(1, Integer.parseInt(results.get(i)));

                    //System.out.println(pst);

                    finalResults.add(db.retrieveQuery(conn, pst));

                } catch (SQLException e) {
                    //System.out.println("Inner");
                    e.printStackTrace();
                }
                response.setStatus(200);

            }

        } catch (SQLException e) {
            //System.out.println("yalla");
            e.printStackTrace();
            response.setStatus(400);
        }


        //Testar sessions





        return finalResults;
    }


    /**
     * Endpoint which returns the stripeid for a given customer
     * @param response, to send back status to Client
     * @param request, to create a new session
     * @return String, stripe ID as a string.
     */
    @GetMapping("/stripe/customer")
    public String getStripeId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("In getStripeID");

        Database db = new Database();
        Connection conn = db.connectToDb();
        String resultString = "";
        ArrayList<Object[]> results = new ArrayList<>();

        HttpSession retrievedSession = request.getSession();
        // if not logged in check for null value in session..
        if (retrievedSession.getAttribute("Snus") == null){
            System.out.println("new customer without login");
            response.setStatus(200);
            return resultString;
        }

        Object sess = retrievedSession.getAttribute("Snus");
        System.out.println(sess.toString());
        int userId = Integer.parseInt(sess.toString());
        if (userId > 0){
            String sqlQuery = "SELECT stripeid FROM customers WHERE id = ?";

            try {
                PreparedStatement pst = conn.prepareStatement(sqlQuery);
                pst.setInt(1, userId);
                results = db.retrieveQuery(conn, pst);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (Arrays.toString(results.get(0)).contains("cus")){
                System.out.println("HALLÅ");
                resultString = Arrays.toString(results.get(0)).substring(1, Arrays.toString(results.get(0)).length()-1);
                //System.out.println("resultString: " + resultString);
                System.out.println("customer with saved card");
                response.setStatus(200);

            } else {
                System.out.println("HELLAAAA");
                response.setStatus(200); // accepted because the user has not saved their card
            }
            System.out.println("Results: ");
            assert results != null;
            System.out.println(Arrays.toString(results.get(0)));
            System.out.println("Resultstring");
            System.out.println(resultString);
        } else {
            System.out.println("UserID < 0");
        }


        return resultString;

    }


    /**
     * Endpoint which returns all orders for a given customer.
     * Takes advantage of sessions to know which customer makes the request
     * @param response, to send back status to Client
     * @param request, to create a new session
     * @return
     */
    //Lists all orders for user
    @GetMapping("/orders")
    public ArrayList<ArrayList<Object[]>> getOrders(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("271");

        System.out.println("274");
        Database db = new Database();
        Connection conn = db.connectToDb();

        ArrayList<ArrayList<Object[]>> finalResults = new ArrayList<>();


        HttpSession retrievedSession = request.getSession();

        Object sess = retrievedSession.getAttribute("Snus");
        System.out.println(sess);

        int userId = Integer.parseInt(sess.toString());


        System.out.println("User id från Session");
        System.out.println(userId);
        if (userId > 0) {

            ArrayList<Object[]> results = new ArrayList<>();
            String sqlQuery = "SELECT id FROM orders WHERE customer_id = ?";
            try {
                System.out.println("282");

                PreparedStatement pst = conn.prepareStatement(sqlQuery);
                pst.setInt(1, userId);
                results = db.retrieveQuery(conn, pst);



            } catch (SQLException e) {
                System.out.println("290");

                e.printStackTrace();
            }

            //Retrieves an array list of objects containing every order_id for customer.

            String newId, id_from_orders, sqlQuery2;
            ArrayList<Object[]> results2 = new ArrayList<>();
            Connection conn2;

            for (int i = 0; i < results.size(); i++) {
                conn2 = db.connectToDb();
                id_from_orders = Arrays.toString(results.get(i));
                newId = id_from_orders.substring(1, id_from_orders.length()-1);
                sqlQuery2 = "SELECT order_id, product_id, status FROM order_details WHERE order_id = ?";

                System.out.println("305");
                try {
                    System.out.println("307");
                   PreparedStatement pst2 = conn2.prepareStatement(sqlQuery2);
                   pst2.setInt(1, Integer.parseInt(newId));
                    try {
                        System.out.println("310");
                        results2 = db.retrieveQuery(conn2, pst2);
                        finalResults.add(results2);
                        response.setStatus(200);

                    } catch (Exception e) {
                        System.out.println("317");
                        e.printStackTrace();
                    }

                } catch (SQLException e) {
                    System.out.println("322");

                    e.printStackTrace();
                    response.setStatus(400);
                }



            }
        } else {
            finalResults = null;
        }
        return finalResults;
    }


    /**
     * Endpoint to return a single order by its ID
     * @param response, to send back status to Client
     * @param request, to create a new session
     * @param order_id TODO: Use this, not the userId assigned within the function
     * @return ArrayList<Object[]>, ArrayList of order
     */
    //Lists order by id
    @GetMapping("/order/{order_id}")
    @ResponseBody
    public ArrayList<Object[]> getOrderById(HttpServletRequest request, HttpServletResponse response, @PathVariable String order_id) {

        //Object session = request.getSession().getAttribute("se");
        //int userId = Integer.parseInt(session.toString());
        int userId = 1;
        Database db = new Database();
        Connection conn = db.connectToDb();
        String sqlQuery1 = "SELECT id FROM orders WHERE customer_id = ?";
        ArrayList<Object[]> results = new ArrayList<>();

        try {
            PreparedStatement pst = conn.prepareStatement(sqlQuery1);
            pst.setInt(1, userId); //Behöver eventuellt vara en sträng
            results = db.retrieveQuery(conn, pst);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        String id_from_orders, newId, sqlQuery2;
        ArrayList<Object[]> results2 = new ArrayList<>();
        Connection conn2 = null;
        int i = 0;
        for (Object[] result : results) {
            id_from_orders = Arrays.toString(result);
            newId = id_from_orders.substring(1, id_from_orders.length() - 1);

            if (newId.equals(order_id)) {

                sqlQuery2 = "SELECT order_id, product_id, status FROM order_details WHERE order_id = ?";

                try {
                    conn2 = db.connectToDb();
                    PreparedStatement pst2 = conn2.prepareStatement(sqlQuery2);
                    pst2.setInt(1, Integer.parseInt(newId));
                    results2 = db.retrieveQuery(conn2, pst2);
                    response.setStatus(200);
                } catch (SQLException e) {
                    e.printStackTrace();
                    response.setStatus(400);
                }


                break;
            }
        }


        //simon
        return results2;
    }

    /**
     * Endpoint which invalidates the current session
     * @param response, to send back status to Client
     * @param request, to create a new session
     */
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        System.out.println("Logoutheheh");
        HttpSession session;
        session = request.getSession();

        System.out.println(session.getAttribute("Snus").toString());
        session.invalidate();

        response.setStatus(200);
    }






}
