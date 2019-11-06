package se.experis.vipscase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


@SpringBootApplication
public class VipscaseApplication {
	/**
	 * main method
	 * @param args argument for starting the app
	 */
	public static void main(String[] args) {
		SpringApplication.run(VipscaseApplication.class, args);


		//Database db = new Database();

		//Connection conn = db.connectToDb();
		//String selectQ = "SELECT * FROM products";
		//ArrayList<Object[]> results = db.retrieveQuery(conn, selectQ);
		//System.out.println(Arrays.toString(results.get(0)));

		String name = "Test13";
		String pass = "Pass132";
		String mail = "nej@hotmail.com";
		String lname = "testsson";
		String street = "testgatan";
		int postcode = 1;
		String city = "testtown";
		int birthYear = 20190101;

		//db.insertQuery(conn, name, pass, mail, lname, street, postcode, city, birthYear);

	}

}
