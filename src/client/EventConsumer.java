/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.google.common.eventbus.Subscribe;
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
            
            //_channel.getProtocolStack().getBottomProtocol().setValue("bind_addr",Inet4Address.getLocalHost());
            _info = info;
            _input = IDinputTerminal;
            PubSubService.getInstance().subscribe(this, _input);
        } catch (Exception ex) {
            Logger.getLogger(EventConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    @Subscribe
    public void notify(Object event) {
        EventBean evt = (EventBean)event;
        _handler.notify(evt);
    }
    
    
   
}
