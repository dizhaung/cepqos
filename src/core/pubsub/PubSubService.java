/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.pubsub;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import java.util.HashMap;

/**
 * publish subscribe based on topics. Each topic is associated with an event
 * Bus, over which its message are published and notified
 *
 * @author epaln
 */
public class PubSubService {

    private static PubSubService _instance = null;
    private HashMap<String, EventBus> _topicBus;

    private PubSubService() {
        _topicBus = Maps.<String, EventBus>newHashMap();
    }

    /**
     * publish an event to the event bus associated to his topic
     *
     * @param evt the event to publish
     * @param topic the topic to publish to
     *
     */
    public boolean publish(Object evt, String topic) {
        EventBus eBus;
        if (!_topicBus.containsKey(topic)) {
            eBus = new EventBus();
            _topicBus.put(topic, eBus);
        } else {
            eBus = _topicBus.get(topic);
        }
        if (evt != null) {
            eBus.post(evt);
        }
        return true;
    }

    /**
     * register an event to the eventBus associated to the given topic
     *
     * @param subscriber
     * @param topic
     */
    public void subscribe(Subscriber subscriber, String topic) {
        EventBus eBus;
        if (!_topicBus.containsKey(topic)) {
            eBus = new EventBus();
            _topicBus.put(topic, eBus);
        }
        else {
            eBus = _topicBus.get(topic);   
        }
        eBus.register(subscriber);
    }

    public static PubSubService getInstance() {
        if (_instance == null) {
            _instance = new PubSubService();
        }
        return _instance;
    }
}
