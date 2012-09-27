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
                    + "SET username=?,stockID=?,price=?,order_date=?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, ask.getUsername());
            ptmt.setInt(2, ask.getStockID());
            ptmt.setDouble(3, ask.getPrice());
            ptmt.setTimestamp(4, ask.getOrder_date());
            ptmt.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            
        }
    }
}
