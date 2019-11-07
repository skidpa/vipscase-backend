package se.experis.vipscase.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.*;
import se.experis.vipscase.model.Order;
import se.experis.vipscase.model.StripePay;
import se.experis.vipscase.model.User;
import se.experis.vipscase.Database;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;


@RestController
public class UserOrderController {
    UserOrderController(){

    }




    @PostMapping("/order")
    @ResponseBody
    //public void postOrder(@RequestBody Order order, StripePay pay) {
    public void postOrder(@RequestBody ObjectNode json) {
        ArrayList prod_id = new ArrayList();
        for (JsonNode str: json.get("order").get("product_id")) {
            prod_id.add(str);
        }

        Order order = new Order(
                json.get("order").get("customer_id").asInt(),
                prod_id,
                json.get("order").get("status").asText()
                );

        StripePay pay = new StripePay(
                json.get("charge").get("description").asText(),
                json.get("charge").get("amount").asInt(),
                json.get("charge").get("stripeEmail").asText(),
                json.get("charge").get("stripeToken").asText(),
                json.get("charge").get("idempotencyThing").asText()
        );

        System.out.println("---- order ----\n" + order.toString() + "\n---- Charge ----\n" + pay.toString());

        Database db = new Database();
        Connection conn = db.connectToDb();
        // set atomic off

        //create order
        int order_id = db.addOrder(conn, order.getCustomer_id());
        //System.out.println(order_id);
        // retrive the new id
        //create order details
        db.addOrderDetails(conn, order_id, order.getProduct_id(), order.getStatus());

        //charge the user
        //stripe pay stuff...
        System.out.println("orders placed now charge my ass");

        //if charge successful commit changes to db
        //commit command
    }

    @PostMapping("/register/user")
    @ResponseBody
    public void registerUser(@RequestBody User user) {
        Database db = new Database();
        Connection conn = db.connectToDb();
        String sDate = user.getBirthdate();

        db.insertQuery(conn, user.getName(), user.getPassword(), user.getEmail(), user.getLastname(), user.getStreet(), user.getPostcode(), user.getCity(), user.getBirthdate());
        //TODO: Hash passwords

        //Simon

    }

    @PostMapping("/login")
    @ResponseBody
    public void loginUser(@RequestBody Object test) {


        //PA

    }

    //Lists all orders
    @GetMapping("/orders")
    public ArrayList<ArrayList<Object[]>> getOrders() {
        //Since Login isn't done, and is some what dependent on frontend, the user identification number is hard coded
        //In the future, this id should be fetched from session or local storage.
        int userId = 10;
        Database db = new Database();
        String sqlQuery1 = "SELECT id FROM orders WHERE customer_id = '" + userId + "';";
        ArrayList<Object[]> results = db.retrieveQuery(db.connectToDb(), sqlQuery1);
        //Retrieves an array list of objects containing every order_id for customer.

        String newId = "";
        String id_from_orders;
        String sqlQuery2;
        ArrayList<Object[]> results2 = new ArrayList<>();
        ArrayList<ArrayList<Object[]>> finalResults = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            System.out.println("Run: " + i);
            id_from_orders = Arrays.toString(results.get(i));
            newId = id_from_orders.substring(1, id_from_orders.length()-1);
            sqlQuery2 = "SELECT order_id, product_id, status FROM order_details WHERE order_id = '" + newId + "';";
            try {
                results2 = db.retrieveQuery(db.connectToDb(), sqlQuery2);
                finalResults.add(results2);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return finalResults;
    }

    //Lists all orders
    @GetMapping("/order/{order_id}")
    @ResponseBody
    public ArrayList<Object[]> getOrderById(@PathVariable String order_id) {
        //Retrieves an order by its id.
        //Must only return if the order matches the current user
        //Invalid users must receive an error
        //Since Login isn't done, and is some what dependent on frontend, the user identification number is hard coded
        //In the future, this id should be fetched from session or local storage.
        int userId = 10;
        Database db = new Database();
        String sqlQuery1 = "SELECT id FROM orders WHERE customer_id = '" + userId + "';";
        ArrayList<Object[]> results = db.retrieveQuery(db.connectToDb(), sqlQuery1);
        String id_from_orders;
        String newId;
        String sqlQuery2 = "";
        ArrayList<Object[]> results2 = new ArrayList<>();
        for (Object[] result : results) {

            id_from_orders = Arrays.toString(result);
            newId = id_from_orders.substring(1, id_from_orders.length() - 1);

            if (newId.equals(order_id)) {
                sqlQuery2 = "SELECT order_id, product_id, status FROM order_details WHERE order_id = '" + newId+ "';";
                results2 = db.retrieveQuery(db.connectToDb(), sqlQuery2);
                break;
            }
        }


        //simon
        return results2;
    }






}
