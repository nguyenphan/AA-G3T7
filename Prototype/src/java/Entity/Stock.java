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
    private int id;
    private String name;

    public Stock(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Stock{" + "id=" + id + ", name=" + name + '}';
    }
    
}
