/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

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
    
    JChannel channel;
    String id;
    String description;
    Receiver _receiver=null;

    public IOTerminal(String id, String description, Receiver receiver) {
        this.id = id;
        this.description = description;
        _receiver = receiver;
    }
    
    public IOTerminal(String id, String description) {
        this(id, description, null);
    }
    
    public boolean open(Collection<Protocol> protocolStack) throws Exception{
        
        channel =new JChannel(protocolStack);
        channel.setDiscardOwnMessages(true);
        if(_receiver!=null){
            channel.setReceiver(_receiver);
        }
        channel.connect(id);
        return true;
    }
    
    
    public boolean open() throws Exception{
        channel =new JChannel("tcp.xml");
        //channel.getProtocolStack().getBottomProtocol().setValue("bind_addr",Inet4Address.getLocalHost());
         channel.setDiscardOwnMessages(true);
        if(_receiver!=null){
            channel.setReceiver(_receiver);
        }
        channel.connect(id);
        return true;
    }

    public JChannel getChannel() {
        return channel;
    }

    public String getId() {
        return id;
    }
    
    public void send(EventBean e) throws Exception{
        channel.send(null, e);
    }
    
}
