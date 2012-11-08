/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bosender;
import Database.ConnectionFactory;
import Database.MatchedTransactionDAO;
import Entity.MatchedTransaction;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author lenovo
 */
public class SlaveBOSender {

    /**
     * @param args the command line arguments
     */
    private static String ipAddress = "10.0.106.239";
    private static String masterAddress = "192.168.0.9";
    private static String randomAddress = "192.168.0.3";
    private static String randomAddress2 = "192.168.0.4";
    
    public SlaveBOSender(){
        runConnections();
    }
    
    private void runConnections(){
        
    }
    
    public static void main(String[] args){
        SlaveBOSender sender = new SlaveBOSender();
        while(true){
            InetAddress ipObject;
            try {
                boolean masterReached = false;
                ipObject = InetAddress.getByName(masterAddress);
                masterReached = ipObject.isReachable(3000);

                if(masterReached){
                    //do nothing
                    System.out.println("Master is alive....");
                    
                }else{
                    
                    boolean randomReached = false;
                    ipObject = InetAddress.getByName(randomAddress);
                    randomReached = ipObject.isReachable(3000);
                    if(randomReached){
                        sender.detectBackOffice();
                    }else{
                        //that means slave is offline...
                        boolean random2Reached = false;
                        ipObject = InetAddress.getByName(randomAddress);
                        random2Reached = ipObject.isReachable(3000);
                        
                        if(random2Reached){
                            sender.detectBackOffice();
                        }else{    
                            System.out.println("Slave is dead...");
                        }    
                        
                    }    
                }    

            } catch (IOException ex) {
                Logger.getLogger(SlaveBOSender.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(SlaveBOSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
    public void detectBackOffice() {
        // TODO code application logic here
        boolean BOLive = false;
        boolean once = true;
        int count = 1;
        while(once){
            try {
                Runtime run = Runtime.getRuntime();
                String cmdText = "ping "+  ipAddress;
                Process process = run.exec(cmdText);
                process.waitFor();
                byte[] buffer = new byte[256];
                int cnt = 0;
                InputStream is = process.getInputStream();
                
                while((cnt=is.read(buffer))>=0) {
                    String tmpString = new String(buffer,0,cnt);
                    System.out.print(tmpString);
                    if(tmpString.indexOf("failure")==-1&&tmpString.indexOf("unreachable")==-1){
                        System.out.println();
                        System.out.println("BO is alive....");
                        BOLive = true;
                        break;
                    }else{
                        System.out.println("BO is dead....");
                        BOLive = false;
                        break;
                    }    
                }
                
            } catch (Exception e) {
                Logger.getLogger(SlaveBOSender.class.getName()).log(Level.SEVERE, null, e);
            }
            if(BOLive){    
                try {
                    FetchLogs();
                    System.out.println("This is the "+count+" time!");
                    count++;
                } catch (SQLException ex) {
                    Logger.getLogger(SlaveBOSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
            once = true;
        }
    }
    
    public void FetchLogs() throws SQLException{
        ArrayList<String> logList = new ArrayList<String>();
        Connection conn = null;
        ConnectionFactory conFact = ConnectionFactory.getInstance();
        
        int currentSQLStringIndex = conFact.getCurrentSQLStringIndex();
        boolean okay = false;
        
        while (!okay) {
            try {

                conn = conFact.getConnectionForCurrentSQLStringIndex(currentSQLStringIndex);
                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                conn.setAutoCommit(false);
                
                //fetch all logs
                ArrayList<MatchedTransaction> transList = MatchedTransactionDAO.getUnsentMatchedTransactions(conn);
                for(MatchedTransaction tmpLog:transList){
                    
                    MatchedTransaction mtFromDB = null;
                    //lock row
                    try{
                        mtFromDB = MatchedTransactionDAO.lockForUpdate(conn, tmpLog);
                    }catch(SQLException e){ //match is being locked by another thread, continue
                        continue;
                    }
                    
                    //double check whether another thread has sendt the log.
                    if(!mtFromDB.getSentToBackOffice()){
                        
                        //send to back office
                        if(sendToBackOffice(tmpLog.toString())){
                            //update flag if sending successful
                            MatchedTransactionDAO.updateMatchedTransactions(conn, tmpLog);
                            System.out.println();
                            
                        }else{
                            System.out.println("");
                        }
                    }
                }    
                //finished transaction, release locks
                conn.commit();
                okay = true;
                
                conFact.confirmWorkingConnectionStringIndex(currentSQLStringIndex);
                
            }catch (SQLException e) {
                currentSQLStringIndex = conFact.anotherConnectionStringIndexDifferentFromIndex(currentSQLStringIndex);
                e.printStackTrace();
                //error! rollback.
                if (conn != null) {
                    conn.rollback();
                }
                
            }finally{

                if (conn != null) {
                    conn.close();
                }

            }
        }
    }
    
    public boolean sendToBackOffice(String txnDescription){
        /*
        bosender.Service service = new bosender.Service();
        boolean status = false;

          try {
            // create new instances of remote Service objects
            bosender.ServiceSoap port = service.getServiceSoap();

            status = port.processTransaction("G3T7", "lime", txnDescription);


            if(status){
                //delete record from database
            }else{
                //resend
            }
            return status;
          }
          catch (Exception ex) {
              // may come here if a time out or any other exception occurs
              // what should you do here??
              System.out.println("What the hell...BakcOffice down...");
              return false;
          }
          */
          return false; // failure due to exception
      
    }
      
}
