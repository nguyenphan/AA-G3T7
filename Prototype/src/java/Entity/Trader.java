/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

/**
 *
 * @author ptlenguyen
 */
public class Trader {
    
    final static int DEFAULT_CREDIT_LIMIT = 1000000;
    
    private String username;
    private int credit;

    public Trader(String username) {
        this.username = username;
        this.credit = DEFAULT_CREDIT_LIMIT;
    }
    
    public Trader(String username, int credit) {
        this.username = username;
        this.credit = credit;
    }
    
    public String getUsername() {
        return username;
    }

    public int getCredit() {
        return credit;
    }

    public void deductCredit(int amt){
        this.credit -=amt;
    }
    
    @Override
    public String toString() {
        return "Trader{" + "username=" + username + ", credit=" + credit + '}';
    }
    
    
}
