/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import core.EPAgent;
import core.IOTerminal;
import core.pubsub.PubSubService;
import core.pubsub.Subscriber;
import event.EventBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.JChannel;

/**
 *
 * @author epaln
 */
public class EventConsumer extends EPAgent implements Subscriber {

    JChannel _channel;
    String _info, _input;
    AnEventHandler _handler;

    public EventConsumer(String info, String IDinputTerminal, AnEventHandler handler) {
        _handler = handler;
        try {
            _info = info;
            _input = IDinputTerminal;
            _type = "Consumer";
            if (handler != null) {
                PubSubService.getInstance().subscribe(this, _input);
            }
        } catch (Exception ex) {
            Logger.getLogger(EventConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void notify(Object event) {
        EventBean[] evts = (EventBean[]) event;
        _handler.notify(evts);
    }

    @Override
    public Collection<IOTerminal> getInputTerminals() {
        ArrayList<IOTerminal> inputs = new ArrayList<IOTerminal>();
        inputs.add(new IOTerminal(_input, null, this));
        return inputs;
    }

    @Override
    public IOTerminal getOutputTerminal() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void process() {
    }

    @Override
    public boolean fetch() {
        return false;
    }

    @Override
    public void run() {
    }

//    @Override
//    public void process(EventBean[] evts) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
