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
    String username;
    double credit;

    public Trader(String username, double credit) {
        this.username = username;
        this.credit = credit;
    }
    
    public String getUsername() {
        return username;
    }

    public double getCredit() {
        return credit;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "Trader{" + "username=" + username + ", credit=" + credit + '}';
    }
    
    
}
