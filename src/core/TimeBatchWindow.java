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
public class TimeBatchWindow extends WindowHandler {

    WindowAgent _wagent;
    Notifier notifier;
    TimeUnit _unit;
    long _timespan;

    public TimeBatchWindow(long timespan, TimeUnit timeUnit) {
        this._unit = timeUnit;
        this._timespan = timespan;
    }

    @Override
    public void register(WindowAgent agent) {
        _wagent = agent;
        Observable<Observable<EventBean>> windows = Reactive.window(_wagent._sourceStream, _timespan, _unit);

        windows.register(new ObserverAdapter<Observable<EventBean>>() {
            @Override
            public void next(Observable<EventBean> aWindow) {

                aWindow.register(new ObserverAdapter<EventBean>() {
                    Queue<EventBean> res = Queues.newArrayDeque();

                    @Override
                    public void next(EventBean evt) {
                        res.add(evt);

                    }

                    @Override
                    public void finish() {
                        if (!res.isEmpty()) {
                            long time = System.currentTimeMillis();
                            EventBean[] evts;
                            evts = res.toArray(new EventBean[0]);
                            long ptime =  System.currentTimeMillis() - (long)evts[0].getValue("#time#");
                             System.out.println(ptime);
                            _wagent.processingTime += ptime;
//                            for (EventBean e : evts) {
//                                long ptime = time - (long) e.getValue("#time#");
//                                System.out.println(ptime);
//                                _wagent.processingTime += ptime;
//                                e.payload.remove("#time#");
//                            }
                            res.clear();
                            EventBean evt = new EventBean();
                            evt.payload.put("window", evts);
                            evt.getHeader().setIsComposite(true);
                            evt.getHeader().setProductionTime(System.currentTimeMillis());
                            evt.getHeader().setDetectionTime(evts[0].getHeader().getDetectionTime());
                            evt.getHeader().setTypeIdentifier("Window");
                            evt.getHeader().setProducerID(_wagent.getName());
                            evt.getHeader().setPriority((short) 1);
                            evt.payload.put("ttl", _wagent.TTL);
                            _wagent.getOutputQueue().put(evt);
                            _wagent.getOutputNotifier().run();
                            //notifier = new Notifier(evts, _wagent.outputTerminal);
                            //notifier.start();
                        }
                    }
                });
            }
        });
    }
}
