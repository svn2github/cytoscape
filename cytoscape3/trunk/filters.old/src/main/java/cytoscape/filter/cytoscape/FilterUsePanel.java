
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

package cytoscape.filter.cytoscape;

import cytoscape.Cytoscape;
import cytoscape.filter.model.Filter;
import cytoscape.filter.model.FilterEditorManager;
import cytoscape.filter.model.FilterManager;
import cytoscape.filter.view.CreateFilterDialog;
import cytoscape.filter.view.FilterEditorPanel;
import cytoscape.filter.view.FilterListPanel;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class FilterUsePanel extends JPanel implements PropertyChangeListener, ActionListener {
	private FilterEditorPanel filterEditorPanel;
	private FilterListPanel filterListPanel;
	private JRadioButton hideFailed;
	private JRadioButton grayFailed;
	private JRadioButton selectPassed;
	private JButton apply;
	private JButton addFilters;
	private JButton removeFilters;
	private JList selectedFilters;
	private JDialog createFilterDialog;
	private JButton addButton;
	private JButton removeButton;
	private JCheckBox select;
	private JCheckBox gray;
	private JCheckBox hide;
	private JCheckBox overwrite;
	private JRadioButton pulsate;
	private JRadioButton spiral;
	private JFrame frame;
	private Runnable updateProgMon;
	private Runnable closeProgMon;
	private Runnable setMinAndMax;
	private int progressCount = 0;
	private int minCount = 0;
	private int maxCount = 100;
	private String progressNote = "";
	private String progressTitle = "Applying filter";
	private boolean applyFilterCanceled = false;
	private static final int TYPE_NODE = 1;
	private static final int TYPE_EDGE = 2;

	/**
	 * 
	 */
	public static final String NEW_FILTER_ADDED = "NEW_FILTER_ADDED";

	/**
	 * Creates a new FilterUsePanel object.
	 *
	 * @param frame  DOCUMENT ME!
	 */
	public FilterUsePanel(JFrame frame) {
		super();
		this.frame = frame;
		frame.setPreferredSize(new Dimension(700, 300));

		setLayout(new BorderLayout());
		// --------------------//
		// FilterEditorPanel
		filterEditorPanel = new FilterEditorPanel();
		filterEditorPanel.getPropertyChangeSupport().addPropertyChangeListener(this);

		// --------------------//
		// Selected Filter Panel
		JPanel selected_filter_panel = new JPanel();
		selected_filter_panel.setLayout(new BorderLayout());
		filterListPanel = new FilterListPanel();
		selected_filter_panel.add(filterListPanel, BorderLayout.CENTER);
		selected_filter_panel.add(createManagePanel(), BorderLayout.NORTH);
		selected_filter_panel.add(createActionPanel(), BorderLayout.SOUTH);

		JSplitPane pane0 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selected_filter_panel,
		                                  filterEditorPanel);
		add(pane0);
		filterListPanel.getSwingPropertyChangeSupport().addPropertyChangeListener(filterEditorPanel);
		filterListPanel.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		initProgressMonitor();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public FilterListPanel getFilterListPanel() {
		return filterListPanel;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == FilterListPanel.FILTER_SELECTED) {
			removeButton.setEnabled(true);
			apply.setEnabled(true);

			// do something on a Filter Selected
		} else if (e.getPropertyName() == FilterListPanel.NO_SELECTION) {
			removeButton.setEnabled(false);
			apply.setEnabled(false);
		} // end of if ()
		else if (e.getPropertyName() == FilterEditorPanel.ACTIVE_PANEL_CHANGED) {
			frame.setPreferredSize(new Dimension(700, 300));
			frame.pack();
		} else if (e.getPropertyName() == NEW_FILTER_ADDED) {
			// Select the new filter just created
			// New filter created is always added at the end of a vector
			int lastIndex = filterListPanel.getFilterList().getModel().getSize() - 1;
			filterListPanel.getFilterList().setSelectedIndex(lastIndex);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JPanel createManagePanel() {
		JPanel result = new JPanel();
		result.setBorder(new TitledBorder("Manage Filters"));
		addButton = new JButton("Create new filter");
		addButton.addActionListener(this);
		removeButton = new JButton("Remove selected filter");
		removeButton.addActionListener(this);
		removeButton.setEnabled(false);
		result.add(addButton);
		result.add(removeButton);

		return result;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addButton) {
			if (createFilterDialog == null) {
				createFilterDialog = new CreateFilterDialog(FilterEditorManager.defaultManager());
			}

			int pre_filterCount = filterListPanel.getFilterList().getModel().getSize();
			createFilterDialog.setVisible(true);

			int post_filterCount = filterListPanel.getFilterList().getModel().getSize();

			/*
			 * Fire an event to notify the JList to select it, if new filter is
			 * added
			 */
			if ((post_filterCount - pre_filterCount) > 0) {
				java.beans.PropertyChangeEvent evt = new java.beans.PropertyChangeEvent(this,
				                                                                        NEW_FILTER_ADDED,
				                                                                        null, null);
				filterListPanel.getSwingPropertyChangeSupport().firePropertyChange(evt);
			}
		}

		if (e.getSource() == removeButton) {
			Filter filter = filterListPanel.getSelectedFilter();

			if (filter != null) {
				FilterManager.defaultManager().removeFilter(filter);
			}
		}
	}

	private void initProgressMonitor() {
		progressTitle = "Applying filter";
		progressNote = "";

		final ProgressMonitor pm = new ProgressMonitor(frame, progressTitle, progressNote, 0, 100);
		pm.setMillisToDecideToPopup(500);
		pm.setMillisToPopup(2000);

		// Create these as inner classes to minimize the amount of thread
		// creation (expensive!)...
		updateProgMon = new Runnable() {
				double dCount = 0;

				public void run() {
					if (pm.isCanceled()) {
						applyFilterCanceled = true;
						pm.close();
					} else {
						dCount = ((double) progressCount / (double) pm.getMaximum());
						progressNote = ((int) (dCount * 100.0) + "% complete");
						pm.setProgress(progressCount);
						pm.setNote(progressNote);
					}
				}
			};

		closeProgMon = new Runnable() {
				public void run() {
					pm.close();
				}
			};

		setMinAndMax = new Runnable() {
				public void run() {
					pm.setMinimum(minCount);
					pm.setMaximum(maxCount);
				}
			};
	}

	/**
	 * This method will take an object and do whatever it is supposed to
	 * according to what the available actions are.
	 */
	protected void passObject(Object object, boolean passes) {
		// Should not use this... Event should fire only once in the filtering
		// process!
		if (passes) {
			if (object instanceof CyNode) {
				Cytoscape.getCurrentNetwork().setSelectedNodeState((CyNode) object, true);
			} else if (object instanceof CyEdge) {
				Cytoscape.getCurrentNetwork().setSelectedEdgeState((CyEdge) object, true);
			}
		}
	}

	private void fireEvent(final List selectedObjList, final int type) {
		if (type == TYPE_NODE) {
			Cytoscape.getCurrentNetwork().setSelectedNodeState(selectedObjList, true);
		} else if (type == TYPE_EDGE) {
			Cytoscape.getCurrentNetwork().setSelectedEdgeState(selectedObjList, true);
		}
	}

	protected void testObjects() {
		final Filter filter = filterListPanel.getSelectedFilter();
		final CyNetwork network = Cytoscape.getCurrentNetwork();

		final List<CyNode> nodes_list = network.nodesList();
		final List<CyEdge> edges_list = network.edgesList();

		if (filter != null) {
			final Class[] passingTypes = filter.getPassingTypes();

			minCount = 0;
			maxCount = getTaskEstimation(passingTypes, nodes_list, edges_list);
			progressCount = 0;
			applyFilterCanceled = false;

			try {
				SwingUtilities.invokeAndWait(setMinAndMax);
			} catch (Exception _e) {
				_e.printStackTrace();
			}

			for (int idx = 0; idx < passingTypes.length; idx++) {
				if (passingTypes[idx].equals(CyNode.class)) {
					final List<CyNode> passedNodes = new ArrayList<CyNode>();

					for (CyNode node : nodes_list) {
						try {
							if (filter.passesFilter(node)) {
								passedNodes.add(node);
							}

							// passObject(node, filter.passesFilter(node));
						} catch (StackOverflowError soe) {
							soe.printStackTrace();

							return;
						}

						progressCount++;

						try {
							SwingUtilities.invokeAndWait(updateProgMon);
						} catch (Exception _e) {
							_e.printStackTrace();
						}

						if (applyFilterCanceled) {
							progressNote = "Please wait...";

							try {
								SwingUtilities.invokeAndWait(updateProgMon);
							} catch (Exception _e) {
							}

							break;
						}
					}

					fireEvent(passedNodes, TYPE_NODE);
				} else if (passingTypes[idx].equals(CyEdge.class)) {
					final List<CyEdge> passedEdges = new ArrayList<CyEdge>();

					for (CyEdge edge : edges_list) {
						try {
							if (filter.passesFilter(edge)) {
								passedEdges.add(edge);
							}

							// passObject(edge, filter.passesFilter(edge));
						} catch (StackOverflowError soe) {
							soe.printStackTrace();

							return;
						}

						progressCount++;

						try {
							SwingUtilities.invokeAndWait(updateProgMon);
						} catch (Exception _e) {
						}

						if (applyFilterCanceled) {
							progressNote = "Please wait...";

							try {
								SwingUtilities.invokeAndWait(updateProgMon);
							} catch (Exception _e) {
							}

							break;
						}
					}

					fireEvent(passedEdges, TYPE_EDGE);
				}
			} // for loop

			try {
				SwingUtilities.invokeAndWait(closeProgMon);
			} catch (Exception _e) {
				_e.printStackTrace();
			}
		}
	}

	// Calculate the total tasks for the progress monitor
	private int getTaskEstimation(Class[] pClassTypes, List pNodeList, List pEdgeList) {
		int taskCount = 0;

		for (int idx = 0; idx < pClassTypes.length; idx++) {
			if (pClassTypes[idx].equals(CyNode.class)) {
				taskCount += pNodeList.size();
			} else if (pClassTypes[idx].equals(CyEdge.class)) {
				taskCount += pEdgeList.size();
			}
		}

		return taskCount;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JPanel createActionPanel() {
		final JPanel actionPanel = new JPanel();

		apply = new JButton(new AbstractAction("Apply selected filter") {
				public void actionPerformed(ActionEvent e) {
					/*
					 * We have to run "Apply filter" in a seperate thread, becasue
					 * we want to monitor the progress
					 */
					ApplyFilterThread applyFilterThread = new ApplyFilterThread();
					applyFilterThread.start();
				}
			});

		apply.setEnabled(false);

		actionPanel.add(apply);

		return actionPanel;
	}

	private class ApplyFilterThread extends Thread {
		public void run() {
			testObjects();
			Cytoscape.getCurrentNetworkView().updateView();
		}
	}
}
