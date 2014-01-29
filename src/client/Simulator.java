/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author epaln
 */
public class Simulator<T> extends Thread {

    CSVFileLoader<T> loader;
    String typeName;
    long delay;
    String fileName;
    EventProducer producer;
    ArrayList<T> realValues;
    double SOMME = 0;
    double ECART_TYPE = 0;
    Random random = new Random();
    long passage = 0;
    Class _clazz;
    String attribute;

    public Simulator(String typeName, String fileName, String[] propertyOrder, Class clazz, long delay) {
        this.typeName = typeName;
        this.delay = delay;
        this.fileName = fileName;
        _clazz = clazz;
        loader = new CSVFileLoader<>(new File(fileName), propertyOrder, clazz);
        realValues = new ArrayList<>();
    }

    @Override
    public void run() {
        boolean isSimulating = false;
        producer = new EventProducer(typeName, _clazz);
        //System.out.println("generating events...");
        int i = 1;
        try {
            while (true) {

                if (!isSimulating) {

                    T evt = loader.getNext(); // get the next event if possible
                    if (evt != null) {
                        producer.sendEvent(evt);
                        System.out.println(" next event generated... (N° " + i + "): " + evt);
                        i++;
                        double value = Double.parseDouble(BeanUtils.getProperty(evt, attribute));
                        realValues.add(evt);
                        SOMME += value;
                        Thread.sleep(delay);
                    } else {
                        isSimulating = true;
                        double moyenne = SOMME / realValues.size();
                        // calcul de l'ecart type entre les valeurs observées
                        double var = 0;
                        for (T e : realValues) {
                            double val = Double.parseDouble(BeanUtils.getProperty(e, attribute));
                            var += Math.pow(val - moyenne, 2);
                        }
                        var = var / realValues.size();
                        ECART_TYPE = Math.sqrt(var);
                        System.out.println("sigma = " + ECART_TYPE);
                    }

                } else {
                    passage++;
                    System.out.println("Start Simulation N°" + passage + "...");
                    for (T evt : realValues) {
                        double value = Double.parseDouble(BeanUtils.getProperty(evt, attribute));
                        // valeur à ajouter ou retrancher
                        double eps = random.nextDouble() * (ECART_TYPE / 2);
                        // ajouter ou retrancher?
                        if (random.nextBoolean()) { // ajouter
                            value += eps;
                        } else {  // retrancher
                            value -= eps;
                            if (value < 0) {
                                value += eps;
                            }
                        }
                        BeanUtils.setProperty(evt, attribute, value);
                        long timestamp = Long.parseLong(BeanUtils.getProperty(evt, "timestampUTC"));
                        timestamp += 86400 * passage;
                        BeanUtils.setProperty(evt, "timestampUTC", timestamp);
                        System.out.println(evt);
                        i++;
                        producer.sendEvent(evt);
                        Thread.sleep(delay);
                    }
                    System.out.println("End Simulation N°" + passage + "...");

                }
            }

        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NumberFormatException | InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public Simulator simulate(String attribute) {
        this.attribute = attribute;
        return this;
    }
}
