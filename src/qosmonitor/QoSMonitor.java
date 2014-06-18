/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qosmonitor;

import java.util.HashMap;

/**
 *
 * @author epaln
 */
public class QoSMonitor {
    
    private static QoSMonitor instance = null;
    private HashMap<String, Object> latenciesMeasures;
    
    private QoSMonitor(){
        latenciesMeasures = new HashMap<>();
    }
    
    public static QoSMonitor getInstance(){
      if(instance == null){
          instance = new QoSMonitor();
      }   
      return instance;
    }
    
}
