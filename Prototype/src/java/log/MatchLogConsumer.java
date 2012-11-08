/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

/**
 *
 * @author lenovo
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.*;
import java.io.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MatchLogConsumer implements ExceptionListener{
    
    //parameters
    String serverUrl = "tcp://localhost:61616";
    String userName = null;
    String password = null;
    
    String destinationName = "a.match";
    //String destinationName = "b.match"; 
    
    //variables
    Connection connection = null;
    Session session = null;
    MessageProducer msgProducer = null;
    MessageConsumer localMsgConsumer = null;
    MessageConsumer foreignMsgConsumer = null;
    Destination localDestination = null;
    Destination foreignDestination = null;
    
    public MatchLogConsumer(){
        try {
            runConnections();
            
        } catch (JMSException ex) {
            Logger.getLogger(MatchLogConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        try {
            MatchLogConsumer listener = new MatchLogConsumer();
            Message tmpMessage = null;
            while(true){
                
                PrintWriter outFile = null;
                //listening to local queue
                tmpMessage = listener.localMsgConsumer.receive(5000);
                
                if (tmpMessage != null) {
                    System.out.println("local: new income message received...");
                
                    try {

                        TextMessage msg = (TextMessage) tmpMessage;
                        System.out.println("Received Message > "+msg.getText());
                        Destination replyDestination = tmpMessage.getJMSReplyTo();
                        TextMessage replyMsg = listener.session.createTextMessage();
                        replyMsg.setJMSCorrelationID(tmpMessage.getJMSCorrelationID());
                        listener.msgProducer.send(replyDestination,replyMsg);

                        System.out.println("Reply Sent!");
                        String fileName = "./logs/matched.log";
                        outFile = new PrintWriter(new FileWriter(fileName, true));
                        outFile.append(msg.getText() + "\n");
                        outFile.close();

                    } catch (IOException ex) {
                        Logger.getLogger(MatchLogConsumer.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JMSException ex) {
                        Logger.getLogger(MatchLogConsumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            }
        } catch (JMSException ex) {
            Logger.getLogger(MatchLogConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void runConnections() throws JMSException{

        System.out.println("===Message Consumer===");
        Message tmpMsg = null;
        String msgType = "UNKNOWN";
        System.out.println("subscribing to destination > "+destinationName);
        
        //initialize JMS connection objects
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(serverUrl);
        connection = factory.createConnection(userName,password);
        session = connection.createSession(false,session.AUTO_ACKNOWLEDGE);
        connection.setExceptionListener(this);
        
        localDestination = session.createQueue(destinationName);
        
        localMsgConsumer = session.createConsumer(localDestination);
        
        msgProducer= session.createProducer(null);
        connection.start(); 
        System.out.println("Connection started...");
        
    }
    
    @Override
    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

}