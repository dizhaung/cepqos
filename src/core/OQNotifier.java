/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.BoundedPriorityBlockingQueue;
import event.EventBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import qosmonitor.QoSTuner;

/**
 *
 * @author epaln
 */
public class OQNotifier implements Runnable {

    private short _strategy;
    private BoundedPriorityBlockingQueue _outputQ;
    private IOTerminal _ioTerm;
    private int batch_size = 20;
    //private long timeout = 60000;
    private volatile long timeCounter;
    ScheduledExecutorService scheduledExecutorService;
    ScheduledFuture scheduledFuture = null;
    Runner r;
    private EPAgent _agent;

    public OQNotifier(EPAgent agent, short strategy) {
        _strategy = strategy;
        _outputQ = agent.getOutputQueue();
        _ioTerm = agent.getOutputTerminal();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        r = new Runner(this);
        _agent = agent;
    }

    public short getStrategy() {
        return _strategy;
    }

    public void setStrategy(short _strategy) {
        this._strategy = _strategy;
    }

    public int getBatch_size() {
        return batch_size;
    }

    public void setBatch_size(int batch_size) {
        this.batch_size = batch_size;
        if (batch_size > _outputQ.getCapacity()) {
            batch_size = _outputQ.getCapacity();
        }
    }

    public long getTimeCounter() {
        return timeCounter;
    }

    public void setTimeCounter(long timeCounter) {
        this.timeCounter = timeCounter;
    }

    public IOTerminal getIoTerm() {
        return _ioTerm;
    }

    public void logStatData(EventBean evt, String msg) {
        try {
            long ptime = System.nanoTime() - (long) evt.getValue("processTime");
            int sizeinput = _agent.getInputTerminals().iterator().next().getReceiver().getInputQueue().size();
            _agent.getLogger().log(_agent.getInfo() + ", "+ msg+ ", " + ptime + ", " + sizeinput + ", " + _agent.getOutputQueue().size());
        } catch (Exception ex) {
            Logger.getLogger(OQNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public BoundedPriorityBlockingQueue getOutputQ() {
        return _outputQ;
    }

    @Override
    public void run() {
        try {
            EventBean[] evts = retrieveZeroTTLs();
            if (evts.length > 0) {
                _ioTerm.send(evts);
                increaseNotificationCount(evts.length);
            }
            if (_strategy == QoSTuner.NOTIFICATION_PRIORITY) { // default strategy
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                    scheduledFuture = null;
                }
                evts = new EventBean[1];
                evts[0] = (EventBean) _outputQ.take();
                evts[0].getHeader().setNotificationTime(System.currentTimeMillis());
                // compute the ptime of every event to be notified
                for (EventBean e : evts) {
//                    long ptime = System.nanoTime() - (long) e.getValue("processTime");
//                    int sizeinput = _agent.getInputTerminals().iterator().next().getReceiver().getInputQueue().size();
//                    _agent.getLogger().log(_agent.getInfo() + ", True, " + ptime + ", " + sizeinput + ", " + _agent.getOutputQueue().size());
                    logStatData(e, "True");
                    e.payload.remove("processTime");
                }
                // notify the events
                _ioTerm.send(evts);
                increaseNotificationCount(evts.length);
            } else if (_strategy == QoSTuner.NOTIFICATION_BATCH) {
                timeCounter = _agent.getQosConstraint().getNotificationTimeout();
                //timeCounter = System.currentTimeMillis();
                if (_outputQ.size() >= batch_size) {
                    List<EventBean> batch = new ArrayList<EventBean>();
                    _outputQ.drainTo(batch, batch_size);
                    for (EventBean evt : batch) {
                        evt.getHeader().setNotificationTime(System.currentTimeMillis());
                        logStatData(evt, "True"); 
                        evt.payload.remove("processTime");
                    }
                    evts = (EventBean[]) batch.toArray(new EventBean[0]);
                    _ioTerm.send(evts);
                    timeCounter = _agent.getQosConstraint().getNotificationTimeout();
                    increaseNotificationCount(evts.length);
                } else {
                    EventBean e = (EventBean) _outputQ.peek();
                    logStatData(e, "True & batched");
                    // notify after the timeout has elapsed...
                    if (scheduledFuture == null) {
                        scheduledFuture
                                = scheduledExecutorService.scheduleAtFixedRate(r, 0, 1, TimeUnit.MILLISECONDS);
                    }

                }

            }

            decreaseAllTTLs();

        } catch (Exception ex) {
            Logger.getLogger(OQNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        //scheduledExecutorService.shutdown();
    }

    /**
     * retrieve the events for which the ttl value equals zero, setting their
     * notification time to System.currentTimeMillis();
     *
     * @return
     */
    private EventBean[] retrieveZeroTTLs() {
        ArrayList<EventBean> zeroTTLs = new ArrayList<>();
        for (EventBean evt : _outputQ) {
            try {
                if ((int) evt.getValue("ttl") == 0) {
                    evt.getHeader().setNotificationTime(System.currentTimeMillis());
                    zeroTTLs.add(evt);
                    _outputQ.remove(evt);
                }
            } catch (Exception ex) {
                Logger.getLogger(OQNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        EventBean[] evts = (EventBean[]) zeroTTLs.toArray(new EventBean[0]);
        return evts;
    }

    public void increaseNotificationCount(int num) {
        _agent.numEventNotified += num;
    }

    private void decreaseAllTTLs() {
        for (EventBean evt : _outputQ) {
            try {
                evt.payload.put("ttl", ((int) (evt.getValue("ttl")) - 1));
            } catch (Exception ex) {
                Logger.getLogger(OQNotifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class Runner implements Runnable {

    private OQNotifier notifier;

    public Runner(OQNotifier not) {
        notifier = not;
    }

    public volatile boolean RUNNING = false;

    public void run() {

        RUNNING = true;
        try {
            notifier.setTimeCounter(notifier.getTimeCounter() - 1);
            if (notifier.getTimeCounter() <= 0 && notifier.getOutputQ().size() > 0) {

                List<EventBean> batch = new ArrayList<EventBean>();
                notifier.getOutputQ().drainTo(batch);
                for (EventBean evt : batch) {
                    evt.getHeader().setNotificationTime(System.currentTimeMillis());
                }
                EventBean[] evs = (EventBean[]) batch.toArray(new EventBean[0]);
                notifier.getIoTerm().send(evs);
                notifier.increaseNotificationCount(evs.length);
                System.out.println("Timeout elapsed... " + evs.length + " events notified");
            }
        } catch (Exception ex) {
            Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            RUNNING = false;
        }
    }
}
