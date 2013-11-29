/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import com.google.common.eventbus.Subscribe;
import core.pubsub.Subscriber;
import event.EventBean;
import java.util.*;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

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
    @Subscribe
    public void notify(Object event) {      
        EventBean evt = (EventBean) event;
        _inputQueue.add(evt);
        _epAgent.signal();
    }
    
}
