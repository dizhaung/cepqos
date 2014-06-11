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
        _outputNotifier = new OQNotifier(outputTerminal, _outputQueue, OQNotifier.PRIORITY);
    }

    public void setAggregator(Aggregate aggregator) {
        this.aggregator = aggregator;
    }

    public Aggregate getAggregator() {
        return aggregator;
    }

    @Override
    public Collection<IOTerminal> getInputTerminals() {
        ArrayList<IOTerminal> inputs = new ArrayList<IOTerminal>();
        inputs.add(inputTerminal);
        return inputs;
    }

    @Override
    public Collection<IOTerminal> getOutputTerminals() {
        ArrayList<IOTerminal> outputs = new ArrayList<IOTerminal>();
        outputs.add(outputTerminal);
        return outputs;
    }

    @Override
    public void process() {
        //EventBean evt = aggregator.aggregate(_selectedEvents.toArray(new EventBean[0]));
        EventBean[] operands;
        EventBean evt = _selectedEvents.remove();
        if(evt.getHeader().getTypeIdentifier().equals("Window")){
            operands = (EventBean[]) evt.getValue("window");
        }
        else{
             operands = new EventBean[1];
             operands[0]=evt;
        }
         evt = aggregator.aggregate(operands);
         evt.payload.put("ttl", TTL);
        _selectedEvents.clear();
        _outputQueue.put(evt);
        _outputNotifier.run();
    }

    @Override
    public boolean fetch() {
       try {
            _selectedEvents.add((EventBean) _receiver.getInputQueue().take());
        } catch (InterruptedException ex) {
            Logger.getLogger(FilterAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return !_selectedEvents.isEmpty();
    }

    /*
    @Override
    public void process(EventBean[] evts) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    * */
}
