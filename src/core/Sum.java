/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import event.EventBean;

/**
 *
 * @author epaln
 */
public class Sum extends Aggregate {

    public Sum(String attribute, String aggregatedAttribute) {

        _attribute = attribute;
        _aggAttribute = aggregatedAttribute;
    }

    /**
     * compute the aggregated value (the sum in this case) of the specified
     * attribute over the specified array of events
     *
     * @param evts
     * @return an eventbean with a unique attribute carrying the aggregated
     * value.
     */
    @Override
    protected EventBean aggregate(EventBean[] evts) {
        double sum = 0; int sumPriority =0;
        for (EventBean evt : evts) {
            sum += Double.parseDouble(evt.getValue(_attribute).toString());
            sumPriority+= evt.getHeader().getPriority();
        }
        EventBean evt = new EventBean();
        evt.getHeader().setDetectionTime(evts[0].getHeader().getDetectionTime());
        evt.getHeader().setIsComposite(true);
        evt.getHeader().setProductionTime(System.currentTimeMillis());
        evt.getHeader().setTypeIdentifier("Sum(" + _attribute + ")");
        evt.payload.put(_aggAttribute, sum);
        evt.getHeader().setPriority((short)Math.round(sumPriority/evts.length));
        return evt;
    }
}
