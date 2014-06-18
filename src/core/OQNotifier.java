/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.BoundedPriorityBlockingQueue;
import event.EventBean;
import java.util.ArrayList;
import java.util.List;
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
    private long timeout = 600000;
    //ScheduledExecutorService scheduledExecutorService;
    //ScheduledFuture scheduledFuture = null;

    public OQNotifier(IOTerminal ioTerm, BoundedPriorityBlockingQueue outputQ, short strategy) {
        _strategy = strategy;
        _outputQ = outputQ;
        _ioTerm = ioTerm;
       // scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

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

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            EventBean[] evts = retrieveZeroTTLs();
            if (evts.length > 0) {
                _ioTerm.send(evts);
            }
            if (_strategy == QoSTuner.NOTIFICATION_PRIORITY) { // default strategy
                evts = new EventBean[1];
                evts[0] = (EventBean) _outputQ.take();
                evts[0].getHeader().setNotificationTime(System.currentTimeMillis());
                _ioTerm.send(evts);
            } else if (_strategy == QoSTuner.NOTIFICATION_BATCH) {
                /*
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(false);
                }
                * */

                if (_outputQ.size() >= batch_size) {
                    List<EventBean> batch = new ArrayList<EventBean>();
                    _outputQ.drainTo(batch, batch_size);
                    for (EventBean evt : batch) {
                        evt.getHeader().setNotificationTime(System.currentTimeMillis());
                    }
                    evts = (EventBean[]) batch.toArray(new EventBean[0]);
                    _ioTerm.send(evts);
                } 
               /* else { // notify after the timeout has elapsed...
                    scheduledFuture =
                            scheduledExecutorService.schedule(new Callable() {
                        public Object call() throws Exception {
                            List<EventBean> batch = new ArrayList<EventBean>();
                            _outputQ.drainTo(batch);
                            for (EventBean evt : batch) {
                                evt.getHeader().setNotificationTime(System.currentTimeMillis());
                            }
                            EventBean[] evs = (EventBean[]) batch.toArray(new EventBean[0]);
                            _ioTerm.send(evs);
                            return "(INFO) Notification achieved after timeout (" + timeout + " ms) elapsed: " + evs.length + " events.";
                        }
                    },
                            timeout,
                            TimeUnit.MILLISECONDS);

                    System.out.println(scheduledFuture.get());
                }
                */
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
            if ((int) evt.getValue("ttl") == 0) {
                evt.getHeader().setNotificationTime(System.currentTimeMillis());
                zeroTTLs.add(evt);
                _outputQ.remove(evt);
            }
        }
        EventBean[] evts = (EventBean[]) zeroTTLs.toArray(new EventBean[0]);
        return evts;
    }

    private void decreaseAllTTLs() {
        for (EventBean evt : _outputQ) {
            evt.payload.put("ttl", ((int) (evt.getValue("ttl")) - 1));
        }
    }
}