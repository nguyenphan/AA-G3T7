/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	String driverClassName = "com.mysql.jdbc.Driver";
	String connectionUrl = "jdbc:mysql://192.168.0.4:5000,192.168.0.3:5000/aastockexchange_db?autoReconnect=true&connectTimeout=500&failOverReadOnly=false";
	String dbUser = "ptlenguyen-PC";
	String dbPwd = "password";

	private static ConnectionFactory connectionFactory = null;

	private ConnectionFactory() {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(DatabaseConnectionString.getInstance().getConnectionString(), dbUser, dbPwd);
		return conn;
	}

	public static ConnectionFactory getInstance() {
		if (connectionFactory == null) {
                    connectionFactory = new ConnectionFactory();
		}
		return connectionFactory;
	}
}
