/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.BoundedPriorityBlockingQueue;
import event.EventBean;
import java.util.Collection;
import java.util.Queue;
import log.MyLogger;
import qosmonitor.QoSConstraint;

/**
 *
 * @author epaln
 */
public abstract class EPAgent extends Thread {

    protected String _type;
    protected TopicReceiver[] _receivers;
    protected String _info;
    protected Queue<EventBean> [] _selectedEvents; 
    protected BoundedPriorityBlockingQueue _outputQueue;
    public int TTL; // ttl value, which respect to the starvation problem...
    protected OQNotifier _outputNotifier;
    private QoSConstraint qosConstraint;
    public volatile float sumLatencies=0;
    public volatile int numEventNotifiedNetwork=0; // number of events notified over the network via Relayer.callPublish(...)
    public volatile long numEventNotified=0;       // number of events notified either by Relayer.callPublish(...) or by pub/sub
    public volatile int numAchievedNotifications=0; // number of calls to Relayer.callPublish(...)
    public volatile int numEventProcessed =0;
    public volatile long processingTime=0;
    protected short selectionMode=SelectionMode.MODE_PRIORITY;
    protected MyLogger logger;

   
    public EPAgent() {
        _selectedEvents = new Queue[2];
        _receivers = new TopicReceiver[2];
        _outputQueue = new BoundedPriorityBlockingQueue(this);
        TTL = _outputQueue.getCapacity()*2; // avoiding starvation after two time the capacity of the output queue
        qosConstraint = new QoSConstraint();
    }

    public BoundedPriorityBlockingQueue getOutputQueue() {
        return _outputQueue;
    }

    public int getNumAchievedNotifications() {
        return numAchievedNotifications;
    }

    public void setNumAchievedNotifications(int numAchievedNotifications) {
        this.numAchievedNotifications = numAchievedNotifications;
    }

    public abstract Collection<IOTerminal> getInputTerminals();

    public abstract IOTerminal getOutputTerminal();

    public String[] getInputTopics() {
        String[] topics = new String[getInputTerminals().size()];
        short i = 0;
        for (IOTerminal t : getInputTerminals()) {
            topics[i] = t.getTopic();
        }
        return topics;
    }

    public MyLogger getLogger() {
        return logger;
    }

    
    public String getOutputTopic() {
        return getOutputTerminal().getTopic();
    }
  
    public abstract void process();

    public abstract boolean fetch();

    public boolean openIOchannels() {

        for (IOTerminal input : getInputTerminals()) {
            input.open();
        }

        getOutputTerminal().open();
        
        return true;
    }

    @Override
    public void run() {
//        try {
//            Thread.sleep(50000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(EPAgent.class.getName()).log(Level.SEVERE, null, ex);
//        }
        while (true) {
            if (fetch()) {
                process();
            }
        }
    }

    
    public int getTTL() {
        return TTL;
    }

    public OQNotifier getOutputNotifier() {
        return _outputNotifier;
    }

    public QoSConstraint getQosConstraint() {
        return qosConstraint;
    }

    public void setQosConstraint(QoSConstraint qosConstraint) {
        this.qosConstraint = qosConstraint;
    }

    
    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }

    public String getInfo() {
        return _info;
    }

    public void setInfo(String _info) {
        this._info = _info;
    }

    @Override
    public String toString() {
        return _type;
    }

    public short getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(short selectionMode) {
        this.selectionMode = selectionMode;
    }
    
    
}