/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import event.EventBean;
import hu.akarnokd.reactive4java.base.Subject;
import hu.akarnokd.reactive4java.util.DefaultObservable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        _sourceStream = new DefaultObservable<EventBean>();
        this._info = info;
        this._type = "WindowsAgent";
        this._receiver = new TopicReceiver(this);
        inputTerminal = new IOTerminal(IDinputTerminal, "input channel " + _type, _receiver);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type);
    }

    @Override
    public Collection<IOTerminal> getInputTerminal() {
        ArrayList<IOTerminal> inputs = new ArrayList<IOTerminal>();
        inputs.add(inputTerminal);
        return inputs;
    }

    @Override
    public Collection<IOTerminal> getOutputTerminal() {
        ArrayList<IOTerminal> outputs = new ArrayList<>();
        outputs.add(outputTerminal);
        return outputs;
    }

    public void setWindowHandler(WindowHandler handler) {
        this._handler = handler;
        _handler.register(this);
    }

    @Override
    public void process() {
        while (!_selectedEvents.isEmpty()) {
            EventBean evt = _selectedEvents.poll();
            _sourceStream.next(evt);
        }
    }

    @Override
    public boolean select() {
        try {
            for (EventBean evt : _receiver.getInputQueue().take()) {
                _selectedEvents.add(evt);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(WindowAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return !_selectedEvents.isEmpty();
    }
}
