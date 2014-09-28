/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.util.Comparator;

/**
 * Sorting events using this comparator lead to a time based sorting, where events are sorted by their
 * order of detection time.  
 * @author epaln
 */
 
public class EventComparator2 implements Comparator<EventBean> {

    @Override
    public int compare(EventBean e1, EventBean e2) {

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
