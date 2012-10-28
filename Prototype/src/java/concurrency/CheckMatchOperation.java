/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency;

import Database.AskDAO;
import Database.BidDAO;
import Entity.Ask;
import Entity.Bid;
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
            Bid newBid = (Bid)objectToCompare;
            AskDAO askDAO = new AskDAO();
            Ask lowestAsk = askDAO.getLowestAskForStock(newBid.getStock());
            returnData[0] = lowestAsk;
            if (lowestAsk ==null || lowestAsk.getPrice() > newBid.getPrice()) {
                returnData[1] = "false";
                return returnData;
            } else {
                returnData[1] = "true";
                return returnData;
            }
        } else if (objectToCompare instanceof Ask) {
            Ask newAsk = (Ask) objectToCompare;
            BidDAO bidDao = new BidDAO();
            Bid highestBid = bidDao.getHighestBidForStock(newAsk.getStock());
            returnData[0] = highestBid;
            if (highestBid != null && (highestBid.getPrice() >= newAsk.getPrice())){
                returnData[1] = "true";
                return returnData;
            } else {
                returnData[1] = "false";
                return returnData;
            }
        }
        return null;
    }
    
}
