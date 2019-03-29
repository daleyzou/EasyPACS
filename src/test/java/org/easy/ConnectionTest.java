package org.easy;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ConnectionTest {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionTest.class);

	@Ignore
	@Test
	public void testMySQL(){

		LOG.info("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			LOG.error("Where is your MySQL JDBC Driver? {}",e.getMessage());
			return;
		}

		LOG.info("MySQL JDBC Driver Registered!");
		Connection connection = null;

		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://54.149.87.48:3306/dbdicom","dbdcmusr", "b9jHmY8LSH3yTcd9");

		} catch (SQLException e) {
			LOG.error("Connection Failed! Check output console: {}",e.getMessage());
			return;
		}

		if (connection != null) {
			LOG.info("You made it, take control your database now!");
		} else {
			LOG.error("Failed to make connection!");
		}
	}


}
