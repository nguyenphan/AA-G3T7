package aa;

import java.io.*;
import java.util.*;

import static java.util.Arrays.asList;
import concurrency.*;

import Database.*;
import Entity.*;
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
    public int getHighestBidPrice(String stock) {
        Bid highestBid = getHighestBid(stock);
        if (highestBid == null) {
            return -1;
        } else {
            return highestBid.getPrice();
        }
    }

    // retrieve unfulfiled current (highest) bid for a particular stock
    // returns null if there is no unfulfiled bid for this stock
    private Bid getHighestBid(String stock) {
        BidDAO bidDAO = new BidDAO();
        return bidDAO.getHighestBidForStock(stock);
    }

    // returns the lowest ask for a particular stock
    // returns -1 if there is no ask at all
    public int getLowestAskPrice(String stock) {
        Ask lowestAsk = getLowestAsk(stock);
        if (lowestAsk == null) {
            return -1;
        } else {
            return lowestAsk.getPrice();
        }
    }

    // retrieve unfulfiled current (lowest) ask for a particular stock
    // returns null if there is no unfulfiled asks for this stock
    private Ask getLowestAsk(String stock) {
        AskDAO askDAO = new AskDAO();
        return askDAO.getLowestAskForStock(stock);
    }

    // check if a buyer is eligible to place an order based on his credit limit
    // if he is eligible, this method adjusts his credit limit and returns true
    // if he is not eligible, this method logs the bid and returns false
    private boolean validateCreditLimit(Bid b) throws Exception {

        //get trader from database
        TraderDAO traderDAO = new TraderDAO();
        Trader trader = traderDAO.getTraderWithUsername(b.getUserId());

        // calculate the total price of this bid
        int totalPriceOfBid = b.getPrice() * 1000; // each bid is for 1000 shares

        if (totalPriceOfBid > trader.getCredit()) {
            // no go - log failed bid and return false
            logRejectedBuyOrder(b);
            return false;

        } else {
            // it's ok - adjust credit limit and return true
            trader.deductCredit(totalPriceOfBid);

            //save to database
            traderDAO.update(trader);
            return true;
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

        //no match, return immedietely
        if (lowestAsk == null || !isMatched) {
            return true;
        }

        //matched:

        //create transaction
        //this is a buying transaction, so transaction price is price of ask
        MatchedTransaction mt = new MatchedTransaction(newBid, lowestAsk, newBid.getDate(), lowestAsk.getPrice());
        MatchedTransactionDAO mtDAO = new MatchedTransactionDAO();
        mtDAO.add(mt);  //transaction id will be generated here

        //update transactionID in ask and bid
        BidDAO bidDAO = new BidDAO();
        AskDAO askDAO = new AskDAO();

        newBid.setTransactionId(mt.getTransactionId());
        bidDAO.update(newBid);
        lowestAsk.setTransactionID(mt.getTransactionId());
        askDAO.update(lowestAsk);

        //log
        logMatchedTransactions();

        //update latest price
        updateLatestPrice(mt);

        //TODO: send to back end office 

        //acknowledge bid
        return true;
    }

    // call this method immediatley when a new ask (selling order) comes in
    public void placeNewAskAndAttemptMatch(Ask newAsk) throws Exception {

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
        //matched:
        if (isMatched) {

            //save ask to database
            AskDAO askDAO = new AskDAO();


            //get highest bid for stock
            BidDAO bidDAO = new BidDAO();

            //create transaction
            //this is a selling transaction, so transaction price is the price of bid
            MatchedTransaction mt = new MatchedTransaction(highestBid, newAsk, newAsk.getDate(), highestBid.getPrice());

            //save transaction
            MatchedTransactionDAO mtDAO = new MatchedTransactionDAO();
            mtDAO.add(mt);

            //update transaction id in bid and ask
            highestBid.setTransactionId(mt.getTransactionId());
            bidDAO.update(highestBid);
            newAsk.setTransactionID(mt.getTransactionId());
            askDAO.update(newAsk);

            //update latest price
            updateLatestPrice(mt);

            //log transaction
            logMatchedTransactions();

            //TODO: send to back office
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
    
    public boolean sendToBackOffice(String txnDescription){
      aa.Service service = new aa.Service();
      boolean status = false;
      
      try {
        // create new instances of remote Service objects
        aa.ServiceSoap port = service.getServiceSoap();

        // invoke the remote method by calling port.processTransaction().
        // processTransaction() will return false if the teamID &/or password is wrong
        // it will return true if the web service is correctly called
        status = port.processTransaction("G3T7", "lime", txnDescription);
        return status;
      }
      catch (Exception ex) {
          // may come here if a time out or any other exception occurs
          // what should you do here??
      }
      return false; // failure due to exception
  }

}
