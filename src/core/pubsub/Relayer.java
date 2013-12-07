/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.pubsub;

import event.EventBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.Message.Flag;
import org.jgroups.View;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

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

    private Relayer() {
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
            disp = new RpcDispatcher(channel, null, l, this);
            call = new MethodCall(getClass().getMethod("publish", EventBean[].class, String.class));
            opts = new RequestOptions(ResponseMode.GET_NONE, DELAY).setFlags(Flag.DONT_BUNDLE);
            channel.connect(CLUSTER);
        } catch (Exception ex) {
            Logger.getLogger(Relayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean publish(EventBean[] evts, String topic) {
        return PubSubService.getInstance().publish(evts, topic);
    }

    //public void callPublish(EventBean evt, String topic) throws Exception {
    public synchronized void callPublish(EventBean[] evts, String topic) throws Exception {
        call.setArgs(evts, topic);
        disp.callRemoteMethods(null, call, opts);
      /*  RspList<Object> rsps = disp.callRemoteMethods(null, call, opts);
        for (Rsp rsp : rsps.values()) {

            if (rsp.wasUnreachable()) {
                System.out.println("<< unreachable: " + rsp.getSender());
            } 

        }
        * */
    }

    public static Relayer getInstance() {
        if (_instance == null) {
            _instance = new Relayer();
        }
        return _instance;
    }
}
