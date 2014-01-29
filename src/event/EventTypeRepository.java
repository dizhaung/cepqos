/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author epaln
 */
public class EventTypeRepository {

    private HashMap<String, Class> repository;
    private static EventTypeRepository instance = null;

    private EventTypeRepository() {
        repository = new HashMap();
    }

    public static EventTypeRepository getInstance() {
        if (instance == null) {
            instance = new EventTypeRepository();
        }
        return instance;
    }

    public boolean addEventType(String typeName, Class clazz) {

        if (repository.containsKey(typeName)) {
            return false;
        } else {
            repository.put(typeName, clazz);
            return true;
        }

    }

    public String findByValue(Class clazz) {
        if (!repository.containsValue(clazz)) {
            return null;
        }

        for (String key : repository.keySet()) {
            if (repository.get(key).equals(clazz)) {
                return key;
            }
        }
        return null;
    }

    public Set<String> getAllRegisteredType() {
        //ArrayList<String> result = Lists.newArrayList();
        return repository.keySet();
    }

    public Class getEventClass(String typeName) {
        return repository.get(typeName);
    }

    public void dump() {
        System.out.println("*** Registered Event types ***");
        int count = 0;
        try {
            for (String eventType : EventTypeRepository.getInstance().getAllRegisteredType()) {
                count++;
                System.out.print(eventType + "(");
                int i = 0;
                for (String s : EventTypeHelper.getPayloadAttribute(EventTypeRepository.getInstance().getEventClass(eventType))) {
                    if (i == 0) {
                        System.out.print(s);
                        i++;
                    } else {
                        System.out.print(", " + s);
                    }
                }
                System.out.println(")");
            }
        } catch (Exception ex) {
            //Logger.getLogger(EPInAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("*** Total: " + count + " event type(s) ***");
    }
}
