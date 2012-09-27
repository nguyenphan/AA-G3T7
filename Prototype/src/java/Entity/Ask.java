package Entity;

import java.util.Date;
import java.sql.Timestamp;

public class Ask {
    private int askID;
    private String username;
    private int stockID;
    private double price;
    private Timestamp order_date;
    private int transactionID;

    //new Ask to be added to DB
    public Ask(String username, int stockID, double price) {
        this.username = username;
        this.stockID = stockID;
        this.price = price;
        Date now = new Date();
        this.order_date = new Timestamp(now.getTime());
    }
    
    //for retrieving from DB
    public Ask(int askID, String username, int stockID, double price, Timestamp order_date, int transactionID) {
        this.askID = askID;
        this.username = username;
        this.stockID = stockID;
        this.price = price;
        this.order_date = order_date;
        this.transactionID = transactionID;
    }

    public int getAskID() {
        return askID;
    }

    public Timestamp getOrder_date() {
        return order_date;
    }

    public double getPrice() {
        return price;
    }

    public int getStockID() {
        return stockID;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public String getUsername() {
        return username;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    @Override
    public String toString() {
        return "Ask{" + "askID=" + askID + ", username=" + username + ", stockID=" + stockID + ", price=" + price + ", order_date=" + order_date + ", transactionID=" + transactionID + '}';
    }
    
}
