/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import core.pubsub.PubSubService;
import core.pubsub.Subscriber;
import event.EventBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.JChannel;

/**
 *
 * @author epaln
 */
public class EventConsumer implements Subscriber{

    JChannel _channel;
    String _info, _input;
    AnEventHandler _handler;

    public EventConsumer(String info, String IDinputTerminal, AnEventHandler handler) {
        _handler = handler;
        try {
            _info = info;
            _input = IDinputTerminal;
            PubSubService.getInstance().subscribe(this, _input);
        } catch (Exception ex) {
            Logger.getLogger(EventConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void notify(Object event) {
        EventBean[] evts = (EventBean[])event;
        _handler.notify(evts);
    }
    
    
   
}
