/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package chemViz.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTable;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import chemViz.model.ChemInfoTableModel;
import chemViz.model.Compound;
import chemViz.model.CompoundColumn;
import chemViz.model.Compound.DescriptorType;
import chemViz.model.TableSorter;

class TableMouseAdapter extends MouseAdapter implements ActionListener {
	private	TableSorter sorter;
	private JTable table;
	private ChemInfoTableModel tableModel;

	TableMouseAdapter(JTable table, ChemInfoTableModel tableModel, TableSorter sorter) {
		this.table = table;
		this.sorter = sorter;
		this.tableModel = tableModel;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getComponent() == table)
		{
			Point p = e.getPoint();
			// int row = table.convertRowIndexToModel(table.rowAtPoint(p));
			int row = sorter.modelIndex(table.rowAtPoint(p));
			int column = table.convertColumnIndexToModel(table.columnAtPoint(p));
			if (tableModel.getColumnClass(column) == Compound.class) {
				final List<Compound> cList = new ArrayList();
				final Compound c = (Compound)tableModel.getValueAt(row, column);
				cList.add(c);
				Runnable t = new Runnable() {
  					public void run() {
						List<GraphObject>goList = new ArrayList();
						goList.add(c.getSource());
   	 				CompoundPopup popup = new CompoundPopup(cList, goList);
						popup.toFront();
					}
				};
				new Thread(t).start();
			}
		} else if (e.getComponent() == table.getTableHeader() && 
		           ((e.getButton() == MouseEvent.BUTTON3) ||
		            (e.getButton() == MouseEvent.BUTTON1 && e.isMetaDown()) ||
		            (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()))) {
			// Popup header context menu
			JPopupMenu headerMenu = new JPopupMenu();
			// Get our column title
			Point p = e.getPoint();
			int column = table.convertColumnIndexToModel(table.columnAtPoint(p));
			String name = tableModel.getColumnName(column);
			// Add removeMenu if we have more than 1 column
			if (tableModel.getColumnCount() > 1) {
				JMenuItem removeMenu = new JMenuItem("Remove Column "+name);
				removeMenu.setActionCommand("removeColumn:"+column);
				removeMenu.addActionListener(this);
				headerMenu.add(removeMenu);
			}
			JMenu addMenu = new JMenu("Add New Column");
			JMenu attrMenu = new JMenu("Cytoscape attributes");
			if (tableModel.hasNodes()) {
				addAttributeMenus(attrMenu, Cytoscape.getNodeAttributes(), "node.", column);
			}
			if (tableModel.hasEdges()) {
				addAttributeMenus(attrMenu, Cytoscape.getEdgeAttributes(), "edge.", column);
			}
			if (attrMenu.getItemCount() > 0) 
				addMenu.add(attrMenu);

			JMenu descMenu = new JMenu("Molecular descriptors");
			addDescriptorMenus(descMenu, column);
			if (descMenu.getItemCount() > 0) 
				addMenu.add(descMenu);

			headerMenu.add(addMenu);
			headerMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith("removeColumn:")) {
			String columnNumber = e.getActionCommand().substring(13);
			int column = Integer.parseInt(columnNumber);
			tableModel.removeColumn(column);
		} else if (e.getActionCommand().startsWith("addColumn:")) {
			String columnNumber = e.getActionCommand().substring(10);
			int column = Integer.parseInt(columnNumber);
		}
	}

	void addAttributeMenus(JMenu addMenu, CyAttributes attributes, String type, int column) {
		String[] attNames = attributes.getAttributeNames();
		for (int i = 0; i < attNames.length; i++) {
			String att = attNames[i];
			if (tableModel.findColumn(att) < 0) {
				addMenu.add(new AddMenu(att, type, column, attributes.getType(att)));
			}
		}
	}

	void addDescriptorMenus(JMenu addMenu, int column) {
		List<DescriptorType> descList = Compound.getDescriptorList();
		for (DescriptorType type: descList) {
			if (tableModel.findColumn(type.toString()) < 0) {
				addMenu.add(new AddMenu(type, column));
			}
		}
	}

	class AddMenu extends JMenuItem implements ActionListener {
		int column;
		CompoundColumn newColumn;
		
		AddMenu(String name, int column) {
			this(name, "", column, CyAttributes.TYPE_STRING);
		}
	
		AddMenu(String name, String prefix, int column, byte type) {
			super(prefix+name);
			this.newColumn = new CompoundColumn(name, prefix, type, -1);
			this.column = column;
			addActionListener(this);
		}
	
		AddMenu(DescriptorType descriptor, int column) {
			super(descriptor.toString());
			this.newColumn = new CompoundColumn(descriptor, -1);
			this.column = column;
			addActionListener(this);
		}
	
		public void actionPerformed(ActionEvent e) {
			tableModel.addColumn(column, newColumn);
		}
	}
}
