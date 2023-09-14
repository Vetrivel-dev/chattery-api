package com.convio.business.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectionDAO.class);
	
	// For cloud environment
	
	public static final String jdbcURL = System.getenv("DATASOURCE_URL");
	public static final String jdbcUsername = System.getenv("DATASOURCE_USERNAME");
	public static final String jdbcPassword = System.getenv("DATASOURCE_PASSWORD");
	
	
	// For local environment
	
//	private String jdbcURL = "jdbc:mysql://13.228.131.80:3306/fullcircledata_bd";
//	private String jdbcUsername = "fullcircledata";
//	private String jdbcPassword = "M#d4Ktre3ToY&8F";
	
	private Connection connection;

	public Connection getConnection() {
		return connection;
	}

	public ConnectionDAO() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
			logger.info("connected");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
