/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.BatchNWindow;
import core.FilterAgent;
import core.GreatherThanFilter;
import core.TimeBatchWindow;
import core.WindowAgent;
import core.pubsub.Relayer;
import event.EventTypeRepository;
import java.util.concurrent.TimeUnit;
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
        TimeBatchWindow win = new TimeBatchWindow(10, TimeUnit.SECONDS);
        WindowAgent windowA = new WindowAgent("Windows", "Circuits", "win_out", win);
//        FilterAgent filterA = new FilterAgent("Energivore", "win_out", "output_A");
//        filterA.addFilter(new GreatherThanFilter("realPowerWatts", 500d));
//        
//        FilterAgent filterB = new FilterAgent("WashingMachine", "Circuits", "output_B");

//        filterB.addFilter(new EqualFilter("circuitName", "WashingMachine"));
        //BatchNWindow win = new BatchNWindow(3000);

  //      filterA.openIOchannels();
        // filterB.openIOchannels();
        windowA.openIOchannels();

        windowA.start();
      //  filterA.start();
        // filterB.start();

    }
}
