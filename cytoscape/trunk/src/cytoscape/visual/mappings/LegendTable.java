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

import cytoscape.visual.Arrow;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.LineType;
import static cytoscape.visual.VisualPropertyType.NODE_HEIGHT;
import static cytoscape.visual.VisualPropertyType.NODE_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_WIDTH;

import cytoscape.visual.ui.IconSupport;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class LegendTable extends JPanel {
    private byte type;

    /**
     * Creates a new LegendTable object.
     *
     * @param data DOCUMENT ME!
     * @param b DOCUMENT ME!
     */
    public LegendTable(Object[][] data, byte b) {
        super();
        type = b;

        setLayout(new GridLayout(data.length, data[0].length, 4, 4));
        setBackground(Color.white);
        setAlignmentX(0);

        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                add(getValue(data[i][j]));
    }

    private JComponent getValue(final Object value) {
        JComponent component = null;

        if (value instanceof Byte) {
            final ImageIcon i = getIcon(value);
            component = new JLabel(i);
        } else if (value instanceof LineType) {
            final ImageIcon i = getIcon(value);
            component = new JLabel(i);
        } else if (value instanceof Arrow) {
            final ImageIcon i = getIcon(value);
            component = new JLabel(i);
        } else if (value instanceof Color)
            component = new JLabel(IconSupport.getColorIcon((Color) value));
        else if (value instanceof Font) {
            final Font f = (Font) value;
            final JLabel lab = new JLabel();
            lab.setText(f.getFontName());
            lab.setFont(f);
            component = lab;
        } else if (value instanceof Double) {
            if (type == NODE_SIZE.getType())
                component = new JLabel(
                        IconSupport.getNodeSizeIcon((Double) value));
            else if (type == NODE_WIDTH.getType())
                component = new JLabel(
                        IconSupport.getNodeWidthIcon((Double) value));
            else if (type == NODE_HEIGHT.getType())
                component = new JLabel(
                        IconSupport.getNodeHeightIcon((Double) value));
        } else if (value instanceof LabelPosition)
            component = new JLabel(
                    IconSupport.getLabelPositionIcon((LabelPosition) value));
        else
            component = new JLabel(value.toString());

        component.setAlignmentX(0);

        return component;
    }

    private ImageIcon getIcon(final Object o) {
        if (o == null)
            return null;

        final IconSupport is = new IconSupport(o);

        return is.getCurrentIcon();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static JPanel getHeader() {
        final JPanel titles = new JPanel();

        titles.setLayout(new GridLayout(1, 2));
        titles.setAlignmentX(0);
        titles.setBackground(Color.white);

        titles.add(new JLabel("Visual Representation"));
        titles.add(new JLabel("Attribute Value"));

        return titles;
    }
}
