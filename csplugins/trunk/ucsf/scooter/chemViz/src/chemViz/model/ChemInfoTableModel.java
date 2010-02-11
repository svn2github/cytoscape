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

package chemViz.model;

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
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
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
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import chemViz.model.Compound;
import chemViz.model.Compound.DescriptorType;
import chemViz.model.TableSorter;


public class ChemInfoTableModel extends AbstractTableModel {
	List<CompoundColumn> columns;
	List<Compound> compoundList = null;

	public ChemInfoTableModel(List<Compound> cList) {
		super();
		columns = new ArrayList();
		compoundList = cList;
	}

	public boolean hasNodes() { return columns.get(0).hasNodes(); }
	public boolean hasEdges() { return columns.get(0).hasEdges(); }

	public void setCompoundList(List<Compound> cList) { compoundList = cList; }
	public List<Compound> getCompoundList() { return compoundList; }

	public void addColumn(int columnNumber, CompoundColumn column) {
		columns.add(columnNumber, column);
		fireTableStructureChanged();
	}

	public void removeColumn(int columnNumber) {
		columns.remove(columnNumber);
		fireTableStructureChanged();
	}

	public void removeColumn(CompoundColumn column) {
		columns.remove(column);
		fireTableStructureChanged();
	}

	public int getColumnCount() { return columns.size(); }
	public int getRowCount() { return compoundList.size(); }

	public String getColumnName(int columnIndex) {
		CompoundColumn column = columns.get(columnIndex);
		return column.getColumnName();
	}

	public Class getColumnClass(int columnIndex) {
		CompoundColumn column = columns.get(columnIndex);
		return column.getColumnClass();
	}
	
	public Object getValueAt(int row, int col) {
		Compound cmpd = compoundList.get(row);
		CompoundColumn column = columns.get(col);
		return column.getValue(cmpd);
	}

	public CompoundColumn getColumnAt(int columnIndex) {
		return columns.get(columnIndex);
	}
}
