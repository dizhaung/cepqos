/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import core.pubsub.Relayer;
import event.EventBean;
import hu.akarnokd.reactive4java.base.Subject;
import hu.akarnokd.reactive4java.util.DefaultObservable;
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
public class WindowAgent extends EPAgent {

    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    Subject<EventBean, EventBean> _sourceStream;
    WindowHandler _handler;

    public WindowAgent(String info, String IDinputTerminal, String IDoutputTerminal) {
        super();
        this.setName(this.getName()+"@"+Relayer.getInstance().getAddress().toString());
        _sourceStream = new DefaultObservable<EventBean>();
        this._info = info;
        this._type = "Window";
        this._receiver[0] = new TopicReceiver();
        inputTerminal = new IOTerminal(IDinputTerminal, "input channel " + _type, _receiver[0], this);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type, this);
        _outputNotifier = new OQNotifier(this, QoSTuner.NOTIFICATION_PRIORITY);
        Queue<EventBean> selected1= Queues.newArrayDeque();
        _selectedEvents[0] = selected1;
    }

    @Override
    public Collection<IOTerminal> getInputTerminals() {
        ArrayList<IOTerminal> inputs = new ArrayList<IOTerminal>();
        inputs.add(inputTerminal);
        return inputs;
    }

    @Override
    public IOTerminal getOutputTerminal() {
        return outputTerminal;
    }

    public void setWindowHandler(WindowHandler handler) {
        this._handler = handler;
        _handler.register(this);
    }

    @Override
    public void process() {
        while (!_selectedEvents[0].isEmpty()) {
            EventBean evt = _selectedEvents[0].poll();
            evt.payload.put("#time#", System.currentTimeMillis()); // start processing this evt at #time#
            _sourceStream.next(evt);          
            numEventProcessed++;
        }
    }

    @Override
    public boolean fetch() {
        try {
            _selectedEvents[0].add((EventBean) _receiver[0].getInputQueue().take());
        } catch (InterruptedException ex) {
            Logger.getLogger(FilterAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return !_selectedEvents[0].isEmpty();
    }

//    @Override
//    public void process(EventBean[] evts) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
