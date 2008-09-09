 /*
  File: CyLogger.java

  Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  - University of California San Francisco

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

/**
 * The CyLogger class is meant to be a wrapper around a general logging
 * mechanism.  The idea is to provide a simple interface that can be used
 * to alert/inform users about various events that occur during the processing
 * within Cytoscape or within a Cytoscape plugin.  Currently, this class provides
 * it's own logging buffers -- in the 3.0 time frame, this will probably be replaced
 * by a more general mechanism such as log4j or java's own logging.  Of note to
 * developers is the <i>cytoscape.debug</i> property, which when set to <b>true</b>
 * will enable the LOG_DEBUG level of logging.  Otherwise, no debug level logging
 * will be done.
 */

package cytoscape.logger;

import cytoscape.CytoscapeInit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class CyLogger {
	private static HashMap<String,CyLogger> logMap = new HashMap();
	private static HashMap<LogLevel,List<CyLogHandler>> globalHandlerMap = new HashMap();
	private HashMap<LogLevel, List<CyLogHandler>> handlerMap = new HashMap();
	private String loggerName = null;
	private boolean debugging = false;

	/**
 	 * Get a logger with the specified name
 	 *
 	 * @param name the name of the logger
 	 * @return the logger to use
 	 */
	public static CyLogger getLogger(String name) {
		CyLogger logger = null;
		if (logMap.containsKey(name)) {
			logger = logMap.get(name);
		} else {
			logger = new CyLogger(name);
		}
		return logger;
	}

	/**
 	 * Get a logger for the specified class
 	 *
 	 * @param logClass the class this logger logs for
 	 * @return the logger to use
 	 */
  public static CyLogger getLogger(Class logClass) {
    return CyLogger.getLogger(logClass.getName());
  }

	/**
 	 * Get the default ("cytoscape") logger
 	 *
 	 * @return the logger to use
 	 */
  public static CyLogger getLogger() {
		return getLogger("cytoscape");
	}

	/**
	 * The constructor to create a CyLogger.  This should
	 * not be used, use getLogger instead.
	 */
	protected CyLogger(String name) {
		loggerName = name;
		logMap.put(name,this);
		Properties properties = CytoscapeInit.getProperties();
		if (properties != null) {
			debugging = false;
			String debug = properties.getProperty("cytoscape.debug");
			debugging = Boolean.parseBoolean(debug);
		}
	}

	/**
	 * Log a debug message.
	 *
	 * @param message the message to be logged
	 */
	public void debug(String message) { log(message,LogLevel.LOG_DEBUG); }

	/**
	 * Log a debug message.
	 *
	 * @param message the message to be logged
	 * @param exception the exception to be logged
	 */
	public void debug(String message, Throwable exception) { log(message,LogLevel.LOG_DEBUG,exception); }

	/**
	 * Log an informational message.
	 *
	 * @param message the message to be logged
	 */
	public void info(String message) { log(message,LogLevel.LOG_INFO); }

	/**
	 * Log an informational message.
	 *
	 * @param message the message to be logged
	 * @param exception the exception to be logged
	 */
	public void info(String message, Throwable exception) { log(message,LogLevel.LOG_INFO,exception); }

	/**
	 * Log a warning message.
	 *
	 * @param message the message to be logged
	 */
	public void warn(String message) { log(message,LogLevel.LOG_WARN); }

	/**
	 * Log a warning message.
	 *
	 * @param message the message to be logged
	 * @param exception the exception to be logged
	 */
	public void warn(String message, Throwable exception) { log(message,LogLevel.LOG_WARN,exception); }

	/**
	 * Log a warning message.
	 *
	 * @param message the message to be logged
	 */
	public void warning(String message) { log(message,LogLevel.LOG_WARN); }

	/**
	 * Log a warning message.
	 *
	 * @param message the message to be logged
	 * @param exception the exception to be logged
	 */
	public void warning(String message, Throwable exception) { log(message,LogLevel.LOG_WARN,exception); }

	/**
	 * Log an error message.
	 *
	 * @param message the message to be logged
	 */
	public void error(String message) { log(message,LogLevel.LOG_ERROR); }

	/**
	 * Log an error exception message.
	 *
	 * @param message the message to be printed
	 * @param exception the exception to be logged
	 */
	public void error(String message, Throwable exception) { log(message, LogLevel.LOG_ERROR, exception); }

	/**
	 * Log a fatal error message.
	 *
	 * @param message the message to be logged
	 */
	public void fatal(String message) { log(message,LogLevel.LOG_FATAL); }

	/**
	 * Log a fatal exception message.
	 *
	 * @param message the message to be printed
	 * @param exception the exception to be logged
	 */
	public void fatal(String message, Throwable exception) { log(message,LogLevel.LOG_FATAL, exception); }

	/**
	 * Set the debug status.  This will override the default debug setting
	 * from cytoscape.debug for this logger only.
	 *
	 * @param debug boolean setting for debugging state.
	 */
	public void setDebug(boolean debug) {
		this.debugging = debug;
	}

	/**
	 * Log a message at the specified log level.
	 *
	 * @param message the message to be logged
	 * @param level the LogLevel to log the message at
	 * @param t a throwable to use to get a stack trace
	 */
	public void log(String message, LogLevel level, Throwable t) {
		if (message != null && message.length() > 0)
			message += "\n";
		if (t != null) {
			message += getStack(t);
		}
		log(message,level); 
	}

	/**
	 * Log a message at the specified log level.
	 *
	 * @param message the message to be logged
	 * @param level the LogLevel to log the message at
	 */
	public void log(String message, LogLevel level) {
		// Is this a DEBUG message?
		if (level == LogLevel.LOG_DEBUG && !debugging) {
			return;
		}
		// Format the message
		String formattedMessage = loggerName+"["+level+"]: "+message;
		// See if there are any handlers at all
		if (globalHandlerMap.size() == 0 && handlerMap.size() == 0) {
			// No, just print the message to the console
			System.out.println(formattedMessage);
			return;
		}

		// Send it to all interested global handlers
		List<CyLogHandler> handlerList = getHandlers(globalHandlerMap, level);
		handlerList.addAll(getHandlers(handlerMap, level));

		for (CyLogHandler handler: handlerList) {
			handler.handleLog(level, formattedMessage);
		}
	}

	/**
	 * Add a new log handler for specific messages
	 *
	 * @param handler the CyLogHandler that will handle the messages
	 * @param loggerName a String that indicates which messages this handler handles
	 * @param level the minimum LogLevel this handler is interested in
	 */
	public void addLogHandler(CyLogHandler handler, String loggerName, LogLevel level) {
		List<CyLogHandler>list = null;
		HashMap<LogLevel,List<CyLogHandler>> map = null;

		// Is this a global handler?
		if (loggerName == null) {
			map = globalHandlerMap;
		} else {
			map = handlerMap;
		}

		if (map.containsKey(level)) {
			list = map.get(level);
		} else {
			list = new ArrayList();
		}
		list.add(handler);
		map.put(level, list);
	}

	/**
	 * Add a new general log handler for all messages
	 *
	 * @param handler the CyLogHandler that will handle the messages
	 * @param level the minimum LogLevel this handler is interested in
	 */
	public void addLogHandler(CyLogHandler handler, LogLevel level) {
		addLogHandler(handler, null, level);
	}

	/**
	 * Returns the current list of log handlers for the specified logger at
	 * the suggested minimum level.
	 *
	 * @param loggerName a String that indicates which messages the handlers handle
	 * @param level the minimum LogLevel the handlers are interested in
	 * @return a List of CyLogHandlers the meet the criteria
	 */
	public List<CyLogHandler> getLogHandlers(String loggerName, LogLevel level) {
		HashMap<LogLevel,List<CyLogHandler>> map = null;

		// Is this a global handler?
		if (loggerName == null) {
			map = globalHandlerMap;
		} else {
			map = handlerMap;
		}

		if (map.containsKey(level)) {
			return map.get(level);
		}
		return null;
	}

	/**
	 * Returns the current list of log handlers for the specified 
	 * minimum level.
	 *
	 * @param level the minimum LogLevel the handlers are interested in
	 * @return a List of CyLogHandlers the meet the criteria
	 */
	public List<CyLogHandler> getLogHandlers(LogLevel level) {
		return getLogHandlers(null, level);
	}

	/**
	 * Remove a log handler for specific messages
	 *
	 * @param handler the CyLogHandler to remove
	 * @param loggerName a String that indicates which messages this handler handles
	 * @param level the minimum LogLevel this handler is interested in
	 */
	public CyLogHandler removeLogHandler(CyLogHandler handler, String loggerName, LogLevel level) {
		HashMap<LogLevel,List<CyLogHandler>> map = null;

		// Is this a global handler?
		if (loggerName == null) {
			map = globalHandlerMap;
		} else {
			map = handlerMap;
		}

		if (!map.containsKey(level)) {
			return null;
		}

		// Get the list
		List<CyLogHandler>list = map.get(level);
		if (list.contains(handler)) {
			list.remove(handler);
			map.put(level, list);
			return handler;
		}
		return null;
	}

	/**
	 * Remove a general log handler
	 *
	 * @param handler the CyLogHandler to remove
	 * @param level the minimum LogLevel this handler is interested in
	 */
	public CyLogHandler removeLogHandler(CyLogHandler handler, LogLevel level) {
		return removeLogHandler(handler, null, level);
	}

	private List<CyLogHandler> getHandlers(HashMap<LogLevel,List<CyLogHandler>> map, LogLevel targetLevel) {
		// Find all of the handlers in this map that apply to this level
		List<CyLogHandler> list = new ArrayList();
		for (LogLevel level: map.keySet()) {
			if (targetLevel.applies(level))
				list.addAll(map.get(level));
		}
		return list;
	}

	private String getStack(Throwable t) {
		String message = null;
		if (t.getMessage() != null)
			message = t.getMessage() + "\n    "+t.toString();
		else
			message = t.toString();
		StackTraceElement[] stackArray = t.getStackTrace();
		for (int i = 0; stackArray != null && i < stackArray.length; i++) {
			message += "\n      at "+stackArray[i].toString();
		}
		return message;
	}
}
