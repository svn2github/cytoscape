
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
import cytoscape.util.CytoscapeToolBar;
import cytoscape.view.CyMenus;
import cytoscape.view.CytoscapeDesktop;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
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

/**
 * Quick Find PlugIn.
 *
 * @author Ethan Cerami.
 */
public class QuickFindPlugIn implements BundleActivator, PropertyChangeListener,
                                                                QuickFindListener {
    static final int REINDEX_THRESHOLD = 1000;
    private QuickFindPanel quickFindToolBar;
	private static final int NODE_SIZE_MULTIPLER = 10;

	/**
	 * Constructor.
	 */
	public void start(BundleContext bc) {
		initListeners();
		initToolBar();
		initIndex();
	}

	public void stop(BundleContext bc) {
	}

	/**
	 * Initializes All Cytoscape Listeners.
	 */
	private void initListeners() {
		// to catch network create/destroy/focus events
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);

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
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CyMenus cyMenus = desktop.getCyMenus();
		CytoscapeToolBar toolBar = cyMenus.getToolBar();
		quickFindToolBar = new QuickFindPanel();

		TextIndexComboBox comboBox = quickFindToolBar.getTextIndexComboBox();
		ActionListener listener = new UserSelectionListener(comboBox);
		comboBox.addFinalSelectionListener(listener);

		JRangeSliderExtended slider = quickFindToolBar.getSlider();
		RangeSelectionListener rangeSelectionListener = new RangeSelectionListener(slider);
		slider.addChangeListener(rangeSelectionListener);
		toolBar.add(quickFindToolBar);
		toolBar.validate();
	}

	/**
	 * Initializes index, if network already exists.
	 * This condition may occur if a user loads up a network from the command
	 * line, and the network is already loaded prior to any plugins being loaded
	 */
	private void initIndex() {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();

		//  If a network already exists within Cytoscape, index it
		final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();

		if ((cyNetwork != null) && (cyNetwork.getNodeCount() > 0)) {
			//  Run Indexer in separate background daemon thread.
			Thread thread = new Thread() {
				public void run() {
					quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
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
	public void propertyChange(PropertyChangeEvent event) {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();

		if (event.getPropertyName() != null) {
			if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
				final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();

				//  Run Indexer in separate background daemon thread.
				Thread thread = new Thread() {
					public void run() {
						quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
					}
				};

				thread.start();
			} else if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_DESTROYED)) {
				GraphView networkView = (GraphView) event.getNewValue();
				CyNetwork cyNetwork = networkView.getGraphPerspective();
				quickFind.removeNetwork(cyNetwork);
				swapCurrentNetwork(quickFind);
			} else if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUS)) {
				swapCurrentNetwork(quickFind);
			}
		}
	}

	/**
	 * Determine which network view now has focus.
	 * If no network view has focus, disable quick find.
	 */
	private void swapCurrentNetwork(QuickFind quickFind) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		boolean networkHasFocus = false;

		if (network != null) {
			GraphView networkView = Cytoscape.getNetworkView(network.getIdentifier());

			if (networkView != Cytoscape.getNullNetworkView()) {
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
		CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
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
		network.unselectAllNodes();
		network.unselectAllEdges();

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
					final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
					GenericIndex index = quickFind.getIndex(cyNetwork);

					if (index.getIndexType() == QuickFind.INDEX_NODES) {
						network.setSelectedNodeState(list, true);
						Cytoscape.getCurrentNetworkView().fitSelected();
					} else {
						network.setSelectedEdgeState(list, true);

						List nodeList = new ArrayList();

						for (int i = 0; i < list.size(); i++) {
							CyEdge edge = (CyEdge) list.get(i);
							CyNode sourceNode = (CyNode) edge.getSource();
							CyNode targetNode = (CyNode) edge.getTarget();
							nodeList.add(sourceNode);
							nodeList.add(targetNode);
						}

						network.setSelectedNodeState(nodeList, true);
						Cytoscape.getCurrentNetworkView().fitSelected();
					}

					//  If only one node is selected, auto-adjust zoom factor
					//  so that node does not take up whole screen.
					if (graphObjects.length == 1) {
						if (graphObjects[0] instanceof CyNode) {
							CyNode node = (CyNode) graphObjects[0];

							//  Obtain dimensions of current InnerCanvas
							GraphView graphView = Cytoscape.getCurrentNetworkView();
							Component innerCanvas = graphView.getComponent();

							NodeView nodeView = Cytoscape.getCurrentNetworkView().getNodeView(node);

							double width = nodeView.getWidth() * NODE_SIZE_MULTIPLER;
							double height = nodeView.getHeight() * NODE_SIZE_MULTIPLER;
							double scaleFactor = Math.min(innerCanvas.getWidth() / width,
							                              (innerCanvas.getHeight() / height));
							Cytoscape.getCurrentNetworkView().setZoom(scaleFactor);
						}
					}

					Cytoscape.getCurrentNetworkView().updateView();
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

	private void selectNodes(final CyNetwork cyNetwork, List rangeList) {
		//  First, do we have any edges selected?  If so, unselect them all
		Set selectedEdgeSet = cyNetwork.getSelectFilter().getSelectedEdges();

		if (selectedEdgeSet.size() > 0) {
			cyNetwork.setSelectedEdgeState(selectedEdgeSet, false);
		}

		//  Then, determine the current set of selected nodes
		Set selectedNodeSet = cyNetwork.getSelectFilter().getSelectedNodes();

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
					cyNetwork.setSelectedNodeState(toBeSelected, true);
					cyNetwork.setSelectedNodeState(toBeUnselected, false);
					Cytoscape.getCurrentNetworkView().updateView();
				}
			});
	}

	private void selectEdges(final CyNetwork cyNetwork, List rangeList) {
		//  First, do we have any nodes selected?  If so, unselect them all
		Set selectedNodeSet = cyNetwork.getSelectFilter().getSelectedNodes();

		if (selectedNodeSet.size() > 0) {
			cyNetwork.setSelectedNodeState(selectedNodeSet, false);
		}

		//  Then, determine the current set of selected edge
		Set selectedEdgeSet = cyNetwork.getSelectFilter().getSelectedEdges();

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
					cyNetwork.setSelectedEdgeState(toBeSelected, true);
					cyNetwork.setSelectedEdgeState(toBeUnselected, false);
					Cytoscape.getCurrentNetworkView().updateView();
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
		final CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();

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
		final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
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

				final CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
                if (cyNetwork.nodesList() != null) {

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
                    else if (cyNetwork.nodesList().size() < QuickFindPlugIn.REINDEX_THRESHOLD) {
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
