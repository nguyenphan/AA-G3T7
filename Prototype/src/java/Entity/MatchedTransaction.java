package Entity;

import java.util.Date;
import java.sql.Timestamp;

// represents a matched bid and ask
public class MatchedTransaction {

    private int transactionId;
    private Bid bid;
    private Ask ask;
    private Date date;
    private int price;

    // constructor
    public MatchedTransaction(Bid b, Ask a, Date d, int p) {
        this.bid = b;
        this.ask = a;
        this.date = d;
        this.price = p;
    }

    // getters
    public int getTransactionId() {
        return transactionId;
    }

    public int getBidID() {
        return bid.getBidId();
    }

    public String getBuyerId() {
        return bid.getUserId();
    }

    public int getAskID() {
        return ask.getAskId();
    }

    public String getSellerId() {
        return ask.getUserId();
    }

    public String getStock() {
        return bid.getStock();  //or ask.getStock()
    }

    public int getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }
    
    public Timestamp getTimestamp(){
        return new Timestamp(date.getTime());
    }

    //setters
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    @Override
    public String toString() {
        return "MatchedTransaction{" + "bid=" + bid + ", ask=" + ask + ", date=" + date + ", price=" + price + '}';
    }
}
