/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.BoundedPriorityBlockingQueue;
import com.google.common.collect.Queues;
import event.EventBean;
import java.util.Collection;
import java.util.Queue;

/**
 *
 * @author epaln
 */
public abstract class EPAgent extends Thread {

    protected String _type;
    protected TopicReceiver _receiver;
    protected String _info;
    protected Queue<EventBean> _selectedEvents; 
    protected BoundedPriorityBlockingQueue _outputQueue;

    // private boolean _process = false;
    public EPAgent() {
        _selectedEvents = Queues.newArrayDeque();
        _outputQueue = new BoundedPriorityBlockingQueue();

    }

    public BoundedPriorityBlockingQueue getOutputQueue() {
        return _outputQueue;
    }

    public abstract Collection<IOTerminal> getInputTerminals();

    public abstract Collection<IOTerminal> getOutputTerminals();

    public String[] getInputTopics() {
        String[] topics = new String[getInputTerminals().size()];
        short i = 0;
        for (IOTerminal t : getInputTerminals()) {
            topics[i] = t.getTopic();
        }
        return topics;
    }

    // public abstract void setInputTopics(String[] inputTopics) ;
    public String[] getOutputTopics() {
        String[] topics = new String[getOutputTerminals().size()];
        
        short i = 0;
        for (IOTerminal t : getOutputTerminals()) {
            topics[i] = t.getTopic();
        }
        return topics;
    }

    // public abstract void setOutputTopic(String outputTopic) ;
    public abstract void process();
   // public abstract void process(EventBean[] evts);

    public abstract boolean fetch();

    public boolean openIOchannels() {

        for (IOTerminal input : getInputTerminals()) {
            input.open();
        }

        for (IOTerminal output : getOutputTerminals()) {
            output.open();
        }
        return true;
    }

    /*
     public void signal(int i) {
        
     synchronized (_mutex) {
     _process = true;
     }
     }
     */
    @Override
    public void run() {
        while (true) {
            if (fetch()) {
                process();
            }
        }
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
}
