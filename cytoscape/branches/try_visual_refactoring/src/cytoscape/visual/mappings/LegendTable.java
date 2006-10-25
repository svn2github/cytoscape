

/*
  File: LegendTable.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
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

package cytoscape.visual.mappings;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Component;

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.ui.IconSupport;
import cytoscape.visual.ui.VizMapUI;

public class LegendTable extends JPanel {


	public LegendTable(Object[][] data, Object[] col,byte b) {
		super();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.white);

		JTable jTable1 = new JTable();

		jTable1.setModel(new DefaultTableModel(data, col));
		jTable1.setGridColor(Color.white);
		jTable1.setIntercellSpacing(new Dimension(2, 2));
		jTable1.setDefaultRenderer(Object.class, new LegendTableCellRenderer(b));
		jTable1.setRowHeight(50);

		add(jTable1);
	}
}

class LegendTableCellRenderer extends DefaultTableCellRenderer {

	private Font normalFont; 
	private byte type;

	public LegendTableCellRenderer(byte b) {
		super();
		type = b;
		setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

		setOpaque(true);
		normalFont = getFont();
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		// initialize everything
		//setHorizontalAlignment(CENTER);
		setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		setText("");
		setIcon(null);
		setBackground(Color.WHITE);
		setForeground(table.getForeground());
		setFont( normalFont );

		// now make column specific changes
		if (column == 1) {
			setText((value == null) ? "" : value.toString());
		} else if (column == 0) {
			if (value instanceof Byte) {
				ImageIcon i = getIcon(value);
				//table.setRowHeight( row, i.getIconHeight() );	
				setIcon( i );
			} else if (value instanceof LineType) {
				ImageIcon i = getIcon(value);
				//table.setRowHeight( row, i.getIconHeight() );	
				setIcon( i );
			} else if (value instanceof Arrow) {
				ImageIcon i = getIcon(value);
				//table.setRowHeight( row, i.getIconHeight() );	
				setIcon( i );
			} else if (value instanceof Color) {
				//setBackground((Color) value);
				setIcon( IconSupport.getColorIcon((Color)value) );
			} else if (value instanceof Font) {
				Font f = (Font) value;
				setFont(f);
				setText(f.getFontName());
			} else if (value instanceof Double) {
				if ( type == VizMapUI.NODE_SIZE )
					setIcon( IconSupport.getNodeSizeIcon((Double)value) );
				else if ( type == VizMapUI.NODE_WIDTH )
					setIcon( IconSupport.getNodeWidthIcon((Double)value) );
				else if ( type == VizMapUI.NODE_HEIGHT )
					setIcon( IconSupport.getNodeHeightIcon((Double)value) );
			} else if (value instanceof LabelPosition) {
					/*
					setIcon( IconSupport.getLabelPositionIcon(value) );
					*/
					setText(value.toString()); // presumably a string or size 
			} else { 
					setText(value.toString()); // presumably a string or size 
			}
		}
		return this;
	}

	private ImageIcon getIcon(Object o) {
		
		if ( o == null )
			return null;

		IconSupport is = new IconSupport(o);
		return is.getCurrentIcon(); 
	}
}
