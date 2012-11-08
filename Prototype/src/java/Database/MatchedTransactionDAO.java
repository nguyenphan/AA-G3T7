package Database;

import Entity.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

public class MatchedTransactionDAO {

    public static void add(Connection conn, MatchedTransaction mt) throws SQLException{
        
        PreparedStatement ptmt = null;        
        String queryString = "INSERT INTO stock_transaction "
                + "SET bidID=?,askID=?,price=?,trans_date=?,sent_backoffice=?";
        
        try {
            
            ptmt = conn.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ptmt.setInt(1, mt.getBidID());
            ptmt.setInt(2, mt.getAskID());
            ptmt.setInt(3, mt.getPrice());
            ptmt.setTime(4, new Time(mt.getDate().getTime()));
            ptmt.setBoolean(5,mt.getSentToBackOffice());
            ptmt.executeUpdate();
            
            ResultSet generatedKeys = ptmt.getGeneratedKeys();
            generatedKeys.next();
            mt.setTransactionId(generatedKeys.getInt(1));
            
        } catch (SQLException e) {
            
            throw e;    //pass back to caller
            
        } finally {
            
            //release resources
            if(ptmt!=null) ptmt.close();
            
        }
    }
    
    public static ArrayList<MatchedTransaction> getUnsentMatchedTransactions(Connection conn) throws SQLException{
        
        PreparedStatement ptmt = null;
        String query = "SELECT * FROM stock_transaction WHERE sent_backoffice=0";

        ArrayList unsentMT = new ArrayList();

        try {

            ptmt = conn.prepareStatement(query);

            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                MatchedTransaction mt = new MatchedTransaction(
                        resultSet.getInt("transactionID"),
                        resultSet.getBoolean("sent_backoffice"));
                
                unsentMT.add(mt);
            }

        } catch (SQLException e) {

            throw e; //pass back to caller

        } finally {

            if (ptmt != null) {
                ptmt.close();
            }

        }

        return unsentMT;
    }
    
    public static MatchedTransaction lockForUpdate(Connection conn, MatchedTransaction mt) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM stock_transaction "
                + "WHERE transactionID=? "
                + "FOR UPDATE";

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.setInt(1, mt.getTransactionId());
            ResultSet resultSet = ptmt.executeQuery();
            resultSet.next();
            return new MatchedTransaction(
                    resultSet.getInt("transactionID"),
                    resultSet.getBoolean("sent_backoffice"));
            
        } catch (SQLException e) {

            throw e;    //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }
        }
        
    }

    
    public static void updateMatchedTransactions(Connection conn, MatchedTransaction mt) throws SQLException{
        
        PreparedStatement ptmt = null;
        String queryString = "UPDATE stock_transaction SET sent_backoffice = ? where transactionID = ?";

        try {

            ptmt = conn.prepareStatement(queryString);
            ptmt.setBoolean(1, mt.getSentToBackOffice());
            ptmt.setInt(2, mt.getTransactionId());
            ptmt.executeUpdate();

        } catch (SQLException e) {

            throw e; //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }

        }
        
    }
    
}
