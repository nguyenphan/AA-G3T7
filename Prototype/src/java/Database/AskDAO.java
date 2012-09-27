/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Entity.Ask;
import java.util.*;

public class AskDAO {
    
    Connection connection = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;

    public AskDAO() {
    }
   
    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = ConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public void add(Ask ask) {
        try{
            String queryString = "INSERT INTO ask "
                    + "SET username=?,stockName=?,price=?,order_date=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, ask.getUserId());
            ptmt.setString(2, ask.getStock());
            ptmt.setDouble(3, ask.getPrice());
            ptmt.setTimestamp(4, ask.getTimestamp());
            ptmt.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            
        }
    }
    
    public boolean update(Ask ask) {
        try{
            String queryString = "UPDATE ask SET transactionID = ? where askID = ?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setInt(1, ask.getTransactionID());
            ptmt.setInt(2, ask.getAskId());
            ptmt.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
           
           return false;
        } finally {
            
        }
        
        return true;
    }
    
    public ArrayList<Ask> getAllAsk() {
        
        ArrayList allAsks = new ArrayList();
        
        try {
            String query = "Select * from ask";
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                Ask anAsk = new Ask(resultSet.getInt("askID"), resultSet.getString("username"), resultSet.getString("stockName"), 
                        resultSet.getInt("price"), resultSet.getTimestamp("order_date"), resultSet.getInt("transactionID"));
                allAsks.add(anAsk);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
        }
        
        return allAsks;
    }
    
    public ArrayList<Ask> getAllAskForUsername(String username) {
        ArrayList allAsks = new ArrayList();
        
        try {
            String query = "Select * from ask where username = ?";
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, username);
            
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                Ask anAsk = new Ask(resultSet.getInt("askID"), resultSet.getString("username"), resultSet.getString("stockName"), 
                        resultSet.getInt("price"), resultSet.getTimestamp("order_date"), resultSet.getInt("transactionID"));
                allAsks.add(anAsk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return allAsks;
    }
}
