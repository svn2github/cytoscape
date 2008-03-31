/*
 * Created on Mar 6, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.treeanno;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Observable;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.plugin.dendroview.GTRView;

/**
 * class to allow editing of TreeViewNodes, altough could easily be generalized
 * to all HeaderInfo types.
 */
public class TableNodeView extends ModelView implements ListSelectionListener {

	private NodeTableModel tableModel;
	private TreeSelectionI selection;
	private JTable nodeTable;
	private HeaderInfo headerInfo;
	
	public void setSelection(TreeSelectionI sel) {
		if (selection != null) {
			selection.deleteObserver(this);	
		}
		selection = sel;
		selection.addObserver(this);
		if (selection != null) {
			update(selection, null);
		}
	}	
	/**
	 * display table representing headerinfo contents.
	 */
	private class NodeTableModel extends AbstractTableModel {

		public int getRowCount() {
			return headerInfo.getNumHeaders();
		}

		public String getColumnName(int i) {
			return headerInfo.getNames()[i];
		}
		public int getColumnCount() {
			return headerInfo.getNumNames();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return headerInfo.getHeader(rowIndex, columnIndex);
		}
		public void setValueAt(Object val, int row, int col) {
			headerInfo.setHeader(row, headerInfo.getNames()[col], (String) val);
		}
		public boolean isCellEditable(int row, int col) {
			String [] names = headerInfo.getNames();
			if (names[col].equals("NODEID")) return false;
			if (names[col].equals("LEFT")) return false;
			if (names[col].equals("RIGHT")) return false;
			if (names[col].equals("CORRELATION")) return false;
			return true;
		}
	}
	/**
	 * @param nodeInfo
	 */
	public TableNodeView(HeaderInfo nodeInfo) {
		headerInfo = nodeInfo;
		headerInfo.addObserver(this);
		tableModel = new NodeTableModel();
		nodeTable = new JTable(tableModel);
		nodeTable.getSelectionModel().addListSelectionListener(this);
		nodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setLayout(new BorderLayout());
		add(new JScrollPane(nodeTable), BorderLayout.CENTER);
	}
	public String viewName() {
		return "Tree Node Editor";
	}

	protected void updateBuffer(Graphics g) {
		// no buffer here.
	}
	public void update(Observable o, Object arg) {
		update((Object) o, arg);
	}
	public void update(Object o, Object arg) {

		if (o == selection) {
			nodeTable.clearSelection();
			String nodeName = selection.getSelectedNode();
			if (nodeName != null) {
				int index = headerInfo.getHeaderIndex(nodeName);
				if (index >= 0) {
					nodeTable.changeSelection(index,0,false, false);
					nodeTable.changeSelection(index,headerInfo.getNumNames(),false, true);
				}
			}
			if (trView != null) {
				trView.scrollToNode(nodeName);
			}
		} else if (o == headerInfo) {
			// dumb table model, doesn't keep things selected.
			int index = nodeTable.getSelectedRow();
			tableModel.fireTableStructureChanged();
			nodeTable.changeSelection(index,0,false, false);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		int row = nodeTable.getSelectedRow();
		if (row >= 0) {
			String name = headerInfo.getHeader(row, 0);
			selection.setSelectedNode(name);
			selection.notifyObservers();
		}
	}
	GTRView trView = null;
	/**
	 * @param map to scroll when selection changes.
	 */
	public void setTree(GTRView globalYmap) {
		trView= globalYmap;
	}

}
