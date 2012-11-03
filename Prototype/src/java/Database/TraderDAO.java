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
    
    public static void add(Connection conn, Trader trader) throws SQLException {

        PreparedStatement ptmt = null;
        String queryString = "INSERT INTO trader VALUE(?,?)";
        
        try {

            ptmt = conn.prepareStatement(queryString);
            ptmt.setString(1, trader.getUsername());
            ptmt.setInt(2, trader.getCredit());
            ptmt.executeUpdate();


        } catch (SQLException e) {

            throw e;    //pass to caller to handle

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }

        }

    }

    public static void update(Connection conn, Trader trader) throws SQLException {

        String queryString = "UPDATE trader SET credit=? WHERE username=?";
        PreparedStatement ptmt = null;

        try {

            ptmt = conn.prepareStatement(queryString);
            ptmt.setInt(1, trader.getCredit());
            ptmt.setString(2, trader.getUsername());
            ptmt.executeUpdate();

        } catch (SQLException e) {

            throw e;    //pass to call to handle

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }

        }

    }

    public static void resetCreditsForAllTraders(Connection conn) throws SQLException{
        
        PreparedStatement ptmt = null;
        String queryString = "UPDATE trader SET credit=?";
            
        try {
        
            ptmt = conn.prepareStatement(queryString);
            ptmt.setInt(1, DEFAULT_CREDIT_LIMIT);
            ptmt.executeUpdate();
            
        } catch (SQLException e) {
            
             throw e;   //pass back to caller
            
        } finally {
            
        }

    }

    public static ArrayList<Trader> getAllTraders(Connection conn) throws SQLException {

        ArrayList<Trader> traders = new ArrayList<Trader>();

        PreparedStatement ptmt = null;
        String queryString = "SELECT * FROM trader";

        try {
            
            ptmt = conn.prepareStatement(queryString);
            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                traders.add(new Trader(resultSet.getString("username"), resultSet.getInt("credit")));
            }

        } catch (SQLException e) {
            
            throw e;    //pass back to caller
            
        } finally {
            
            //release resources
            if(ptmt!=null) ptmt.close();
            
        }
        
        return traders;
    }

    public static Trader getTraderWithUsername(Connection conn, String username) throws SQLException {


        PreparedStatement ptmt = null;
        String queryString = "SELECT * FROM trader where username = ?";

        try {

            ptmt = conn.prepareStatement(queryString);
            ptmt.setString(1, username);
            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                return new Trader(username, resultSet.getInt("credit"));
            }

        } catch (SQLException e) {

            throw e;    //pass to caller to handle

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }

        }

        return null;
    }
}
