/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import core.Aggregate;
import core.AggregatorAgent;
import core.DisjunctionAgent;
import core.EPAgent;
import core.EqualFilter;
import core.FilterAgent;
import core.FollowedByAgent;
import core.GreatherThanFilter;
import core.LessThanFilter;
import core.SelectionMode;
import core.Sum;
import core.TimeBatchWindow;
import core.WindowAgent;
import core.WindowHandler;
import core.pubsub.Relayer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import qosmonitor.QoSConstraint;
import qosmonitor.QoSMonitor;
import qosmonitor.QoSTuner;

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

        ArrayList<EPAgent> EPNetwork = new ArrayList<>();
        // start the pub/sub middleware...
        Relayer.getInstance();

        QoSTuner tuner = new QoSTuner();
        tuner.bound(null);
       
        // filter node
        FilterAgent filterA = new FilterAgent("filterA", "MeterEvent", "filterA");
        filterA.addFilter(new EqualFilter("meterID", "b1005"));
        filterA.addFilter(new LessThanFilter("realPowerWatts", 1.5d));
        EPNetwork.add(filterA);
   
        tuner.bound(filterA);
        tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_PRIORITY);
        //tuner.setNotificationBatchSize(100, 5000);
        
        
           
        //QoSConstraint qos = new QoSConstraint();
        //qos.setMaxLatency(10);
        
        //qos.setNetworkOccupationMax(10);
        //qos.setFullOutputQStrategy(QoSTuner.QUEUE_NOTIFY);
        //tuner.setOutputQueueCapacity(50);
       // tuner.setFullOututQStrategy(QoSTuner.QUEUE_NOTIFY);
     
        // window node
        
        WindowHandler win = new TimeBatchWindow(10, TimeUnit.SECONDS);
        WindowAgent windowA = new WindowAgent("windowA", "MeterEvent", "window");
        windowA.setWindowHandler(win);  
        EPNetwork.add(windowA);
        
        // Disjunction node
        DisjunctionAgent OrA= new DisjunctionAgent("Disjunction", "window", "filterA", "OrAgent");
        OrA.setSelectionMode(SelectionMode.MODE_CONTINUOUS);
        EPNetwork.add(OrA);
        
/*
        // aggregator node
        AggregatorAgent aggrA = new AggregatorAgent("AggrA", "window", "SumPwr");
        Aggregate sum = new Sum("realPowerWatts", "sumPwr");
        aggrA.setAggregator(sum);
        EPNetwork.add(aggrA);
        //tuner.bound(aggrA);
        //tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_BATCH);
//        tuner.setNotificationBatchSize(10, 5000);
//        tuner.setOutputQueueCapacity(50);
//        tuner.setFullOututQStrategy(QoSTuner.QUEUE_NOTIFY);
        
        // filter node 2
        FilterAgent filter2 = new FilterAgent("filter2A", "SumPwr", "filter2");
        filter2.addFilter(new GreatherThanFilter("sumPwr", 10d));
        EPNetwork.add(filter2);
//        tuner.bound(filter2);
//        tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_BATCH);
//        tuner.setNotificationBatchSize(10, 5000);
//        tuner.setOutputQueueCapacity(50);
//        tuner.setFullOututQStrategy(QoSTuner.QUEUE_NOTIFY);
        
        
        FilterAgent filterB = new FilterAgent("filterB", "MeterEvent", "filterB");
        filterB.addFilter(new EqualFilter("meterID", "b1006"));
        filterB.addFilter(new GreatherThanFilter("realPowerWatts", 0d));
        EPNetwork.add(filterB);
//        tuner.bound(filterB);
//        tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_BATCH);
//        tuner.setNotificationBatchSize(10, 5000);
//        tuner.setOutputQueueCapacity(50);
//        tuner.setFullOututQStrategy(QoSTuner.QUEUE_NOTIFY);

        // window node     
        WindowAgent windowB = new WindowAgent("windowB", "filterB", "windowB");
        WindowHandler winB = new TimeBatchWindow(10, TimeUnit.SECONDS);
        windowB.setWindowHandler(winB);
        EPNetwork.add(windowB);
        
//        WindowHandler win2B = new SlidingWindow(10, 1, TimeUnit.SECONDS);
//        WindowAgent window2B = new WindowAgent(null, "filterA", "window2");
//        windowA.setWindowHandler(win2B);
//        EPNetwork.add(window2B);
//        
        // aggregator node
        AggregatorAgent aggrB = new AggregatorAgent("aggrB", "windowB", "SumPwrB");
        Aggregate sumB = new Sum("realPowerWatts", "sumPwr");
        aggrB.setAggregator(sumB);
//        tuner.bound(aggrB);
//        tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_BATCH);
//        tuner.setNotificationBatchSize(10, 5000);
//        tuner.setOutputQueueCapacity(50);
//        tuner.setFullOututQStrategy(QoSTuner.QUEUE_NOTIFY);
        EPNetwork.add(aggrB);
        
        // filter node 2
        FilterAgent filter2B = new FilterAgent("filter2B", "SumPwrB", "filter2B");
        filter2B.addFilter(new GreatherThanFilter("sumPwr", 10d));
//        tuner.bound(filter2B);
//        tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_BATCH);
//        tuner.setNotificationBatchSize(10, 5000);
//        tuner.setOutputQueueCapacity(50);
//        tuner.setFullOututQStrategy(QoSTuner.QUEUE_NOTIFY);
        EPNetwork.add(filter2B);
        //tuner.bound(filter2B);
        
        FollowedByAgent followedBy = new FollowedByAgent("Followed By", "SumPwr", "SumPwrB", "Seq");
//        tuner.bound(followedBy);
//        tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_BATCH);
//        tuner.setNotificationBatchSize(10, 5000);
//        tuner.setOutputQueueCapacity(50);
//        tuner.setFullOututQStrategy(QoSTuner.QUEUE_NOTIFY);
        EPNetwork.add(followedBy);
        
        QoSConstraint qos = new QoSConstraint();
        qos.setMaxLatency(10);
        qos.setNetworkOccupationMax(10);
        qos.setFullOutputQStrategy(QoSTuner.QUEUE_NOTIFY);
        
        */
        // Open the Event processing network connections 
        for(EPAgent epu: EPNetwork){
            //epu.setQosConstraint(qos);
            epu.openIOchannels();
        }
        // start the Event processing network
        for(EPAgent epu: EPNetwork){
            epu.start();
        }
        
//        QoSMonitor.getInstance().setEPNetwork(EPNetwork);
//        QoSMonitor.getInstance().setMode(QoSMonitor.MODE_STAT_COLLECT);
//        QoSMonitor.getInstance().startMonitoring();
    }
}
