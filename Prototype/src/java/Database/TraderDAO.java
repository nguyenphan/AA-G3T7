/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Entity.Trader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ptlenguyen
 */
public class TraderDAO {

    Connection connection = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;

    public TraderDAO() {
    }

    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = ConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public void add(Trader trader) {
        try{
            String queryString = "INSERT INTO trader VALUE(?,?)";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, trader.getUsername());
            ptmt.setDouble(2, 1000000.00);
            ptmt.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            
        }
    }

    public void update(Trader trader) {
    }

    public Trader getTraderWithUsername(String username) {
        try {
            String queryString = "SELECT * FROM trader where username = ?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, username);
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                return new Trader(username, resultSet.getFloat("credit"));
            }
                
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            
        }

        return null;
    }
}
