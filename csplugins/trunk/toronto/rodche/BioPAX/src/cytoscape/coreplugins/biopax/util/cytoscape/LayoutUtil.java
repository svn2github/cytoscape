//
// WARNING - THIS FILE IS AUTO-GENERATED - ANY CHANGES YOU MAKE HERE WILL BE LOST
//
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package cytoscape.coreplugins.biopax.util.cytoscape;

// imports
import giny.view.NodeView;
import java.util.Iterator;
import cytoscape.Cytoscape;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutProperties;

import java.util.List;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * This class implements CyLayoutAlgorithim.  Used to layout BioPAX graphs.
 *
 * @author Benjamin Gross
 */
public class LayoutUtil implements CyLayoutAlgorithm {

    /**
     * Our implementation of LayoutAlgorithm.doLayout().
     */
    public void doLayout() {
        doLayout(Cytoscape.getCurrentNetworkView(), null);
    }

	/**
	 * Our implementation of LayoutAlgorithm.doLayout(..).
	 */
	public void doLayout(CyNetworkView networkView) {
	    doLayout(networkView, null);
    }

	/**
	 * Our implementation of LayoutAlgorithm.doLayout(..,..).
	 */
	public void doLayout(CyNetworkView networkView, TaskMonitor monitor) {

		double distanceBetweenNodes = 50.0d;
		int columns = (int) Math.sqrt(networkView.nodeCount());
		Iterator nodeViews = networkView.getNodeViewsIterator();
		double currX = 0.0d;
		double currY = 0.0d;
		int count = 0;

		while (nodeViews.hasNext()) {
			NodeView nView = (NodeView) nodeViews.next();
			nView.setOffset(currX, currY);
			count++;

			if (count == columns) {
				count = 0;
				currX = 0.0d;
				currY += distanceBetweenNodes;
			} else {
				currX += distanceBetweenNodes;
			}
		}
    }

	/**
	 * Our implementation of LayoutAlgorithm.supportsSelectedOnly().
	 */
	public boolean supportsSelectedOnly() {
        return false;
    }

	/**
	 * Our implementation of LayoutAlgorithm.setSelectedOnly(..).
	 */
	public void setSelectedOnly(boolean selectedOnly) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.supportsNodeAttributes().
	 */
	public byte[] supportsNodeAttributes() {
	    return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.supportsEdgeAttributes().
	 */
	public byte[] supportsEdgeAttributes() {
	    return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.setLayoutAttribute(..).
	 */
	public void setLayoutAttribute(String attributeName) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.getInitialAttributeList().
	 */
	public List<String> getInitialAttributeList() {
	    return new ArrayList<String>();
    }

	/**
	 * Our implementation of LayoutAlgorithm.getSettingsPanel().
	 */
	public JPanel getSettingsPanel() {
	    return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.revertSettings().
	 */
	public void revertSettings() {
    }

	/**
	 * Our implementation of LayoutAlgorithm.updateSettings().
	 */
	public void updateSettings() {
    }

	/**
	 * Our implementation of LayoutAlgorithm.getSettings().
	 */
	public LayoutProperties getSettings() {
		return null;
    }

	/**
	 * Our implementation of LayoutAlgorithm.getName().
	 */
	public String getName() {
	    return "BioPax Plugin Layout Algorithm";
    }

	/**
	 * Our implementation of LayoutAlgorithm.lockNodes(..).
	 */
	public void lockNodes(NodeView[] nodes) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.lockNode(..).
	 */
	public void lockNode(NodeView v) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.unlockNode(..).
	 */
	public void unlockNode(NodeView v) {
    }

	/**
	 * Our implementation of LayoutAlgorithm.unlockAllNodes().
	 */
	public void unlockAllNodes() {
    }

	/**
	 * Our implementation of LayoutAlgorithm.halt().
	 */
	public void halt() {
    }
}

