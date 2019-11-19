package se.experis.vipscase.model;


public class User {

    User(){
        System.out.println("I user och skapar skit");
    }

    private int id;
    private String name;

    private String password;

    private String email;

    private String lastname;
    private String street;
    private int postcode;
    private String city;
    private String birthdate;
    private String stripeid;

    public int getId() {
        System.out.println("Fetching id: " + id + " from user");

        return id;
    }

    public void setId(int id) {
        this.id = id;
        System.out.println("Sätter ID i user");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        System.out.println();
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
    public String getStripeid() {
        return stripeid;
    }

    public void setStripeid(String stripeid) {
        this.stripeid = stripeid;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", lastname='" + lastname + '\'' +
                ", street='" + street + '\'' +
                ", postcode=" + postcode +
                ", city='" + city + '\'' +
                ", birthdate='" + birthdate + '\'' +
                '}';
    }
}
