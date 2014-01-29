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

    String _attribute;
    String _aggAttribute;

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
        double sum = 0;
        for (EventBean evt : evts) {
            sum += Double.parseDouble(evt.getValue(_attribute).toString());
        }
        EventBean evt = new EventBean();
        evt.getHeader().setOccurenceTime(System.currentTimeMillis());
        evt.getHeader().setIsComposite(true);
        evt.getHeader().setTypeIdentifier("Sum(" + _attribute + ")");
        evt.payload.put(_aggAttribute, sum);

        return evt;
    }
}
