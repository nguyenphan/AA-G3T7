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
                    ipObject = InetAddress.getByName(randomAddress1);
                    if (random2Reached) {
                        System.out.println("Master is in control...");
                        sender.detectBackOffice();
                    } else {
                        boolean random3Reached = false;
                        ipObject = InetAddress.getByName(randomAddress1);
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
        ArrayList<String> logList = new ArrayList<String>();
        Connection conn = null;
        ConnectionFactory conFact = ConnectionFactory.getInstance();

        int currentSQLStringIndex = conFact.getCurrentSQLStringIndex();
        try {

            conn = conFact.getConnectionForCurrentSQLStringIndex(currentSQLStringIndex);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            conn.setAutoCommit(false);

            //fetch all logs
            ArrayList<MatchedTransaction> transList = MatchedTransactionDAO.getUnsentMatchedTransactions(conn);
            for (MatchedTransaction tmpLog : transList) {

                MatchedTransaction mtFromDB = null;
                //lock row
                try {
                    mtFromDB = MatchedTransactionDAO.lockForUpdate(conn, tmpLog);
                } catch (SQLException e) { //match is being locked by another thread, continue
                    continue;
                }

                //double check whether another thread has sendt the log.
                if (!mtFromDB.getSentToBackOffice()) {

                    Bid b = BidDAO.getBid(conn, mtFromDB.getBidID());
                    Ask a = AskDAO.getAsk(conn, mtFromDB.getAskID());
                    tmpLog.setBid(b);
                    tmpLog.setAsk(a);
                    
                    //send to back office
                    if (sendToBackOffice(tmpLog.toString())) {
                        //update flag if sending successful
                        tmpLog.setSendToBackOffice(true);
                        MatchedTransactionDAO.updateMatchedTransactions(conn, tmpLog);
                        System.out.println();

                    } else {
                        System.out.println("");
                    }
                }
            }
            //finished transaction, release locks
            conn.commit();

            conFact.confirmWorkingConnectionStringIndex(currentSQLStringIndex);

        } catch (SQLException e) {
            currentSQLStringIndex = conFact.anotherConnectionStringIndexDifferentFromIndex(currentSQLStringIndex);
            e.printStackTrace();
            //error! rollback.
            if (conn != null) {
                conn.rollback();
            }

        } finally {

            if (conn != null) {
                conn.close();
            }

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
