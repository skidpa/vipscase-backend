package se.experis.vipscase.controllers;


import org.springframework.web.bind.annotation.*;
import se.experis.vipscase.model.User;
import se.experis.vipscase.Database;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    @GetMapping("/order")
    public ArrayList<Object> getOrders() {
        //Simon

        return null;
    }

    //Lists all orders
    @GetMapping("/order/{order_id}")
    public ArrayList<Object> getOrderById() {
        //Pa
        return null;
    }






}
