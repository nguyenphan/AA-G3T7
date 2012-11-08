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
        int privateConnectionStringIndex = 0;
	String dbUser = "root";
	String dbPwd = "";

	private static ConnectionFactory connectionFactory = null;

	private ConnectionFactory() {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
        
        public int getCurrentSQLStringIndex() {
            return privateConnectionStringIndex;
        }

	public Connection getConnectionForCurrentSQLStringIndex(int index) throws SQLException {
		Connection conn = null;
                
                String connectionUrl = null;
                
                if (index == 0) {
                    connectionUrl =  "jdbc:mysql://127.0.0.1:5000/aastockexchange_db?autoReconnect=true&connectTimeout=100&failOverReadOnly=false";
                } else {
                    connectionUrl =  "jdbc:mysql://127.0.0.1:5000/aastockexchange_db?autoReconnect=true&connectTimeout=100&failOverReadOnly=false";
                }
                
		//conn = DriverManager.getConnection(DatabaseConnectionString.getInstance().getConnectionString(), dbUser, dbPwd);
		conn = DriverManager.getConnection(connectionUrl,dbUser,dbPwd);
                return conn;
	}
        
        public int anotherConnectionStringIndexDifferentFromIndex(int currentUsingIndex) {
            if (currentUsingIndex == 0) {
                return 1;
            } else {
                return 0;
            }
        }
        
        public synchronized void confirmWorkingConnectionStringIndex(int workingConnectionIndex) {
            privateConnectionStringIndex = workingConnectionIndex;
        }
        
	public static ConnectionFactory getInstance() {
		if (connectionFactory == null) {
                    connectionFactory = new ConnectionFactory();
		}
		return connectionFactory;
	}
}
