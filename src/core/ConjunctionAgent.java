/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import core.pubsub.Relayer;
import event.EventBean;
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
 * this operator implements the logic AND operation between 2 event streams
 * @author epaln
 */
public class ConjunctionAgent extends EPAgent {

    IOTerminal inputTerminalL;
    IOTerminal inputTerminalR;
    IOTerminal outputTerminal;

    public ConjunctionAgent(String info, String IDinputTerminalL, String IDinputTerminalR, String IDoutputTerminal) {

        super();
        this.setName(this.getName() + "@" + Relayer.getInstance().getAddress().toString());
        this._info = info;
        this._type = "Conjunction";
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
        logger = new MyLogger("ConjunctionMeasures", ConjunctionAgent.class.getName());
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

            // many event instances can match... the selection mode should clearly define the event to select
            switch (selectionMode) {
                case SelectionMode.MODE_CONTINUOUS: {

                    for (EventBean l : lValues) {
                        for (EventBean r : rValues) {
                            EventBean ec = new EventBean();
                            ec.getHeader().setDetectionTime(Math.min(r.getHeader().getDetectionTime(), l.getHeader().getDetectionTime()));
                            ec.getHeader().setPriority((short) Math.max(l.getHeader().getPriority(), r.getHeader().getPriority()));
                            ec.getHeader().setIsComposite(true);
                            ec.getHeader().setProductionTime(System.currentTimeMillis());
                            ec.getHeader().setProducerID(this.getName());
                            ec.getHeader().setTypeIdentifier("Conjunction");
                            ec.payload.put("l", l);
                            ec.payload.put("r", r);
                            ec.payload.put("ttl", TTL);
                            ec.payload.put("processTime", ntime);
                            _outputQueue.put(ec);
                            numEventProduced++;
                            getExecutorService().execute(getOutputNotifier());
                        }
                    }
                }
                break;
                case SelectionMode.MODE_CHRONOLOGIC: {

                    Arrays.sort(lValues, 0, lValues.length - 1, new EventComparator2());
                    Arrays.sort(lValues, 0, rValues.length - 1, new EventComparator2());
                    EventBean ec = new EventBean();
                    ec.getHeader().setDetectionTime(Math.min(rValues[0].getHeader().getDetectionTime(), lValues[0].getHeader().getDetectionTime()));
                    ec.getHeader().setPriority((short) Math.max(lValues[0].getHeader().getPriority(), rValues[0].getHeader().getPriority()));
                    ec.getHeader().setIsComposite(true);
                    ec.getHeader().setProductionTime(System.currentTimeMillis());
                    ec.getHeader().setProducerID(this.getName());
                    ec.getHeader().setTypeIdentifier("Conjunction");
                    ec.payload.put("l", lValues[0]);
                    ec.payload.put("r", rValues[0]);
                    ec.payload.put("ttl", TTL);
                    ec.payload.put("processTime", ntime);
                    _outputQueue.put(ec);
                    numEventProduced++;
                    getExecutorService().execute(getOutputNotifier());
                }
                break;

                case SelectionMode.MODE_PRIORITY: {
                    // long ntime = System.nanoTime();
                    EventBean ec = new EventBean();
                    ec.getHeader().setDetectionTime(Math.min(rValues[0].getHeader().getDetectionTime(), lValues[0].getHeader().getDetectionTime()));
                    ec.getHeader().setPriority((short) Math.max(lValues[0].getHeader().getPriority(), rValues[0].getHeader().getPriority()));
                    ec.getHeader().setIsComposite(true);
                    ec.getHeader().setProductionTime(System.currentTimeMillis());
                    ec.getHeader().setProducerID(this.getName());
                    ec.getHeader().setTypeIdentifier("Conjunction");
                    ec.payload.put("l", lValues[0]);
                    ec.payload.put("r", rValues[0]);
                    ec.payload.put("ttl", TTL);
                    ec.payload.put("processTime", ntime);
                    _outputQueue.put(ec);
                    numEventProduced++;
                    getExecutorService().execute(getOutputNotifier());
                }
                break;

                default: { /// mode recent
                    //long ntime = System.nanoTime();
                    Arrays.sort(lValues, 0, lValues.length - 1, new EventComparator2());
                    Arrays.sort(lValues, 0, rValues.length - 1, new EventComparator2());
                    EventBean ec = new EventBean();
                    ec.getHeader().setDetectionTime(Math.min(rValues[rValues.length - 1].getHeader().getDetectionTime(), lValues[lValues.length - 1].getHeader().getDetectionTime()));
                    ec.getHeader().setPriority((short) Math.max(lValues[lValues.length - 1].getHeader().getPriority(), rValues[rValues.length - 1].getHeader().getPriority()));
                    ec.getHeader().setIsComposite(true);
                    ec.getHeader().setProductionTime(System.currentTimeMillis());
                    ec.getHeader().setProducerID(this.getName());
                    ec.getHeader().setTypeIdentifier("Conjunction");
                    ec.payload.put("l", lValues[lValues.length - 1]);
                    ec.payload.put("r", rValues[rValues.length - 1]);
                    ec.payload.put("ttl", TTL);
                    ec.payload.put("processTime", ntime);
                    numEventProduced++;
                    _outputQueue.put(ec);
                    getExecutorService().execute(getOutputNotifier());
                }
                break;
            }
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
            // update statistics: number event processed
             numEventProcessed+= (lValues.length+rValues.length);
        }
        // update statistics: processing time
        time = System.currentTimeMillis()-time;
        processingTime+=time;        
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
