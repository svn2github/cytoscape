package org.cytoscape.log.internal;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

/**
 * @author Pasteur
 */
public class Log4jInit
{
	public Log4jInit(Appender rootAppender)
	{
		Logger log = Logger.getRootLogger();
		log.addAppender(rootAppender);
	}
}
