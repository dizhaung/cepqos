/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import event.EventBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epaln
 */
public class AggregatorAgent extends EPAgent {

    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    Aggregate aggregator = null;

    public AggregatorAgent(String info, String IDinputTerminal, String IDoutputTerminal) {
        super();
        //this._filter = filter;
        this._info = info;
        this._type = "Aggregator";
        this._receiver = new TopicReceiver(this);
        inputTerminal = new IOTerminal(IDinputTerminal, "input channel " + _type, _receiver);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type);
    }

    public void setAggregator(Aggregate aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public Collection<IOTerminal> getInputTerminal() {
        ArrayList<IOTerminal> inputs = new ArrayList<IOTerminal>();
        inputs.add(inputTerminal);
        return inputs;
    }

    @Override
    public Collection<IOTerminal> getOutputTerminal() {
        ArrayList<IOTerminal> outputs = new ArrayList<IOTerminal>();
        outputs.add(outputTerminal);
        return outputs;
    }

    @Override
    public void process() {
        EventBean evt = aggregator.aggregate(_selectedEvents.toArray(new EventBean[1]));
        _selectedEvents.clear();
        EventBean[] evts = {evt};
        try {
            outputTerminal.send(evts);
        } catch (Exception ex) {
            Logger.getLogger(AggregatorAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean select() {
        try {
            for (EventBean evt : _receiver.getInputQueue().take()) {
                _selectedEvents.add(evt);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AggregatorAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return !_selectedEvents.isEmpty();
    }
}
