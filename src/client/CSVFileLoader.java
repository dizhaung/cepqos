/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.google.common.collect.Lists;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epaln
 */
public class CSVFileLoader<T> {

    private File file;
    private String[] format;
    private Class clazz;
    private BufferedReader input;
    private ArrayList<T> data;

    public CSVFileLoader(File file, String[] format, Class clazz) {
        this.file = file;
        this.format = format;
        this.clazz = clazz;
        try {
            input = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVFileLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * (1): iterate over the lines in the csv file
     * (2): for each line do the following:
     *      (a): read the line string
     *      (b): create a bean object of the specified class
     *      (c): compute the tokens strings. Remember that token 0 is the value of 
     *           format[0] field in the created bean object...
     *      (d): for each token tok number i, do:
     *           - set the value of attribute format[i] to token: bean.format[i]= tok; !! a cast may be needed !!
     *      (e): add bean object to data ArrayList
     *           
     */
    public void loadData() throws Exception {
        data = Lists.newArrayList();

        String line = input.readLine();
        Class classHandle = Class.forName(clazz.getName());
        while (line != null) {
            Object bean = classHandle.newInstance();
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                boolean ok = set(bean, format[i], token);
                i++;
            }
            //System.out.println(bean);
            data.add((T) bean);
            line = input.readLine();
        }
        input.close();

    }

    public ArrayList<T> getData() {
        return data;
    }

    private boolean set(Object object, String fieldName, String fieldValue) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                String type = field.getType().getSimpleName();
                field.setAccessible(true);
                switch (type) {
                    case "long": {
                        long castedValue = Long.parseLong(fieldValue);
                        field.setLong(object, castedValue);
                        return true;
                    }
                    case "int": {
                        int castedValue = Integer.parseInt(fieldValue);
                        field.setInt(object, castedValue);
                        return true;
                    }
                    case "float": {
                        float castedValue = Float.parseFloat(fieldValue);
                        field.setFloat(object, castedValue);
                        return true;
                    }
                    case "double": {
                        double castedValue = Double.parseDouble(fieldValue);
                        field.setDouble(object, castedValue);
                        return true;
                    }
                    case "short": {
                        short castedValue = Short.parseShort(fieldValue);
                        field.setShort(object, castedValue);
                        return true;
                    }
                    case "boolean": {
                        boolean castedValue = Boolean.parseBoolean(fieldValue);
                        field.setBoolean(object, castedValue);
                        return true;
                    }
                    default:
                        field.set(object, fieldValue);
                        return true;
                }

            } catch (NoSuchFieldException ex) {
                clazz = clazz.getSuperclass();
            }
        }
        return false;
    }

    public T getNext() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        String line = input.readLine();
        if (line != null) {
            Class classHandle = Class.forName(clazz.getName());

            Object bean = classHandle.newInstance();
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                boolean ok = set(bean, format[i], token);
                i++;
            }

            return (T) bean;
        } else {
            closeInput();
            return null;
        }


    }

    public void closeInput() throws IOException {
        input.close();
    }
}
