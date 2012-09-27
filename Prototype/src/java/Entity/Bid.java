package Entity;

import java.util.Date;
import java.sql.Timestamp;

// represents a bid (in a buy order)
public class Bid {

    private int bidId;
    private String stock;
    private int price; // bid price
    private String userId; // user who made this buy order
    private Timestamp timeStamp;
    private int transactionId;

    // constructor
    public Bid(String stock, int price, String userId) {
        this.stock = stock;
        this.price = price;
        this.userId = userId;
        Date now = new Date();
        this.timeStamp = new Timestamp(now.getTime());
    }
    //for retrieving from DB
    public Bid(int bidId, String username, String stock, int price, Timestamp timestamp, int transactionId) {
        this.bidId = bidId;
        this.userId = username;
        this.stock = stock;
        this.price = price;
        this.timeStamp = timestamp;
        this.transactionId = transactionId;
    }
    
    // getters
    public int getBidId() {
        return bidId;
    }
    
    public String getStock() {
        return stock;
    }

    public int getPrice() {
        return price;
    }

    public String getUserId() {
        return userId;
    }

    public Date getDate() {
        return new Date(this.timeStamp.getTime());
    }


    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public int getTransactionId() {
        return transactionId;
    }
    
    //setter
    public void setBidId(int bidId){
        this.bidId = bidId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Bid{" + "bidId=" + bidId + ", stock=" + stock + ", price=" + price + ", userId=" + userId + ", timeStamp=" + timeStamp + ", transactionId=" + transactionId + '}';
    }
    
}
