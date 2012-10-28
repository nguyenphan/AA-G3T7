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
public class AddEntityOperation implements Callable<Object[]> {
    private final Object objectToAdd;

    public AddEntityOperation(Object objectToAdd) {
        this.objectToAdd = objectToAdd;
    }
    
    public Object[] call() throws Exception {
        if (objectToAdd instanceof Ask) {
            AskDAO askDAO = new AskDAO();
            
            askDAO.add((Ask)objectToAdd);
        } else {
            BidDAO bidDAO = new BidDAO();
            
            bidDAO.add((Bid)objectToAdd);
        }
        
        return null;
    }

}
