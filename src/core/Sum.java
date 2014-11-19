/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.NameValuePair;
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
     * @return an Attribute/value pair carrying the aggregated
     * value and its attribute name
     */
    @Override
    protected NameValuePair aggregate(EventBean[] evts) {
        double sum = 0; //int sumPriority =0;
        for (EventBean evt : evts) {
            sum += Double.parseDouble(evt.getValue(_attribute).toString());
            //sumPriority+= evt.getHeader().getPriority();
        }
        NameValuePair  res = new NameValuePair(_aggAttribute, sum);
//        EventBean evt = new EventBean();
//        evt.getHeader().setDetectionTime(evts[0].getHeader().getDetectionTime());
//        evt.getHeader().setIsComposite(true);
//        evt.getHeader().setProductionTime(System.currentTimeMillis());
//        evt.getHeader().setTypeIdentifier("Sum(" + _attribute + ")");
//        evt.payload.put(_aggAttribute, sum);
//        evt.getHeader().setPriority((short)Math.round(sumPriority/evts.length));
        return res;
    }
}
