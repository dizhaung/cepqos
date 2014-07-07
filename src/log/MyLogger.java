/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package log;


import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 *
 * @author Orleant
 */
public class MyLogger {
    private String fileName;
    private FileHandler fileTxt;
    private SimpleFormatter formatterTxt;
    private String classname;
    private Logger logger;
    
    public MyLogger(String fileName, String classname) {
        this.fileName = fileName;
        this.classname = classname;
        parametrer();
    }
    
    
	private void parametrer(){
        try {
            
            // Create Logger
            logger = Logger.getLogger(classname);
            logger.setLevel(Level.ALL);
            fileTxt = new FileHandler(fileName+".txt");
            
            // Create txt Formatter
            formatterTxt = new MyFormatter();
            fileTxt.setFormatter(formatterTxt);
            logger.addHandler(fileTxt);
        } catch (Exception ex) {
            
        }
	}
        
        public void log(String msg){
            logger.info(msg); 
        }
    
}
