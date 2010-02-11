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

package chemViz.ui.renderers;

import java.awt.Component;
import java.awt.Image;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import giny.model.GraphObject;

import chemViz.model.Compound;
import chemViz.model.Compound.DescriptorType;
import chemViz.model.TableSorter;

public class CompoundRenderer implements TableCellRenderer {
	private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

	private Map<GraphObject,List<Integer>> rowMap;
	private TableSorter sorter;

	public CompoundRenderer(TableSorter sorter, Map<GraphObject,List<Integer>> rm) {
		rowMap = rm;
		this.sorter = sorter;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
	                                        boolean hasFocus, int viewRow, int viewColumn) {

		int row = sorter.modelIndex(viewRow);
		int column = table.convertColumnIndexToModel(viewColumn);

		adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, viewRow, viewColumn);
		Compound c = (Compound)table.getModel().getValueAt(row, column);
		TableColumn clm = table.getColumnModel().getColumn(viewColumn);
		int width = clm.getPreferredWidth();
		if (width != table.getRowHeight())
			table.setRowHeight(width); // Note, this will trigger a repaint!
		Image resizedImage = c.getImage(width,width);
		if (resizedImage == null) return null;
		JLabel l = new JLabel(new ImageIcon(resizedImage));
		if (!rowMap.containsKey(c.getSource())) {
			rowMap.put(c.getSource(), new ArrayList());
		}

		rowMap.get(c.getSource()).add(Integer.valueOf(row));
		l.setBackground(adaptee.getBackground());
		l.setForeground(adaptee.getForeground());
		return l;
	}
}
