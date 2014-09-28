/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epaln
 */
public class EventBean implements Serializable {

    EventHeader header;
    public ConcurrentHashMap<String, Object> payload;

    public EventBean() {
        header = new EventHeader();
        payload = new ConcurrentHashMap<>();
    }

    public EventHeader getHeader() {
        return header;
    }

    public Object getValue(String attr) {
        
        if (!payload.containsKey(attr)) {
            System.out.println("property named '" + attr + "' is not valid for this type");
            try {
                throw new Exception("property named '" + attr + "' is not valid for this type");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return payload.get(attr);
    }

    @Override
    public String toString() {
        return payload.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
