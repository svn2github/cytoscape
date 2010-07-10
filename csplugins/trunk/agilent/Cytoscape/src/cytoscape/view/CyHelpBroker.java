/*
 File: CyHelpBroker.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.view;


import cytoscape.logger.CyLogger;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.help.HelpBroker;
import javax.help.HelpSet;


/**
 * This class creates the Cytoscape Help Broker for managing the JavaHelp system
 * and help set access
 */
public class CyHelpBroker {
	private static HelpBroker hb;
	private static HelpSet masterHelpSet;
	private static final String HELP_RESOURCE = "/cytoscape/help/jhelpset.hs";

	static {
		new CyHelpBroker();
	}

	/**
	 * Creates a new CyHelpBroker object.
	 */
	private CyHelpBroker() {
		hb = null;
		masterHelpSet = null;

		URL hsURL = getClass().getResource(HELP_RESOURCE);
		try {
			masterHelpSet = new HelpSet(null, hsURL);
			hb = masterHelpSet.createHelpBroker();
			hb.setCurrentID("d0e1");
		} catch (Exception e) {
			CyLogger.getLogger().info("HelpSet " + e.getMessage());
			CyLogger.getLogger().info("HelpSet " + masterHelpSet + " not found.");
		}
	}

	/**
	 * Returns the HelpBroker. 
	 *
	 * @return the HelpBroker. 
	 */
	public static HelpBroker getHelpBroker() {
		return hb;
	}

	/**
	 * Returns the HelpSet. 
	 *
	 * @return the HelpSet. 
	 */
	public static HelpSet getHelpSet() {
		return masterHelpSet;
	}

	/**
	 *  @return true if we successfully added the help set, else false
	 */
	public static boolean addHelpSet(final HelpSet newHelpSet) {
		try {
			masterHelpSet.add(newHelpSet);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 *  Removes a help set that was previously added with addHelpSet().
	 *
	 *  @return true if "hs" has been successfully removed, false otherwise
	 */
	public static boolean removeHelpSet(final HelpSet hs) {
		return masterHelpSet.remove(hs);
	}
}
