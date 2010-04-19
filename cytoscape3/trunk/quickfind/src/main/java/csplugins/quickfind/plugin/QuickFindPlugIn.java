
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

package csplugins.quickfind.plugin;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.quickfind.util.QuickFindListener;
import csplugins.quickfind.util.TaskMonitorBase;
import csplugins.quickfind.view.QuickFindPanel;
import csplugins.widgets.autocomplete.index.GenericIndex;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;
import csplugins.widgets.slider.JRangeSliderExtended;
import cytoscape.Cytoscape;
import cytoscape.view.CyToolBar;//cytoscape.util.CytoscapeToolBar;
import cytoscape.view.CyMenus;
import cytoscape.view.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
//import org.cytoscape.view.GraphView;
//import org.cytoscape.view.NodeView;
import org.cytoscape.view.model.View;
import prefuse.data.query.NumberRangeModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.session.events.NetworkViewDestroyedEvent;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.CyNetworkManager;
//import cytoscape.view.CySwingApplication;
import org.cytoscape.view.model.CyNetworkView;

import java.util.Iterator;
import java.util.Map;
import org.cytoscape.model.CyDataTableUtil;

import org.cytoscape.session.events.SetCurrentNetworkViewListener;
import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.session.events.NetworkViewDestroyedListener;

import org.cytoscape.session.CyNetworkManager;
import cytoscape.view.CySwingApplication;
import org.cytoscape.work.TaskManager;

/**
 * Quick Find PlugIn.
 *
 * @author Ethan Cerami.
 */
public class QuickFindPlugIn implements QuickFindListener,SetCurrentNetworkViewListener,
NetworkViewDestroyedListener, NetworkViewAddedListener {
    static final int REINDEX_THRESHOLD = 1000;
    private QuickFindPanel quickFindToolBar;
	private static final int NODE_SIZE_MULTIPLER = 10;

	public static CySwingApplication cytoscapeDesktop;
	public static CyNetworkManager cyNetworkManagerServiceRef;
	public static TaskManager taskManager;	
	/**
	 * Constructor.
	 */	
	public QuickFindPlugIn(CySwingApplication cytoscapeDesktop, CyNetworkManager cyNetworkManagerServiceRef,
			TaskManager taskmgr){
		QuickFindPlugIn.cytoscapeDesktop = cytoscapeDesktop;
		QuickFindPlugIn.cyNetworkManagerServiceRef = cyNetworkManagerServiceRef;
		QuickFindPlugIn.taskManager = taskmgr;
				
		initListeners();
		initToolBar();
		initIndex();
	}


	/**
	 * Initializes All Cytoscape Listeners.
	 */
	private void initListeners() {
		// to catch network create/destroy/focus events
		//this.cytoscapeDesktop.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		
		
        //  to catch network modified events
        NetworkModifiedListener networkModifiedListener = new NetworkModifiedListener();
        Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(networkModifiedListener);

        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		quickFind.addQuickFindListener(this);
	}

	/**
	 * Initalizes Tool Bar.
	 */
	private void initToolBar() {
		
		CyMenus cyMenus = QuickFindPlugIn.cytoscapeDesktop.getCyMenus();
		CyToolBar toolBar = cyMenus.getToolBar();
		quickFindToolBar = new QuickFindPanel( QuickFindPlugIn.cyNetworkManagerServiceRef, 
				QuickFindPlugIn.cytoscapeDesktop, QuickFindPlugIn.taskManager);

		TextIndexComboBox comboBox = quickFindToolBar.getTextIndexComboBox();
		ActionListener listener = new UserSelectionListener(comboBox);
		comboBox.addFinalSelectionListener(listener);

		JRangeSliderExtended slider = quickFindToolBar.getSlider();
		RangeSelectionListener rangeSelectionListener = new RangeSelectionListener(slider);
		slider.addChangeListener(rangeSelectionListener);
		toolBar.getJToolBar().add(quickFindToolBar);
		toolBar.getJToolBar().validate();
	}

	/**
	 * Initializes index, if network already exists.
	 * This condition may occur if a user loads up a network from the command
	 * line, and the network is already loaded prior to any plugins being loaded
	 */
	private void initIndex() {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();

		//  If a network already exists within Cytoscape, index it
		final CyNetwork cyNetwork = this.cyNetworkManagerServiceRef.getCurrentNetwork();

		if ((cyNetwork != null) && (cyNetwork.getNodeCount() > 0)) {
			//  Run Indexer in separate background daemon thread.
			Thread thread = new Thread() {
				public void run() {
					
					//TaskMonitor tm =  new TaskMonitor();
					//quickFind.addNetwork(cyNetwork, tm);
				}
			};

			thread.start();
		}
	}

	/**
	 * Property change listener - to get network/network view destroy events.
	 *
	 * @param event PropertyChangeEvent
	 */
	public void handleEvent(SetCurrentNetworkViewEvent event) {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		swapCurrentNetwork(quickFind);
	}
	
	public void handleEvent(NetworkViewAddedEvent event) {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();

		final CyNetwork cyNetwork = this.cyNetworkManagerServiceRef.getCurrentNetwork();

		//  Run Indexer in separate background daemon thread.
		Thread thread = new Thread() {
			public void run() {
				quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
			}
		};

		thread.start();
	}
	
	public void handleEvent(NetworkViewDestroyedEvent event) {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();

		//GraphView networkView = (GraphView) event.getNewValue();
		CyNetworkView networkView = (CyNetworkView) event.getSource();//.getNewValue();
		CyNetwork cyNetwork = networkView.getSource(); //.getGraphPerspective();
		quickFind.removeNetwork(cyNetwork);
		swapCurrentNetwork(quickFind);
	}
	
	
	/**
	 * Determine which network view now has focus.
	 * If no network view has focus, disable quick find.
	 */
	private void swapCurrentNetwork(QuickFind quickFind) {
		CyNetwork network = this.cyNetworkManagerServiceRef.getCurrentNetwork();
		boolean networkHasFocus = false;

		if (network != null) {
			//GraphView networkView = Cytoscape.getNetworkView(network.getIdentifier());
			View networkView = this.cyNetworkManagerServiceRef.getNetworkView(network.getSUID());
			
			//if (networkView != Cytoscape.getNullNetworkView()) {
			if (networkView != null) {
				TextIndex textIndex = (TextIndex) quickFind.getIndex(network);

				if (textIndex != null) {
					quickFindToolBar.setIndex(textIndex);
					networkHasFocus = true;
				}
			}
		}

		if (!networkHasFocus) {
			quickFindToolBar.noNetworkLoaded();
		}
	}

	/**
	 * Event:  Network Added to Index.
	 *
	 * @param network GraphPerspective Object.
	 */
	public void networkAddedToIndex(CyNetwork network) {
		//  No-op
	}

	/**
	 * Event:  Network Removed from Index.
	 *
	 * @param network GraphPerspective Object.
	 */
	public void networkRemovedfromIndex(CyNetwork network) {
		//  No-op
	}

	/**
	 * Indexing started.
	 *
	 * @param cyNetwork     GraphPerspective.
	 * @param indexType     QuickFind.INDEX_NODES or QuickFind.INDEX_EDGES.
	 * @param controllingAttribute Controlling Attribute.
	 */
	public void indexingStarted(CyNetwork cyNetwork, int indexType, String controllingAttribute) {
		quickFindToolBar.indexingInProgress();
	}

	/**
	 * Indexing ended.
	 */
	public void indexingEnded() {
		QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		CyNetwork cyNetwork = this.cyNetworkManagerServiceRef.getCurrentNetwork();
		GenericIndex index = quickFind.getIndex(cyNetwork);
		quickFindToolBar.setIndex(index);
		quickFindToolBar.enableAllQuickFindButtons();
	}

	
	/**
	 * Indicates that the user has selected a text item within the QuickFind
	 * Search Box.
	 *
	 * @param network the current GraphPerspective.
	 * @param hit     hit value chosen by the user.
	 */
	public void onUserSelection(final CyNetwork network, Hit hit) {
		//network.unselectAllNodes();
		QuickFindPlugIn.setNodeSelectionState(network.getNodeList(), false);		
		//network.unselectAllEdges();
		QuickFindPlugIn.setEdgeSelectionState(network.getEdgeList(), false);

		//  Assemble Hit Objects
		final Object[] graphObjects = hit.getAssociatedObjects();
		final ArrayList list = new ArrayList();

		for (int i = 0; i < graphObjects.length; i++) {
			list.add(graphObjects[i]);
		}

		//  Fit Selected Content
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
					final CyNetwork cyNetwork = QuickFindPlugIn.cyNetworkManagerServiceRef.getCurrentNetwork();
					GenericIndex index = quickFind.getIndex(cyNetwork);

					if (index.getIndexType() == QuickFind.INDEX_NODES) {
						//network.setSelectedNodeState(list, true);
						QuickFindPlugIn.setSelectedNodeState(network, list, true);
						QuickFindPlugIn.this.cyNetworkManagerServiceRef.getCurrentNetworkView().fitSelected();
					} else {
						//network.setSelectedEdgeState(list, true);
						QuickFindPlugIn.setSelectedEdgeState(network, list, true);
						List nodeList = new ArrayList();

						for (int i = 0; i < list.size(); i++) {
							CyEdge edge = (CyEdge) list.get(i);
							CyNode sourceNode = (CyNode) edge.getSource();
							CyNode targetNode = (CyNode) edge.getTarget();
							nodeList.add(sourceNode);
							nodeList.add(targetNode);
						}

						//network..setSelectedNodeState(nodeList, true);
						QuickFindPlugIn.setSelectedNodeState(network, nodeList, true);
						//QuickFindPlugIn.this.cyNetworkManagerServiceRef.fitSelected();
					}

					//  If only one node is selected, auto-adjust zoom factor
					//  so that node does not take up whole screen.
					if (graphObjects.length == 1) {
						if (graphObjects[0] instanceof CyNode) {
							CyNode node = (CyNode) graphObjects[0];

							//  Obtain dimensions of current InnerCanvas
							View graphView = QuickFindPlugIn.this.cyNetworkManagerServiceRef.getCurrentNetworkView();
							//Component innerCanvas = graphView.getComponent();

							View<CyNode> nodeView = QuickFindPlugIn.this.cyNetworkManagerServiceRef.getCurrentNetworkView().getNodeView(node);

							//double width = nodeView.getWidth() * NODE_SIZE_MULTIPLER;
							//double height = nodeView.getHeight() * NODE_SIZE_MULTIPLER;
							//double scaleFactor = Math.min(innerCanvas.getWidth() / width,
							//                              (innerCanvas.getHeight() / height));
							//QuickFindPlugIn.this.cyNetworkManagerServiceRef.getCurrentNetworkView().setZoom(scaleFactor);
						}
					}

					QuickFindPlugIn.this.cyNetworkManagerServiceRef.getCurrentNetworkView().updateView();
				}
			});
	}

	/**
	 * Indicates that the user has selected a number range within the QuickFind
	 * Range selector.
	 *
	 * @param network   the current GraphPerspective.
	 * @param lowValue  the low value of the range.
	 * @param highValue the high value of the range.
	 */
	public void onUserRangeSelection(CyNetwork network, Number lowValue, Number highValue) {
		try {
			QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
			GenericIndex index = quickFind.getIndex(network);
			NumberIndex numberIndex = (NumberIndex) index;
			final java.util.List rangeList = numberIndex.getRange(lowValue, highValue);

			if (index.getIndexType() == QuickFind.INDEX_NODES) {
				selectNodes(network, rangeList);
			} else {
				selectEdges(network, rangeList);
			}
		} catch (IllegalArgumentException exc) {
		}
	}

	
	public static void setNodeSelectionState(List nodeList, boolean value){
		for (int i=0; i< nodeList.size();i++){
			CyNode n = (CyNode)nodeList.get(i);
			n.attrs().set("selected", false);
		}
	}

	public static void setEdgeSelectionState(List edgeList, boolean value){
		Iterator it = edgeList.iterator();
		while (it.hasNext()){
			CyNode n = (CyNode)it.next();
			n.attrs().set("selected", false);
		}
	}

	public static void setSelectedEdgeState(CyNetwork n, List edgeList, boolean value){
		Iterator<CyEdge> it = edgeList.iterator();
		while (it.hasNext()) {
			CyEdge e = (CyEdge) it.next();
			e.attrs().set("selected", value);
		}
	}

	public static void setSelectedEdgeState(CyNetwork n, Set edgeSet, boolean value){
		Iterator<CyEdge> it = edgeSet.iterator();
		while (it.hasNext()) {
			CyEdge e = (CyEdge) it.next();
			e.attrs().set("selected", value);
		}
	}
	
	public static void setSelectedNodeState(CyNetwork n, List nodeList, boolean value){
		Iterator<CyNode> it =  nodeList.iterator();
		while (it.hasNext()) {
			CyNode e = (CyNode) it.next();
			e.attrs().set("selected", value);
		}
	}

	public static void setSelectedNodeState(CyNetwork n, Set nodeSet, boolean value){
		Iterator<CyNode> it =  nodeSet.iterator();
		while (it.hasNext()) {
			CyNode e = (CyNode) it.next();
			e.attrs().set("selected", value);
		}
	}

	private void selectNodes(final CyNetwork cyNetwork, List rangeList) {
		//  First, do we have any edges selected?  If so, unselect them all
		//Set selectedEdgeSet = cyNetwork.getSelectFilter().getSelectedEdges();
		List selectedEdgeList = CyDataTableUtil.getEdgesInState(cyNetwork, "select", true);

		if (selectedEdgeList.size() > 0) {
			//cyNetwork.setSelectedEdgeState(selectedEdgeSet, false);
			QuickFindPlugIn.setSelectedEdgeState(cyNetwork, selectedEdgeList, false);
		}

		//  Then, determine the current set of selected nodes
		//Set selectedNodeSet = cyNetwork.getSelectFilter().getSelectedNodes();
		List selectedNodeSet = CyDataTableUtil.getNodesInState(cyNetwork, "selected", true);
		
		
		//  Then, figure out which new nodes to select
		//  This is the set operation:  R - S
		final List toBeSelected = new ArrayList();
		toBeSelected.addAll(rangeList);
		toBeSelected.removeAll(selectedNodeSet);

		//  Then, figure out which current nodes to unselect
		//  This is the set operation:  S - R
		final List toBeUnselected = new ArrayList();
		toBeUnselected.addAll(selectedNodeSet);
		toBeUnselected.removeAll(rangeList);

		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Iterator it = toBeSelected.iterator();
					while (it.hasNext()) {
						CyNode n = (CyNode) it.next();
						n.attrs().set("selected", true);
					}
					it = toBeUnselected.iterator();
					while (it.hasNext()) {
						CyNode n = (CyNode) it.next();
						n.attrs().set("selected", false);
					}
					//cyNetwork.setSelectedNodeState(toBeSelected, true);
					//cyNetwork.setSelectedNodeState(toBeUnselected, false);
					QuickFindPlugIn.cyNetworkManagerServiceRef.getCurrentNetworkView().updateView();
				}
			});
	}

	private void selectEdges(final CyNetwork cyNetwork, List rangeList) {
		//  First, do we have any nodes selected?  If so, unselect them all
		List selectedNodeSet = CyDataTableUtil.getNodesInState(cyNetwork, "selected", true);

		if (selectedNodeSet.size() > 0) {
			//cyNetwork.setSelectedNodeState(selectedNodeSet, false);
			Iterator<CyNode> it = selectedNodeSet.iterator();
			while (it.hasNext()){
				CyNode n = (CyNode) it.next();
				n.attrs().set("selected", true);
			}
		}

		//  Then, determine the current set of selected edge
		List selectedEdgeSet = CyDataTableUtil.getEdgesInState(cyNetwork, "selected", true);

		//  Then, figure out which new nodes to select
		//  This is the set operation:  R - S
		final List toBeSelected = new ArrayList();
		toBeSelected.addAll(rangeList);
		toBeSelected.removeAll(selectedEdgeSet);

		//  Then, figure out which current nodes to unselect
		//  This is the set operation:  S - R
		final List toBeUnselected = new ArrayList();
		toBeUnselected.addAll(selectedEdgeSet);
		toBeUnselected.removeAll(rangeList);

		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					//cyNetwork.getEdgeCyDataTables().put(key, value);
					
					Iterator it = toBeSelected.iterator();
					while (it.hasNext()){						
						//cyNetwork.setSelectedEdgeState(toBeSelected, true);	
						CyEdge e = (CyEdge) it.next();
						e.attrs().set("selected", true);
					}
						
					it = toBeUnselected.iterator();
					while (it.hasNext()){
						//cyNetwork.setSelectedEdgeState(toBeUnselected, false);
						CyEdge e = (CyEdge) it.next();
						e.attrs().set("selected", false);
					}
					QuickFindPlugIn.this.cyNetworkManagerServiceRef.getCurrentNetworkView().updateView();
				}
			});
	}
}


/**
 * Listens for Final Selection from User.
 *
 * @author Ethan Cerami.
 */
class UserSelectionListener implements ActionListener {
	private TextIndexComboBox comboBox;

	/**
	 * Constructor.
	 *
	 * @param comboBox TextIndexComboBox.
	 */
	public UserSelectionListener(TextIndexComboBox comboBox) {
		this.comboBox = comboBox;
	}

	/**
	 * User has made final selection.
	 *
	 * @param e ActionEvent Object.
	 */
	public void actionPerformed(ActionEvent e) {
		//  Get Current Network
		final CyNetwork currentNetwork = QuickFindPlugIn.cyNetworkManagerServiceRef.getCurrentNetwork();

		//  Get Current User Selection
		Object o = comboBox.getSelectedItem();

		if ((o != null) && o instanceof Hit) {
			QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
			Hit hit = (Hit) comboBox.getSelectedItem();
			quickFind.selectHit(currentNetwork, hit);
		}
	}
}


/**
 * Action to select a range of nodes.
 *
 * @author Ethan Cerami.
 */
class RangeSelectionListener implements ChangeListener {
	private JRangeSliderExtended slider;

	/**
	 * Constructor.
	 *
	 * @param slider JRangeSliderExtended Object.
	 */
	public RangeSelectionListener(JRangeSliderExtended slider) {
		this.slider = slider;
	}

	/**
	 * State Change Event.
	 *
	 * @param e ChangeEvent Object.
	 */
	public void stateChanged(ChangeEvent e) {
		QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		final CyNetwork cyNetwork = QuickFindPlugIn.cyNetworkManagerServiceRef.getCurrentNetwork();
		GenericIndex index = quickFind.getIndex(cyNetwork);
		NumberRangeModel model = (NumberRangeModel) slider.getModel();

		if (slider.isVisible()) {
			if (index instanceof NumberIndex) {
				Number lowValue = (Number) model.getLowValue();
				Number highValue = (Number) model.getHighValue();
				quickFind.selectRange(cyNetwork, lowValue, highValue);
			}
		}
	}
}

class NetworkModifiedListener implements PropertyChangeListener {

	/**
	 * Property change listener - to get network modified events.
	 *
	 * @param event PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent event) {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();

        if (event.getPropertyName() != null) {
            if (event.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {

				final CyNetwork cyNetwork = QuickFindPlugIn.cyNetworkManagerServiceRef.getCurrentNetwork();
                if (cyNetwork.getNodeList() != null) {

					// this network may not have been added to quick find - 
					// this can happen if an empty network was added
					if (quickFind.getIndex(cyNetwork) == null) {
						//  Run Indexer in separate background daemon thread.
						Thread thread = new Thread() {
								public void run() {
									quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
								}
							};
						thread.start();
					}
                    //  Only re-index if network has fewer than REINDEX_THRESHOLD nodes
                    //  I put this in to prevent quick find from auto re-indexing
                    //  very large networks.  
                    else if (cyNetwork.getNodeList().size() < QuickFindPlugIn.REINDEX_THRESHOLD) {
                        //  Run Indexer in separate background daemon thread.
                        Thread thread = new Thread() {
                            public void run() {
                                GenericIndex index = quickFind.getIndex(cyNetwork);
                                quickFind.reindexNetwork(cyNetwork, index.getIndexType(),
                                        index.getControllingAttribute(), new TaskMonitorBase());
                            }
                        };
                        thread.start();
                    }
                }
            }
		}
	}

}
