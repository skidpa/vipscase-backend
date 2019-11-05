package se.experis.vipscase.model;

/**
 * StripePay class to help handle payment settings
 */
public class StripePay {

    /*public enum Currency {
        EUR, USD;
    }*/
    private String description;
    private int amount;
    //private Currency currency;
    private String stripeEmail;
    private String stripeToken;
    private String idempotencyThing;

    public StripePay() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    /*public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }*/

    public String getStripeEmail() {
        return stripeEmail;
    }

    public void setStripeEmail(String stripeEmail) {
        this.stripeEmail = stripeEmail;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public String getIdempotencyThing() {
        return idempotencyThing;
    }

    public void setIdempotencyThing(String idempotencyThing) {
        this.idempotencyThing = idempotencyThing;
    }

    @Override
    public String toString() {
        return "StripePay{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                ", stripeEmail='" + stripeEmail + '\'' +
                ", stripeToken='" + stripeToken + '\'' +
                ", idempotencyThing='" + idempotencyThing + '\'' +
                '}';
    }
}
