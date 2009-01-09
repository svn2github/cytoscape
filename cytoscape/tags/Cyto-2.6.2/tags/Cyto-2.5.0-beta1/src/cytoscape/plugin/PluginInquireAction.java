/**
 * 
 */
package cytoscape.plugin;

import java.util.List;
import cytoscape.util.IndeterminateProgressBar;

/**
 * Object should be implmeneted by anyone using the {@link PluginManager#inquireThread(String, PluginInquireAction)}
 * method to get plugins from a given site.
 */
public abstract class PluginInquireAction {

	private Exception threadE;
	private java.io.IOException ioe;
	private org.jdom.JDOMException jde;
	
	
	public boolean isExceptionThrown() {
		if (threadE != null || ioe != null || jde != null) return true;
		else return false;
	}
	
	
	public Exception getException() {
		return threadE;
	}
	
	public org.jdom.JDOMException getJDOMException() {
		return jde;
	}
	
	
	public java.io.IOException getIOException() {
		return ioe;
	}
	
	/**
	 * If the thread has thrown an exception while running {@link PluginManager#inquireThread(String, PluginInquireAction)}
	 * it will be set here.
	 */
	protected void setExceptionThrown(Exception e) {
	
		if (java.io.IOException.class.isAssignableFrom(e.getClass())) {
			ioe = (java.io.IOException) e;
		} else if (org.jdom.JDOMException.class.isAssignableFrom(e.getClass())) {
			jde = (org.jdom.JDOMException) e;
		} else {	
			threadE = e;
		}
	}
	
	
	protected IndeterminateProgressBar getProgressBar() {
		IndeterminateProgressBar bar = new IndeterminateProgressBar(cytoscape.Cytoscape.getDesktop(),
				getProgressBarMessage(), "Connecting to Plugin Website");
		bar.pack();
		bar.setLocationRelativeTo(cytoscape.Cytoscape.getDesktop());
		return bar;
	}
	
	
	/**
	 * 
	 * @return Message to display in progress bar
	 */
	public abstract String getProgressBarMessage();
	
	/**
	 * 
	 * @return True to display the IndeterminateProgressBar while running
	 * 			{@link PluginManager#inquireThread(String, PluginInquireAction)}
	 */
	public abstract boolean displayProgressBar();
	
	
	/**
	 * Does some work on the list that results from a successful inquire query.
	 * @param 
	 * @throws Exception
	 */
	public abstract void inquireAction(List<PluginInfo> results);
	
	
}
