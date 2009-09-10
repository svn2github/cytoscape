package org.cytoscape.cmdline.internal;


import org.cytoscape.cmdline.launcher.CommandLineProvider;
import org.springframework.osgi.context.event.OsgiBundleApplicationContextEvent;
import org.springframework.osgi.context.event.OsgiBundleApplicationContextListener;


/**
 * This class is used to start the <i>Interception</i> and <i>Parsing</i> of the <code>TFWrappers</code> once they have ALL been detected, and stored
 * 
 * @author pasteur
 *
 */
public final class ParserStarter implements OsgiBundleApplicationContextListener {

	/**
	 * Container of all the <code>TaskFactories</code>
	 */
	private TaskFactoryGrabber tfg;
	
	/**
	 * Provider of commandline arguments
	 */
	private CommandLineProvider clp;

	/**
	 * Get the arguments from the <code>commandline</code> and the grabber of <code>TFWrappers</code> to start the interception of <code>TaskFactories</code>
	 *  
	 * @param tfg Grabber with all the <code>TFWrappers</code> detected and stored
	 * @param clp Provides the arguments typed by the user in the <code>commandline</code> when running Cytoscape in headless-mode
	 */
	public ParserStarter(TaskFactoryGrabber tfg, CommandLineProvider clp) {
		this.tfg = tfg;
		this.clp = clp;
	}

	/**
	 * <p><pre>
	 * Executes automatically the <code>CLTaskFactoryInterceptor</code> on the <code>TFWrappers</code> once the <code>core-task-impl<code> <i>OSGi Bundle</i>
	 * has been started (which means that all the <code>TaskFactories</code> have previously been detected and stored)
	 * </pre></p>
	 */
	public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) { 
		if ( event.getBundle().getSymbolicName().equals("org.cytoscape.core-task-impl") ) {
			CLTaskFactoryInterceptor cl = new CLTaskFactoryInterceptor(clp,tfg);	
		}
	}
}

