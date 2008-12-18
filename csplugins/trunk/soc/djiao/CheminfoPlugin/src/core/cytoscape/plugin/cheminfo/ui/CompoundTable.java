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

package cytoscape.plugin.cheminfo.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

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
import cytoscape.view.CyNetworkView;

import cytoscape.plugin.cheminfo.model.Compound;
import cytoscape.plugin.cheminfo.model.TableSorter;

public class CompoundTable extends JDialog implements ListSelectionListener,SelectEventListener {
	
	private List<Compound> compoundList;
	private Map<GraphObject,Integer> rowMap;
	private ChemInfoTableModel tableModel;
	private	TableColumnModel columnModel;
	private	ListSelectionModel selectionModel;
	private	JTable table;
	private	TableSorter sorter;
	private CyNetwork network;
	private CyNetworkView networkView;
	private	boolean modifyingSelection = false;
	private JDialog thisDialog;
	private static int DEFAULT_IMAGE_SIZE=80;

	public CompoundTable (List<Compound> compoundList) {
		super(Cytoscape.getDesktop());
		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();
		this.compoundList = compoundList;
		setTitle("2D Structure Table");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.rowMap = new HashMap();

		// Create the table
		initTable();

		// Listen for network selection events
		network.addSelectEventListener(this);

		pack();
		thisDialog = this;
	}

	public void setCompounds(List<Compound> newList) {
		this.compoundList = newList;
		this.rowMap = new HashMap();
		tableModel.fireTableDataChanged();
	}

	private void initTable() {
		tableModel = new ChemInfoTableModel();
		sorter = new TableSorter(tableModel);
		table = new JTable(sorter);
		sorter.setTableHeader(table.getTableHeader());

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setDefaultRenderer(Compound.class, new CompoundRenderer());
		table.setDefaultRenderer(String.class, new StringRenderer());
		table.setRowHeight(DEFAULT_IMAGE_SIZE);

		// Figure out all of our default column widths
		columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(100);
		columnModel.getColumn(1).setPreferredWidth(100);
		columnModel.getColumn(2).setPreferredWidth(200);
		columnModel.getColumn(3).setPreferredWidth(100);
		columnModel.getColumn(4).setPreferredWidth(DEFAULT_IMAGE_SIZE);

		// Add our mouse listener (specific for 2D image popup)
		table.addMouseListener(new MyMouseAdapter());

		// Add our row selection listener
		selectionModel = table.getSelectionModel();
		selectionModel.addListSelectionListener(this);

		JScrollPane pane = new JScrollPane(table);
		pane.setPreferredSize(new Dimension(500+DEFAULT_IMAGE_SIZE+20,520));
		add(pane);
	}


	/**
 	 * valueChanged is called when a user changes the selection in the table.
 	 *
 	 * @param e the ListSelectionEvent that tells us what was done.
 	 */
	public void valueChanged(ListSelectionEvent e) {
		if (modifyingSelection) return;
		if (e.getSource() == table.getSelectionModel()) {
			modifyingSelection = true;
			int[] rows = table.getSelectedRows();
			network.unselectAllNodes();
			network.unselectAllEdges();
			for (int i = 0; i < rows.length; i++) {
				Compound c = compoundList.get(sorter.modelIndex(rows[i]));
				GraphObject obj = c.getSource();
				if (obj instanceof CyNode) {
					network.setSelectedNodeState((CyNode)obj, true);
				} else {
					network.setSelectedEdgeState((CyEdge)obj, true);
				}
			}
			modifyingSelection = false;
		}
		networkView.updateView();
	}

	public void onSelectEvent(SelectEvent event) {
		if (modifyingSelection) return;
		modifyingSelection = true;
		selectionModel.clearSelection();
		selectObjects(network.getSelectedNodes());
		selectObjects(network.getSelectedEdges());
		modifyingSelection = false;
	}

	private void selectObjects(Set<GraphObject>selectedObjects) {
		for (GraphObject obj: selectedObjects) {
			if (rowMap.containsKey(obj)) {
				int row = sorter.viewIndex(rowMap.get(obj).intValue());
				selectionModel.addSelectionInterval(row,row);
			}
		}
	}


	class ChemInfoTableModel extends AbstractTableModel {
		public int getColumnCount() { return 5; }
		public int getRowCount() { return compoundList.size(); }

		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "Node or Edge";
			case 1:
				return "Attribute";
			case 2:
				return "Molecular Identifier";
			case 3:
				return "Molecular Wt";
			case 4:
				return "2D Image";
			}
			return "";
		}

		public Class getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
			case 1:
			case 2:
				return String.class;
			case 3:
				return Double.class;
			case 4:
				return Compound.class;
			}
			return null;
		}
		
		public Object getValueAt(int row, int col) {
			Compound cmpd = compoundList.get(row);
			switch (col) {
			case 0:
				GraphObject obj = cmpd.getSource();
				return obj.getIdentifier();
			case 1:
				return cmpd.getAttribute();
			case 2:
				return cmpd.getMoleculeString();
			case 3:
				double mw = cmpd.getMolecularWeight();
				if (mw == 0.0f) return null;
				return Double.valueOf(mw);
			case 4:
				return cmpd;
			}
			return null;
		}
	}

	class CompoundRenderer implements TableCellRenderer {
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		                                        boolean hasFocus, int row, int column) {
			adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			Compound c = compoundList.get(row);
			TableColumn clm = table.getColumnModel().getColumn(column);
			int width = clm.getPreferredWidth();
			table.setRowHeight(width);
			Image resizedImage = c.getImage(width,width);
			JLabel l = new JLabel(new ImageIcon(resizedImage));
			rowMap.put(c.getSource(), Integer.valueOf(row));
			l.setBackground(adaptee.getBackground());
			l.setForeground(adaptee.getForeground());
			return l;
		}
	}

	class StringRenderer extends JTextArea implements TableCellRenderer {
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

		public StringRenderer () {
			setLineWrap(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		                                        boolean hasFocus, int row, int column) {
			adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setBackground(adaptee.getBackground());
			setBorder(adaptee.getBorder());
			setFont(adaptee.getFont());
			setText(adaptee.getText());
			setForeground(adaptee.getForeground());
			return this;
		}
	}

	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2)
			{
				Point p = e.getPoint();
				// int row = table.convertRowIndexToModel(table.rowAtPoint(p));
				int row = sorter.modelIndex(table.rowAtPoint(p));
				int column = table.convertColumnIndexToModel(table.columnAtPoint(p));
				final Compound c = compoundList.get(row);
				if (column == 4) {
					final List<Compound> cList = new ArrayList();
					cList.add(c);
					Runnable t = new Runnable() {
  						public void run() {
   	 					CompoundPopup popup = new CompoundPopup(cList, c.getSource());
							popup.toFront();
						}
					};
					new Thread(t).start();
				}
			}
		}
	}

}
