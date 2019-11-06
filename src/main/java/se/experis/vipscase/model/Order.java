package se.experis.vipscase.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order {

    Map<String, Object> orders;
    private int customer_id;
    private ArrayList product_id;
    private String status;



    public Order() {
    }

    public Order(int customer_id, ArrayList product_id, String status) {
        this.customer_id = customer_id;
        this.product_id = product_id;
        this.status = status;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public ArrayList getProduct_id() {
        return product_id;
    }

    public void setProduct_id(ArrayList product_id) {
        this.product_id = product_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer_id=" + customer_id +
                ", product_id=" + product_id +
                ", status='" + status + '\'' +
                '}';
    }

    public Map<String, Object> getOrders() {
        return orders;
    }

    public void setOrders(Map<String, Object> orders) {
        this.orders = orders;
    }
}
