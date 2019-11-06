package se.experis.vipscase.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;
import se.experis.vipscase.Database;
import se.experis.vipscase.model.Order;
import se.experis.vipscase.model.StripePay;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class UserOrderController {
    UserOrderController(){}




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
    public void registerUser(@RequestBody Object test) {
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
