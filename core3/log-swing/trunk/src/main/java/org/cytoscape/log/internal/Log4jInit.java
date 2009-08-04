package org.cytoscape.log.internal;

import org.apache.log4j.Logger;
import org.apache.log4j.Appender;

/**
 * @author Pasteur
 */
public class LogInit
{
	public LogInit(Appender rootAppender)
	{
		Logger log = Logger.getRootLogger();
		log.addAppender(rootAppender);
	}
}
