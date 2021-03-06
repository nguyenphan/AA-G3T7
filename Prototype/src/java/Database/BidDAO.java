package Database;

import Entity.Bid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class BidDAO {

    public static void add(Connection conn, Bid bid) throws SQLException {

        PreparedStatement ptmt = null;
        String queryString = "INSERT INTO bid SET username=?,stockName=?,price=?,order_date=?";

        try {

            ptmt = conn.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ptmt.setString(1, bid.getUserId());
            ptmt.setString(2, bid.getStock());
            ptmt.setInt(3, bid.getPrice());
            ptmt.setLong(4, bid.getTime());
            ptmt.executeUpdate();

            //set bid id to bid object
            ResultSet generatedKeys = ptmt.getGeneratedKeys();
            generatedKeys.next();
            bid.setBidId(generatedKeys.getInt(1));

        } catch (SQLException e) {

            throw e; //pass to caller to handle

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }

        }
    }

    public static Bid getBid(Connection conn, int bidID) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM bid "
                + "WHERE bidID=?";

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.setInt(1,bidID);
            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                return new Bid(resultSet.getInt("bidID"),
                        resultSet.getString("username"),
                        resultSet.getString("stockName"),
                        resultSet.getInt("price"),
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
            }

        } catch (SQLException e) {

            throw e;    //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }
        }

        return null;

    }
    
    public static ArrayList<Bid> getUnfulfilledBidsForStock(Connection conn, String stockName) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM bid WHERE transactionID IS NULL AND stockName=?";

        ArrayList unfulfilledAsks = new ArrayList();

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.setString(1, stockName);

            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                Bid ask = new Bid(resultSet.getInt("bidID"),
                        resultSet.getString("username"),
                        stockName,
                        resultSet.getInt("price"),
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
                unfulfilledAsks.add(ask);
            }

        } catch (SQLException e) {

            throw e; //pass back to caller

        } finally {

            if (ptmt != null) {
                ptmt.close();
            }

        }

        return unfulfilledAsks;
    }

    public static Bid getHighestBidForStock(Connection conn, String stockName) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM bid "
                + "WHERE transactionID IS NULL AND stockName=?"
                + "ORDER by price DESC, order_date ASC "
                + "LIMIT 1;";

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.setString(1, stockName);
            ResultSet resultSet = ptmt.executeQuery();

            while (resultSet.next()) {
                return new Bid(resultSet.getInt("bidID"),
                        resultSet.getString("username"),
                        stockName,
                        resultSet.getInt("price"),
                        resultSet.getLong("order_date"),
                        resultSet.getInt("transactionID"));
            }

        } catch (SQLException e) {

            throw e;    //pass back to caller

        } finally {

            //release resources
            if (ptmt != null) {
                ptmt.close();
            }
        }

        return null;

    }
    
    public static void lockForUpdate(Connection conn, Bid bid) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "SELECT * FROM bid "
                + "WHERE bidID=? "
                + "FOR UPDATE";

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.setInt(1, bid.getBidId());
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

    public static void update(Connection conn, Bid bid) throws SQLException {

        PreparedStatement ptmt = null;
        String queryString = "UPDATE bid SET transactionID = ? where bidID = ?";

        try {

            ptmt = conn.prepareStatement(queryString);
            ptmt.setInt(1, bid.getTransactionId());
            ptmt.setInt(2, bid.getBidId());
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

    public static void clearUnfulfilledBids(Connection conn) throws SQLException {

        PreparedStatement ptmt = null;
        String query = "DELETE FROM bid WHERE transactionID IS NULL";

        try {

            ptmt = conn.prepareStatement(query);
            ptmt.executeUpdate();

        } catch (SQLException e) {

            throw e;    //pass back to caller

        } finally {

            if (ptmt != null) {
                ptmt.close();
            }

        }

    }
}
