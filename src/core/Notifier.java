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

    EventBean[] _evt;
    IOTerminal _output;

    public Notifier(EventBean[] evt, IOTerminal output) {
        _evt = evt;
        _output = output;
    }

    @Override
    public void run() {
        try {
            _output.send(_evt);
        } catch (Exception ex) {
            Logger.getLogger(Notifier.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
