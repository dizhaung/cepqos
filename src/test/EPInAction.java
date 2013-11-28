/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.EqualFilter;
import core.FilterAgent;
import core.LessThanFilter;
import event.EventTypeRepository;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 *
 * @author epaln
 */
public class EPInAction {
    
    static {
      LogManager.getLogManager().reset();
//      Logger globalLogger = Logger.getGlobal();
//      globalLogger.setLevel(Level.OFF);
   }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EventTypeRepository.getInstance().dump();
        FilterAgent filterA = new FilterAgent("LessThan-25-Only", "Test", "outputfilter");        
        
        FilterAgent filterB = new FilterAgent("10-Only", "outputfilter", "enfant");
        filterA.addFilter(new LessThanFilter("age", 25));
        filterB.addFilter(new EqualFilter("age", 10));
        try {
            filterA.openIOchannels();
            //Thread.sleep(4000);
            filterB.openIOchannels();
        } catch (Exception ex) {
            Logger.getLogger(EPInAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        filterA.start();
        filterB.start();
    }
    
}
