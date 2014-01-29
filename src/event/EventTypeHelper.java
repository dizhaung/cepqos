/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.MethodUtils;

/**
 *
 * @author epaln
 */
public class EventTypeHelper {

    public static List<String> getPayloadAttribute(Class clazz) throws IntrospectionException {
        List<String> result = new ArrayList<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

        for (int i = 0; i < pds.length; i++) {
            String name = pds[i].getName();
            if (name.equals("class")) {
                continue;
            }
            if (MethodUtils.getAccessibleMethod(clazz, pds[i].getReadMethod()) != null) {
                result.add(name);
            }
        }

        return result;
    }
}
