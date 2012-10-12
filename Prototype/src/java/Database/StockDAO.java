package Database;

import Entity.Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockDAO {

    Connection connection = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;

    public StockDAO() {
    }

    private Connection getConnection() throws SQLException {
        Connection conn;
        conn = ConnectionFactory.getInstance().getConnection();
        return conn;
    }

    public Stock getStockWithName(String name) {
        Stock s = null;
        
        try {
            String queryString = "SELECT * FROM stock where stockName = ?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, name);
            ptmt.setQueryTimeout(2);
            resultSet = ptmt.executeQuery();
           
            while (resultSet.next()) {
                s = new Stock(name);
            }

        } catch (Exception e) {
            DatabaseConnectionString.getInstance().switchConnectionString();
            e.printStackTrace();
            return this.getStockWithName(name);
        } finally {
            
        }
        
        return s;
    }
}
