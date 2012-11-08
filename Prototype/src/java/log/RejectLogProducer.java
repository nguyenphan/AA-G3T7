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
import org.apache.activemq.ActiveMQConnectionFactory;

public class RejectLogProducer implements ExceptionListener{

    //parameters
    String serverUrl = "tcp://192.168.1.111:61616";
    String userName = null;
    String password = null;
    //for master web server
    String localDestinationName = "a.reject";
    String foreignDestinationName = "b.reject";
    String localReplyDestinationName = "a.a.reject";
    String foreignReplyDestinationName = "a.b.reject";
    
    //String localDestinationName = "a.reject";
    //String foreignDestinationName = "b.reject";
    //String localReplyDestinationName = "b.a.reject";
    //String foreignReplyDestinationName = "b.b.reject";
    
    //for slave web server
    
    //variables
    Connection connection = null;
    Session session = null;
    MessageProducer msgProducer = null;
    MessageConsumer localReplyConsumer = null;
    MessageConsumer foreignReplyConsumer = null;
    
    //local = queue for this webserver, foreigh = queue for the other webserver 
    Destination localDestination = null;
    Destination foreignDestination = null;
    //set up reply destinations
    Destination localReplyDestination = null;
    Destination foreignReplyDestination = null;
    
    public RejectLogProducer(){
        try {
            runConnections();
        } catch (JMSException ex) {
            Logger.getLogger(RejectLogProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args){
            new RejectLogProducer();
    }
    
    public void sendMessage(String inputMsg){
        try {
            //create text message
            TextMessage msg = session.createTextMessage();
            msg.setStringProperty("logType","match");
            msg.setText(inputMsg);
            
            TextMessage msg2 = session.createTextMessage();
            msg2.setStringProperty("logType","match");
            msg2.setText(inputMsg);
            msg.setJMSReplyTo(localReplyDestination);
            msg2.setJMSReplyTo(foreignReplyDestination);
            
            msgProducer.send(localDestination,msg);
            msgProducer.send(foreignDestination,msg2);
            
            System.out.println("Message Type > "+msg.getStringProperty("logType"));
            System.out.println("Sent Message > "+msg.getText());
            System.out.println("Sent Message > "+msg2.getText());
            
            boolean wait = true;
            while(wait){
                
                Message localReply = localReplyConsumer.receive(60000);
                Message foreignReply = foreignReplyConsumer.receive(60000);
                if(localReply==null||foreignReply==null){
                    wait = false;
                    break;
                }else{
                    TextMessage tmpMsg = (TextMessage) localReply;
                    System.out.println("local reply received!");
                    
                    tmpMsg = (TextMessage) localReply;
                    System.out.println("foreign reply received!");
                    wait = false;
                    break;
                }
            }
            connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(RejectLogProducer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    void runConnections() throws JMSException{

        System.out.println("===Message Producer===");
        System.out.println("subscribing to local destination > "+localDestinationName);
        System.out.println("subscribing to foreign destination > "+foreignDestinationName);

        //initialize JMS connection objects
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(userName, password, serverUrl);
            
        //ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);
        connection = factory.createConnection(userName,password);
        session = connection.createSession(false,session.AUTO_ACKNOWLEDGE);
        connection.setExceptionListener(this);
        
        localDestination = session.createQueue(localDestinationName);
        foreignDestination = session.createQueue(foreignDestinationName);
        localReplyDestination = session.createQueue(localReplyDestinationName);
        foreignReplyDestination = session.createQueue(foreignReplyDestinationName);
        
        msgProducer = session.createProducer(null);
        localReplyConsumer = session.createConsumer(localReplyDestination);
        foreignReplyConsumer = session.createConsumer(foreignReplyDestination);
        
        //start connection
        connection.start();
        System.out.println("Connection started...");
        System.out.println("Local Destination > "+localDestinationName);
        System.out.println("Foreign Destination > "+foreignDestinationName);
        System.out.println("Local Reply Destination > "+localReplyDestinationName);
        System.out.println("Foreign Reply Destination > "+foreignReplyDestinationName);
        
        
    }
	

    @Override
    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
