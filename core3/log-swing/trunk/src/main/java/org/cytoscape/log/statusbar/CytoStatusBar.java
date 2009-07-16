package org.cytoscape.log.statusbar;

import javax.swing.Icon;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

/**
 * Interface for posting the latest user message to a status bar.
 * 
 * @author Pasteur
 */
public interface CytoStatusBar
{
	/**
	 * Assigns a message to the status bar.
	 * If one wishes to clear the status bar, pass in <code>null</code>
	 * for <code>message</code> and <code>icon</code>.
	 *
	 * @param message The message to be posted to the status bar.
	 * @param icon The icon of the message.
	 */
	public void setMessage(String message, Icon icon);
	
	/**
	 * Adds an <code>ActionListener</code> for when the status bar is clicked on.
	 */
	public void addActionListener(ActionListener listener);
}
