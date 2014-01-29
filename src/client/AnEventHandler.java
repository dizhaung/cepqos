/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import event.EventBean;

/**
 *
 * @author epaln
 */
public interface AnEventHandler {

    public void notify(EventBean[] evts);
}
