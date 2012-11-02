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
public class LogConsumer implements ExceptionListener{
    
    //parameters
    String serverUrl = "127.0.0.1,127.0.0.1,127.0.0.1,127.0.0.1,127.0.0.1,127.0.0.1";
    String userName = null;
    String password = null;
    //for consumer on master webserver
    //String destinationName = "master.local.request";
    String destinationName = "slave.foreign.request";
    
    //for consumer on master webserver
    //String destinationName = "master.foreign.request";
    //String destinationName = "slave.local.request";
    
    //variables
    Connection connection = null;
    Session session = null;
    MessageProducer msgProducer = null;
    MessageConsumer msgConsumer = null;
    Destination destination = null;
    public LogConsumer(){
        try {
            runConnections();
        } catch (JMSException ex) {
            Logger.getLogger(LogConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        LogConsumer listener = new LogConsumer();
        while(true){
            {
                PrintWriter outFile = null;
                try {
                    Message tmpMessage = listener.msgConsumer.receive(10000);
                    if (tmpMessage != null) {
                        TextMessage msg = (TextMessage) tmpMessage;
                        System.out.println("Received Message > "+msg.getText());
                        Destination replyDestination = tmpMessage.getJMSReplyTo();
                        TextMessage replyMsg = listener.session.createTextMessage();
                        replyMsg.setJMSCorrelationID(tmpMessage.getJMSCorrelationID());
                        listener.msgProducer.send(replyDestination,replyMsg);
                        
                        System.out.println("Reply Sent!");
                        String fileName = "c:\\test.log";
                        outFile = new PrintWriter(new FileWriter(fileName, true));
                        outFile.append(msg.getText() + "\n");
                        outFile.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(LogConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }catch (JMSException ex) {
                   Logger.getLogger(LogConsumer.class.getName()).log(Level.SEVERE, null, ex);
               }
            }
        }    
    }
    void runConnections() throws JMSException{

        System.out.println("===Message Consumer===");
        Message tmpMsg = null;
        String msgType = "UNKNOWN";
        System.out.println("subscribing to destination > "+destinationName);

        //initialize JMS connection objects
        ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);
        /*
        factory.setReconnAttemptCount(10);
        factory.setReconnAttemptDelay(1000);
        factory.setReconnAttemptTimeout(1000);
        */
        connection = factory.createConnection(userName,password);
        session = connection.createSession(false,session.AUTO_ACKNOWLEDGE);
        connection.setExceptionListener(this);
        destination = session.createQueue(destinationName);
        msgConsumer = session.createConsumer(destination);
        msgProducer= session.createProducer(null);
        //start connection
        connection.start(); 
        System.out.println("Connection started...");
        
    }
    
    @Override
    public void onException(JMSException jmse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}