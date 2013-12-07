/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.BatchNWindow;
import core.WindowAgent;
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
//        FilterAgent filterA = new FilterAgent("NoEnergivore", "Circuits", "output_A");        
//        
//        FilterAgent filterB = new FilterAgent("WashingMachine", "Circuits", "output_B");
//        filterA.addFilter(new GreatherThanFilter("realPowerWatts", 300d));
//        filterB.addFilter(new EqualFilter("circuitName", "WashingMachine"));
        BatchNWindow win = new BatchNWindow(3);
        WindowAgent windowA= new WindowAgent("Windows", "Circuits", "win_out", win);
        
        try {
            // filterA.openIOchannels();            
            // filterB.openIOchannels();
            windowA.openIOchannels();
        } catch (Exception ex) {
            Logger.getLogger(EPInAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        // filterA.start();
        // filterB.start();
        windowA.start();
    }
    
}
