/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.google.common.collect.Queues;
import event.EventBean;
import hu.akarnokd.reactive4java.base.Observable;
import hu.akarnokd.reactive4java.reactive.Reactive;
import hu.akarnokd.reactive4java.util.ObserverAdapter;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author epaln
 */
public class SlidingWindow extends WindowHandler {

    WindowAgent _wagent;
    Notifier notifier;
    TimeUnit _unit;
    long _timespan;
    long _timeshift;
    //int sumPriority =0;

    public SlidingWindow(long timespan, long timeshift, TimeUnit timeUnit) {
        this._unit = timeUnit;
        this._timespan = timespan;
        this._timeshift = timeshift;
    }

    @Override
    public void register(WindowAgent agent) {
        _wagent = agent;
        
        Observable<Observable<EventBean>> windows = Reactive.window(_wagent._sourceStream, _timespan, _timeshift, _unit);

        windows.register(new ObserverAdapter<Observable<EventBean>>() {
            @Override
            public void next(Observable<EventBean> aWindow) {

                aWindow.register(new ObserverAdapter<EventBean>() {
                    Queue<EventBean> res = Queues.newArrayDeque();

                    @Override
                    public void next(EventBean evt) {
                        res.add(evt);
                        //sumPriority+= evt.getHeader().getPriority();
                    }

                    @Override
                    public void finish() {
                        
                        if (!res.isEmpty()) {
                            EventBean[] evts;
                            evts = res.toArray(new EventBean[0]);
                            res.clear();
                            EventBean evt = new EventBean();
                            evt.payload.put("window", evts);
                            evt.getHeader().setIsComposite(true);
                            evt.getHeader().setProductionTime(System.currentTimeMillis());
                            evt.getHeader().setDetectionTime(evts[0].getHeader().getDetectionTime());
                            evt.getHeader().setTypeIdentifier("Window");
                            evt.getHeader().setProducerID(_wagent.getName());
                            //evt.getHeader().setPriority((short)Math.round(sumPriority/evts.length));
                            evt.getHeader().setPriority((short)1);
                            evt.payload.put("ttl", _wagent.TTL);
                            //sumPriority=0;
                            _wagent.getOutputQueue().put(evt);
                            _wagent.getOutputNotifier().run();
                          //  notifier = new Notifier(evts, _wagent.outputTerminal);
                           // notifier.start();
                        }
                    }
                });
            }
        });
    }
}
