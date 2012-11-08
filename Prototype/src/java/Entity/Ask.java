package Entity;

import java.util.Date;

// represents an Ask (in a sell order)
public class Ask {

  private int askId;
  private String stock;
  private int price; // ask price
  private String userId; // user who made this sell order
  private int transactionID;
  private long time;

  //default constructor
  public Ask(){}
  
  // constructor
  public Ask(String stock, int price, String userId) {
    this.stock = stock;
    this.price = price;
    this.userId = userId;
    this.time = (new Date()).getTime();
  }
  
  //for retrieving from DB
    public Ask(int askID, String username, String stock, int price, long time, int transactionID) {
        this.askId = askID;
        this.userId = username;
        this.stock = stock;
        this.price = price;
        this.time = time;
        this.transactionID = transactionID;
    }

  // getters

    public int getAskId() {
        return askId;
    }

    public int getTransactionID() {
        return transactionID;
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

    public long getTime(){
        return time;
    }
    
    public Date getDate() {
        return new Date(time);
    }
    
    // setter

    public void setAskId(int askId) {
        this.askId = askId;
    }
    
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    @Override
    public String toString() {
        return "Ask{" + "askId=" + askId + ", stock=" + stock + ", price=" + price + ", userId=" + userId + ", time=" + time + ", transactionID=" + transactionID + '}';
    }
}