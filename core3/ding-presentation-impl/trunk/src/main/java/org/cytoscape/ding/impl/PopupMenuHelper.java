
/*
 Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.ding.impl;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Map; 
import java.util.Collection; 
import java.util.List; 
import java.util.ArrayList; 

import org.cytoscape.ding.NodeView;
import org.cytoscape.ding.EdgeView;

import org.cytoscape.util.swing.JMenuTracker;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

// TODO Consider generalizing this class so that it can be used by anyone
// who needs a popup menu based on TaskFactories.

/**
 * A class that encapsulates the creation of JPopupMenus based
 * on TaskFactory services.
 */
class PopupMenuHelper {

	// provides access to the necessary task factories and managers
	DGraphView m_view; 

	// the component we should create the popup menu on
	Component invoker;

	PopupMenuHelper(DGraphView v, Component inv) {
		m_view = v;
		invoker = inv;
	}

	/**
	 * Creates a menu based on the EdgeView.
	 */
	void createEdgeViewMenu(EdgeView edgeView, int x, int y, String action) {
		if (edgeView != null ) {

			Collection<EdgeViewTaskFactory> usableTFs = getPreferredActions(m_view.edgeViewTFs,action);
			View<CyEdge> ev = edgeView.getEdgeView();

			// build a menu of actions if more than factory exists
			if ( usableTFs.size() > 1) {
				String edgeLabel = ev.getSource().attrs().get("interaction",String.class);
				JPopupMenu menu = new JPopupMenu(edgeLabel);
				JMenuTracker tracker = new JMenuTracker(menu);

				for ( EdgeViewTaskFactory evtf : usableTFs ) {
					evtf.setEdgeView(ev,m_view.cyNetworkView);
					createMenuItem( menu, evtf, tracker, m_view.edgeViewTFs.get(evtf) );
				}

				menu.show(invoker, x, y);

			// execute the task directly if only one factory exists 
			} else if ( usableTFs.size() == 1) {
				EdgeViewTaskFactory tf  = usableTFs.iterator().next();
				tf.setEdgeView(ev,m_view.cyNetworkView);
				executeTask(tf);
			}
		}
	}

	/**
	 * Creates a menu based on the NodeView.
	 */
	void createNodeViewMenu(NodeView nview, int x, int y , String action) {
		if (nview != null ) {
			Collection<NodeViewTaskFactory> usableTFs = getPreferredActions(m_view.nodeViewTFs,action);
			View<CyNode> nv = nview.getNodeView();

			// build a menu of actions if more than factory exists
			if ( usableTFs.size() > 1) {
				String nodeLabel = nv.getSource().attrs().get("name",String.class);
				JPopupMenu menu = new JPopupMenu(nodeLabel);
				JMenuTracker tracker = new JMenuTracker(menu);

				for ( NodeViewTaskFactory nvtf : usableTFs ) {
					nvtf.setNodeView(nv,m_view.cyNetworkView);
					createMenuItem(menu, nvtf, tracker, m_view.nodeViewTFs.get( nvtf ));
				}

				menu.show(invoker, x, y);

			// execute the task directly if only one factory exists 
			} else if ( usableTFs.size() == 1) {
				NodeViewTaskFactory tf  = usableTFs.iterator().next();
				tf.setNodeView(nv,m_view.cyNetworkView);
				executeTask(tf);
			}
		}
	}

	/**
	 * Creates a menu based on the NetworkView.
	 */
	void createEmptySpaceMenu(int x, int y, String action) {
		// build a menu of actions if more than factory exists
		Collection<NetworkViewTaskFactory> usableTFs = getPreferredActions(m_view.emptySpaceTFs,action);
		if ( usableTFs.size() > 1 ) {
			JPopupMenu menu = new JPopupMenu("Double Click Menu: empty");
			JMenuTracker tracker = new JMenuTracker(menu);
			for ( NetworkViewTaskFactory nvtf : usableTFs ) {
				nvtf.setNetworkView(m_view.cyNetworkView);
				createMenuItem(menu, nvtf, tracker, m_view.emptySpaceTFs.get( nvtf ) );
			}
			menu.show(invoker, x, y);
		// execute the task directly if only one factory exists 
		} else if ( usableTFs.size() == 1) {
			NetworkViewTaskFactory tf = usableTFs.iterator().next();
			tf.setNetworkView(m_view.cyNetworkView);
			executeTask(tf);
		}
	}

	/**
	 * This method creates popup menu submenus and menu items based on the 
	 * "title" and "preferredMenu" keywords, depending on which are present
	 * in the service properties.
	 */
	private void createMenuItem(JPopupMenu popup, TaskFactory tf, 
	                            JMenuTracker tracker, Map props) { 

		String title = (String)(props.get("title"));
		String pref = (String)(props.get("preferredMenu"));

		// no title and no preferred menu
		if ( title == null && pref == null ) {
			title = "Unidentified Task: " + Integer.toString(tf.hashCode());
			popup.add( new JMenuItem( new PopupAction(tf, title) ) );

		// title, but no preferred menu
		} else if ( title != null && pref == null ) {
			popup.add( new JMenuItem( new PopupAction(tf, title) ) );
			
		// no title, but preferred menu
		} else if ( title == null && pref != null ) {
			int last = pref.lastIndexOf("."); 

			// if the preferred menu is delimited
			if ( last > 0 ) {
				title = pref.substring(last+1);
				pref = pref.substring(0,last);
				JMenu menu = tracker.getMenu(pref);
				menu.add( new JMenuItem( new PopupAction(tf, title) ) );
			// otherwise just use the preferred menu as the menuitem name
			} else {
				title = pref;
				popup.add( new JMenuItem( new PopupAction(tf, title) ) );
			}

		// title and preferred menu
		} else {
			JMenu menu = tracker.getMenu(pref);
			menu.add( new JMenuItem( new PopupAction(tf, title) ) );
		}  
	}

	/**
	 * Extract and return all T's that match the defined action.  If action is null,
	 * then return everything.
	 */
	private <T> Collection<T> getPreferredActions(Map<T,Map> tfs, String action) {
		// if the action is null, return all available
		if ( action == null ) {
			return tfs.keySet();
		}

		// otherwise figure out if any TaskFactories match the specified preferred action
		java.util.List<T> usableTFs = new ArrayList<T>();
		for ( T evtf : tfs.keySet() ) {
			String prefAction = (String)(tfs.get( evtf ).get("preferredAction"));
			if ( action != null && action.equals(prefAction) )
				usableTFs.add(evtf);
		}
		return usableTFs;
	}

	/**
	 * A simple action that executes the specified TaskFactory
	 */
	private class PopupAction extends AbstractAction {
		TaskFactory tf;
		PopupAction(TaskFactory tf, String title) {
			super( title );
			this.tf = tf;
		}

		public void actionPerformed(ActionEvent ae) {
			executeTask(tf);
		}
	}

	/**
	 * A place to capture the common task execution behavior.
	 */
	private void executeTask(TaskFactory tf) {
		Task task = tf.getTask();
		m_view.interceptor.loadTunables(task);
		if ( !m_view.interceptor.createUI(task) )
			return;
		m_view.manager.execute(task);
	}
}
