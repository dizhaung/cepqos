/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import event.EventBean;
import java.util.*;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 *
 * @author epaln
 */
public class ChannelReceiver extends ReceiverAdapter{
    private EPAgent _epAgent;
    private Queue<EventBean> _inputQueue;
    boolean _logging = false;
    
 
    public ChannelReceiver(EPAgent _epAgent) {
        this._epAgent = _epAgent;
        _inputQueue = Queues.newConcurrentLinkedQueue();
    }
    
    public ChannelReceiver(EPAgent _epAgent, boolean logging) {
       this(_epAgent);
       _logging = logging;
       
    }
    
    /**
     *
     * @param msg
     */
    @Override
    public void receive(Message msg) {
        super.receive(msg); 
        EventBean evt = (EventBean) msg.getObject();
        _inputQueue.add(evt);
        _epAgent.signal();
       // System.out.println(evt);
    }

    @Override
    public void viewAccepted(View view) {
        super.viewAccepted(view); 
        if(_logging)
            System.out.println("** view: " + view +"; "+view.getMembers().size()+" member(s)");
    }

    public Queue<EventBean> getInputQueue() {
        return _inputQueue;
    }   
    
}
