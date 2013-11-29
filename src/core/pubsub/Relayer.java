/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.pubsub;

import event.EventBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;

/**
 *
 * @author epaln
 */
public class Relayer {

    private Channel channel;
    private RpcDispatcher disp;
    private String props = "tcp.xml", CLUSTER = "PubSub";
    private MethodCall call;
    private static Relayer _instance = null;
    private RequestOptions opts;
    private static final int DELAY = 5000;

    private Relayer() {
        try {
            channel = new JChannel(props);
            channel.setDiscardOwnMessages(true);
            disp = new RpcDispatcher(channel, this);
            call = new MethodCall(getClass().getMethod("publish", EventBean.class, String.class));
            opts = new RequestOptions(ResponseMode.GET_NONE, DELAY);
            channel.connect(CLUSTER);
        } catch (Exception ex) {
            Logger.getLogger(Relayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean publish(EventBean evt, String topic) {
        return PubSubService.getInstance().publish(evt, topic);
    }

    public void callPublish(EventBean evt, String topic) throws Exception {
        call.setArgs(evt, topic);
         disp.callRemoteMethods(null, call, opts);
        //System.out.println("Responses: " + rsp_list);
    }
    
    public static Relayer getInstance(){
        if(_instance == null){
            _instance = new Relayer();
        }
        return _instance;
    }
}
