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

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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
	JLabel operationIcon;
	JComboBox operations;
	DefaultListModel networkData;
	DefaultListModel unselectedNetworkData;
	boolean cancelled = true;

	protected final int LIST_WIDTH = 150;
	protected final int DIALOG_SPACE = 110;

	protected static final String UNION = "Union";
	protected static final String INTERSECTION = "Intersection";
	protected static final String DIFFERENCE = "Difference";
	protected final String[] OPERATIONS = { UNION, INTERSECTION, DIFFERENCE };

	protected final ImageIcon UNION_ICON = new ImageIcon(getClass().getResource("/union.png"));
	protected final ImageIcon INTERSECTION_ICON = new ImageIcon(getClass().getResource("/intersection.png"));
	protected final ImageIcon DIFFERENCE_ICON = new ImageIcon(getClass().getResource("/difference.png"));
	protected final ImageIcon[] OPERATION_ICONS =  { UNION_ICON, INTERSECTION_ICON, DIFFERENCE_ICON };

	/**
	 * Creates a new MergeDialog object.
	 */
	public MergeDialog() {
		super(Cytoscape.getDesktop(),true);
		/*
		 * Set up all of the GUI bits
		 */
		setTitle("Merge Networks");
		setSize(new Dimension((2 * LIST_WIDTH) + DIALOG_SPACE, 180));
		setResizable(true);

		networkData = new DefaultListModel();
		unselectedNetworkData = new DefaultListModel();

		for (Iterator networkIt = Cytoscape.getNetworkSet().iterator(); networkIt.hasNext();) {
			unselectedNetworkData.addElement(new NetworkContainer((CyNetwork) networkIt.next()));
		}

		networkList = new JList(networkData);
		networkList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
					int [] indices = networkList.getSelectedIndices();
					
					if (indices == null || indices.length == 0) {
						return;
					}

					// Remove the objects from right list
					Vector<Object> objs = new Vector<Object>();
					for (int i= indices.length-1; i>=0; i--) {
						Object removed = networkData.remove(indices[i]);
						objs.add(removed);						
					}

					// Add the objects to the left list in the same order as left
					for (int i=objs.size()-1; i>=0; i--) {
						unselectedNetworkData.addElement(objs.elementAt(i));
					}

					// Select new objects in left list
					int [] indices2 = new int[indices.length];
					for (int i=0; i< indices.length; i++) {
						indices2[i] = unselectedNetworkData.getSize() - 1 -i; 
					}
					unselectedNetworkList.setSelectedIndices(indices2);
	
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
										
					int [] indices = unselectedNetworkList.getSelectedIndices();
					
					if (indices == null || indices.length == 0) {
						return;
					}

					// Remove the objects from left list
					Vector<Object> objs = new Vector<Object>();
					for (int i= indices.length-1; i>=0; i--) {
						Object removed = unselectedNetworkData.remove(indices[i]);
						objs.add(removed);						
					}

					// Add the objects to the right list in the same order as left
					for (int i=objs.size()-1; i>=0; i--) {
						networkData.addElement(objs.elementAt(i));
					}
					
					// Select new objects in right list
					int [] indices2 = new int[indices.length];
					for (int i=0; i< indices.length; i++) {
						indices2[i] = networkData.getSize() - 1 -i; 
					}
					
					networkList.setSelectedIndices(indices2);
					
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

		JPanel lrButtonPanel = new JPanel();
		lrButtonPanel.setLayout(new BoxLayout(lrButtonPanel, BoxLayout.PAGE_AXIS));
		lrButtonPanel.add(leftButton);
		lrButtonPanel.add(rightButton);

		JScrollPane rightPane = new JScrollPane(networkList);
		rightPane.setBorder(new TitledBorder("Selected Networks"));
		rightPane.setPreferredSize(new Dimension(LIST_WIDTH, 100));

		JPanel udButtonPanel = new JPanel();
		udButtonPanel.setLayout(new BoxLayout(udButtonPanel, BoxLayout.PAGE_AXIS));
		udButtonPanel.add(upButton);
		udButtonPanel.add(downButton);

		JPanel operationsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		operations = new JComboBox(OPERATIONS);
		operations.addActionListener(new OperationsComboBoxListener());
		JLabel operationLabel = new JLabel("Operation: ");
		operationsPanel.add(operationLabel);
		operationsPanel.add(operations);

		JSeparator separator0 = new JSeparator();

		operationIcon = new JLabel();
		updateIcon();

		ok = new JButton("   OK   ");
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

		JSeparator separator1 = new JSeparator();

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(cancel);
		buttonPanel.add(ok);

		Container content = getContentPane();
		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new java.awt.Insets(5,5,5,5);
		
		c.gridx = 0;			c.gridy = 0;
		c.gridwidth = 4;		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.anchor = GridBagConstraints.LINE_START;
		content.add(operationsPanel, c);

		c.gridx = 0;			c.gridy = 1;
		c.gridwidth = 4;		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;		c.weighty = 0.0;
		c.anchor = GridBagConstraints.LINE_START;
		content.add(operationIcon, c);

		c.gridx = 0;			c.gridy = 2;
		c.gridwidth = 4;		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.anchor = GridBagConstraints.LINE_START;
		content.add(separator0, c);

		c.gridx = 0;			c.gridy = 3;
		c.gridwidth = 1;		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		content.add(leftPane, c);

		c.gridx = 1;			c.gridy = 3;
		c.gridwidth = 1;		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		content.add(lrButtonPanel, c);

		c.gridx = 2;			c.gridy = 3;
		c.gridwidth = 1;		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		content.add(rightPane, c);

		c.gridx = 3;			c.gridy = 3;
		c.gridwidth = 1;		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		content.add(udButtonPanel, c);

		c.gridx = 0;			c.gridy = 4;
		c.gridwidth = 4;		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.anchor = GridBagConstraints.LINE_START;
		content.add(separator1, c);

		c.gridx = 0;			c.gridy = 5;
		c.gridwidth = 4;		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;		c.weighty = 0.0;
		c.anchor = GridBagConstraints.LINE_START;
		content.add(buttonPanel, c);

		this.pack();
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

	private class OperationsComboBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			updateIcon();
		}
	}

	private void updateIcon()
	{
		int oldHeight = 0;
		if (operationIcon.getIcon() != null)
			oldHeight = operationIcon.getIcon().getIconHeight();

		int index = operations.getSelectedIndex();
		if (index < 0 || index >= OPERATION_ICONS.length)
			operationIcon.setIcon(null);
		else
			operationIcon.setIcon(OPERATION_ICONS[operations.getSelectedIndex()]);

		int newHeight = 0;
		if (operationIcon.getIcon() != null)
			newHeight = operationIcon.getIcon().getIconHeight();
		
		setSize(new Dimension(getWidth(), getHeight() - oldHeight + newHeight));
	}
}
