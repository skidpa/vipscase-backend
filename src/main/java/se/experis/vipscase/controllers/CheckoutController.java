package se.experis.vipscase.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.experis.vipscase.model.StripePay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "https://pa-vips-back.herokuapp.com"}, maxAge = 3600)
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
    public void stripePayment(HttpServletResponse response, HttpServletRequest request, @RequestBody StripePay pay){

        System.out.println(pay.toString());
        System.out.println("setting stripe key");

        Stripe.apiKey = stripeKey;
        /*
        System.out.println("session id: " + request.getSession().getId());
        System.out.println("reading idempotency");
        //pay.setIdempotencyThing(request.getSession().getId());
        System.out.println(pay.getIdempotencyThing());

        System.out.println("creating chagreParams map");
        Map<String, Object> chargeParams = new HashMap<String,Object>();
        System.out.println("setting amount");
        chargeParams.put("amount", pay.getAmount());
        System.out.println("settiing currency");
        chargeParams.put("currency", "sek");
        //chargeParams.put("description", "front end test");
        System.out.println("setting source");
        chargeParams.put("source", pay.getToken_id()); // tok_xx from frontend
        */
        // set stuff like user id to be able to track payments to set order id would be nice as well.
        // will be visible in payment info on stripe
        //Map<String, String> initalMetadata = new HashMap<String, String>();
        //initalMetadata.put("customer_id", "1234"); // Can place stuff in meta data whatever we want...
        //initalMetadata.put("order_id", "1");
        //chargeParams.put("metadata", initalMetadata);

        // payment intent stuff since we are europeans and not americans..

        Map<String, Object> paymentIntentParams = new HashMap<String, Object>();
        System.out.println("setting intent amount");
        paymentIntentParams.put("amount", pay.getAmount());
        System.out.println("setting intent currency");
        paymentIntentParams.put("currency", "sek");

        ArrayList payment_method_types = new ArrayList();
        payment_method_types.add("card");
        System.out.println("setting payment_metod_types");
        paymentIntentParams.put("payment_method_types", payment_method_types);




        try {
            // idempotency
            System.out.println("setting idempotency with session id");
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(pay.getIdempotencyThing())
                    .build();
            // charge the card
            /*Charge charge = Charge.create(chargeParams, options);
            System.out.println("charge: " + charge);
            System.out.println("charge: " + charge.getStatus());
            System.out.println("creating session with stripes session thing this is after charge");
            //Session session = Session.create(chargeParams); // this gives some kind of error/warning and is probably pointless
            System.out.println("charge is now done...");*/

            System.out.println("createing intetn charge with idempotency option set");
            PaymentIntent.create(paymentIntentParams, options);
            response.setStatus(201);
        } catch (StripeException e){
            System.out.println("stripe exception caught");
            e.printStackTrace();
            response.setStatus(400);
        }
    }

    @PostMapping("stripe/intent")
    @ResponseBody
    public String stripeIntent(HttpServletResponse response, HttpServletRequest request, @RequestBody StripePay pay){

        Stripe.apiKey = stripeKey;

        Map<String, Object> paymentIntentParams = new HashMap<String, Object>();
        paymentIntentParams.put("amount", pay.getAmount());
        paymentIntentParams.put("currency", "sek");
        ArrayList payment_method_types = new ArrayList();
        payment_method_types.add("card");
        paymentIntentParams.put("payment_method_types", payment_method_types);
        PaymentIntent intent = null;

        try {
            // idempotency
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(pay.getIdempotencyThing())
                    .build();

            intent = PaymentIntent.create(paymentIntentParams, options);
            response.setStatus(201);
            return intent.toJson();
        } catch (StripeException e){
            e.printStackTrace();
            response.setStatus(400);
            return null;
        }
    }

    @GetMapping("stripe/setupintent")
    @ResponseBody
    public String stripeSetupIntent(HttpServletResponse response, HttpServletRequest request/*, @RequestBody StripePay pay*/){

        Stripe.apiKey = stripeKey;
        List<Object> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");
        Map<String, Object> params = new HashMap<>();
        params.put("payment_method_types", paymentMethodTypes);
        params.put("usage", "on_session"); // setup_future_usage ?


        try {
            SetupIntent setupIntent = SetupIntent.create(params);
            System.out.println("returning setupintent");
            response.setStatus(201);
            return setupIntent.toJson();
        } catch (StripeException e){
            e.printStackTrace();
            response.setStatus(400);
            return null;
        }
    }

    @PostMapping("stripe/webhook")
    @ResponseBody
    public String stripeSaveCard(HttpServletResponse response, HttpServletRequest request, @RequestBody StripePay pay){
        Stripe.apiKey = stripeKey;
        String payload = request.toString();
        System.out.println("pay load: " + payload);
        Event event = null;

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObj = null;
        if(dataObjectDeserializer.getObject().isPresent()){
            stripeObj = dataObjectDeserializer.getObject().get();
        } else {
            System.out.println("event object deserialization failed...");
        }

        //handle the event...
        switch (event.getType()){
            case "payment_intent.succeeded":
                System.out.println("PaymentIntent was successful");
                break;
            case "payment_method.attached":
                PaymentMethod paymentMethod = (PaymentMethod) stripeObj;
                System.out.println("Payment was attached to a customer");
                break;
            default:
                response.setStatus(400);
                return "";
        }

        response.setStatus(200);
        return "";

        /*PaymentIntent intent = (PaymentIntent) stripeObj;
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("payment_method", intent.getPaymentMethod());
        Customer customer = null;
        try {
            customer.create(customerParams);
            System.out.println("customer" + customer);
            response.setStatus(201);
            return "yayyy";
        } catch (StripeException e) {
            e.printStackTrace();
            response.setStatus(400);
            return null;
        }*/

        /*

        Map<String, Object> paymentIntentParams = new HashMap<String, Object>();
        paymentIntentParams.put("amount", pay.getAmount());
        paymentIntentParams.put("currency", "sek");
        ArrayList payment_method_types = new ArrayList();
        payment_method_types.add("card");
        paymentIntentParams.put("payment_method_types", payment_method_types);
        PaymentIntent intent = null;

        try {
            // idempotency
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(pay.getIdempotencyThing())
                    .build();

            intent = PaymentIntent.create(paymentIntentParams, options);
            response.setStatus(201);
            return intent.toJson();
        } catch (StripeException e){
            e.printStackTrace();
            response.setStatus(400);
            return null;
        }*/

    }


}
