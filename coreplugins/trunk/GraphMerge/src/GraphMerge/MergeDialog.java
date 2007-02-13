/*
  File: MergeDialog.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute of Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package GraphMerge;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.util.GraphSetUtils;

import cytoscape.view.CyNetworkView;

import giny.model.Edge;
import giny.model.Node;

import giny.view.EdgeView;
import giny.view.Label;
import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


class MergeDialog extends JDialog {
	JButton upButton;
	JButton downButton;
	JButton leftButton;
	JButton rightButton;
	JButton ok;
	JButton cancel;
	JList networkList;
	JList unselectedNetworkList;
	JComboBox operations;
	DefaultListModel networkData;
	DefaultListModel unselectedNetworkData;
	boolean cancelled = true;
	protected static int LIST_WIDTH = 150;
	protected static int DIALOG_SPACE = 110;
	protected static String UNION = "Union";
	protected static String INTERSECTION = "Intersection";
	protected static String DIFFERENCE = "Difference";

	/**
	 * Creates a new MergeDialog object.
	 */
	public MergeDialog() {
		/*
		 * Set up all of the GUI bits
		 */

		/*
		 * Set up the menu system
		 */
		JMenu help = new JMenu("Help");
		help.add(new AbstractAction("About") {
				public void actionPerformed(ActionEvent ae) {
					JTextPane tp = new JTextPane();
					JScrollPane js = new JScrollPane();
					js.getViewport().add(tp);

					JDialog jf = new JDialog(MergeDialog.this);
					jf.getContentPane().add(js);
					jf.pack();
					jf.setSize(400, 500);
					jf.setVisible(true);

					try {
						tp.setPage(getClass().getResource("/about.html"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		help.add(new AbstractAction("Manual") {
				public void actionPerformed(ActionEvent ae) {
					JTextPane tp = new JTextPane();
					JScrollPane js = new JScrollPane();
					js.getViewport().add(tp);

					JDialog jf = new JDialog(MergeDialog.this);
					jf.getContentPane().add(js);
					jf.pack();
					jf.setSize(400, 500);
					jf.setVisible(true);

					try {
						tp.setPage(getClass().getResource("/manual.html"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		setJMenuBar(new JMenuBar());
		getJMenuBar().add(help);

		setModal(true);
		setTitle("Merge Networks");
		getContentPane().setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

		networkData = new DefaultListModel();
		unselectedNetworkData = new DefaultListModel();

		for (Iterator networkIt = Cytoscape.getNetworkSet().iterator(); networkIt.hasNext();) {
			unselectedNetworkData.addElement(new NetworkContainer((CyNetwork) networkIt.next()));
		}

		networkList = new JList(networkData);
		networkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		networkList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int index = networkList.getMinSelectionIndex();

					if (index > -1) {
						unselectedNetworkList.getSelectionModel().clearSelection();
					}

					if (index == -1) {
						upButton.setEnabled(false);
						downButton.setEnabled(false);
						leftButton.setEnabled(false);
					} else if (networkData.size() < 2) {
						upButton.setEnabled(false);
						downButton.setEnabled(false);
						leftButton.setEnabled(true);
					} else if (index == 0) {
						upButton.setEnabled(false);
						downButton.setEnabled(true);
						leftButton.setEnabled(true);
					} else if (index == (networkData.size() - 1)) {
						upButton.setEnabled(true);
						downButton.setEnabled(false);
						leftButton.setEnabled(true);
					} else {
						upButton.setEnabled(true);
						downButton.setEnabled(true);
						leftButton.setEnabled(true);
					}
				}
			});
		unselectedNetworkList = new JList(unselectedNetworkData);
		unselectedNetworkList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int index = unselectedNetworkList.getMinSelectionIndex();

					if (index > -1) {
						networkList.getSelectionModel().clearSelection();
					}

					if (index == -1) {
						rightButton.setEnabled(false);
					} else {
						rightButton.setEnabled(true);
					}
				}
			});

		ImageIcon upIcon = new ImageIcon(getClass().getResource("/up16.gif"));
		ImageIcon downIcon = new ImageIcon(getClass().getResource("/down16.gif"));
		ImageIcon leftIcon = new ImageIcon(getClass().getResource("/left16.gif"));
		ImageIcon rightIcon = new ImageIcon(getClass().getResource("/right16.gif"));
		upButton = new JButton(upIcon);
		downButton = new JButton(downIcon);
		leftButton = new JButton(leftIcon);
		rightButton = new JButton(rightIcon);

		upButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int currentIndex = networkList.getMinSelectionIndex();
					Object removed = networkData.remove(currentIndex);
					networkData.add(currentIndex - 1, removed);
					networkList.setSelectedIndex(currentIndex - 1);
					networkList.repaint();
				}
			});
		downButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int currentIndex = networkList.getMinSelectionIndex();
					Object removed = networkData.remove(currentIndex);
					networkData.add(currentIndex + 1, removed);
					networkList.setSelectedIndex(currentIndex + 1);
					networkList.repaint();
				}
			});
		leftButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int currentIndex = networkList.getMinSelectionIndex();
					Object removed = networkData.remove(currentIndex);
					unselectedNetworkData.addElement(removed);
					unselectedNetworkList.setSelectedIndex(unselectedNetworkData.getSize() - 1);
					networkList.repaint();
					unselectedNetworkList.repaint();

					if (networkData.getSize() > 1) {
						ok.setEnabled(true);
						ok.setToolTipText(null);
					} else {
						ok.setEnabled(false);
						ok.setToolTipText("Select at least two network to merge");
					}
				}
			});
		rightButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					int currentIndex = unselectedNetworkList.getMinSelectionIndex();
					Object removed = unselectedNetworkData.remove(currentIndex);
					networkData.addElement(removed);
					networkList.setSelectedIndex(networkData.getSize() - 1);
					networkList.repaint();
					unselectedNetworkList.repaint();

					if (networkData.getSize() > 1) {
						ok.setEnabled(true);
						ok.setToolTipText(null);
					} else {
						ok.setEnabled(false);
						ok.setToolTipText("Select at least two network to merge");
					}
				}
			});

		upButton.setEnabled(false);
		upButton.setToolTipText("Set the ordering of selected networks");
		downButton.setEnabled(false);
		downButton.setToolTipText("Set the ordering of selected networks");
		leftButton.setEnabled(false);
		rightButton.setEnabled(false);

		JScrollPane leftPane = new JScrollPane(unselectedNetworkList);
		leftPane.setBorder(new TitledBorder("Available Networks"));
		leftPane.setPreferredSize(new Dimension(LIST_WIDTH, 100));
		centerPanel.add(leftPane);

		JPanel lrButtonPanel = new JPanel();
		lrButtonPanel.setLayout(new BoxLayout(lrButtonPanel, BoxLayout.Y_AXIS));
		lrButtonPanel.add(leftButton);
		lrButtonPanel.add(rightButton);
		centerPanel.add(lrButtonPanel);

		JScrollPane rightPane = new JScrollPane(networkList);
		rightPane.setBorder(new TitledBorder("Selected Networks"));
		rightPane.setPreferredSize(new Dimension(LIST_WIDTH, 100));
		centerPanel.add(rightPane);

		JPanel udButtonPanel = new JPanel();
		udButtonPanel.setLayout(new BoxLayout(udButtonPanel, BoxLayout.Y_AXIS));
		udButtonPanel.add(upButton);
		udButtonPanel.add(downButton);
		centerPanel.add(udButtonPanel);

		getContentPane().add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		operations = new JComboBox(new String[] { UNION, INTERSECTION, DIFFERENCE });

		ok = new JButton("OK");
		ok.setEnabled(false);
		ok.setToolTipText("Select at least two network to merge");
		cancel = new JButton("Cancel");
		ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					cancelled = false;
					MergeDialog.this.setVisible(false);
				}
			});
		cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					MergeDialog.this.setVisible(false);
				}
			});

		southPanel.add(operations);
		southPanel.add(cancel);
		southPanel.add(ok);
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		setSize(new Dimension((2 * LIST_WIDTH) + DIALOG_SPACE, 180));
		setResizable(true);

		// this.pack();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getOperation() {
		return (String) operations.getSelectedItem();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getNetworkList() {
		Vector result = new Vector();

		for (int idx = 0; idx < networkData.size(); idx++) {
			result.add(((NetworkContainer) networkData.elementAt(idx)).getNetwork());
		}

		return result;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isCancelled() {
		return cancelled;
	}
}
