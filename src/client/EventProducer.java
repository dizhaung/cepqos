/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import Exceptions.EventTypeException;
import core.EPAgent;
import core.IOTerminal;
import core.pubsub.PubSubService;
import core.pubsub.Relayer;
import event.EventBean;
import event.EventTypeRepository;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;

/**
 *
 * @author epaln
 */
public class EventProducer extends EPAgent {

    private Class _clazz;
    private String _topicName;
    private IOTerminal output;

    public EventProducer(String typeName, Class clazz) {
        _clazz = clazz;
        _topicName = typeName;
        if (clazz != null) {
            declareEventType(typeName, clazz);
        }
        _type = "Producer";
        output = new IOTerminal(_topicName, null, this);
    }

    //private boolean declareEventType(String typeName, Class clazz) throws EventTypeException {
    private boolean declareEventType(String typeName, Class clazz) {
        if (!EventTypeRepository.getInstance().addEventType(typeName, clazz)) {
            //throw new EventTypeException("The type name " + typeName + " has already been defined");
            return false;
        } else {
            return true;
        }
    }

    /**
     * construct an event bean using the given event object and send it to the
     * corresponding channel
     *
     * @param o the event carrying the data to be send
     * @return true if the event all happens correctly or false otherwise
     */
    public boolean sendEvent(Object o) {
        try {
            String type = EventTypeRepository.getInstance().findByValue(o.getClass());
            if (type != null) {
                EventBean evt = new EventBean();
                evt.getHeader().setDetectionTime(System.currentTimeMillis());
                evt.getHeader().setTypeIdentifier(type);
                evt.getHeader().setPriority((short)0);
                evt.getHeader().setProductionTime(System.currentTimeMillis());
                evt.getHeader().setProducerID(this.getName());
                BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());

                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

                for (int i = 0; i < pds.length; i++) {
                    String attribute = pds[i].getName();
                    if (attribute.equals("class")) {
                        continue;
                    }

                    Method getter = MethodUtils.getAccessibleMethod(o.getClass(), pds[i].getReadMethod());
                    if (getter != null) {
                        Object value = getter.invoke(o, null);
                        //Object value=BeanUtils.getProperty(o, attribute);
                        evt.payload.put(attribute, value);
                    }
                }
                
                         
                EventBean[] evts = {evt};
                output.send(evts);
                return true;
            } else {
                throw new EventTypeException("The underlying event type has not been registered");
            }

        } catch (Exception ex) {
            Logger.getLogger(EventProducer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public void run() {
    }

    @Override
    public Collection<IOTerminal> getInputTerminals() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public IOTerminal getOutputTerminal() {
        return output;
    }

    @Override
    public void process() {
    }

    @Override
    public boolean fetch() {
        return false;
    }

//    @Override
//    public void process(EventBean[] evts) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
