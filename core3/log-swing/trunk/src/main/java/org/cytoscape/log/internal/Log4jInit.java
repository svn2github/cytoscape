package org.cytoscape.log.internal;

//import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class Log4jInit
{
	public Log4jInit()
	{
		//PropertyConfigurator.configure(Log4jInit.class.getResource("/log4j-configuration.xml"));

		Logger userLog = Logger.getLogger("org.cytoscape.userlog");
		userLog.addAppender(new UserLogAppender());
		userLog.setAdditivity(false);

		Logger developerLog = Logger.getRootLogger();
		developerLog.addAppender(new DeveloperLogAppender());
	}
}
