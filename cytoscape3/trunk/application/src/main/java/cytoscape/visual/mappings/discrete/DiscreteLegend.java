/*
  File: DiscreteLegend.java

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
package cytoscape.visual.mappings.discrete;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import org.jdesktop.swingx.border.DropShadowBorder;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.mappings.LegendTable;


/**
 *
 */
public class DiscreteLegend extends JPanel {
	private final static long serialVersionUID = 1202339875908701L;
	
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 14);
	private static final Font TITLE_FONT2 = new Font("SansSerif", Font.BOLD, 18);
	private static final Color TITLE_COLOR = new Color(10, 200, 255);
	private static final Border BORDER = new MatteBorder(0, 6, 3, 0, Color.DARK_GRAY);
	
	/**
	 * Creates a new DiscreteLegend object.
	 *
	 * @param legendMap  DOCUMENT ME!
	 * @param dataAttr  DOCUMENT ME!
	 * @param vpt  DOCUMENT ME!
	 */
	public DiscreteLegend(Map legendMap, String dataAttr, VisualPropertyType vpt) {
		super();

		setLayout(new BorderLayout());
		setBackground(Color.white);
		setBorder(BORDER);

		final JLabel title = new JLabel(" " + vpt.getName() + " Mapping");
		title.setFont(TITLE_FONT2);
		title.setForeground(TITLE_COLOR);
		title.setBorder(new MatteBorder(0, 10, 1, 0, TITLE_COLOR));
//		title.setHorizontalAlignment(SwingConstants.CENTER);
//		title.setVerticalAlignment(SwingConstants.CENTER);
		title.setHorizontalTextPosition(SwingConstants.LEADING);
//		title.setVerticalTextPosition(SwingConstants.CENTER);
		
		title.setPreferredSize(new Dimension(1, 50));
		add(title, BorderLayout.NORTH);

		/*
		 * Build Key array.
		 */
		final Object[][] data = new Object[legendMap.keySet().size()][2];
		final Iterator it = legendMap.keySet().iterator();

		for (int i = 0; i < legendMap.keySet().size(); i++) {
			Object key = it.next();
			data[i][0] = legendMap.get(key);
			data[i][1] = key;
		}

		add(LegendTable.getHeader(dataAttr, vpt), BorderLayout.CENTER);
		add(new LegendTable(data, vpt), BorderLayout.SOUTH);
	}
}
