package se.experis.vipscase.controllers;


import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class UserOrderController {
    UserOrderController(){}




    @PostMapping("/order")
    @ResponseBody
    public void postOrder(@RequestBody Object test) {
        //PA

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
