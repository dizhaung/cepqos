/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;




/**
 *
 * @author epaln
 */
public abstract class WindowHandler {
    
    /**
     * register an observer to the source stream (observable) of the given Window agent.
     * The registered observer start a notification thread (notifier) each time it receive a new window
     * 
     * @param agent the Agent which contains the source stream on which we wan to compute the windows 
     */
      public abstract void register(WindowAgent agent);
   
}
