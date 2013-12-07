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


/**
 *
 * @author epaln
 */
public class WindowAgent extends EPAgent{
    
    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    Subject<EventBean, EventBean> _sourceStream;
    WindowHandler _handler;

    public WindowAgent(String info, String IDinputTerminal, String IDoutputTerminal, WindowHandler handler) {
        super();  
        //this._filter = filter;
        _handler = handler;
        _sourceStream = new DefaultObservable<EventBean>();
        this._info=info;
        this._type ="WindowsAgent";
        this._receiver = new TopicReceiver(this);
        inputTerminal = new IOTerminal(IDinputTerminal,"input channel "+_type, _receiver);
        outputTerminal = new IOTerminal(IDoutputTerminal,"output channel "+_type);
        handler.register(this);
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
    
    @Override
    public void process() {
        while (!_selectedEvents.isEmpty()){            
            EventBean evt = _selectedEvents.poll();
            _sourceStream.next(evt);           
        }
    }

    @Override
    public boolean select(int numbertoSelect) {
        boolean ok = false;
        for (int i = 1; i <= numbertoSelect; i++) {
            EventBean evt = _receiver.getInputQueue().poll();
            if (evt != null) {
                _selectedEvents.add(evt);
                ok = true;
            }
        }
        return ok;
    }
}
