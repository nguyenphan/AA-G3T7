package aa;

import Database.*;
import Entity.*;
import concurrency.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import static java.util.Arrays.asList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExchangeBean {

    // location of log files - change if necessary
    private final String MATCH_LOG_FILE = "c:\\temp\\matched.log";
    private final String REJECTED_BUY_ORDERS_LOG_FILE = "c:\\temp\\rejected.log";
    // used to calculate remaining credit available for buyers
    private final int DAILY_CREDIT_LIMIT_FOR_BUYERS = 1000000;
    // used to keep track of all matched transactions (asks/bids) in the system
    // matchedTransactions is cleaned once the records are written to the log file successfully
    private ArrayList<MatchedTransaction> matchedTransactions = new ArrayList<MatchedTransaction>();
    // keeps track of the latest price for each of the 3 stocks
    private int latestPriceForSmu = -1;
    private int latestPriceForNus = -1;
    private int latestPriceForNtu = -1;

    // this method is called once at the end of each trading day. It can be called manually, or by a timed daemon
    // this is a good chance to "clean up" everything to get ready for the next trading day
    public void endTradingDay() {
        // reset attributes
        latestPriceForSmu = -1;
        latestPriceForNus = -1;
        latestPriceForNtu = -1;

        // dump all unfulfilled buy and sell orders
        BidDAO bidDAO = new BidDAO();
        bidDAO.clearUnfulfilledBids();

        AskDAO askDAO = new AskDAO();
        askDAO.clearUnfulfilledAsks();

        // reset all credit limits of users
        TraderDAO traderDAO = new TraderDAO();
        traderDAO.resetCreditsForAllTraders();
    }

    // returns a String of unfulfilled bids for a particular stock
    // returns an empty string if no such bid
    // bids are separated by <br> for display on HTML page
    public String getUnfulfilledBidsForDisplay(String stock) {
        BidDAO bidDAO = new BidDAO();
        ArrayList<Bid> unfulfilledBids = bidDAO.getUnfulfilledBidsForStock(stock);

        StringBuilder returnString = new StringBuilder();
        for (Bid bid : unfulfilledBids) {
            returnString.append(bid.toString() + "<br />");
        }
        return returnString.toString();
    }

    // returns a String of unfulfilled asks for a particular stock
    // returns an empty string if no such ask
    // asks are separated by <br> for display on HTML page
    public String getUnfulfilledAsks(String stock) {
        AskDAO askDAO = new AskDAO();
        ArrayList<Ask> unfulfilledAsks = askDAO.getUnfulfilledAsksForStock(stock);

        StringBuilder returnString = new StringBuilder();
        for (Ask ask : unfulfilledAsks) {
            returnString.append(ask.toString() + "<br />");
        }
        return returnString.toString();
    }

    // returns the highest bid for a particular stock
    // returns -1 if there is no bid at all
    public int getHighestBidPrice(String stock) throws SQLException {
        Bid highestBid = getHighestBid(stock);
        if (highestBid == null) {
            return -1;
        } else {
            return highestBid.getPrice();
        }
    }

    // retrieve unfulfiled current (highest) bid for a particular stock
    // returns null if there is no unfulfiled bid for this stock
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

    // returns the lowest ask for a particular stock
    // returns -1 if there is no ask at all
    public int getLowestAskPrice(String stock) throws SQLException {
        Ask lowestAsk = getLowestAsk(stock);
        if (lowestAsk == null) {
            return -1;
        } else {
            return lowestAsk.getPrice();
        }
    }

    // retrieve unfulfiled current (lowest) ask for a particular stock
    // returns null if there is no unfulfiled asks for this stock
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

    // check if a buyer is eligible to place an order based on his credit limit
    // if he is eligible, this method adjusts his credit limit and returns true
    // if he is not eligible, this method logs the bid and returns false
    private boolean validateCreditLimit(Bid b) throws Exception {

        TraderDAO traderDAO = new TraderDAO();

        //start transaction
        Connection conn = null;
        try {

            conn = ConnectionFactory.getInstance().getConnection();
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);   //phantom reads okay, non-repeatable not allowed. we don't want credit value to be changed midway.
            conn.setAutoCommit(false);

            //get trader from database
            Trader trader = traderDAO.getTraderWithUsername(conn, b.getUserId());   //will be read locked.

            // calculate the total price of this bid, each bid is for 1000 shares
            int totalPriceOfBid = b.getPrice() * 1000;

            //check if trader has sufficient credit
            boolean sufficientCredit = false;
            if (totalPriceOfBid > trader.getCredit()) {

                //insufficient, log failure
                logRejectedBuyOrder(b);

            } else {

                //sufficient, deduct credit and update database
                trader.deductCredit(totalPriceOfBid);
                traderDAO.update(conn, trader);
                sufficientCredit = true;

            }

            conn.commit();  //release locks
            return sufficientCredit;

        } catch (SQLException e) {

            //TODO: error handling
            return false;

        } finally {

            //release connection
            conn.close();

        }

    }

    // call this to append all rejected buy orders to log file
    // TODO: Concurrency Lock
    private void logRejectedBuyOrder(Bid b) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(REJECTED_BUY_ORDERS_LOG_FILE, true));
            outFile.append(b.toString() + "\n");
            outFile.close();
        } catch (IOException e) {
            // Think about what should happen here...
            System.out.println("IO EXCEPTIOn: Cannot write to file");
            e.printStackTrace();
        } catch (Exception e) {
            // Think about what should happen here...
            System.out.println("EXCEPTION: Cannot write to file");
            e.printStackTrace();
        }
    }

    // call this to append all matched transactions in matchedTransactions to log file and clear matchedTransactions
    // TODO: Concurrency Lock
    private void logMatchedTransactions() {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(MATCH_LOG_FILE, true));
            for (MatchedTransaction m : matchedTransactions) {
                outFile.append(m.toString() + "\n");
            }
            matchedTransactions.clear(); // clean this out
            outFile.close();
        } catch (IOException e) {
            // Think about what should happen here...
            System.out.println("IO EXCEPTIOn: Cannot write to file");
            e.printStackTrace();
        } catch (Exception e) {
            // Think about what should happen here...
            System.out.println("EXCEPTION: Cannot write to file");
            e.printStackTrace();
        }
    }

    // returns a string of HTML table rows code containing the list of user IDs and their remaining credits
    // this method is used by viewOrders.jsp for debugging purposes
    public String getAllCreditRemainingForDisplay() {

        String returnString = "";

        TraderDAO traderDAO = new TraderDAO();
        ArrayList<Trader> traders = traderDAO.getAllTraders();

        for (Trader t : traders) {
            returnString += "<tr><td>" + t.getUsername() + "</td><td>" + t.getCredit() + "</td></tr>";
        }

        return returnString;
    }

    // call this method immediatley when a new bid (buying order) comes in
    // this method returns false if this buy order has been rejected because of a credit limit breach
    // it returns true if the bid has been successfully added
    public boolean placeNewBidAndAttemptMatch(Bid newBid) throws Exception {

        //check if buyer has enough credit
        boolean okToContinue = validateCreditLimit(newBid);
        if (!okToContinue) {
            return false;
        }

        //sufficient credits, add bid to database and check for match
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<Object[]>> results = executor.invokeAll(asList(new AddEntityOperation(newBid), new CheckMatchOperation(newBid)));
        executor.shutdown();

        boolean isMatched = false;
        Ask lowestAsk = null;
        for (Future<Object[]> result : results) {
            Object[] resultArray = result.get();
            if (resultArray != null) {
                lowestAsk = (Ask) resultArray[0];
                isMatched = Boolean.parseBoolean((String) resultArray[1]);
            }
        }

        System.out.println("isMatched = " + isMatched);

        //matched, create transaction
        if (isMatched) {

            Connection conn = null;
            try {

                conn = ConnectionFactory.getInstance().getConnection();
                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                conn.setAutoCommit(false);

                BidDAO bidDAO = new BidDAO();
                AskDAO askDAO = new AskDAO();

                //lock ask and bid for update
                bidDAO.lockForUpdate(conn, newBid);
                askDAO.lockForUpdate(conn, lowestAsk);

                //this is a buying transaction, so transaction price is price of ask
                MatchedTransaction mt = new MatchedTransaction(newBid, lowestAsk, newBid.getDate(), lowestAsk.getPrice());
                MatchedTransactionDAO.add(conn, mt);  //transaction id will be generated here

                //TODO: log transaction, make concurrent
                logMatchedTransactions();

                //TODO: send to back office, make concurrent


                //update latest price
                updateLatestPrice(mt);

                //update transaction id in bid and ask, and save to database
                newBid.setTransactionId(mt.getTransactionId());
                lowestAsk.setTransactionID(mt.getTransactionId());
                bidDAO.update(conn, newBid);
                askDAO.update(conn, lowestAsk);

                //finished transaction, release locks
                conn.commit();

            } catch (SQLException e) {
                
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

        //acknowledge bid, even if match failed.
        return true;
    }

    // call this method immediatley when a new ask (selling order) comes in
    public void placeNewAskAndAttemptMatch(Ask newAsk) throws Exception {

        //add ask to database and check for match
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Future<Object[]>> results = executor.invokeAll(asList(new AddEntityOperation(newAsk), new CheckMatchOperation(newAsk)));
        executor.shutdown();

        boolean isMatched = false;
        Bid highestBid = null;

        for (Future<Object[]> result : results) {
            Object[] resultArray = result.get();
            if (resultArray != null) {
                highestBid = (Bid) resultArray[0];
                isMatched = Boolean.parseBoolean((String) resultArray[1]);
            }
        }

        System.out.println("isMatched = " + isMatched);
        
        //matched:
        if (isMatched) {

            Connection conn = null;

            try {
                
                conn = ConnectionFactory.getInstance().getConnection();
                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                conn.setAutoCommit(false);
                
                BidDAO bidDAO = new BidDAO();
                AskDAO askDAO = new AskDAO();
                
                //lock ask and bid for update
                bidDAO.lockForUpdate(conn, highestBid);
                askDAO.lockForUpdate(conn, newAsk);
                
                //create transaction and save in database
                //this is a selling transaction, so transaction price is the price of bid
                MatchedTransaction mt = new MatchedTransaction(highestBid, newAsk, newAsk.getDate(), highestBid.getPrice());
                MatchedTransactionDAO.add(conn, mt);    //transactionID will also be generated at the same time.

                //TODO: log transaction, make concurrent
                logMatchedTransactions();

                //TODO: send to back office, make concurrent
                
                //update latest price
                updateLatestPrice(mt);
                
                //update transaction id in bid and ask, and save to database
                highestBid.setTransactionId(mt.getTransactionId());
                newAsk.setTransactionID(mt.getTransactionId());
                bidDAO.update(conn, highestBid);
                askDAO.update(conn, newAsk);
                
                //finished, release locks
                conn.commit();
                
            } catch (SQLException e) {

                e.printStackTrace();
                
                if (conn != null) {
                    conn.rollback();
                }

            } finally {

                if (conn != null) {
                    conn.close();
                }

            }

        }
        
        
    }

    // updates either latestPriceForSmu, latestPriceForNus or latestPriceForNtu
    // based on the MatchedTransaction object passed in
    // TODO: Concurrency Lock
    private void updateLatestPrice(MatchedTransaction m) {
        String stock = m.getStock();
        int price = m.getPrice();
        // update the correct attribute
        if (stock.equals("smu")) {
            latestPriceForSmu = price;
        } else if (stock.equals("nus")) {
            latestPriceForNus = price;
        } else if (stock.equals("ntu")) {
            latestPriceForNtu = price;
        }
    }

    // updates either latestPriceForSmu, latestPriceForNus or latestPriceForNtu
    // based on the MatchedTransaction object passed in
    // TODO: Concurrency Lock
    public int getLatestPrice(String stock) {
        if (stock.equals("smu")) {
            return latestPriceForSmu;
        } else if (stock.equals("nus")) {
            return latestPriceForNus;
        } else if (stock.equals("ntu")) {
            return latestPriceForNtu;
        }
        return -1; // no such stock
    }
}
