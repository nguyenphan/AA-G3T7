/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

/**
 *
 * @author Shi Ling Tai
 */
public class Stock {
    private String name;

    public Stock(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Stock{" + ", name=" + name + '}';
    }
    
}
