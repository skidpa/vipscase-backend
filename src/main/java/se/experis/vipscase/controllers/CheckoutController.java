package se.experis.vipscase.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.experis.vipscase.model.StripePay;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CheckoutController {

    // store the private key in application properties and dont push it to git...
    @Value("${STRIPE_SECRET_KEY}")
    private String stripeKey;

    /**
     * Method to handle payments with stripe requires params in the pay param.
     * @param response sets the http response code
     * @param pay the request as json
     */
    @PostMapping("stripe/checkout")
    @ResponseBody
    public void stripePayment(HttpServletResponse response, @RequestBody StripePay pay){

        //System.out.println(test.toString());

        System.out.println(stripeKey);
        Stripe.apiKey = stripeKey;




        Map<String, Object> chargeParams = new HashMap<String,Object>();
        chargeParams.put("amount", pay.getAmount());
        chargeParams.put("currency", "sek");
        chargeParams.put("description", pay.getDescription());
        chargeParams.put("source", pay.getStripeToken()); // tok_xx from frontend

        // set stuff like user id to be able to track payments to set order id would be nice as well.
        // will be visible in payment info on stripe
        Map<String, String> initalMetadata = new HashMap<String, String>();
        initalMetadata.put("customer_id", "1234"); // Can place stuff in meta data whatever we want...
        initalMetadata.put("order_id", "1");
        chargeParams.put("metadata", initalMetadata);


        try {
            // idempotency
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(pay.getIdempotencyThing())
                    .build();
            // charge the card
            Charge charge = Charge.create(chargeParams, options);
            System.out.println("charge: " + charge);
            System.out.println("charge: " + charge.getStatus());
            response.setStatus(201);
        } catch (StripeException e){
            System.out.println("stripe exception caught");
            e.printStackTrace();
            response.setStatus(400);
        }
    }

}
