package Database;

import Entity.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class MatchedTransactionDAO {

    public static void add(Connection conn, MatchedTransaction mt) throws SQLException{
        
        PreparedStatement ptmt = null;        
        String queryString = "INSERT INTO stock_transaction "
                + "SET bidID=?,askID=?,price=?,trans_date=?";
        
        try {
            
            ptmt = conn.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ptmt.setInt(1, mt.getBidID());
            ptmt.setInt(2, mt.getAskID());
            ptmt.setInt(3, mt.getPrice());
            ptmt.setTime(4, new Time(mt.getDate().getTime()));
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
    
    
}
