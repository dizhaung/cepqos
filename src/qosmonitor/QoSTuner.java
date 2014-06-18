/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qosmonitor;

import core.EPAgent;
import core.IOTerminal;

/**
 *
 * @author epaln
 */
public class QoSTuner {
    
    private EPAgent agent;
    
    public static final short NOTIFICATION_PRIORITY = 0;
    public static final short NOTIFICATION_BATCH = 1;
    
    public static final short QUEUE_REPLACE = 0; // replace an event in the queue in case it is full
    public static final short QUEUE_IGNORE = 1; // cancel the event insertion
    public static final short QUEUE_NOTIFY = 2; // notify events in the ouptut queue

    public QoSTuner() {
        
    }

    public QoSTuner(EPAgent agent) {
        this.agent = agent;
    }
    
    
    public void bound(EPAgent agt){
        agent = agt;
    }
    public void unbound(){
        agent = null;
    }
    
    public void setInputQueuesCapacity(int capacity){
        if(agent!=null){
            for(IOTerminal io: agent.getInputTerminals()){
                io.getReceiver().getInputQueue().setCapacity(capacity);
            }
        }   
    }
    public void setOutputQueueCapacity(int capacity){
        if(agent!=null){
            agent.getOutputQueue().setCapacity(capacity);
        }
    }
    
    public void setFullInputQStrategy(short strategy){
        if(agent!=null){
            for(IOTerminal io: agent.getInputTerminals()){
                io.getReceiver().getInputQueue().setStrategy(strategy);
            }
        }   
    }
    
    public void setFullOututQStrategy(short strategy){
        if(agent!=null)
            agent.getOutputQueue().setStrategy(strategy);
    }
    
    public void setNotificationStrategy(short strategy){
        if(agent!=null){
            agent.getOutputNotifier().setStrategy(strategy);
        }
    }
     public void setNotificationBatchSize(int batch_size){
        if(agent!=null){
            agent.getOutputNotifier().setBatch_size(batch_size);
        }
    }
     
     public void setNotificationBatchSize(int batch_size, long timeout){
        if(agent!=null){
            agent.getOutputNotifier().setBatch_size(batch_size);
            agent.getOutputNotifier().setTimeout(timeout);
        }
    }
}
