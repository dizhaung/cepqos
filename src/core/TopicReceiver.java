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
        _inputQueue = new BoundedPriorityBlockingQueue(null);
    }

     public TopicReceiver() {
        _inputQueue = new BoundedPriorityBlockingQueue(null);
    }

    public BoundedPriorityBlockingQueue getInputQueue() {
        return _inputQueue;
    }
    
    @Override
    public void notify(Object event) {
        EventBean[] evts = (EventBean[]) event;
         for(EventBean evt: evts){
            evt.getHeader().setReceptionTime(System.currentTimeMillis());
             //System.out.println("ProducerID:"+evt.getHeader().getProducerID()+"; type:"+evt.getHeader().getTypeIdentifier()+"; latency: "+ (evt.getHeader().getReceptionTime()-evt.getHeader().getProductionTime()) );
           _inputQueue.put(evt); 
        }
       
        //_epAgent.process(evts);
    }
}