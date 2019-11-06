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


		Database db = new Database();

		Connection conn = db.connectToDb();

	}

}
