/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author epaln
 */
public class MyFormatter extends SimpleFormatter{
    @Override
    public String format(LogRecord record) {
		//return new java.util.Date(record.getMillis()) + " " + record.getLevel() + " " + record.getMessage() + "\r\n";
                return record.getMessage() + "\r\n";
	}
}
