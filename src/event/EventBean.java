/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.io.Serializable;
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
        String[] attrs = attr.split("\\.");
        if (attrs.length == 1) {
            if (!payload.containsKey(attrs[0])) {
                System.out.println("property named '" + attrs[0] + "' is not valid for this type");
                try {
                    throw new Exception("property named '" + attrs[0] + "' is not valid for this type");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
            return payload.get(attr);
        } else {
            EventBean e = (EventBean) payload.get(attrs[0]);
            if (e != null) {
                return e.getValue(attr.substring(attrs[0].length() + 1));
            } else {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return payload.toString(); //To change body of generated methods, choose Tools | Templates.
    }

}
