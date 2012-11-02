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

    public static void add(Connection conn, Ask ask) throws SQLException {

        PreparedStatement ptmt = null;
        String queryString = "INSERT INTO ask "
                + "SET username=?,stockName=?,price=?,order_date=?";

        try {

            ptmt = conn.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ptmt.setString(1, ask.getUserId());
            ptmt.setString(2, ask.getStock());
            ptmt.setDouble(3, ask.getPrice());
            ptmt.setLong(4, ask.getTime());
            ptmt.executeUpdate();

            ResultSet generatedKeys = ptmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                ask.setAskId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Failed to create Ask, no generated ID obtained.");
            }

        } catch (SQLException e) {

            throw e;    //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }

        }
    }

    public static void update(Connection conn, Ask ask) throws SQLException {

        PreparedStatement ptmt = null;
        String queryString = "UPDATE ask SET transactionID = ? where askID = ?";

        try {

            ptmt = conn.prepareStatement(queryString);
            ptmt.setInt(1, ask.getTransactionID());
            ptmt.setInt(2, ask.getAskId());
            ptmt.executeUpdate();

        } catch (SQLException e) {

            throw e;    //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }

        }

    }

    public static void lockForUpdate(Connection conn, Ask ask) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM ask "
                + "WHERE askID=? "
                + "FOR UPDATE";

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.setInt(1, ask.getAskId());
            ptmt.executeQuery();

        } catch (SQLException e) {

            throw e;    //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }
        }

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

    public static ArrayList<Ask> getUnfulfilledAsksForStock(Connection conn, String stockName) throws SQLException{
        
        ArrayList unfulfilledAsks = new ArrayList();

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM ask WHERE transactionID IS NULL AND stockName=?";
            
        try {
        
            ptmt = conn.prepareStatement(query);
            ptmt.setString(1, stockName);

            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                Ask ask = new Ask(resultSet.getInt("askID"),
                        resultSet.getString("username"),
                        stockName,
                        resultSet.getInt("price"),
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
                unfulfilledAsks.add(ask);
            }
            
        } catch (SQLException e) {
            
            throw e;    //pass back to caller
            
        }finally{
            
            if(ptmt!=null) ptmt.close();
            
        }

        return unfulfilledAsks;
        
    }

    public static Ask getLowestAskForStock(Connection conn, String stockName) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM ask "
                + "WHERE transactionID IS NULL AND stockName=?"
                + "ORDER by price ASC, order_date ASC "
                + "LIMIT 1;";

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.setString(1, stockName);
            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                return new Ask(resultSet.getInt("askID"),
                        resultSet.getString("username"),
                        stockName,
                        resultSet.getInt("price"),
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
            }

        } catch (SQLException e) {

            throw e;   //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }
        }

        return null;
    }

    public void clearUnfulfilledAsks() {
        try {
            String query = "DELETE FROM ask WHERE transactionID IS NULL";

            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            ptmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
