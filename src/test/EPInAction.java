/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.EqualFilter;
import core.FilterAgent;
import core.GreatherThanFilter;
import core.LessThanFilter;
import core.pubsub.Relayer;
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
        Relayer.getInstance();
        EventTypeRepository.getInstance().dump();
        FilterAgent filterA = new FilterAgent("Energivore", "Circuits", "output_A");        
        
        FilterAgent filterB = new FilterAgent("WashingMahine", "Circuits", "output_B");
        filterA.addFilter(new GreatherThanFilter("realPowerWatts", 10d));
        filterB.addFilter(new EqualFilter("circuitName", "WashingMahine"));
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
