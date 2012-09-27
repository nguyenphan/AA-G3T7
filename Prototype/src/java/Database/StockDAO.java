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
        try {
            String queryString = "SELECT * FROM stock where stockName = ?";
            connection = getConnection();
            ptmt = connection.prepareStatement(queryString);
            ptmt.setString(1, name);
            resultSet = ptmt.executeQuery();

            Stock s = null;
            while (resultSet.next()) {
                s = new Stock(resultSet.getInt("stockID"), name);
            }
            return s;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
        }

        return null;
    }
}
