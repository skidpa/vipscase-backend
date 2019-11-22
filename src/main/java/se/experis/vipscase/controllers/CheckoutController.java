package se.experis.vipscase.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodListParams;
import com.stripe.param.PaymentMethodRetrieveParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.experis.vipscase.Database;
import se.experis.vipscase.model.StripePay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(
        allowCredentials = "true",
        allowedHeaders = "*",
        origins = {
                "http://localhost:3000",
                "https://pa-vips-front.herokuapp.com"
        },
        maxAge = 3600
)

@RestController
public class CheckoutController {

    // store the private key in application properties and dont push it to git...
    // the stripe private api key
    @Value("${STRIPE_SECRET_KEY}")
    private String stripeKey;

    /**
     * Creates a stripe payment intent for customers that has previously stored their card and is saved as a
     * stripe customer.
     * @param response send back status to Client
     * @param request, to create a new session
     * @param pay StripePay object with variables
     * @return stripe payment intent as json
     */

    @PostMapping("stripe/savedcustomer")
    @ResponseBody
    public String stripeSavedCardIntent(HttpServletResponse response, HttpServletRequest request, @RequestBody StripePay pay){
        Stripe.apiKey = stripeKey;

        //first get the payment method from stripe.
        PaymentMethodListParams listParams = new PaymentMethodListParams
                .Builder()
                .setCustomer(pay.getStripeCustomer())
                .setType(PaymentMethodListParams.Type.CARD)
                .build();

        PaymentMethodCollection paymentMethods = null;
        try {
            paymentMethods = PaymentMethod.list(listParams);
        } catch (StripeException e) {
            System.out.println("ERROR Exception: " + e.getMessage());
        }
        //Convert the paymentMethod we got from stripe and get the payment method id so that we can charge the card
        String paymentmethod = null;
        try {
            paymentmethod = paymentMethods
                    .getRawJsonObject()
                    .get("data")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .get("id")
                    .toString();
            paymentmethod = paymentmethod.substring(1, paymentmethod.length() -1);
        } catch (Exception e) {
            System.out.println("ERROR Exception: " + e.getMessage());
        }

        PaymentIntentCreateParams createParams = null;

        PaymentIntent intent = null;
        try {
            // set idempotence
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(pay.getIdempotencyThing())
                    .build();
            // set the paymentIntent parameters
            createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency("sek")
                    .setAmount(pay.getAmount())
                    .setCustomer(pay.getStripeCustomer())
                    .setPaymentMethod(paymentmethod)
                    .setConfirm(true)
                    .setOffSession(false)
                    .build();
            intent = PaymentIntent.create(createParams, options);

            response.setStatus(200);
            return intent.toJson();
        } catch (StripeException e) {
            System.out.println("ERROR Exception: " + e.getMessage());
            response.setStatus(400);
            return null;
        }
    }

    /**
     * Creates a stripe payment intent using variables sent from the frontend
     * @param response send back status to Client
     * @param request, to create a new session
     * @param pay StripePay object with variables
     * @return stripe payment intent as json
     */
    @PostMapping("stripe/intent")
    @ResponseBody
    public String stripeIntent(HttpServletResponse response, HttpServletRequest request, @RequestBody StripePay pay){

        Stripe.apiKey = stripeKey;

        // Setup the paymentIntent variables
        Map<String, Object> paymentIntentParams = new HashMap<String, Object>();
        paymentIntentParams.put("amount", pay.getAmount());
        paymentIntentParams.put("currency", "sek");

        // Prepare the payment method type
        ArrayList payment_method_types = new ArrayList();
        payment_method_types.add("card");

        paymentIntentParams.put("payment_method_types", payment_method_types);

        // Setup metadata that the webhook listens for (if logged in and save_card is true the webhook will create a stripe customer and store it in our db)
        Map<String, String> initalMetadata = new HashMap<String, String>();
        initalMetadata.put("save_card", Boolean.toString(pay.isSaveCard()));
        initalMetadata.put("user_id", pay.getUserId());

        paymentIntentParams.put("metadata", initalMetadata);
        PaymentIntent intent = null;

        try {
            // Set idempotence using the uuidv4 the frontend provided
            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(pay.getIdempotencyThing())
                    .build();
            // create the payment intent that will be sent to stripe for card confirmation and the charging of card
            intent = PaymentIntent.create(paymentIntentParams, options);

            response.setStatus(201);
            return intent.toJson();
        } catch (StripeException e){
            System.out.println("ERROR Exception: " + e.getMessage());
            response.setStatus(400);
            return null;
        }
    }

    /**
     * Webhook that stripe calls on a successfull payment and if the user has ticked the save card box
     * a new stripe customer will be created and the card will be stored in stripe.
     * The stripe customerid will also be stored in our database
     * @param response send back status to Client
     * @param request, to create a new session
     * @return http response code
     */
    @PostMapping("stripe/webhook")
    @ResponseBody
    public Object stripeSaveCard(HttpServletResponse response, HttpServletRequest request){
        Stripe.apiKey = stripeKey;
        String payload = "";
        StringBuilder result = null;
        try {
            // Get the body stripe posts on successfull payment
            payload = request.getInputStream().toString();
            InputStream in = request.getInputStream();
            BufferedReader buff = new BufferedReader( new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while((line = buff.readLine()) != null){
                result.append(line);
            }

        } catch (IOException e) {
            System.out.println("ERROR Exception: " + e.getMessage());
        }

        // Create an event using the stripe body json
        Event event = null;
        try {
            event = ApiResource.GSON.fromJson(String.valueOf(result), Event.class);
        } catch (JsonSyntaxException e) {
            System.out.println("ERROR Exception: " + e.getMessage());
            response.setStatus(400);
            return "";
        }

        // Deserialize the event so that we can use the stripeBody to access variables later
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if(dataObjectDeserializer.getObject().isPresent()){
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            System.out.println("ERROR: Deserialization failed");
        }

        switch (event.getType()){
            /*
            * If the stripe payment status is succeeded we check the metadata variables to se if the card
            * should be saved and if so we create a stripe customer that saves the card on stripe's side
            * and add that stripe customerid to our database so that we can charge the customer with the
            * saved card later
            */
            case "payment_intent.succeeded":
                PaymentIntent intent = (PaymentIntent) stripeObject;

                // get the url for the reciept
                String receiptUrl = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getReceiptUrl();

                // get the customer information
                String city = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getAddress().getCity();
                String country = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getAddress().getCountry();
                String state = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getAddress().getState();
                String zipCode = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getAddress().getPostalCode();
                String street = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getAddress().getLine1();
                String customerName = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getName();
                String email = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getEmail();
                String phone = ((PaymentIntent) stripeObject).getCharges().getData().get(0).getBillingDetails().getPhone();

                try {

                    if(intent.getMetadata().get("save_card").equals("true")){

                        // Set the customer parameters using the body we got from stripe
                        Map<String, Object> customerParams = new HashMap<String, Object>();
                        customerParams.put("payment_method", intent.getPaymentMethod());
                        customerParams.put("email", email);
                        customerParams.put("name", customerName);
                        customerParams.put("phone", phone);
                        customerParams.put("description", "VipsCase web customer");

                        // Set the customer billing parameters using the stripe body
                        Map<String, Object> billingParams = new HashMap<String, Object>();
                        billingParams.put("city", city);
                        billingParams.put("country", country);
                        billingParams.put("line1", street);
                        billingParams.put("postal_code", zipCode);
                        billingParams.put("state", state);
                        customerParams.put("address", billingParams);

                        Customer customer = null;

                        try {
                            // Now tell stripe to create the customer
                            customer = Customer.create(customerParams);

                            // Connect to our db and save the stripe customer id
                            Database db = new Database();
                            Connection conn = db.connectToDb();
                            String query = "UPDATE customers SET stripeid = ? WHERE id = ?";
                            PreparedStatement pst = null;

                            // get the stripe customer id from the customer we just created
                            String customerStr = customer.getRawJsonObject()
                                    .get("id")
                                    .getAsString();

                            try {
                                pst = conn.prepareStatement(query);
                                pst.setString(1, customerStr); // set the cus_ str from strip
                                pst.setInt(2, Integer.parseInt(intent.getMetadata().get("user_id"))); // set our customer id from meta data
                                db.insertQuery(conn, pst);
                                response.setStatus(201);
                            } catch (SQLException e) {
                                System.out.println("ERROR Exception: " + e.getMessage());
                                response.setStatus(400);
                            }
                        } catch (StripeException e) {
                            System.out.println("ERROR Exception: " + e.getMessage());
                            response.setStatus(400);
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println("ERROR Exception: " + e.getMessage());
                }
                break;
            default:
                response.setStatus(204);
                return "";
        }

        response.setStatus(201);
        return "";

    }


}
