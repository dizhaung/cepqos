/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import event.EventBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 *
 * @author epaln
 */
public class EventConsumer extends ReceiverAdapter{

    JChannel _channel;
    String _info, _input;
    AnEventHandler _handler;

    public EventConsumer(String info, String IDinputTerminal, AnEventHandler handler) {
        _handler = handler;
        try {
            _channel = new JChannel("tcp.xml");
            _channel.setReceiver(this);
            //_channel.getProtocolStack().getBottomProtocol().setValue("bind_addr",Inet4Address.getLocalHost());
            _info = info;
            _input = IDinputTerminal;
            _channel.connect(_input);
        } catch (Exception ex) {
            Logger.getLogger(EventConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void receive(Message msg) {
        super.receive(msg); 
        EventBean evt = (EventBean) msg.getObject();
        _handler.notify(evt);
    }

    @Override
    public void viewAccepted(View view) {
        super.viewAccepted(view); 
        System.out.println("** view: " + view);
    }
    
    
   
}
