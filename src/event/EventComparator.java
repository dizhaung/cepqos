/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.util.Comparator;

/**
 * Sorting events using this comparator lead to a priority based sorting, where high priority events are 
 * always in the head of the sorted set.
 * @author epaln
 */
public class EventComparator implements Comparator<EventBean> {

    public EventComparator() {
    }

    @Override
    public int compare(EventBean e1, EventBean e2) {
        if (e1.getHeader().getPriority() < e2.getHeader().getPriority()) {
            return 1;
        }
        if (e1.getHeader().getPriority() > e2.getHeader().getPriority()) {
            return -1;
        } 
        else {
            long delta = e1.getHeader().getDetectionTime() - e2.getHeader().getDetectionTime();
            
            if (delta > 0) { // e2 is recent, should be treated first (e1>e2)
                return 1;
            }
            if (delta < 0) {
                return -1;
            }
            return 0;
        }
    }
}
