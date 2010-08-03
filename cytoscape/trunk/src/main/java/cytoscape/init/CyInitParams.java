/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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
package cytoscape.init;

import java.util.List;
import java.util.Properties;


/**
 * An interface that describes the initialization parameters needed
 * by cytoscape.  Anything initialing Cytoscape should implement this
 * interface and pass that object into CytoscapeInit.  By setting the
 * mode you can control how Cytoscape is initialized.
 */
public interface CyInitParams {
	/**
	 *  Returns the properties that were defined at initialization. 
	 *
	 * @return A Properties object containing whatever property values
	 * that were defined at initialization.
	 */
	public Properties getProps();

	/**
	 * Returns properties specific to the VizMapper.  These properties
	 * aren't necessarily intended for human consumption.
	 *
	 * @return A Properties object containing VizMapper specific properties. 
	 */
	public Properties getVizProps();

	/**
	 * A list of Strings that describe graph file locations. The strings may
	 * represent URLs.
	 *
	 * @return A list of Strings representing graph file locations.
	 */
	public List getGraphFiles();

	/**
	 * A list of Strings that describe edge attribute file locations. 
	 *
	 * @return A list of Strings representing edge attribute file locations.
	 */
	public List getEdgeAttributeFiles();

	/**
	 * A list of Strings that describe node attribute file locations. 
	 *
	 * @return A list of Strings representing node attribute file locations.
	 */
	public List getNodeAttributeFiles();

	/**
	 * A list of Strings that describe expression matrix file locations. 
	 *
	 * @return A list of Strings representing expression matrixfile locations.
	 */
	public List getExpressionFiles();

	/**
	 * A list of Strings that describe plugins.  The descriptions can be any of the following: 
	 * <ul>
	 * <li>A jar file location, file or URL </li>
	 * <li>A directory name </li>
	 * <li>A plugin class name </li>
	 * <li>A local file location where the file lists one plugin jar per line</li>
	 * </ul>
	 *
	 * @return A list of strings describing the plugin locations. 
	 */
	public List getPlugins();

	/**
	 * A single string describing the session file location. 
	 *
	 * @return A string describing the session file locaton. 
	 */
	public String getSessionFile();

	/**
	 * Returns and int representing the mode cytoscape runs in.  The possible
	 * modes are:
	 * <ul>
	 * <li>ERROR </li>
	 * <li>GUI - normal operation as a gui </li>
	 * <li>TEXT - headless mode where cytoscape acts as a command line app </li>
	 * <li>LIBRARY - headless mode wehre cytoscape acts as a server or daemon</li>
	 * <li>EMBEDDED_WINDOW - gui mode where cytoscape is embedded in another app</li>
	 * </ul>
	 *
	 * @return the int representing the mode
	 */
	public int getMode();

	/**
	 * Returns the arguments used to trigger the initialization. While args are 
	 * appropriate for the command line, they're not really appropriate for other modes.
	 * Therefore, to pass initialization information to cytoscape, it's better to
	 * use getProperties().
	 *
	 * @return An array of strings representing arguments used to intialize cytoscape. 
	 */
	public String[] getArgs();

	/**
	 * Error.  Something is wrong.
	 */
	public static final int ERROR = 0;

	/**
	 * Normal gui mode.
	 */
	public static final int GUI = 1;

	/**
	 * Headless mode (no gui) were cytoscape acts like a normal command line app and exits
	 * once finished processing.
	 */
	public static final int TEXT = 2;

	/**
	 * Another headless mode (no gui), but meant for a server or daemon that runs in the 
	 * background without exiting.
	 */
	public static final int LIBRARY = 3;

	/**
	 * A gui mode, but where cytoscape is embedded within a different application
	 * meaning the usual menus and/or toolbars may not be present.
	 */
	public static final int EMBEDDED_WINDOW = 4;
}
