package org.cytoscape.groups.results.internal.ui;

import giny.model.GraphPerspective;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupImpl;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: Vuk Pavlovic
 * * Description: The Results Panel displaying found clusters
 */

/**
 * Reports the results of MCODE cluster finding. This class sets up the UI.
 */
public class GroupViewerPanel extends JPanel {

	private static final long serialVersionUID = -1175390819765633541L;
	
	// Groups to be displayed
//	private List<CyGroup> groups;

	private CyNetwork network;

	protected JTable table;
	protected GroupBrowserTableModel modelBrowser;
	private JPanel clusterBrowserPanel;

	// table size parameters
	protected static final int graphPicSize = 80;
	protected static final int defaultRowHeight = graphPicSize + 8;


	public GroupViewerPanel() {
		setLayout(new BorderLayout());

		clusterBrowserPanel = createClusterBrowserPanel();
		add(clusterBrowserPanel, BorderLayout.CENTER);
	}

	/**
	 * Creates a panel that contains the browser table with a scroll bar.
	 * 
	 * @param imageList
	 *            images of cluster graphs
	 * @return panel
	 */
	private JPanel createClusterBrowserPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Group Browser"));

		// main data table
		modelBrowser = new GroupBrowserTableModel();
		table = new JTable(modelBrowser);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(CyGroupImpl.class,
				new GroupBrowserTableRenderer(defaultRowHeight));
		table.setIntercellSpacing(new Dimension(0, 4)); // gives a little
		// vertical room between
		// clusters
		table.setFocusable(false); // removes an outline that appears when the
		// user clicks on the images

		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.getViewport().setBackground(Color.WHITE);

		panel.add(tableScrollPane, BorderLayout.CENTER);

		return panel;
	}



	private GraphPerspective toGP(CyGroup cluster) {
		int[] nodes = new int[cluster.getNodes().size()];
		for (int i = 0; i < cluster.getNodes().size(); i++) {
			nodes[i] = cluster.getNodes().get(i).getRootGraphIndex();
		}
		return network.createGraphPerspective(nodes);
	}

	public void addGroup(CyGroup group) {

		this.network = Cytoscape.getCurrentNetwork();
		Image groupImage = MCODEUtil.convertNetworkToImage(toGP(group),
				graphPicSize, graphPicSize, null, true);
		Object[] newRow = new Object[2];
		newRow[0] = new ImageIcon(groupImage);
		newRow[1] = group;
		this.modelBrowser.addRow(newRow);

	}
	
	protected JTable getTable() {
		return this.table;
	}

	public void removeGroup(CyGroup group) {
		// TODO Auto-generated method stub
		System.out.println("Remove group not implemented yet");
	}
}
