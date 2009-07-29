package org.cytoscape.log.internal;

import ch.qos.logback.core.Appender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * @author Pasteur
 */
public class LogInit
{
	public LogInit(Appender<LoggingEvent> rootAppender)
	{
		rootAppender.start();
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger log = context.getLogger(LoggerContext.ROOT_NAME);
		log.addAppender(rootAppender);
	}
}
