/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.NameValuePair;
import com.google.common.collect.Queues;
import core.pubsub.Relayer;
import event.EventBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.MyLogger;
import qosmonitor.QoSTuner;

/**
 *
 * @author epaln
 */
public class AggregatorAgent extends EPAgent {

    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    List<Aggregate> aggregators = new ArrayList<>();

    public AggregatorAgent(String info, String IDinputTerminal, String IDoutputTerminal) {
        super();
        //this._filter = filter;
        this.setName(this.getName()+"@"+Relayer.getInstance().getAddress().toString());
        this._info = info;
        this._type = "Aggregator";
        this._receivers[0] = new TopicReceiver();
        inputTerminal = new IOTerminal(IDinputTerminal, "input channel " + _type, _receivers[0], this);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type, this);
        _outputNotifier = new OQNotifier(this, QoSTuner.NOTIFICATION_PRIORITY);
        Queue<EventBean> selected1= Queues.newArrayDeque();
        _selectedEvents[0]=selected1;
        logger = new MyLogger("AggregatorMeasures", AggregatorAgent.class.getName());
        logger.log("Operator, isProduced, Processing Time, InputQ Size, OutputQ Size ");
    }

    public void addAggregator(Aggregate aggregator) {
        this.aggregators.add(aggregator);
    }

    public List<Aggregate> getAggregators() {
        return aggregators;
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
        //start processing time
        long time = System.currentTimeMillis(); 
        long ntime = System.nanoTime();
        EventBean evt = _selectedEvents[0].remove();
        if(evt.getHeader().getTypeIdentifier().equals("Window")){
            operands = (EventBean[]) evt.getValue("window");
        }
        else{
             operands = new EventBean[1];
             operands[0]=evt;
        }
        // update the number of event processed by this EPU
        numEventProcessed+=operands.length;
         int sumPriority = 0;     
        for (EventBean e : operands) {
            sumPriority+= e.getHeader().getPriority();
        }
        EventBean ec = new EventBean();
        ec.getHeader().setDetectionTime(operands[0].getHeader().getDetectionTime());
        ec.getHeader().setIsComposite(true);
        ec.getHeader().setProductionTime(System.currentTimeMillis());
        ec.getHeader().setTypeIdentifier("Aggregate");
        evt.payload.put("processTime", ntime);
        ec.getHeader().setPriority((short)Math.round(sumPriority/operands.length));
        for (Aggregate aggregator: aggregators ){
            NameValuePair res = aggregator.aggregate(operands);
            ec.payload.put(res.getAttribute(), res.getValue());
        }
         
         evt.payload.put("ttl", TTL);
        _selectedEvents[0].clear();
        _outputQueue.put(evt);
        numEventProduced++;
        // update the processing time of this EPU
        time = System.currentTimeMillis()-time;
        processingTime+=time;
       getExecutorService().execute(getOutputNotifier());
    }

    @Override
    public boolean fetch() {
       try {
            _selectedEvents[0].add((EventBean) _receivers[0].getInputQueue().take());
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
