package se.experis.vipscase.controllers;


import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import se.experis.vipscase.model.Order;
import se.experis.vipscase.model.StripePay;
import se.experis.vipscase.model.User;
import se.experis.vipscase.model.Product;
import se.experis.vipscase.Database;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserOrderController {
    UserOrderController(){

    }




    @PostMapping("/order")
    @ResponseBody
    public void postOrder(HttpServletResponse response, @RequestBody Order order) {
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
    }

    @PostMapping("/register/user")
    @ResponseBody
    public void registerUser(HttpServletResponse response,@RequestBody User user) {
        Database db = new Database();
        Connection conn = db.connectToDb();
        String cpass = db.hashStuff(user.getPassword());

        String insertQ = "INSERT INTO " +
                "customers (customername, customerpass, email, lastname, streetname, postcode, city, birthyear) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement pst = conn.prepareStatement(insertQ);
            pst.setString(1, user.getName());
            pst.setString(2, cpass);
            pst.setString(3, user.getEmail());
            pst.setString(4, user.getLastname());
            pst.setString(5, user.getStreet());
            pst.setInt(6, user.getPostcode()); //?
            pst.setString(7, user.getCity());
            pst.setString(8, user.getBirthdate());
            db.insertQuery(conn, pst);
            response.setStatus(201);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(400);
        }
        //db.insertQuery(conn, user.getName(), user.getPassword(), user.getEmail(), user.getLastname(), user.getStreet(), user.getPostcode(), user.getCity(), user.getBirthdate());

        //Simon

    }

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

    @PostMapping("/login")
    @ResponseBody
    public void loginUser(HttpServletResponse response, HttpServletRequest request, @RequestBody User user) {

        Database db = new Database();
        ArrayList<Object[]> userCred = new ArrayList<>();
        String newHashed = "", dbPass = "";

        Connection conn = db.connectToDb();
        String sql = "SELECT customerpass FROM customers WHERE email= ?";
        try {

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user.getEmail());
            userCred = db.retrieveQuery(conn, pst);

            dbPass = Arrays.toString(userCred.get(0));
            dbPass = dbPass.substring(1, dbPass.length() -1);

            newHashed = db.hashStuff(user.getPassword());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Connection conn2 = db.connectToDb();

        if(newHashed.equals(dbPass)) {

            String userSql = "SELECT id FROM customers WHERE email= ?";
            try {
                PreparedStatement pst2 = conn2.prepareStatement(userSql);
                pst2.setString(1, user.getEmail());
                userCred = db.retrieveQuery(conn2, pst2);

                String usrid = Arrays.toString(userCred.get(0));
                usrid = usrid.substring(1, usrid.length() -1);

                HttpSession session = request.getSession();
                session.setAttribute("se",usrid);
                response.setStatus(200);
                Cookie cook = new Cookie("test", "s");
                response.addCookie(cook);
                HttpHeaders header = new HttpHeaders();
                header.setAccessControlAllowCredentials(true);


                // test

                CorsConfiguration corsconf = new CorsConfiguration();
                corsconf.setAllowCredentials(true);
                corsconf.setAllowedOrigins(Arrays.asList("*"));
                corsconf.setAllowedMethods(Arrays.asList("GET", "POST"));
                corsconf.setAllowedHeaders(Arrays.asList("X-Requested-With","Origin","Content-Type","Accept","Authorization"));
                corsconf.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Authorization, x-xsrf-token, Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, " +
                        "Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"));

                /*UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsconf);

                return source;*/

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Hacker be Gone!");
            response.setStatus(400);
        }
        //PA
        //return null;

    }


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


    @GetMapping("/randomproducts")
    public ArrayList<ArrayList<Object[]>> getRandomProducts(HttpServletResponse response) {
        Database db = new Database();
        Connection conn = db.connectToDb();
        ArrayList<Object[]> nrOfRows;
        ArrayList<String> results = new ArrayList<>();
        String countQuery = "SELECT id FROM products GROUP BY id";
        String id_from_orders, newId;
        ArrayList<ArrayList<Object[]>> finalResults = new ArrayList<>();
        String finalQuery = "SELECT productname, productdescription, instock, price FROM products WHERE id = ?";
        PreparedStatement pst = null;
        int randInt = 0;
        try {
            PreparedStatement pst2 = conn.prepareStatement(countQuery);
            nrOfRows = db.retrieveQuery(conn, pst2);
            //id_from_orders = Arrays.toString(nrOfRows.get(0));

            for (int i = 0; i < 4; i++) {
                System.out.println("Run: " + i);
                System.out.println("1");
                conn = db.connectToDb();
                System.out.println("2");
                Random r = new Random();
                System.out.println("3");

                try {
                    randInt = r.nextInt(nrOfRows.size() - 1) + 1;
                    System.out.println("Rand: ");
                    System.out.println(randInt);
                    id_from_orders = Arrays.toString(nrOfRows.get(randInt));
                    newId = id_from_orders.substring(1, id_from_orders.length() - 1);
                    results.add(newId);

                    pst = conn.prepareStatement(finalQuery);
                    pst.setInt(1, Integer.parseInt(results.get(i)));

                    System.out.println(pst);

                    finalResults.add(db.retrieveQuery(conn, pst));

                } catch (SQLException e) {
                    System.out.println("Inner");
                    e.printStackTrace();
                }
                response.setStatus(200);

            }

        } catch (SQLException e) {
            System.out.println("yalla");
            e.printStackTrace();
            response.setStatus(400);
        }





        return finalResults;
    }

    //Lists all orders
    @GetMapping("/orders")
    public ArrayList<ArrayList<Object[]>> getOrders(HttpServletRequest request, HttpServletResponse response) {

        Object session = request.getSession().getAttribute("se");
        int userId = Integer.parseInt(session.toString());
        Database db = new Database();
        Connection conn = db.connectToDb();

        ArrayList<Object[]> results = new ArrayList<>();
        String sqlQuery = "SELECT id FROM orders WHERE customer_id = ?";
        try {

            PreparedStatement pst = conn.prepareStatement(sqlQuery);
            pst.setInt(1, userId);
            results = db.retrieveQuery(conn, pst);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Retrieves an array list of objects containing every order_id for customer.

        String newId, id_from_orders, sqlQuery2;
        ArrayList<Object[]> results2 = new ArrayList<>();
        ArrayList<ArrayList<Object[]>> finalResults = new ArrayList<>();
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
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(400);
            }



        }

        return finalResults;
    }

    //Lists all orders
    @GetMapping("/order/{order_id}")
    @ResponseBody
    public ArrayList<Object[]> getOrderById(HttpServletRequest request, HttpServletResponse response, @PathVariable String order_id) {

        Object session = request.getSession().getAttribute("se");
        int userId = Integer.parseInt(session.toString());
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

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        HttpSession session;
        session = request.getSession();
        session.invalidate();
        response.setStatus(200);
    }






}
