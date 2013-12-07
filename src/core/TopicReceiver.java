/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import core.pubsub.Subscriber;
import event.EventBean;
import java.util.*;


/**
 *
 * @author epaln
 */
public class TopicReceiver implements Subscriber{
    private EPAgent _epAgent;
    private Queue<EventBean> _inputQueue;
 
    public TopicReceiver(EPAgent _epAgent) {
        this._epAgent = _epAgent;
        _inputQueue = Queues.newConcurrentLinkedQueue();
    }

    public Queue<EventBean> getInputQueue() {
        return _inputQueue;
    }   

    @Override
    public void notify(Object event) {      
        EventBean[] evts = (EventBean[]) event;
        synchronized(_inputQueue){
           for(EventBean e: evts){
           _inputQueue.add(e); 
        } 
        }                
        _epAgent.signal(evts.length);
    }    
}
