/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.pubsub;

import event.EventBean;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.Message;
import org.jgroups.Message.Flag;
import org.jgroups.MessageListener;
import org.jgroups.View;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Util;

/**
 *
 * @author epaln
 */
public class Relayer {
    
    private Channel channel;
    private RpcDispatcher disp;
    private String props = "udp.xml", CLUSTER = "PubSub";
    private MethodCall call;
    private static Relayer _instance = null;
    private RequestOptions opts;
    private static final int DELAY = 5000;
    private ConcurrentHashMap<String, HashSet<Address>> addressTable;
    
    private Relayer() {
        addressTable = new ConcurrentHashMap<>();
        try {
            channel = new JChannel(props);
            channel.setDiscardOwnMessages(true);
            MembershipListener l = new MembershipListener() {
                @Override
                public void viewAccepted(View view) {
                    
                    System.out.println("** view: " + view);
                }
                
                @Override
                public void suspect(Address suspected_mbr) {
                    System.out.println(" Suspected member: " + suspected_mbr);
                }
                
                @Override
                public void block() {
                }
                
                @Override
                public void unblock() {
                }
            };
            MessageListener m = new MessageListener() {
                @Override
                public void receive(Message msg) {
                    
                }
                
                @Override
                public void getState(OutputStream output) throws Exception {
                    synchronized (addressTable) {
                        Util.objectToStream(addressTable, new DataOutputStream(output));
                    }
                }
                
                @Override
                public void setState(InputStream input) throws Exception {
                    ConcurrentHashMap<String, HashSet<Address>> chm = (ConcurrentHashMap<String, HashSet<Address>>) Util.objectFromStream(new DataInputStream(input));
                    synchronized (addressTable) {
                        addressTable.clear();
                        addressTable.putAll(chm);
                    }
                    //System.out.println("State transfer : OK");
                }
            };
            disp = new RpcDispatcher(channel, m, l, this);
            //call = new MethodCall(getClass().getMethod("publish", EventBean[].class, String.class));
            opts = new RequestOptions(ResponseMode.GET_NONE, DELAY).setFlags(Flag.DONT_BUNDLE);
            channel.connect(CLUSTER);
            channel.getState(null, DELAY);
        } catch (Exception ex) {
            Logger.getLogger(Relayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean publish(EventBean[] evts, String topic) {
        return PubSubService.getInstance().publish(evts, topic);
    }

    //public void callPublish(EventBean evt, String topic) throws Exception {
    public synchronized void callPublish(EventBean[] evts, String topic) throws Exception {
        call = new MethodCall(getClass().getMethod("publish", EventBean[].class, String.class));
        call.setArgs(evts, topic);
        disp.callRemoteMethods(addressTable.get(topic), call, opts);
        /*  RspList<Object> rsps = disp.callRemoteMethods(null, call, opts);
         for (Rsp rsp : rsps.values()) {

         if (rsp.wasUnreachable()) {
         System.out.println("<< unreachable: " + rsp.getSender());
         } 

         }
         * */
    }
    
    public void advertise(String topic, Address addr) {
        HashSet<Address> addrs = addressTable.get(topic);
        if (addrs == null) {
            addrs = new HashSet();
            addrs.add(addr);
            addressTable.put(topic, addrs);
        } else {
            addrs.add(addr);
        }
    }
    
    public void callAdvertise(String topic, Address addr) {
        try {
            call = new MethodCall(getClass().getMethod("advertise", String.class, Address.class));
            call.setArgs(topic, addr);
            disp.callRemoteMethods(null, call, opts);
            
        } catch (Exception ex) {
            Logger.getLogger(Relayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Relayer getInstance() {
        if (_instance == null) {
            _instance = new Relayer();
        }
        return _instance;
    }
    
    public Address getAddress() {
        return channel.getAddress();
    }
}
