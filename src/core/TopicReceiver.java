/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.BoundedPriorityBlockingQueue;
import core.pubsub.Subscriber;
import event.EventBean;


/**
 *
 * @author epaln
 */
public class TopicReceiver implements Subscriber {

    private EPAgent _epAgent;
    //private LinkedBlockingQueue<EventBean[]> _inputQueue;
    private BoundedPriorityBlockingQueue _inputQueue;
    

    public TopicReceiver(EPAgent _epAgent) {
        this._epAgent = _epAgent;
        //_inputQueue = Queues.newLinkedBlockingQueue();
        _inputQueue = new BoundedPriorityBlockingQueue();
    }

    /*
     * public LinkedBlockingQueue<EventBean[]> getInputQueue() {
    return _inputQueue;
    }
     */
    public BoundedPriorityBlockingQueue getInputQueue() {
        return _inputQueue;
    }
    
    
    @Override
    public void notify(Object event) {
        EventBean[] evts = (EventBean[]) event;
        for(EventBean evt: evts){
           _inputQueue.put((EventBean)event); 
        }
        //_epAgent.process(evts);
    }
}
