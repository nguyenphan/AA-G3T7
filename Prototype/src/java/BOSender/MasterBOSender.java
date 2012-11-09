/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BOSender;

import Database.AskDAO;
import Database.BidDAO;
import Database.ConnectionFactory;
import Database.MatchedTransactionDAO;
import Entity.Ask;
import Entity.Bid;
import Entity.MatchedTransaction;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lenovo
 */
public class MasterBOSender {

    /**
     * @param args the command line arguments
     */
    private static String ipAddress = "10.0.106.239";
    private static String randomAddress1 = "192.168.1.9";
    private static String randomAddress2 = "192.168.1.3";
    private static String randomAddress3 = "192.168.1.4";

    public MasterBOSender() {
        runConnections();
    }

    private void runConnections() {
    }

    public static void main(String[] args) {
        MasterBOSender sender = new MasterBOSender();
        while (true) {
            try {

                InetAddress ipObject;
                boolean random1Reached = false;
                ipObject = InetAddress.getByName(randomAddress1);
                random1Reached = ipObject.isReachable(3000);

                if (random1Reached) {
                    System.out.println("Master is in control...");
                    sender.detectBackOffice();
                } else {
                    boolean random2Reached = false;
                    ipObject = InetAddress.getByName(randomAddress2);
                    if (random2Reached) {
                        System.out.println("Master is in control...");
                        sender.detectBackOffice();
                    } else {
                        boolean random3Reached = false;
                        ipObject = InetAddress.getByName(randomAddress3);
                        if (random3Reached) {
                            System.out.println("Master is in control...");
                            sender.detectBackOffice();
                        } else {
                            System.out.println("Master is dead...");
                        }


                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(MasterBOSender.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(MasterBOSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void detectBackOffice() {
        // TODO code application logic here
        System.out.println("Starting to ping back office...");
        boolean BOLive = false;

        try {
    
            List<String> commands = new ArrayList<String>();
            commands.add("ping");
            commands.add("-c");
            commands.add("5");
            commands.add(ipAddress);
            
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process process = pb.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String s = null;
            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                if(s.indexOf("timeout")==-1){
                    BOLive = true;
                    System.out.println("Back office detected...");
                    break;
                }    
            }
            
        } catch (Exception e) {
            Logger.getLogger(MasterBOSender.class.getName()).log(Level.SEVERE, null, e);
        }
        if (BOLive) {
            try {
                System.out.println("Start to fetch trans from database.....");
                FetchLogs();

            } catch (SQLException ex) {
                Logger.getLogger(MasterBOSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    public void FetchLogs() throws SQLException {
       
        ConnectionFactory cf = ConnectionFactory.getInstance();
        int currentSQLStringIndex = cf.getCurrentSQLStringIndex();
        
        Connection conn = null;
        try{
            conn = cf.getConnectionForCurrentSQLStringIndex(currentSQLStringIndex);
            conn.setAutoCommit(false);

            MatchedTransaction mt = MatchedTransactionDAO.lockUnsentLimitOneForUpdate(conn);
            mt.setAsk(AskDAO.getAsk(conn, mt.getAskID()));
            mt.setBid(BidDAO.getBid(conn, mt.getBidID()));
            
            //send to back office
            if(sendToBackOffice(mt.toString())){
                //update flag in database
                mt.setSendToBackOffice(true);
                MatchedTransactionDAO.updateMatchedTransactions(conn, mt);
            }
                
            conn.commit();
            
        }catch(SQLException e){
            
        }finally{
            if(conn!=null) conn.close();
        }

    }

    public boolean sendToBackOffice(String txnDescription) {

        aa.Service service = new aa.Service();
        boolean status = false;

        try {
            // create new instances of remote Service objects
            aa.ServiceSoap port = service.getServiceSoap();

            status = port.processTransaction("G3T7", "lime", txnDescription);

            return status;
        } catch (Exception ex) {
            // may come here if a time out or any other exception occurs
            // what should you do here??
            System.out.println("What the hell...BakcOffice down...");
            return false;
        }

    }
}
