/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import core.pubsub.Relayer;
import event.EventBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import qosmonitor.QoSTuner;

/**
 *
 * @author epaln
 */
public class FollowedByAgent extends EPAgent {

    IOTerminal inputTerminalL;
    IOTerminal inputTerminalR;
    IOTerminal outputTerminal;

    public FollowedByAgent(String info, String IDinputTerminalL, String IDinputTerminalR, String IDoutputTerminal) {
        super();
        this.setName(this.getName()+"@"+Relayer.getInstance().getAddress().toString());
        this._info = info;
        this._type = "FollowedBy";
        this._receivers[0] = new TopicReceiver();
        this._receivers[1] = new TopicReceiver();
        inputTerminalL = new IOTerminal(IDinputTerminalL, "input channel " + _type, _receivers[0], this);
        inputTerminalR = new IOTerminal(IDinputTerminalR, "input channel " + _type, _receivers[1], this);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type, this);
        _outputNotifier = new OQNotifier(this, QoSTuner.NOTIFICATION_PRIORITY);
        Queue<EventBean> selectedL = Queues.newArrayDeque();
        _selectedEvents[0] = selectedL;
        Queue<EventBean> selectedR = Queues.newArrayDeque();
        _selectedEvents[1] = selectedR;
    }

    @Override
    public Collection<IOTerminal> getInputTerminals() {
        ArrayList<IOTerminal> inputs = new ArrayList<IOTerminal>();
        inputs.add(inputTerminalL);
        inputs.add(inputTerminalR);
        return inputs;
    }

    @Override
    public IOTerminal getOutputTerminal() {
        return outputTerminal;
    }

    @Override
    public void process() {
        EventBean[] lValues, rValues; 
        EventBean evtL= _selectedEvents[0].poll();
        EventBean evtR = _selectedEvents[1].poll();
        if(evtL.getHeader().getTypeIdentifier().equals("Window")){
            lValues = (EventBean[])evtL.getValue("window");
        }
        else {
            lValues = new EventBean[1];
            lValues[0] =evtL;
        }
        if(evtR.getHeader().getTypeIdentifier().equals("Window")){
            rValues = (EventBean[])evtR.getValue("window");
        }
        else {
            rValues = new EventBean[1];
            rValues[0] =evtR;
        }
        for(EventBean l: lValues){
            for(EventBean r: rValues){
                if(l.getHeader().getDetectionTime()<r.getHeader().getDetectionTime()){
                    EventBean ec = new EventBean();
                    ec.getHeader().setDetectionTime(r.getHeader().getDetectionTime());
                    ec.getHeader().setPriority((short)Math.max(l.getHeader().getPriority(), r.getHeader().getPriority()));
                    ec.getHeader().setIsComposite(true);
                    ec.getHeader().setProductionTime(System.currentTimeMillis());
                    ec.getHeader().setProducerID(this.getName());
                    ec.getHeader().setTypeIdentifier("FollowedBy");
                    ec.payload.put("before", l);
                    ec.payload.put("after", r);
                    ec.payload.put("ttl", TTL);
                    _outputQueue.put(ec);
                }
            }
        }
        if (!_outputQueue.isEmpty()) {
            _outputNotifier.run();
        }
    }

    @Override
    public boolean fetch() {
        try {
            _selectedEvents[0].add((EventBean) _receivers[0].getInputQueue().take());
            _selectedEvents[1].add((EventBean) _receivers[1].getInputQueue().take());
        } catch (InterruptedException ex) {
            Logger.getLogger(FilterAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (!_selectedEvents[0].isEmpty()) && (!_selectedEvents[1].isEmpty());
    }

}
