/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.Aggregate;
import core.AggregatorAgent;
import core.EqualFilter;
import core.FilterAgent;
import core.GreatherThanFilter;
import core.Sum;
import core.TimeBatchWindow;
import core.WindowAgent;
import core.WindowHandler;
import core.pubsub.Relayer;
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
        
        // start the pub/sub middleware...
        Relayer.getInstance();
        
        // filter node
        FilterAgent filterA = new FilterAgent(null, "MeterEvent", "filterA");
        filterA.addFilter(new EqualFilter("meterName", "b878"));
        filterA.addFilter(new GreatherThanFilter("realPowerWatts", 0d));
        
        // window node
        
        WindowHandler win = new TimeBatchWindow(10, TimeUnit.SECONDS);
        WindowAgent windowA = new WindowAgent(null, "filterA", "window");
        windowA.setWindowHandler(win);
        
        // aggregator node
        AggregatorAgent aggrA = new AggregatorAgent(null, "window", "SumPwr");
        Aggregate sum = new Sum("realPowerWatts", "sumPwr");
        aggrA.setAggregator(sum);
        
       
        // Open the Event processing network connections
        filterA.openIOchannels();        
        windowA.openIOchannels();
        aggrA.openIOchannels();
        
        // start the Event processing network
        windowA.start();
        filterA.start();
        aggrA.start();

    }
}
