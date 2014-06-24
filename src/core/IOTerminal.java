/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import core.pubsub.PubSubService;
import core.pubsub.Relayer;
import event.EventBean;

/**
 *
 * @author epaln
 */
public class IOTerminal {

    private String _topic;
    private String description;
    private TopicReceiver _receiver = null;
    private EPAgent _agent;

    public IOTerminal(String topic, String description, TopicReceiver receiver, EPAgent epu) {
        this._topic = topic;
        this.description = description;
        _receiver = receiver;
        _agent = epu;
    }

    public IOTerminal(String id, String description, EPAgent epu) {
        this(id, description, null, epu);
    }

    public TopicReceiver getReceiver() {
        return _receiver;
    }

    
    public boolean open() {

        if (_receiver != null) {
            PubSubService.getInstance().subscribe(_receiver, _topic);
        } else {
            PubSubService.getInstance().publish(null, _topic); // add the new topic
        }

        return true;
    }

    public String getTopic() {
        return _topic;
    }

    public String getId() {
        return _topic;
    }

    public void send(EventBean[] e) throws Exception {
        PubSubService.getInstance().publish(e, _topic); // publish locally
        // compute the latency of evts notified locally?
        Relayer.getInstance().callPublish(e, _agent);  // publish remotely
    }

    public EPAgent getAgent() {
        return _agent;
    }
    
}
