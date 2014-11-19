/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import base.NameValuePair;
import event.EventBean;

/**
 *
 * @author epaln
 */
public class Count extends Aggregate {

    public Count(String aggregatedAttribute) {
        //_attribute = attribute;
        _aggAttribute = aggregatedAttribute;
    }

    /**
     * compute the aggregated value (the count in this case) of the specified
     * attribute over the specified array of events
     *
     * @param evts
     * @return an Attribute/value pair carrying the aggregated
     * value and its attribute name
     */
    @Override
    protected NameValuePair aggregate(EventBean[] evts) {
     
         NameValuePair  res = new NameValuePair(_aggAttribute, evts.length);
        return res;
    }
    
}
