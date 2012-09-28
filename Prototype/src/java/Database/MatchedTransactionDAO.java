package Database;

import Entity.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import java.util.ArrayList;

public class MatchedTransactionDAO {

    Connection connection = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;

    public MatchedTransactionDAO() {
    }

    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = ConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public void add(MatchedTransaction mt) {
        try {
            String queryString = "INSERT INTO stock_transaction "
                    + "SET bidID=?,askID=?,price=?,trans_date=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ptmt.setInt(1, mt.getBidID());
            ptmt.setInt(2, mt.getAskID());
            ptmt.setInt(3, mt.getPrice());
            ptmt.setTime(4, new Time(mt.getDate().getTime()));
            ptmt.executeUpdate();
            
            ResultSet generatedKeys = ptmt.getGeneratedKeys();
            if(generatedKeys.next()){
                mt.setTransactionId(generatedKeys.getInt(1));
            }else{
                throw new SQLException("Failed to create Matched Transaction, no generated ID obtained.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
