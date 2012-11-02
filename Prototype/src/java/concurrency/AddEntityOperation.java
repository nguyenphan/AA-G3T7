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
public class AddEntityOperation implements Callable<Object[]> {
    private final Object objectToAdd;

    public AddEntityOperation(Object objectToAdd) {
        this.objectToAdd = objectToAdd;
    }
    
    public Object[] call() throws Exception {
        
        Connection conn = null;
        
        try{
            
            conn = ConnectionFactory.getInstance().getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);  //inserting stuff, use range locks
            conn.setAutoCommit(false);
            
            if (objectToAdd instanceof Ask) {

                AskDAO askDAO = new AskDAO();
                askDAO.add(conn,(Ask)objectToAdd);
            } else {

                BidDAO bidDAO = new BidDAO();
                bidDAO.add(conn, (Bid)objectToAdd);

            }
            
            conn.commit();  //release lock
          
        }catch(SQLException e){
            
            //TODO: handle exception
            
        }finally{
            
            //release connection
            if(conn!=null) conn.close();
            
        }
        
        return null;
    }

}
