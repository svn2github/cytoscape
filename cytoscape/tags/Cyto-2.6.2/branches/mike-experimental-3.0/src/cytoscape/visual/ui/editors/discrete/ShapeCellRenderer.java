
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.visual.ui.editors.discrete;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.ui.icon.VisualPropertyIcon;

import java.awt.Component;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTable;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class ShapeCellRenderer extends DefaultCellRenderer {
	private final static long serialVersionUID = 1202339868999601L;
	private final Map<Object, Icon> icons;
	private VisualPropertyType type;

	/**
	 * Creates a new ShapeCellRenderer object.
	 *
	 * @param type DOCUMENT ME!
	 */
	public ShapeCellRenderer(VisualPropertyType type) {
		this.type = type;
		icons = type.getVisualProperty().getIconSet();
	}

	/**
	     * DOCUMENT ME!
	     *
	     * @param table
	     *            DOCUMENT ME!
	     * @param value
	     *            DOCUMENT ME!
	     * @param isSelected
	     *            DOCUMENT ME!
	     * @param hasFocus
	     *            DOCUMENT ME!
	     * @param row
	     *            DOCUMENT ME!
	     * @param column
	     *            DOCUMENT ME!
	     *
	     * @return DOCUMENT ME!
	     */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
	                                               boolean hasFocus, int row, int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}

		if (value != null) {
			final VisualPropertyIcon shapeIcon = (VisualPropertyIcon) icons.get(value);

			if (shapeIcon != null) {
				if (type.equals(VisualPropertyType.EDGE_SRCARROW_SHAPE)
				    || type.equals(VisualPropertyType.EDGE_TGTARROW_SHAPE)) {
					shapeIcon.setIconHeight(16);
					shapeIcon.setIconWidth(40);
					shapeIcon.setBottomPadding(-6);
				} else {
					shapeIcon.setIconHeight(16);
					shapeIcon.setIconWidth(16);
				}

				this.setIcon(shapeIcon);
			}

			this.setIconTextGap(10);
			this.setText(value.toString());
		} else {
			this.setIcon(null);
			this.setText(null);
		}

		return this;
	}
}
