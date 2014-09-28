/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import core.EPAgent;
import event.EventBean;
import event.EventComparator;
import java.util.Arrays;
import java.util.concurrent.PriorityBlockingQueue;
import qosmonitor.QoSTuner;

/**
 *
 * @author epaln
 */
public class BoundedPriorityBlockingQueue extends PriorityBlockingQueue<EventBean> {

    private int _capacity = 1000;
    volatile private int numberDefaults = 0;
    
    private short strategy = QoSTuner.QUEUE_REPLACE;
    private EPAgent _agent;
    

    public BoundedPriorityBlockingQueue(EPAgent agent) {
       this(1000, agent);
    }

    public BoundedPriorityBlockingQueue(int maxCapacity, EPAgent agent) {
        super(maxCapacity, new EventComparator());
        _capacity = maxCapacity; 
        _agent = agent;
    }

    @Override
    public boolean offer(EventBean e) {
        if (this.size() == _capacity) {
            numberDefaults++;
           // System.out.println("full queue: evts "+this.size()+" in "+ _capacity);
            if (strategy == QoSTuner.QUEUE_IGNORE) {                
                return false;
            }else if(strategy==QoSTuner.QUEUE_NOTIFY){
                _agent.getOutputNotifier().run();
                return super.offer(e);
            } 
            else { // strategy = REPLACE
                boolean success = super.offer(e);
                if (!success) {
                    return false;
                } else {
                    EventBean[] items = (EventBean[]) this.toArray(new EventBean[0]);
                    Arrays.sort(items, new EventComparator());
                    this.remove(items[items.length-1]);
                    return true;
                }
            }
        } else {
            return super.offer(e);
        }
    }

    public int getNumberDefaults() {
        return numberDefaults;
    }

    
    public int getCapacity() {
        return _capacity;
    }

    
    public void setCapacity(int _capacity) {
        this._capacity = _capacity;
    }

    public short getStrategy() {
        return strategy;
    }

    public void setStrategy(short strategy) {
        this.strategy = strategy;
    }
    
    
}
