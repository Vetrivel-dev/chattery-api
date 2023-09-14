package com.convio.client.dao;

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
	
//	private String jdbcURL = "jdbc:mysql://localhost:3306/convio";
//	private String jdbcUsername = "root";
//	private String jdbcPassword = "password";
	
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
