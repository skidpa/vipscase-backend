package se.experis.vipscase.controllers;


import org.springframework.web.bind.annotation.*;
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
    public void postOrder(@RequestBody Object test) {
        //PA

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
    public ArrayList<Object[]> getOrders() {
        //Since Login isn't done, and is some what dependent on frontend, the user identification number is hard coded
        int userId = 10;
        Database db = new Database();
        String sqlQuery1 = "SELECT id FROM orders WHERE customer_id = '" + userId + "';";
        ArrayList<Object[]> results = db.retrieveQuery(db.connectToDb(), sqlQuery1);
        System.out.println(results.getClass().getName());

        System.out.println(results.get(0).getClass().getName());
        System.out.println(Arrays.toString(results.get(0)));



        String newId = "";
        String id_from_orders;
        String sqlQuery2;
        ArrayList<Object[]> results2 = null;

        for (int i = 0; i < results.size(); i++) {
            System.out.println("Run: " + i);
            id_from_orders = Arrays.toString(results.get(i));
            newId = id_from_orders.substring(1, id_from_orders.length()-1);
            sqlQuery2 = "SELECT order_id, product_id, status FROM order_details WHERE order_id = '" + newId + "';";
            results2 = db.retrieveQuery(db.connectToDb(), sqlQuery2);


        }
        System.out.println(results2.size());

        for (int i = 0; i < results2.size(); i++) {
            System.out.println(Arrays.toString(results2.get(i)));
        }

        /*String newId = id_from_orders.substring(1, id_from_orders.length()-1);

        ArrayList<Object[]> results2 = db.retrieveQuery(db.connectToDb(), sqlQuery2);
*/
        return results2;
    }

    //Lists all orders
    @GetMapping("/order/{order_id}")
    public ArrayList<Object> getOrderById() {
        //Pa
        return null;
    }






}