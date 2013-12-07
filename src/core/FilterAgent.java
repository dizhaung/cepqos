/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.Func1;
import event.EventBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Applique un ensemble de filtres sur un événement. Les événements notifiés
 * sont ceux qui passent tous les filtres présents dans la liste de filtres
 *
 * @author epaln
 */
public class FilterAgent extends EPAgent {

    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    private final short COUNT = 3; // number of time we try to notify an event 
    List<Func1<EventBean, Boolean>> _filters = new ArrayList<>();

    public FilterAgent(String info, String IDinputTerminal, String IDoutputTerminal) {
        super();
        //this._filter = filter;
        this._info = info;
        this._type = "FilterAgent";
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
        ArrayList<IOTerminal> outputs = new ArrayList<IOTerminal>();
        outputs.add(outputTerminal);
        return outputs;
    }

    private boolean notify(EventBean e) {
        System.out.println("[" + this._info + "] notify event: " + e.payload);
        EventBean[] evts ={e};
        try {
            outputTerminal.send(evts);

        } catch (Exception ex) {
            Logger.getLogger(FilterAgent.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Cannot send the eventBean Message :(");
            return false;
        }
        return true;
    }

    private void p(String msg) {
        System.out.println(msg);
    }

    @Override
    public void process() {
        while (!_selectedEvents.isEmpty()) {
            boolean notified, pass_filters = true;
            int attempt = 0;
            EventBean evt = _selectedEvents.poll();
            for (Func1<EventBean, Boolean> _filter : _filters) {
                if (!_filter.invoke(evt)) {
                    pass_filters = false;
                    break;
                }
            }
            if (pass_filters) {
                do {
                    notified = notify(evt);
                    attempt++;
                } while (!notified && (attempt != COUNT));
                if (attempt == COUNT) {
                    p("Event not notified: " + evt.payload);
                }
            }
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

    public void addFilter(Func1<EventBean, Boolean> filter) {
        this._filters.add(filter);
    }
}
