/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.AggregatorAgent;
import core.EqualFilter;
import core.FilterAgent;
import core.GreatherThanFilter;
import core.Sum;
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
        WindowAgent windowA = new WindowAgent("Windows", "filterA", "window", win);
        
        FilterAgent filterA = new FilterAgent("Energivore", "MeterEvent", "filterA");
        //filterA.addFilter(new GreatherThanFilter("realPowerWatts", 30d));
        filterA.addFilter(new EqualFilter("meterName", "b878"));
        AggregatorAgent aggrA = new AggregatorAgent(null, "window", "SumPwr");
        Sum sum = new Sum(aggrA, "realPowerWatts", "sumPwr");
        aggrA.setAggregator(sum);
        FilterAgent filterB = new FilterAgent("WashingMachine", "Circuits", "filterB");

        filterB.addFilter(new EqualFilter("circuitName", "WashingMachine"));
        //BatchNWindow batchwin = new BatchNWindow(3000);

        filterA.openIOchannels();
        filterB.openIOchannels();
        windowA.openIOchannels();
        aggrA.openIOchannels();

        windowA.start();
        filterA.start();
        filterB.start();
        aggrA.start();

    }
}
