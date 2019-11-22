package se.experis.vipscase.controllers;

import org.springframework.web.bind.annotation.*;
import se.experis.vipscase.model.Order;
import se.experis.vipscase.model.User;
import se.experis.vipscase.model.Product;
import se.experis.vipscase.Database;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.*;


//CORS
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

        Database db = new Database();
        Connection conn = db.connectToDb();
        String insertQ = "INSERT INTO orders (customer_id) VALUES (?)";
        PreparedStatement pst = null;


        try {
            //Retrieves customer id
            pst = conn.prepareStatement(insertQ, Statement.RETURN_GENERATED_KEYS);
            pst.setObject(1, order.getCustomer_id());
            int order_id = db.addOrder(conn, pst);

            Database db2 = new Database();
            PreparedStatement pst2 = null;
            Connection conn2 = null;
            String insertQ2;

            //Inserts product into orderdetails
            for (int i = 0; i < order.getProduct_id().size(); i++) {
                conn2 = db2.connectToDb();
                insertQ2 = "INSERT INTO order_details (order_id, product_id, status) " +
                        "VALUES (?,?,?)";

                try {
                    pst2 = conn.prepareStatement(insertQ2);
                    pst2.setInt(1, order_id);
                    pst2.setInt(2,Integer.parseInt(order.getProduct_id().get(i).toString()));
                    pst2.setString(3, order.getStatus());
                    db.addOrderDetails(conn2, pst2);

                } catch (SQLException d) {
                    d.getMessage();
                }
            }
            response.setStatus(201);
        } catch (SQLException e) {
            e.getMessage();
            response.setStatus(400);
        }
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

        Database db = new Database();
        Connection conn = db.connectToDb();
        String cpass = db.hashStuff(user.getPassword());

        String insertQ = "INSERT INTO " +
                "customers (customername, customerpass, email, lastname, streetname, postcode, city, birthyear) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        int usrId = 0;
        try {
            //Using prepared statement to mitigate sql injections
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
            e.getMessage();
            response.setStatus(400);

        }
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
            //Using prepared statement to mitigate sql injections
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, product.getProductname());
            pst.setString(2, product.getProductdescription());
            pst.setInt(3, product.getInstock());
            pst.setInt(4, product.getPrice());
            db.insertQuery(conn, pst);
            response.setStatus(201);
        } catch (SQLException e) {
            e.getMessage();
            response.setStatus(400);
        }
    }


    /**
     * Endpoint which handles login for a user. Retrieves hashed password from database if email can be found
     * and compares the stored hash with a new hash for a given password. If the hashes match, user ID is retrieved
     * and a session is created, as well as a cookie containing that user's ID.
     *
     * @param response, to send back status to Client
     * @param request, to create a new session
     * @param user, to create a customer object which calls getEmail() and getPassword()
     */
    @PostMapping("/login")
    @ResponseBody
    public void loginUser(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {

        Database db = new Database();
        ArrayList<Object[]> userCred = new ArrayList<>();
        String newHashed = "", dbPass = "";

        Connection conn = db.connectToDb();
        String usrid = "";
        String sql = "SELECT customerpass FROM customers WHERE email= ?";
        try {
            //Using prepared statement to mitigate sql injections

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user.getEmail());
            userCred = db.retrieveQuery(conn, pst);
            //Parses string to required format
            dbPass = Arrays.toString(userCred.get(0));
            dbPass = dbPass.substring(1, dbPass.length() -1);
            //Hashes the password
            newHashed = db.hashStuff(user.getPassword());
            Connection conn2 = db.connectToDb();

            //Checking password
            if(newHashed.equals(dbPass)) {
                String userSql = "SELECT id FROM customers WHERE email= ?";
                try {
                    PreparedStatement pst2 = conn2.prepareStatement(userSql);
                    pst2.setString(1, user.getEmail());
                    userCred = db.retrieveQuery(conn2, pst2);


                    usrid = Arrays.toString(userCred.get(0));
                    usrid = usrid.substring(1, usrid.length() -1);

                    //Creating a session for the current user
                    HttpSession sess = request.getSession();
                    sess.setAttribute("Snus", usrid);
                    sess.setMaxInactiveInterval(15*60);
                    response.setStatus(200);

                } catch (SQLException e) {
                    e.getMessage();
                    response.setStatus(401);
                } catch (NullPointerException e){
                response.setStatus(401);
            }
            } else {
                response.setStatus(401);
            }

        } catch (SQLException e) {
            response.setStatus(401);
            e.getMessage();
        } catch (NullPointerException e){
            response.setStatus(401);
        }

        //PA
    }

    /**
     * Performs a test to see if a user is logged in
     * @param req, the HttpServlet request
     * @param resp, the HttpServlet response
     * @return the customer ID of the active user
     */

    @GetMapping("/loggedin")
    public int isLoggedIn(HttpServletRequest req, HttpServletResponse resp){

        Object sess = null;
        int returnInt = 0;
        HttpSession retrievedSession = req.getSession();
        sess = retrievedSession.getAttribute("Snus");
        if (retrievedSession.getAttribute("Snus") == null){
            resp.setStatus(200);
            return returnInt;
        }

        if (sess != null) {
            returnInt = Integer.parseInt(sess.toString());
        }

        return returnInt;
    }

    /**
     * Endpoint which retrieves every product from the database.
     * @param response, to send back status to Client
     * @return ArrayList<Object[]>, ArrayList of produts
     */
    @GetMapping("/products")
    public ArrayList<Object[]> getAllProduct(HttpServletResponse response) {
        Database db = new Database();
        String query = "SELECT * FROM products";
        Connection conn = db.connectToDb();
        ArrayList<Object[]> results = new ArrayList<>();
        try {
            //Selects every product from the database
            PreparedStatement pst = conn.prepareStatement(query);
            results = db.retrieveQuery(conn, pst);
            response.setStatus(200);
        } catch (SQLException e) {
            e.getMessage();
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
            //Counts number of rows in the products table
            PreparedStatement pst2 = conn.prepareStatement(countQuery);
            nrOfRows = db.retrieveQuery(conn, pst2);
            //Creates connections and randomizes elements
            for (int i = 0; i < 4; i++) {
                conn = db.connectToDb();
                Random r = new Random();
                //Adding randomized products to finalResults
                try {
                    randInt = r.nextInt(nrOfRows.size() - 1) + 1;
                    id_from_orders = Arrays.toString(nrOfRows.get(randInt));
                    newId = id_from_orders.substring(1, id_from_orders.length() - 1);
                    results.add(newId);

                    //Using prepared statement to mitigate sql injections
                    pst = conn.prepareStatement(finalQuery);
                    pst.setInt(1, Integer.parseInt(results.get(i)));

                    finalResults.add(db.retrieveQuery(conn, pst));

                } catch (SQLException e) {
                    e.getMessage();
                }
                response.setStatus(200);
            }

        } catch (SQLException e) {
            e.getMessage();
            response.setStatus(400);
        }

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

        Database db = new Database();
        Connection conn = db.connectToDb();
        String resultString = "";

        ArrayList<Object[]> results = new ArrayList<>();


        HttpSession retrievedSession = request.getSession();

        // Returns empty string if session not found
        if (retrievedSession.getAttribute("Snus") == null){
            response.setStatus(200);
            return resultString;
        }

        //Retrieves the active session
        Object sess = retrievedSession.getAttribute("Snus");
        int userId = Integer.parseInt(sess.toString());
        if (userId > 0) {

            //Retrieves stripe id from customer table
            //Preparedstatement to mitigate sql injections
            String sqlQuery = "SELECT stripeid FROM customers WHERE id = ?";
            try {
                PreparedStatement pst = conn.prepareStatement(sqlQuery);
                pst.setInt(1, userId);
                results = db.retrieveQuery(conn, pst);

            } catch (SQLException e) {
                e.getMessage();
            }//If the string contains the keyword: cus, add to resultString
            if (Arrays.toString(results.get(0)).contains("cus")) {
                resultString = Arrays.toString(results.get(0)).substring(1, Arrays.toString(results.get(0)).length() - 1);
                response.setStatus(200);

            } else {
                response.setStatus(200);
            }
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
    @GetMapping("/orders")
    public ArrayList<ArrayList<Object[]>> getOrders(HttpServletRequest request, HttpServletResponse response) {

        Database db = new Database();
        Connection conn = db.connectToDb();
        ArrayList<ArrayList<Object[]>> finalResults = new ArrayList<>();

        //Retrieves the current session
        HttpSession retrievedSession = request.getSession();
        Object sess = retrievedSession.getAttribute("Snus");

        int userId = Integer.parseInt(sess.toString());
        if (userId > 0) {

            //Retrieves a single customer from db
            ArrayList<Object[]> results = new ArrayList<>();
            String sqlQuery = "SELECT id FROM orders WHERE customer_id = ?";
            try {

                PreparedStatement pst = conn.prepareStatement(sqlQuery);
                pst.setInt(1, userId);
                results = db.retrieveQuery(conn, pst);
            } catch (SQLException e) {
                response.setStatus(400);
                e.getMessage();
                return null;
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

                try {
                   PreparedStatement pst2 = conn2.prepareStatement(sqlQuery2);
                   pst2.setInt(1, Integer.parseInt(newId));
                    try {
                        results2 = db.retrieveQuery(conn2, pst2);
                        finalResults.add(results2);
                        response.setStatus(200);

                    } catch (Exception e) {
                        e.getMessage();
                    }

                } catch (SQLException e) {
                    e.getMessage();
                    response.setStatus(400);
                }
            }
        } else {
            finalResults = null;
        }
        return finalResults;
    }

    /**
     * Endpoint which invalidates the current session
     * @param response, to send back status to Client
     * @param request, to create a new session
     */
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        //Invalidates the session
        HttpSession session;
        session = request.getSession();
        session.invalidate();
        response.setStatus(200);
    }

    /**
     * Endpoint to return a single order by its ID
     * @param response, to send back status to Client
     * @param request, to create a new session
     * @param order_id provided in the request
     * @return ArrayList<Object[]>, ArrayList of order
     */
    @GetMapping("/order/{order_id}")
    @ResponseBody
    public ArrayList<Map<String, Object>> getOrderById(HttpServletRequest request, HttpServletResponse response, @PathVariable String order_id) {

        //Retrieves the active session
        Object session = null;
        HttpSession retrivedSession = request.getSession();
        session = retrivedSession.getAttribute("Snus");

        //If it doesn't exist, return null
        if(retrivedSession.getAttribute("Snus") == null){
            response.setStatus(400);
            return null;
        }

        //Retrieves product id's from order_details tables
        Database db = new Database();
        Connection conn = db.connectToDb();
        ArrayList<Integer> prodIds = new ArrayList<>();
        //Mitigating sql injections
        String detailSql = "SELECT product_id FROM order_details WHERE order_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(detailSql);
            pstmt.setInt(1, Integer.parseInt(order_id));
            ResultSet rs = pstmt.executeQuery();
            //Getting the next product id
            while(rs.next()){
                prodIds.add(rs.getInt("product_id"));
            }

        } catch (SQLException e) {
            e.getMessage();
            response.setStatus(400);
        }

        //Retrieving all products from products with the help of previously collected product id
        String prodSql = "SELECT * FROM products WHERE id = ?";
        ArrayList<Map<String,Object>> test = new ArrayList<>();
        for (Integer id: prodIds) {
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(prodSql);
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();

                //Loops through the resultset
                while(resultSet.next()){
                    //Creates a map containing id, prodname, proddescription and price of a single item
                    Map<String, Object> orderJson = new HashMap<String, Object>();
                    orderJson.put("id", resultSet.getInt("id"));
                    orderJson.put("productname", resultSet.getString("productname"));
                    orderJson.put("productdescription", resultSet.getString("productdescription"));
                    orderJson.put("price", resultSet.getString("price"));
                    test.add(orderJson);
                }
                response.setStatus(200);
            } catch (SQLException e) {
                e.getMessage();
                response.setStatus(400);
            }
        }
        db.closeConnect(conn);

        return test;
    }

}
