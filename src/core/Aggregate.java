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
public abstract class Aggregate {
    
    protected abstract EventBean aggregate(EventBean[] evts);
    
}
