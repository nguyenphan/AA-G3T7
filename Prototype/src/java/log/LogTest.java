/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

/**
 *
 * @author lenovo
 */
public class LogTest {
    public static void main(String[] args){
        LogProducer testProducer = new LogProducer();
        testProducer.sendMessage("Test 1");
    }
}
