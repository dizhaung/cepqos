/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.Func1;
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
 * Applique un ensemble de filtres sur un événement. Les événements notifiés
 * sont ceux qui passent tous les filtres présents dans la liste de filtres
 *
 * @author epaln
 */
public class FilterAgent extends EPAgent {

    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    List<Func1<EventBean, Boolean>> _filters = new ArrayList<>();

    public FilterAgent(String info, String IDinputTerminal, String IDoutputTerminal) {
        super();
        this.setName(this.getName()+"@"+Relayer.getInstance().getAddress().toString());
        this._info = info;
        this._type = "Filter";
        this._receivers[0] = new TopicReceiver();
        inputTerminal = new IOTerminal(IDinputTerminal, "input channel " + _type, _receivers[0], this);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type, this);
        _outputNotifier = new OQNotifier(this, QoSTuner.NOTIFICATION_PRIORITY);
         Queue<EventBean> selected1= Queues.newArrayDeque();
        _selectedEvents[0]= selected1;
        logger = new MyLogger("FilterMeasures", FilterAgent.class.getName());
        logger.log("Operator, isProduced, Processing Time, InputQ Size, OutputQ Size ");
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

    private void p(String msg) {
        System.out.println(msg);
    }

    @Override
    public void process() {
        while (!_selectedEvents[0].isEmpty()) {
            
            // statistics: #events processed, processing time
            long time = System.currentTimeMillis();     
            long ntime = System.nanoTime(); // to compute the processing time for that cycle
            numEventProcessed++;
            boolean pass_filters = true;
            EventBean evt = _selectedEvents[0].poll();
            evt.payload.put("processTime", ntime);
            for (Func1<EventBean, Boolean> _filter : _filters) {
                if (!_filter.invoke(evt)) {
                    pass_filters = false;
                    break;
                }
            }
            if (pass_filters) {
                evt.getHeader().setProductionTime(System.currentTimeMillis());
                evt.getHeader().setIsComposite(true);
                evt.payload.put("ttl", TTL);
                _outputQueue.put(evt);
                time = System.currentTimeMillis()-time;
                numEventProduced++;
                //logger.log(this.getInfo()+" ,True, "+time+", "+ this.getInputTerminals().iterator().next().getReceiver().getInputQueue().size()+
                  //      ", "+ this.getOutputQueue().size());
            }
            else{
                time = System.currentTimeMillis()-time;
                
                logger.log(this.getInfo()+", False, "+(System.nanoTime()-ntime)+", "+ this.getInputTerminals().iterator().next().getReceiver().getInputQueue().size()+
                        ", "+ this.getOutputQueue().size());
            }
            
            processingTime+=time;
        }

        if (!_outputQueue.isEmpty()) {
            getExecutorService().execute(getOutputNotifier());
        }
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

    public void addFilter(Func1<EventBean, Boolean> filter) {
        this._filters.add(filter);
    }
}
