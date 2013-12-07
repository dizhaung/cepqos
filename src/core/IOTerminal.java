/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import core.pubsub.PubSubService;
import core.pubsub.Relayer;
import event.EventBean;
import java.net.Inet4Address;
import java.util.Collection;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.stack.Protocol;



/**
 *
 * @author epaln
 */
public class IOTerminal {
    
    String _topic;
    String description;
    TopicReceiver _receiver=null;

    public IOTerminal(String topic, String description, TopicReceiver receiver) {
        this._topic = topic;
        this.description = description;
        _receiver = receiver;
    }
    
    public IOTerminal(String id, String description) {
        this(id, description, null);
    }
    
    public boolean open() {
        
        if(_receiver!=null){
            PubSubService.getInstance().subscribe(_receiver, _topic);
        }
        else{
            PubSubService.getInstance().publish(null, _topic); // advertise the new topic
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
       Relayer.getInstance().callPublish(e, _topic);  // publish remotely
    }
     
    
}
