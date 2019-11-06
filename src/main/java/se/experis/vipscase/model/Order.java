package se.experis.vipscase.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private int customer_id;
    private ArrayList<Integer> product_id;
    private String status;


    public Order() {
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public List getProduct_id() {
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
                //", product_id=" + product_id +
                ", status='" + status + '\'' +
                '}';
    }
}
