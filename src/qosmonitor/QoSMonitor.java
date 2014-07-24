/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qosmonitor;

import core.EPAgent;
import core.IOTerminal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import log.MyLogger;

/**
 *
 * @author epaln
 */
public class QoSMonitor {
    
    public static final short MODE_STAT_COLLECT =0;
    public static final short MODE_MONITOR =1;
    private static QoSMonitor instance = null;
    private final HashMap<String, QoSMeasures> allQosMeasures;
    private ArrayList<EPAgent> EPNetwork;
    private final ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture scheduledFuture = null;
    private final MyLogger logger;
    private int mode;
    
    private QoSMonitor(){
        allQosMeasures = new HashMap<>();
        EPNetwork = new ArrayList<>();
        mode = MODE_MONITOR; 
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        logger= new MyLogger("QoSMeasures", QoSMonitor.class.getName());
        logger.log("EPU_IDENTIFIER, EPU_INFO, #EPU_NETWORK_NOTIFICATION, EPU_MEAN_NOTIFICATION_LATENCY, EPU_OUTPUTQ_CAPACITY, EPU_PROCESSING_TIME, #EVENTS_PROCESSED, #EVENTS_PRODUCED, #INPUTQ_DEFAULTS");
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public static QoSMonitor getInstance(){
      if(instance == null){
          instance = new QoSMonitor();
      }   
      return instance;
    }
    
    public void startMonitoring(){
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new MonitoringTask(), 1, 1, TimeUnit.MINUTES);
    }
    
    public void stopMonitoring(){
        if(scheduledFuture!= null){
            scheduledFuture.cancel(true);
        }
    }
    
    public void addEPAgent(EPAgent epa){
        EPNetwork.add(epa);
    }

    public HashMap<String, QoSMeasures> getAllQosMeasures() {
        return allQosMeasures;
    }

    public ArrayList<EPAgent> getEPNetwork() {
        return EPNetwork;
    }

    public void setEPNetwork(ArrayList<EPAgent> EPNetwork) {
        this.EPNetwork = EPNetwork;
    }

    public MyLogger getLogger() {
        return logger;
    }
}

class MonitoringTask implements Runnable{
    private long stepsBeforeMonitoring;
    static boolean isMonitoring = false;
    static long turn =0;

    public MonitoringTask() {
        stepsBeforeMonitoring =0;
    }

    public MonitoringTask(long stepsBeforeMonitoring) {
        this.stepsBeforeMonitoring = stepsBeforeMonitoring;
    }
    
    @Override
    public void run() {
        turn ++;
        if(turn==stepsBeforeMonitoring){    
            isMonitoring = true;
        }
        QoSTuner tuner = new QoSTuner();
        for(EPAgent epa: QoSMonitor.getInstance().getEPNetwork()){
            QoSMeasures qosM = QoSMonitor.getInstance().getAllQosMeasures().get(epa.getName());
            if(qosM== null){
                qosM = new QoSMeasures();
            }
            // collect the QoS measures of each epu
            if(epa.numEventNotifiedNetwork==0){ // no networked event notification, => this epa notified locally, the latency is set to 0, means negligible
                qosM.setObservedMeanLatency(0);
            }
            else{
                qosM.setObservedMeanLatency(epa.sumLatencies/epa.numEventNotifiedNetwork);               
            }
             qosM.setObservedNumberNotifications(epa.numAchievedNotifications);
             qosM.setNumEventProcessed(epa.numEventProcessed);
             qosM.setProcessingTime(epa.processingTime);
             qosM.setNumEventProduced(epa.numEventNotified);
            // reset the counters
            epa.sumLatencies=0;
            epa.numEventNotifiedNetwork=0;
            epa.numEventNotified=0;
            epa.numEventProcessed=0;
            epa.numAchievedNotifications =0;
            epa.processingTime =0;
            QoSMonitor.getInstance().getAllQosMeasures().put(epa.getName(), qosM);
            //log the qos measures
            int def = epa.getInputTerminals().iterator().next().getReceiver().getInputQueue().getNumberDefaults();
            int size = epa.getInputTerminals().iterator().next().getReceiver().getInputQueue().size();
            QoSMonitor.getInstance().getLogger().log(epa.getName()+", "+epa.getInfo()+", "+qosM.getObservedNumberNotifications()+", "
                    +qosM.getObservedMeanLatency()+", "+epa.getOutputQueue().getCapacity()+", "+qosM.getProcessingTime()+", "
            + qosM.getNumEventProcessed()+", "+qosM.getNumEventProduced()+", "+def+"/"+size);
            
            // check for any constraint violation...
            if((QoSMonitor.getInstance().getMode()==QoSMonitor.MODE_MONITOR) && isMonitoring){
            // network occupation violation ?
            tuner.bound(epa);
            if(qosM.getObservedNumberNotifications()>epa.getQosConstraint().getNetworkOccupationMax()){
                if(epa.getQosConstraint().getNetworkOccupationMax()==0){                  
                    tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_PRIORITY);
                }
                else{
                    tuner.setNotificationStrategy(QoSTuner.NOTIFICATION_BATCH);
                    float observed, maxim;
                    observed = qosM.getObservedNumberNotifications();
                    maxim =epa.getQosConstraint().getNetworkOccupationMax();
                    tuner.setNotificationBatchSize((int)Math.ceil(observed/maxim));
                    System.out.println("[QoSMonitor] Batch notification strategy applied at "+epa.getName()+"-"+epa.getInfo());
                    System.out.println("Network occupation max: "+epa.getQosConstraint().getNetworkOccupationMax()+
                            "; Observed: "+qosM.getObservedNumberNotifications());
                    System.out.println("new batch size: " + epa.getOutputNotifier().getBatch_size());
                }
            }
            // latency violation?
            if(qosM.getObservedMeanLatency() > epa.getQosConstraint().getMaxLatency()){
               int newK = Math.max(1, (int)Math.floor(epa.getOutputQueue().getCapacity()*epa.getQosConstraint().getMaxLatency()/qosM.getObservedMeanLatency()));
               System.out.println("[QoSMonitor] Notification latency violated at "+epa.getName()+"-"+epa.getInfo());
               System.out.println("Notification latency max: "+epa.getQosConstraint().getMaxLatency()+
                            "; Observed: "+qosM.getObservedMeanLatency());
               System.out.println("old ouputQ size: "+epa.getOutputQueue().getCapacity() + "; new outputQ size: " + newK);
               tuner.setOutputQueueCapacity(newK);     
            }
            }
        }
    }
    
}