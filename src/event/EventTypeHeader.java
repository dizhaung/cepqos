/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author epaln
 */
public class EventTypeHeader implements Serializable {

    // type description attribute
    private boolean isComposite = false;
    private String typeIdentifier;
    // header attribute indicators
    private long occurenceTime; // interval ?
    private long detectionTime;
    //private Object eventSource;
    //private String eventIdentity; // UUID generated 
    //private String eventAnnotation; // human readable event description

    public EventTypeHeader() {
        //eventIdentity = UUID.randomUUID().toString();
    }

    public EventTypeHeader(String typeIdentifier, long occurenceTime, long detectionTime, Object eventSource, String eventIdentity, String eventAnnotation) {
        this.typeIdentifier = typeIdentifier;
        this.occurenceTime = occurenceTime;
        this.detectionTime = detectionTime;
        //this.eventSource = eventSource;
        //this.eventIdentity = eventIdentity;
        //this.eventAnnotation = eventAnnotation;
    }

    public boolean isIsComposite() {
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

    public long getOccurenceTime() {
        return occurenceTime;
    }

    public void setOccurenceTime(long occurenceTime) {
        this.occurenceTime = occurenceTime;
    }

    public long getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(long detectionTime) {
        this.detectionTime = detectionTime;
    }
    /*
     public Object getEventSource() {
     return eventSource;
     }

     public void setEventSource(Object eventSource) {
     this.eventSource = eventSource;
     }

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
}
