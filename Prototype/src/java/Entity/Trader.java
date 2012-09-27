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

    public void deductCredit(double amt){
        this.credit -=amt;
    }
    
    @Override
    public String toString() {
        return "Trader{" + "username=" + username + ", credit=" + credit + '}';
    }
    
    
}
