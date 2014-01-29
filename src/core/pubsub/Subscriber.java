/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.pubsub;

import com.google.common.eventbus.Subscribe;

/**
 *
 * @author epaln
 */
public interface Subscriber<T> {

    @Subscribe
    public void notify(T event);
}
