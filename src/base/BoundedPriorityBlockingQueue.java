/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import event.EventBean;
import event.EventComparator;
import java.util.Arrays;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author epaln
 */
public class BoundedPriorityBlockingQueue extends PriorityBlockingQueue<EventBean> {

    private int _capacity = 1000;
    public static final short REPLACE = 0;
    public static final short IGNORE = 1;
    private short strategy = REPLACE;

    public BoundedPriorityBlockingQueue() {
       this(1000);
    }

    public BoundedPriorityBlockingQueue(int maxCapacity) {
        super(maxCapacity, new EventComparator());
        _capacity = maxCapacity;
    }

    @Override
    public boolean offer(EventBean e) {
        if (this.size() == _capacity) {
            if (strategy == IGNORE) {
                return false;
            } else { // strategy = REPLACE
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
