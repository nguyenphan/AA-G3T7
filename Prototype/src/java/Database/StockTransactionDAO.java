/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import Entity.StockTransaction;

/**
 *
 * @author ptlenguyen
 */
public class StockTransactionDAO {
    Connection connection = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;

    public StockTransactionDAO() {
    }
   
    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = ConnectionFactory.getInstance().getConnection();
        return conn;
    }
    
    public void add(StockTransaction stockTrans) {
        try{
            String queryString = "INSERT INTO stock_transaction "
                    + "SET askID=?, bidID=?, price=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setInt(1, stockTrans.getAskId());
            ptmt.setInt(2, stockTrans.getBidId());
            ptmt.setInt(3, stockTrans.getPrice());
            ptmt.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            
        }
    }
    
    public ArrayList<StockTransaction> getAllStockTransaction() {
        ArrayList arrayList = new ArrayList();
        
        try {
            String query = "SELECT * FROM stockTransaction";
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                StockTransaction aSt = new StockTransaction(resultSet.getInt("transactionID"), resultSet.getInt("askID"),
                        resultSet.getInt("bidID"), resultSet.getInt("price"));
                
                arrayList.add(aSt);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return arrayList;
    }
    
    public ArrayList<StockTransaction> getAllStockTransactionForUserId(String userId) {
        ArrayList arrayList = new ArrayList();
        
        try {
            String query = "SELECT * FROM stockTransaction as s, ask as a, bid as b WHERE a.askID = s.askID ";
            connection = getConnection();
            ptmt = connection.prepareStatement(query);
            resultSet = ptmt.executeQuery();
            
            while (resultSet.next()) {
                StockTransaction aSt = new StockTransaction(resultSet.getInt("transactionID"), resultSet.getInt("askID"),
                        resultSet.getInt("bidID"), resultSet.getInt("price"));
                
                arrayList.add(aSt);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return arrayList;
    }
    
}
