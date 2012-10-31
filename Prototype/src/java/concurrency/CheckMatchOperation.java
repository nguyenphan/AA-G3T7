/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency;

import Database.*;
import Entity.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 *
 * @author ptlenguyen
 */
public class CheckMatchOperation implements Callable<Object[]> {

    private final Object objectToCompare;

    public CheckMatchOperation(Object objectToCompare) {
        this.objectToCompare = objectToCompare;
    }

    public Object[] call() throws Exception {

        Object[] returnData = new Object[2];
        if (objectToCompare instanceof Bid) {

            Bid newBid = (Bid) objectToCompare;

            Ask lowestAsk = getLowestAsk(newBid.getStock());
            returnData[0] = lowestAsk;

            if (lowestAsk != null && (newBid.getPrice() >= lowestAsk.getPrice())) {
                returnData[1] = "true";
            } else {
                returnData[1] = "false";
            }
            
            return returnData;

        } else if (objectToCompare instanceof Ask) {

            Ask newAsk = (Ask) objectToCompare;
            
            Bid highestBid = getHighestBid(newAsk.getStock());
            returnData[0] = highestBid;
            
            if (highestBid != null && (highestBid.getPrice() >= newAsk.getPrice())) {
                returnData[1] = "true";
            } else {
                returnData[1] = "false";
            }
            
            return returnData;

        }

        return null;

    }

    public Ask getLowestAsk(String stock) throws SQLException {

        Connection conn = null;
        try {

            conn = ConnectionFactory.getInstance().getConnection();
            AskDAO askDAO = new AskDAO();
            return askDAO.getLowestAskForStock(conn, stock);

        } catch (SQLException e) {

            throw e;    //pass back to caller to handle

        } finally {

            if (conn != null) {
                conn.close();
            }

        }

    }

    public Bid getHighestBid(String stock) throws SQLException {

        Connection conn = null;
        try {

            conn = ConnectionFactory.getInstance().getConnection();
            BidDAO bidDAO = new BidDAO();
            return bidDAO.getHighestBidForStock(conn, stock);

        } catch (SQLException e) {

            throw e;    //pass back to caller to handle

        } finally {

            if (conn != null) {
                conn.close();
            }

        }

    }
}
