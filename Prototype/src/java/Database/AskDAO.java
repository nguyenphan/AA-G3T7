/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Entity.Ask;
import java.util.ArrayList;

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
            ptmt = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ptmt.setString(1, ask.getUserId());
            ptmt.setString(2, ask.getStock());
            ptmt.setDouble(3, ask.getPrice());
            ptmt.setLong(4, ask.getTime());
            ptmt.executeUpdate();
            
            ResultSet generatedKeys = ptmt.getGeneratedKeys();
            if(generatedKeys.next()){
                ask.setAskId(generatedKeys.getInt(1));
            }else{
                throw new SQLException("Failed to create Ask, no generated ID obtained.");
            }
            
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
                Ask anAsk = new Ask(resultSet.getInt("askID"), 
                        resultSet.getString("username"), 
                        resultSet.getString("stockName"), 
                        resultSet.getInt("price"), 
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
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
                Ask anAsk = new Ask(resultSet.getInt("askID"), 
                        resultSet.getString("username"),
                        resultSet.getString("stockName"), 
                        resultSet.getInt("price"), 
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
                allAsks.add(anAsk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return allAsks;
    }
    
    public ArrayList<Ask> getUnfulfilledAsksForStock(String stockName) {
        ArrayList unfulfilledAsks = new ArrayList();
        
        try {
            String query = "SELECT * FROM ask WHERE transactionID IS NULL AND stockName=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, stockName);
            
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                Ask ask = new Ask(resultSet.getInt("askID"), 
                        resultSet.getString("username"), 
                        stockName,
                        resultSet.getInt("price"), 
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
                unfulfilledAsks.add(ask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return unfulfilledAsks;
    }
    
    public Ask getLowestAskForStock(String stockName){
        
        try {
            String query = "SELECT * FROM ask "
                    + "WHERE transactionID IS NULL AND stockName=?"
                    + "ORDER by price ASC, order_date ASC "
                    + "LIMIT 1;";
            
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, stockName);
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                return new Ask(resultSet.getInt("askID"), 
                        resultSet.getString("username"), 
                        stockName,
                        resultSet.getInt("price"), 
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
