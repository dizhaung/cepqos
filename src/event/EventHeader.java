/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.io.Serializable;

/**
 *
 * @author epaln
 */
public class EventHeader implements Serializable {

    // type description attribute
    private boolean isComposite = false;
    private String typeIdentifier;
    // header attribute indicators
    private long receptionTime; // interval ?
    private long detectionTime;
    private long notificationTime;
    private long productionTime;
    private short priority = 0;
    private String producerID;
    //private String eventIdentity; // UUID generated 
    //private String eventAnnotation; // human readable event description

    public EventHeader() {
        //eventIdentity = UUID.randomUUID().toString();
    }

    public EventHeader(String typeIdentifier, long detectionTime, Object eventSource, String eventIdentity, String eventAnnotation) {
        this.typeIdentifier = typeIdentifier;
        this.detectionTime = detectionTime;
        //this.eventSource = eventSource;
        //this.eventIdentity = eventIdentity;
        //this.eventAnnotation = eventAnnotation;
    }

    public boolean isComposite() {
        return isComposite;
    }

    public void setIsComposite(boolean isComposite) {
        this.isComposite = isComposite;
    }

    public String getTypeIdentifier() {
        return typeIdentifier;
    }

    public void setTypeIdentifier(String typeIdentifier) {
        this.typeIdentifier = typeIdentifier;
    }

    public long getReceptionTime() {
        return receptionTime;
    }

    public void setReceptionTime(long receptionTime) {
        this.receptionTime = receptionTime;
    }

    public long getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(long detectionTime) {
        this.detectionTime = detectionTime;
    }

    public String getProducerID() {
        return producerID;
    }

    public void setProducerID(String producerID) {
        this.producerID = producerID;
    }

    /*

     public String getEventIdentity() {
     return eventIdentity;
     }

     public void setEventIdentity(String eventIdentity) {
     this.eventIdentity = eventIdentity;
     }

     public String getEventAnnotation() {
     return eventAnnotation;
     }

     public void setEventAnnotation(String eventAnnotation) {
     this.eventAnnotation = eventAnnotation;
     }
     */
    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public long getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(long productionTime) {
        this.productionTime = productionTime;
    }
    
}
