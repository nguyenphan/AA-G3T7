package Entity;

import java.util.Date;

// represents a bid (in a buy order)
public class Bid {

    private int bidId;
    private String stock;
    private int price; // bid price
    private String userId; // user who made this buy order
    private long time;
    private int transactionId;

    // constructor
    public Bid(String stock, int price, String userId) {
        this.stock = stock;
        this.price = price;
        this.userId = userId;
        this.time = (new Date()).getTime();
    }
    //for retrieving from DB

    public Bid(int bidId, String username, String stock, int price, long time, int transactionId) {
        this.bidId = bidId;
        this.userId = username;
        this.stock = stock;
        this.price = price;
        this.time = time;
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

    public long getTime() {
        return time;
    }

    public Date getDate() {
        return new Date(time);
    }

    public int getTransactionId() {
        return transactionId;
    }

    //setter
    public void setBidId(int bidId) {
        this.bidId = bidId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Bid{ bidId:" + bidId + ", stock: " + stock + ", price: " + price + ", userId: " + userId + ", date: " + getDate() + ", transactionId:" + transactionId + '}';
    }
}
