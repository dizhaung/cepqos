/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Queues;
import core.pubsub.Relayer;
import event.EventBean;
import java.util.ArrayList;
//import hu.akarnokd.reactive4java.util.DefaultObservable;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import log.MyLogger;
import qosmonitor.QoSTuner;

/**
 *
 * @author epaln
 */
public class TimeWindowAgentBase extends EPAgent {

    IOTerminal inputTerminal;
    IOTerminal outputTerminal;
    ListMultimap<Long, EventBean> activeWindows;
    private long timeshift, timewindows;
    //ScheduledExecutorService scheduledExecutorService;
    Timer timer;
    long index = 0;
    TimerHandler r;
    Lock lock;
    //ScheduledFuture scheduledFuture = null;

    public TimeWindowAgentBase(String info, String IDinputTerminal, String IDoutputTerminal) {
        super();
        this.setName(this.getName() + "@" + Relayer.getInstance().getAddress().toString());
        //_sourceStream = new DefaultObservable<EventBean>();
        this._info = info;
        this._type = "Window";
        this._receivers[0] = new TopicReceiver();
        inputTerminal = new IOTerminal(IDinputTerminal, "input channel " + _type, _receivers[0], this);
        outputTerminal = new IOTerminal(IDoutputTerminal, "output channel " + _type, this);
        _outputNotifier = new OQNotifier(this, QoSTuner.NOTIFICATION_PRIORITY);
        Queue<EventBean> selected1 = Queues.newArrayDeque();
        _selectedEvents[0] = selected1;
        activeWindows = ArrayListMultimap.create();
        //activeWindows = Multimaps.synchronizedListMultimap(activeWindows);
        //scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        timer = new Timer();
        logger = new MyLogger("WindowsMeasures", WindowAgent.class.getName());
        logger.log("Operator, isProduced, Processing Time, InputQ Size, OutputQ Size ");
        r = new TimerHandler(this);
        lock = new ReentrantLock();
    }

    public IOTerminal getInputTerminal() {
        return inputTerminal;
    }

    public void setInputTerminal(IOTerminal inputTerminal) {
        this.inputTerminal = inputTerminal;
    }

    public ListMultimap<Long, EventBean> getActiveWindows() {
        return activeWindows;
    }

    public void setActiveWindows(ListMultimap<Long, EventBean> activeWindows) {
        this.activeWindows = activeWindows;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getTimeshift() {
        return timeshift;
    }

    public long getTimewindows() {
        return timewindows;
    }

    public Lock getLock() {
        return lock;
    }

    public void setTimers(long timeWidows, long timeShift, TimeUnit unit) {
        if (unit == TimeUnit.DAYS) {
            this.timewindows = timeWidows * 86400000;
            this.timeshift = timeShift * 86400000;
        } else if (unit == TimeUnit.HOURS) {
            this.timewindows = timeWidows * 3600000;
            this.timeshift = timeShift * 3600000;
        } else if (unit == TimeUnit.MINUTES) {
            this.timewindows = timeWidows * 60000;
            this.timeshift = timeShift * 60000;
        } else if (unit == TimeUnit.SECONDS) {
            this.timewindows = timeWidows * 1000;
            this.timeshift = timeShift * 1000;
        } else {
            this.timewindows = timeWidows;
            this.timeshift = timeShift;
        }
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
        while (!_selectedEvents[0].isEmpty()) {
            EventBean evt = _selectedEvents[0].poll();
            long ntime = System.nanoTime();
            evt.payload.put("processTime", ntime);

            try {
             lock.lockInterruptibly();     
            //               System.out.println("acquired the lock: want to add an entry");
            //if (activeWindows.isEmpty()) {
            activeWindows.put(index, evt);
            //   } else {

            for (Long l : activeWindows.keySet()) {
                if (l == index) {
                    continue;
                }
                activeWindows.put(l, evt);
            }
                        
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // release the lock
                lock.unlock();
                System.out.println("release the lock: added an entry");
            }
            numEventProcessed++;
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

    @Override
    public void run() {
        index = System.currentTimeMillis();
        //scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(r, timeshift, timeshift, _unit);
        timer.scheduleAtFixedRate(r, timeshift, timeshift);
        // start the timer task here
        while (true) {
            if (fetch()) {
                process();
            }
        }
    }

}

class TimerHandler extends TimerTask {

    private TimeWindowAgentBase agent;

    public TimerHandler(TimeWindowAgentBase agt) {
        agent = agt;
    }

    // close a window and open a new one...
    @Override
    public void run() {
        long index = System.currentTimeMillis();
        // add a new window        
        try {
         agent.getLock().lockInterruptibly();
        agent.setIndex(index);//keys().add(index);
        // System.out.println("acquired the lock: want to update entries");
        // remove the closing window and notify it
        for (Long l : agent.getActiveWindows().keySet()) {
            if (index - l >= agent.getTimewindows()) { /// then the window at index l should be removed and notify
                List<EventBean> win = agent.getActiveWindows().get(l);
                agent.getActiveWindows().keys().remove(l);
                EventBean[] evts = win.toArray(new EventBean[0]);
                if (evts.length != 0) {
                    EventBean evt = new EventBean();
                    long ptime = index - l;
                    agent.processingTime += ptime;
                    evt.payload.put("window", evts);
                    //long processTime = (long) evts[0].getValue("processTime");
                    evt.payload.put("processTime", System.nanoTime());
//                    for (EventBean e : evts) {
//                        e.payload.remove("processTime");
//                    }
                    evt.getHeader().setIsComposite(true);
                    evt.getHeader().setProductionTime(index);
                    evt.getHeader().setDetectionTime(evts[0].getHeader().getDetectionTime());
                    evt.getHeader().setTypeIdentifier("Window");
                    evt.getHeader().setProducerID(agent.getName());
                    evt.getHeader().setPriority((short) 1);
                    evt.payload.put("ttl", agent.TTL);
                    agent.getOutputQueue().put(evt);
                    agent.numEventProduced++;
                    agent.getOutputNotifier().run();
                }
                break;
            }
        }
         }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            agent.lock.unlock();
            System.out.println("released the lock: update some entries");
        }
    }
}
