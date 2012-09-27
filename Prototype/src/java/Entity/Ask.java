package Entity;

import java.util.Date;
import java.sql.Timestamp;
// represents an Ask (in a sell order)
public class Ask {

  private int askId;
  private String stock;
  private int price; // ask price
  private String userId; // user who made this sell order
  private int transactionID;
  private Timestamp timestamp;

  // constructor
  public Ask(String stock, int price, String userId) {
    this.stock = stock;
    this.price = price;
    this.userId = userId;
    
    Date now = new Date();
    this.timestamp = new Timestamp(now.getTime());
    
  }
  
  //for retrieving from DB
    public Ask(int askID, String username, String stock, int price, Timestamp order_date, int transactionID) {
        this.askId = askID;
        this.userId = username;
        this.stock = stock;
        this.price = price;
        this.timestamp = order_date;
        this.transactionID = transactionID;
    }

  // getters

    public int getAskId() {
        return askId;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
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
        return new Date(this.timestamp.getTime());
    }
    
    // setter

    public void setAskId(int askId) {
        this.askId = askId;
    }
    
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }
    
    
    // toString
    public String toString() {
        return "Ask{" + "askID=" + askId + ", username=" + userId + ", stockID=" + stock + ", price=" + price + ", order_date=" + this.getDate() + ", transactionID=" + transactionID + '}';
    }
}