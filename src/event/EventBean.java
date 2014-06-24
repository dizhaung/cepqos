/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
        }

        return payload.get(attr);
    }
}
