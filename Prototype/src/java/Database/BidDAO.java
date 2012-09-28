package Database;

import Entity.Bid;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

public class BidDAO {

    Connection connection = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;

    public BidDAO() {
    }
   
    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = ConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public void add(Bid bid) {
        try{
            String queryString = "INSERT INTO bid "
                    + "SET username=?,stockName=?,price=?,order_date=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ptmt.setString(1, bid.getUserId());
            ptmt.setString(2, bid.getStock());
            ptmt.setInt(3, bid.getPrice());
            ptmt.setLong(4, bid.getTime());
            ptmt.executeUpdate();
            
            ResultSet generatedKeys = ptmt.getGeneratedKeys();
            if(generatedKeys.next()){
                bid.setBidId(generatedKeys.getInt(1));
            }else{
                throw new SQLException("Failed to create Bid, no generated ID obtained.");
            }
            
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            
        }
    }
    
    public boolean update(Bid bid) {
        try{
            String queryString = "UPDATE bid SET transactionID = ? where bidID = ?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setInt(1, bid.getTransactionId());
            ptmt.setInt(2, bid.getBidId());
            ptmt.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
           return false;
        } finally {
            
        }
        
        return true;
    }
    
    public ArrayList<Bid> getAllBid() {
        
        ArrayList allBids = new ArrayList();
        
        try {
            String query = "Select * from bid";
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                Bid aBid = new Bid(resultSet.getInt("bidID"), 
                        resultSet.getString("username"), 
                        resultSet.getString("stockName"), 
                        resultSet.getInt("price"), 
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
                allBids.add(aBid);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
        }
        
        return allBids;
    }
    
    public ArrayList<Bid> getAllAskForUsername(String username) {
        ArrayList allBids = new ArrayList();
        
        try {
            String query = "Select * from bid where username = ?";
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, username);
            
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                Bid aBid = new Bid(resultSet.getInt("bidID"), 
                        resultSet.getString("username"), 
                        resultSet.getString("stockName"), 
                        resultSet.getInt("price"), 
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
                allBids.add(aBid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return allBids;
    }
    
    public Bid getHighestBidForStock(String stockName){
        try {
            String query = "SELECT * FROM bid "
                    + "WHERE transactionID IS NULL AND stockName=?"
                    + "ORDER by price DESC, order_date ASC "
                    + "LIMIT 1;";
            
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.setString(1, stockName);
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                return new Bid(resultSet.getInt("bidID"), 
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
