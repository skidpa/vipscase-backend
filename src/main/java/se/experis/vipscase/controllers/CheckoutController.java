package se.experis.vipscase.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CheckoutController {
    private String secretApiKey = "sk_test_UkYzGuDcK6rciVa41u7ojFIN007s7ETXBI";

    // Stripe.apiKey = "sk_test_UkYzGuDcK6rciVa41u7ojFIN007s7ETXBI"; //min

    @GetMapping("stripe/test")
    public void stripeTest(){

        Stripe.apiKey = "sk_test_UkYzGuDcK6rciVa41u7ojFIN007s7ETXBI";

        Map<String, Object> chargeMap = new HashMap<String,Object>();
        chargeMap.put("amount", 1000);
        chargeMap.put("currency", "sek");
        chargeMap.put("source", "tok_visa"); // tok_visa = credit card type beeing used. should probbably be sent from form.

        try {
            Charge charge = Charge.create(chargeMap);
            System.out.println("charge: " + charge);
        } catch (StripeException e){
            System.out.println("stripe exception caught");
            e.printStackTrace();
        }
    }

}
