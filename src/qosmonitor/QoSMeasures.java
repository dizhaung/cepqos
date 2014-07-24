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
    private float observedMeanLatency;
    private int observedNumberNotifications;
    private int numEventProcessed =0;
    private  long processingTime=0;
    private long numEventProduced =0;

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

    public float getObservedMeanLatency() {
        return observedMeanLatency;
    }

    public void setObservedMeanLatency(float observedMeanLatency) {
        this.observedMeanLatency = observedMeanLatency;
    }

    public int getObservedNumberNotifications() {
        return observedNumberNotifications;
    }

    public void setObservedNumberNotifications(int observedNumberNotifications) {
        this.observedNumberNotifications = observedNumberNotifications;
    }

}
