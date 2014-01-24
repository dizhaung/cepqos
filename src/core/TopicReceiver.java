/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import core.pubsub.Subscriber;
import event.EventBean;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epaln
 */
public class TopicReceiver implements Subscriber {

    private EPAgent _epAgent;
    private LinkedBlockingQueue<EventBean[]> _inputQueue;

    public TopicReceiver(EPAgent _epAgent) {
        this._epAgent = _epAgent;
        _inputQueue = Queues.newLinkedBlockingQueue();
    }

    public LinkedBlockingQueue<EventBean[]> getInputQueue() {
        return _inputQueue;
    }

    @Override
    public void notify(Object event) {
        EventBean[] evts = (EventBean[]) event;
        try {
            _inputQueue.put(evts);
        } catch (InterruptedException ex) {
            Logger.getLogger(TopicReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
