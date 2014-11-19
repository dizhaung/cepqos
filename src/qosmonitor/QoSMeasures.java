/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qosmonitor;

/**
 *
 * @author epaln
 */
public class QoSMeasures {
    private long observedMeanLatency;
    private int observedNumberNotifications;
    private int numEventProcessed =0;
    private  long processingTime=0;
    private long numEventProduced =0;
    private long observedmeanNetworkLatency;

    public int getNumEventProcessed() {
        return numEventProcessed;
    }

    public void setNumEventProcessed(int numEventProcessed) {
        this.numEventProcessed = numEventProcessed;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public long getNumEventProduced() {
        return numEventProduced;
    }

    public void setNumEventProduced(long numEventProduced) {
        this.numEventProduced = numEventProduced;
    }
    
    public QoSMeasures() {
    }

    public long getObservedMeanLatency() {
        return observedMeanLatency;
    }

    public void setObservedMeanLatency(long observedMeanLatency) {
        this.observedMeanLatency = observedMeanLatency;
    }

    

    public int getObservedNumberNotifications() {
        return observedNumberNotifications;
    }

    public void setObservedNumberNotifications(int observedNumberNotifications) {
        this.observedNumberNotifications = observedNumberNotifications;
    }

    public long getObservedmeanNetworkLatency() {
        return observedmeanNetworkLatency;
    }

    public void setObservedmeanNetworkLatency(long observedmeanNetworkLatency) {
        this.observedmeanNetworkLatency = observedmeanNetworkLatency;
    }

    
}
