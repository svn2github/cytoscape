#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.Cy3Plugin;

public class Cy3PluginImpl implements Cy3Plugin {
	
	private static final Logger logger = LoggerFactory.getLogger(Cy3PluginImpl.class);
	
	public Cy3PluginImpl() {
		logger.debug("An instance of Cy3PluginImpl has been created.");
	}

	public void doSomething() {
		// Write your function here.
	}

}
