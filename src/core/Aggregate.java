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

    protected String _attribute;
    protected String _aggAttribute;

    protected abstract EventBean aggregate(EventBean[] evts);

    public String getAttribute() {
        return _attribute;
    }

    public void setAttribute(String _attribute) {
        this._attribute = _attribute;
    }

    public String getAggAttribute() {
        return _aggAttribute;
    }

    public void setAggAttribute(String _aggAttribute) {
        this._aggAttribute = _aggAttribute;
    }
}
