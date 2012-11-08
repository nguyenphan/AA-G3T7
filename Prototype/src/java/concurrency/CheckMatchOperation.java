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

    public Object[] call() throws SQLException {

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

    private Ask getLowestAsk(String stock) throws SQLException {

        Connection conn = null;
        int currentSQLStringIndex = ConnectionFactory.getInstance().getCurrentSQLStringIndex();
        boolean okay = false;
        while(!okay){
            try {

                conn = ConnectionFactory.getInstance().getConnectionForCurrentSQLStringIndex(currentSQLStringIndex);
                
                Ask returnAsk = AskDAO.getLowestAskForStock(conn, stock);
                
                okay = true;
                ConnectionFactory.getInstance().confirmWorkingConnectionStringIndex(currentSQLStringIndex);
                
                return returnAsk;
                
            } catch (SQLException e) {
                currentSQLStringIndex = ConnectionFactory.getInstance().anotherConnectionStringIndexDifferentFromIndex(currentSQLStringIndex);
                System.err.println(e.getMessage());
                
            } finally {

                if (conn != null) {
                    conn.close();
                }

            }
        }
        return null;

    }

    public Bid getHighestBid(String stock) throws SQLException {

        Connection conn = null;
        int currentSQLStringIndex = ConnectionFactory.getInstance().getCurrentSQLStringIndex();
        boolean okay = false;
        while(!okay){
            try {

                conn = ConnectionFactory.getInstance().getConnectionForCurrentSQLStringIndex(currentSQLStringIndex);
                
                Bid returnBid = BidDAO.getHighestBidForStock(conn, stock);
                okay = true;
                
                ConnectionFactory.getInstance().confirmWorkingConnectionStringIndex(currentSQLStringIndex);
                
                return returnBid;

            } catch (SQLException e) {
                currentSQLStringIndex = ConnectionFactory.getInstance().anotherConnectionStringIndexDifferentFromIndex(currentSQLStringIndex);
                System.err.println(e.getMessage());
                
            } finally {

                if (conn != null) {
                    conn.close();
                }

            }
        }
        return null;

    }
}
