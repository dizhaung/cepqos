/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.EqualFilter;
import core.FilterAgent;
import core.GreatherThanFilter;
import core.TimeBatchWindow;
import core.WindowAgent;
import core.pubsub.Relayer;
import event.EventTypeRepository;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

/**
 *
 * @author epaln
 */
public class EPInAction {

    static {
        LogManager.getLogManager().reset();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Relayer.getInstance();
        
        EventTypeRepository.getInstance().dump();
        
        TimeBatchWindow win = new TimeBatchWindow(10, TimeUnit.SECONDS);
        WindowAgent windowA = new WindowAgent("Windows", "MeterEvent", "window", win);
        
        FilterAgent filterA = new FilterAgent("Energivore", "window", "filterA");
        filterA.addFilter(new GreatherThanFilter("realPowerWatts", 30d));

        FilterAgent filterB = new FilterAgent("WashingMachine", "Circuits", "filterB");

        filterB.addFilter(new EqualFilter("circuitName", "WashingMachine"));
        //BatchNWindow batchwin = new BatchNWindow(3000);

        filterA.openIOchannels();
        filterB.openIOchannels();
        windowA.openIOchannels();

        windowA.start();
        filterA.start();
        filterB.start();

    }
}
