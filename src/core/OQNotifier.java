/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import base.BoundedPriorityBlockingQueue;
import event.EventBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epaln
 */
public class OQNotifier implements Runnable {

    public static final short PRIORITY = 0;
    public static final short BATCH = 1;
    private short _strategy;
    private BoundedPriorityBlockingQueue _outputQ;
    private IOTerminal _ioTerm;
    private int batch_size = 20;
    private long timeout = 600000;
    
    public OQNotifier(IOTerminal ioTerm, BoundedPriorityBlockingQueue outputQ, short strategy) {
        _strategy = strategy;
        _outputQ = outputQ;
        _ioTerm = ioTerm;
    }

    public short getStrategy() {
        return _strategy;
    }

    public void setStrategy(short _strategy) {
        this._strategy = _strategy;
    }

    public int getBatch_size() {
        return batch_size;
    }

    public void setBatch_size(int batch_size) {
        this.batch_size = batch_size;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    @Override
    public void run() {
        try {
            EventBean[] evts = retrieveZeroTTLs();
            if (evts.length > 0) {
                _ioTerm.send(evts);
            }
            if(_strategy==PRIORITY){ // default strategy
                evts = new EventBean[1];
                evts[0]= (EventBean) _outputQ.take();
                evts[0].getHeader().setNotificationTime(System.currentTimeMillis());
                _ioTerm.send(evts);
            }
            else if(_strategy==BATCH){
                if(_outputQ.size()>= batch_size){
                    List<EventBean> batch = new ArrayList<EventBean>();
                    for(EventBean evt: batch){
                        evt.getHeader().setNotificationTime(System.currentTimeMillis());
                    }
                     _outputQ.drainTo(batch, batch_size);
                     evts = (EventBean[]) batch.toArray(new EventBean[0]);
                     _ioTerm.send(evts);
                }
            }
            
            decreaseAllTTLs();
            
        } catch (Exception ex) {
            Logger.getLogger(OQNotifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * retrieve the events for which the ttl value equals zero, setting their notification time to System.currentTimeMillis();
     * @return 
     */
    private EventBean[] retrieveZeroTTLs() {
        ArrayList<EventBean> zeroTTLs= new ArrayList<>();
        for (EventBean evt: _outputQ){
            if((int)evt.getValue("ttl")==0){
                evt.getHeader().setNotificationTime(System.currentTimeMillis());
                zeroTTLs.add(evt);
                _outputQ.remove(evt);
            }
        }
        EventBean[] evts= (EventBean[]) zeroTTLs.toArray(new EventBean[0]);
        return evts;
    }
    
    private void decreaseAllTTLs(){
        for(EventBean evt: _outputQ){
            evt.payload.put("ttl", ((int)(evt.getValue("ttl"))-1));
        }
    }
}
