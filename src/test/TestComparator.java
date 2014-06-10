/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import base.BoundedPriorityBlockingQueue;
import event.EventBean;
import event.EventComparator;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epaln
 */
public class TestComparator {
  
    static EventBean[] evts = new EventBean[3];
   
    
    public static void main(String[] args){
        BoundedPriorityBlockingQueue bpbq =new BoundedPriorityBlockingQueue(2);
        bpbq.setStrategy(BoundedPriorityBlockingQueue.IGNORE);
         
        evts[0] = new EventBean();
        evts[1] = new EventBean();
        evts[2] = new EventBean();
        
        evts[0].getHeader().setPriority((short)1);
        evts[2].getHeader().setPriority((short)3);
        evts[1].getHeader().setPriority((short)3);
        
        evts[0].getHeader().setDetectionTime(1);
        evts[1].getHeader().setDetectionTime(2);
        evts[2].getHeader().setDetectionTime(3);
        
        //Arrays.sort(evts, new EventComparator());
        
        for(EventBean evt: evts){
         //   System.out.println(evt.getHeader().getPriority()+";"+evt.getHeader().getDetectionTime());
            bpbq.put(evt);
        }
        EventBean evt;
        while(!bpbq.isEmpty()){
            
            try {
                evt = (EventBean) bpbq.take();
                System.out.println(evt.getHeader().getPriority()+";"+evt.getHeader().getDetectionTime());
            } catch (InterruptedException ex) {
                Logger.getLogger(TestComparator.class.getName()).log(Level.SEVERE, null, ex);
            }
           
           
        }
        
    }
}
