/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import event.EventBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epaln
 */
public class Notifier extends Thread {

    EventBean[] _evts;
    IOTerminal _output;

    public Notifier(EventBean[] evts, IOTerminal output) {
        _evts = evts;
        _output = output;
    }

    @Override
    public void run() {
        try {
            _output.send(_evts);
        } catch (Exception ex) {
            Logger.getLogger(Notifier.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
