/*
 * To change this template, choose Tools | Templates
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
public class AggregatorAgent extends EPAgent {

    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    Aggregate aggregator = null;

    public AggregatorAgent(String info, String IDinputTerminal, String IDoutputTerminal) {
        super();
        //this._filter = filter;
        this.setName(this.getName()+"@"+Relayer.getInstance().getAddress().toString());
        this._info = info;
        this._type = "Aggregator";
        this._receiver[0] = new TopicReceiver();
        inputTerminal = new IOTerminal(IDinputTerminal, "input channel " + _type, _receiver[0], this);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type, this);
        _outputNotifier = new OQNotifier(this, QoSTuner.NOTIFICATION_PRIORITY);
        Queue<EventBean> selected1= Queues.newArrayDeque();
        _selectedEvents[0]=selected1;
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
    public IOTerminal getOutputTerminal() {
        return outputTerminal;
    }

    @Override
    public void process() {
        //EventBean evt = aggregator.aggregate(_selectedEvents.toArray(new EventBean[0]));
        EventBean[] operands;
        EventBean evt = _selectedEvents[0].remove();
        if(evt.getHeader().getTypeIdentifier().equals("Window")){
            operands = (EventBean[]) evt.getValue("window");
        }
        else{
             operands = new EventBean[1];
             operands[0]=evt;
        }
         evt = aggregator.aggregate(operands);
         evt.payload.put("ttl", TTL);
        _selectedEvents[0].clear();
        _outputQueue.put(evt);
        _outputNotifier.run();
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

    /*
    @Override
    public void process(EventBean[] evts) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    * */
}
