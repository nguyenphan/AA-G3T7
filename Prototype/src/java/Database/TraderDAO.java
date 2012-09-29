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

import java.util.ArrayList;

/**
 *
 * @author ptlenguyen
 */
public class TraderDAO {

    final static int DEFAULT_CREDIT_LIMIT = 1000000;
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

    public void add(Trader trader) throws SQLException{
        
        try {
            
            String queryString = "INSERT INTO trader VALUE(?,?)";
            
            //start transaction
            connection = getConnection();
            connection.setAutoCommit(false);
            
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, trader.getUsername());
            ptmt.setInt(2, trader.getCredit());
            ptmt.executeUpdate();
            
            //end transaction, commit
            connection.commit();
            
        } catch (SQLException e) {
            
            e.printStackTrace();
            
            //if successfully connected to db, rollback any changes.
            if(connection!=null){
                connection.rollback();
                System.err.print("Transaction is rolled back.");
            }
            
            throw e;
            
        } finally {
            
            //release resources
            if(ptmt!=null) ptmt.close();
            if(connection!=null) connection.close();
            
        }
        
    }

    public void update(Trader trader) {
        try {
            String queryString = "UPDATE trader SET credit=? WHERE username=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setInt(1, trader.getCredit());
            ptmt.setString(2, trader.getUsername());
            ptmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    public void resetCreditsForAllTraders() {
        try {
            String queryString = "UPDATE trader SET credit=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setInt(1, DEFAULT_CREDIT_LIMIT);
            ptmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    public ArrayList<Trader> getAllTraders() {
        ArrayList<Trader> traders = new ArrayList<Trader>();
        try {
            String queryString = "SELECT * FROM trader";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                traders.add(new Trader(resultSet.getString("username"), resultSet.getInt("credit")));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return traders;
    }

    public Trader getTraderWithUsername(String username) throws SQLException{
        
        String queryString = "SELECT * FROM trader where username = ?";
            
        try {
            
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, username);
            resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                return new Trader(username, resultSet.getInt("credit"));
            }

        } catch (SQLException e) {
            
            e.printStackTrace();
            throw e;
            
        } finally {
            
            //release resources
            if(ptmt!=null) ptmt.close();
            if(connection!=null) connection.close();
            
        }

        return null;
    }
}
