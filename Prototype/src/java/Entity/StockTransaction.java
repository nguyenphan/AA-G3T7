/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

/**
 *
 * @author ptlenguyen
 */
public class StockTransaction {
    private int transactionId;
    private int askId;
    private int bidId;
    private int price;

    public StockTransaction(int askId, int bidId, int price) {
        this.askId = askId;
        this.bidId = bidId;
        this.price = price;
    }
    
    // for DB
    public StockTransaction(int transactionId, int askId, int bidId, int price) {
        this.transactionId = transactionId;
        this.askId = askId;
        this.bidId = bidId;
        this.price = price;
    }
    
    public int getTransactionId() {
        return transactionId;
    }

    public int getAskId() {
        return askId;
    }

    public int getBidId() {
        return bidId;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "StockTransaction{" + "transactionId=" + transactionId + ", askId=" + askId + ", bidId=" + bidId + ", price=" + price + '}';
    }
    
}
