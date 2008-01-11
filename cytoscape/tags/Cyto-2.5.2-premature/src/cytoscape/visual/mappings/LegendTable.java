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

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.ui.icon.VisualPropertyIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;


/**
 * DOCUMENT ME!0
1 *
 * @author $author$
  */
public class LegendTable extends JPanel {
	private static VisualPropertyType type;
	private JTable legendTable;

	/**
	 * Creates a new LegendTable object.
	 *
	 * @param data DOCUMENT ME!
	 * @param b DOCUMENT ME!
	 * @deprecated Use VisualPropertyType constructor instead. Gone 5/2008.
	 */
	@Deprecated
	public LegendTable(Object[][] data, byte b) {
		this(data, VisualPropertyType.getVisualPorpertyType(b));
	}

	/**
	 * Creates a new LegendTable object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param vpt  DOCUMENT ME!
	 */
	public LegendTable(Object[][] data, VisualPropertyType vpt) {
		super();
		legendTable = new JTable(data.length, 2);
		legendTable.setRowHeight(50);
		legendTable.setDefaultRenderer(Object.class, (TableCellRenderer) new LegendCellRenderer());
		type = vpt;
		setLayout(new BorderLayout());

		Object value = null;

		for (int i = 0; i < data.length; i++) {
			value = getValue(data[i][0]);

			if (value != null) {
				legendTable.getModel().setValueAt(value, i, 0);
			}

			legendTable.getModel().setValueAt(data[i][1], i, 1);
		}

		add(legendTable, SwingConstants.CENTER);
	}

	private Object getValue(final Object value) {
		final VisualPropertyIcon icon;

		if (value == null) {
			return null;
		}

		icon = (VisualPropertyIcon) type.getVisualProperty().getIcon(value);
		icon.setLeftPadding(5);

		return icon;

		//		if (value instanceof Byte || value instanceof NodeShape) {
		//			component = new JLabel(type.getVisualProperty().getIcon(value));
		//		} else if (value instanceof LineType) {
		//			i = getIcon(value);
		//			component = new JLabel(i);
		//		} else if (value instanceof Arrow) {
		//			i = getIcon(value);
		//			component = new JLabel(i);
		//		} else if (value instanceof Color) {
		//			i = type.getVisualProperty().getIcon(value);
		//			component = new JLabel(i);
		//		} else if (value instanceof Font) {
		//			final Font f = (Font) value;
		//			final JLabel lab = new JLabel();
		//			lab.setText(f.getFontName());
		//			lab.setFont(f);
		//			component = lab;
		//		} else if (value instanceof Double) {
		//			if (type == NODE_SIZE)
		//				component = new JLabel(IconSupport.getNodeSizeIcon((Double) value));
		//			else if (type == NODE_WIDTH)
		//				component = new JLabel(IconSupport.getNodeWidthIcon((Double) value));
		//			else if (type == NODE_HEIGHT)
		//				component = new JLabel(IconSupport.getNodeHeightIcon((Double) value));
		//			else if (type == NODE_OPACITY) {
		//				component = new JLabel(type.getVisualProperty().getDefaultIcon());
		//			}
		//		} else if (value instanceof LabelPosition)
		//			component = new JLabel(IconSupport.getLabelPositionIcon((LabelPosition) value));
		//		else
		//			component = new JLabel(value.toString());
		//
		//		if (component == null) {
		//			return null;
		//		}
		//
		//		component.setAlignmentX(50);
		//		component.setHorizontalAlignment(SwingConstants.CENTER);
		//
		//		component.setPreferredSize(new Dimension(100, 50));
		//
		//		return component;
	}

	//	private ImageIcon getIcon(final Object o) {
	//		if (o == null)
	//			return null;
	//
	//		final IconSupport is = new IconSupport(o);
	//
	//		return is.getCurrentIcon();
	//	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	@Deprecated
	public static JPanel getHeader() {
		final JPanel titles = new JPanel();
		final JLabel[] labels = new JLabel[2];
		labels[0] = new JLabel("Visual");
		labels[1] = new JLabel("Attribute Value");

		for (int i = 0; i < labels.length; i++) {
			labels[i].setVerticalAlignment(SwingConstants.CENTER);
			labels[i].setVerticalTextPosition(SwingConstants.CENTER);
			labels[i].setHorizontalTextPosition(SwingConstants.LEADING);
			labels[i].setForeground(Color.DARK_GRAY);
		}

		titles.setLayout(new GridLayout(1, 2));
		titles.setAlignmentX(0);
		titles.setBackground(Color.white);

		titles.add(labels[0]);
		titles.add(labels[1]);

		return titles;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param attrName DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static JPanel getHeader(String attrName, VisualPropertyType type) {
		final JPanel titles = new JPanel();
		final JLabel[] labels = new JLabel[2];
		labels[0] = new JLabel(type.getName());
		labels[1] = new JLabel(attrName);

		for (int i = 0; i < labels.length; i++) {
			labels[i].setVerticalAlignment(SwingConstants.CENTER);
			labels[i].setHorizontalAlignment(SwingConstants.LEADING);
			labels[i].setVerticalTextPosition(SwingConstants.CENTER);
			labels[i].setHorizontalTextPosition(SwingConstants.LEADING);
			labels[i].setForeground(Color.DARK_GRAY);
			labels[i].setBorder(new EmptyBorder(10, 0, 7, 10));
			labels[i].setFont(new Font("SansSerif", Font.BOLD, 14));
		}

		titles.setLayout(new GridLayout(1, 2));
		titles.setBackground(Color.white);

		titles.add(labels[0]);
		titles.add(labels[1]);
		titles.setBorder(new MatteBorder(0, 0, 1, 0, Color.DARK_GRAY));

		return titles;
	}

	public class LegendCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int column) {
			final JLabel cell = new JLabel();

			if (value instanceof Icon) {
				VisualPropertyIcon icon = (VisualPropertyIcon) value;
				icon.setBottomPadding(0);
				cell.setIcon(icon);

				cell.setVerticalAlignment(SwingConstants.CENTER);
				cell.setHorizontalAlignment(SwingConstants.CENTER);
			} else {
				cell.setText(value.toString());
				cell.setVerticalTextPosition(SwingConstants.CENTER);
				cell.setVerticalAlignment(SwingConstants.CENTER);
				cell.setHorizontalAlignment(SwingConstants.LEADING);
				cell.setHorizontalTextPosition(SwingConstants.LEADING);
			}

			cell.setPreferredSize(new Dimension(170, 1));

			return cell;
		}
	}
}
