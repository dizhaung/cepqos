/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import core.pubsub.Relayer;
import event.EventBean;
import event.EventComparator;
import event.EventComparator2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.MyLogger;
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
        this.setName(this.getName() + "@" + Relayer.getInstance().getAddress().toString());
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
        logger = new MyLogger("FollowedByMeasures", FollowedByAgent.class.getName());
        logger.log("Operator, isProduced, Processing Time, InputQ Size, OutputQ Size ");
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
        // statistics: #events processed, processing time
        long time = System.currentTimeMillis();
        long ntime = System.nanoTime(); // to compute the processing time for that cycle
        EventBean evtL = _selectedEvents[0].peek();
        EventBean evtR = _selectedEvents[1].peek();
        lValues = rValues = new EventBean[0];
        if (evtL != null) {
            if (evtL.getHeader().getTypeIdentifier().equals("Window")) {
                lValues = (EventBean[]) evtL.getValue("window");
            } else {
                lValues = _selectedEvents[0].toArray(new EventBean[0]);
            }
        }
        if (evtR != null) {
            if (evtR.getHeader().getTypeIdentifier().equals("Window")) {
                rValues = (EventBean[]) evtR.getValue("window");
            } else {
                rValues = _selectedEvents[1].toArray(new EventBean[0]);
            }
        }
        if (lValues.length != 0 && rValues.length != 0) {
            ArrayList<EventBean> produced = new ArrayList<>();
            for (EventBean l : lValues) {
                for (EventBean r : rValues) {
                    if (l.getHeader().getDetectionTime() < r.getHeader().getDetectionTime()) {
                        EventBean ec = new EventBean();
                        ec.getHeader().setDetectionTime(r.getHeader().getDetectionTime());
                        ec.getHeader().setPriority((short) Math.max(l.getHeader().getPriority(), r.getHeader().getPriority()));
                        ec.getHeader().setIsComposite(true);
                        ec.getHeader().setProductionTime(System.currentTimeMillis());
                        ec.getHeader().setProducerID(this.getName());
                        ec.getHeader().setTypeIdentifier("FollowedBy");
                        ec.payload.put("before", l);
                        ec.payload.put("after", r);
                        ec.payload.put("ttl", TTL);
                        ec.payload.put("processTime", ntime);
                        produced.add(ec);
                    }
                }

            }
            switch (selectionMode) {
                case SelectionMode.MODE_CONTINUOUS: {
                    for (EventBean e : produced) {
                        _outputQueue.put(e);
                        numEventProduced++;
                        getExecutorService().execute(getOutputNotifier());
                    }
                }
                break;

                case SelectionMode.MODE_CHRONOLOGIC: {
                    _outputQueue.put(produced.get(0));
                    numEventProduced++;
                    getExecutorService().execute(getOutputNotifier());
                }
                break;
                case SelectionMode.MODE_PRIORITY: {
                    EventBean[] prod = produced.toArray(new EventBean[0]);
                    Arrays.sort(prod, new EventComparator());
                    _outputQueue.put(prod[0]);
                    numEventProduced++;
                    getExecutorService().execute(getOutputNotifier());
                }
                break;
                default: { // mode recent
                    _outputQueue.put(produced.get(produced.size() - 1));
                    numEventProduced++;
                    getExecutorService().execute(getOutputNotifier());
                }
                break;
            }
            // update statistics: number event processed
             numEventProcessed+= (lValues.length+rValues.length);
            // removes the processed events from the selected events
            if (evtL != null) {
                if (evtL.getHeader().getTypeIdentifier().equals("Window")) {
                    _selectedEvents[0].remove(evtL);
                } else {
                    _selectedEvents[0].clear();
                }
            }
            if (evtR != null) {
                if (evtR.getHeader().getTypeIdentifier().equals("Window")) {
                    _selectedEvents[1].remove(evtR);
                } else {
                    _selectedEvents[1].clear();
                }
            }
            // update statistics: processing time
            processingTime+=time;
        }
    }

    @Override
    public boolean fetch() {
        try {
            if (_receivers[0].getInputQueue().size() != 0) {
                _selectedEvents[0].add((EventBean) _receivers[0].getInputQueue().take());
            }
            if (_receivers[1].getInputQueue().size() != 0) {
                _selectedEvents[1].add((EventBean) _receivers[1].getInputQueue().take());
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DisjunctionAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (!_selectedEvents[0].isEmpty()) || (!_selectedEvents[1].isEmpty());
    }

}
