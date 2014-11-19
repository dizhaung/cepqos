/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qosmonitor;

/**
 *
 * @author epaln
 */
public class QoSConstraint {
    private long maxLatency=0; // en millisec
    //public float meanLatency=0;
    private int networkOccupationMax=0; //100 
    private int queueCapacity= 1000;
    private short fullInputQStrategy;
    private short fullOutputQStrategy;
    private long notificationTimeout = 3600000; // 1h
    private long batch_size = 50;

    public long getBatch_size() {
        return batch_size;
    }

    public void setBatch_size(long batch_size) {
        this.batch_size = batch_size;
    }
    
    public long getMaxLatency() {
        return maxLatency;
    }

    public void setMaxLatency(long maxLatency) {
        this.maxLatency = maxLatency;
    }

    public long getNotificationTimeout() {
        return notificationTimeout;
    }

    public void setNotificationTimeout(long notificationTimeout) {
        this.notificationTimeout = notificationTimeout;
    }

    public int getNetworkOccupationMax() {
        return networkOccupationMax;
    }

    public void setNetworkOccupationMax(int networkOccupationMax) {
        this.networkOccupationMax = networkOccupationMax;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public short getFullInputQStrategy() {
        return fullInputQStrategy;
    }

    public void setFullInputQStrategy(short fullInputQStrategy) {
        this.fullInputQStrategy = fullInputQStrategy;
    }

    public short getFullOutputQStrategy() {
        return fullOutputQStrategy;
    }

    public void setFullOutputQStrategy(short fullOutputQStrategy) {
        this.fullOutputQStrategy = fullOutputQStrategy;
    }
    
    
}
