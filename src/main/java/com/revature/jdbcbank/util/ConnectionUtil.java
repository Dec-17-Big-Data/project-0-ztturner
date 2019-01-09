package com.revature.jdbcbank.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConnectionUtil {
	private static Connection connectionInstance = null;
	private static final Logger logger = LogManager.getLogger(ConnectionUtil.class);
	
	private ConnectionUtil () {
		
	}
	
	public static Connection getConnection() {
		logger.traceEntry();
		if(ConnectionUtil.connectionInstance != null) {
			return logger.traceExit(ConnectionUtil.connectionInstance);
		}
		
		InputStream in = null;
		
		try {
			Properties props = new Properties();
			in = new FileInputStream("src\\main\\resources\\connection.properties");
			props.load(in);
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			String endpoint = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");
			
			connectionInstance = DriverManager.getConnection(endpoint, username, password);

			return logger.traceExit(connectionInstance);
		}
		catch(Exception e) {
			logger.error("Unable to get connection to database.");
		}
		finally {
			try {
				in.close();
			}
			catch(IOException e) {
				
			}
		}
		
		return null;
	}
}
