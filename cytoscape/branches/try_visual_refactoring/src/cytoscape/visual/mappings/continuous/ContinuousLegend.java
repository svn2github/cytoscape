
/*
  File: ContinuousUI.java 
  
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

package cytoscape.visual.mappings.continuous;

import cytoscape.CyNetwork;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.*;
import cytoscape.visual.mappings.LegendTable;
import cytoscape.visual.ui.ValueDisplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ContinuousLegend extends JPanel {

	List points;
	Object obj; 

	int width = 20;
	int height = 40;
	int yoff = height;
	int xoff = 100;

	public ContinuousLegend(String visualAttr, String dataAttr, List points,Object obj) {
		super();
		this.points = points;
		this.obj = obj;
        	setBackground(Color.white);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

	        add(new JLabel(visualAttr + " is continuously mapped to " + dataAttr));
		if ( obj instanceof Color ) 
	        	add( new ColorPanel() );
		else 
	        	add( new ObjectPanel() );

	}
	

	private class ColorPanel extends JPanel {

		public ColorPanel() {
			setPreferredSize(new Dimension(xoff*3,height*(points.size()+1)));
        		setBackground(Color.white);
		}

		public void paint(Graphics g) {
			
			super.paint(g);
			Graphics2D g2 = (Graphics2D)g;

			ContinuousMappingPoint curr = null;
			ContinuousMappingPoint next = null;

			Rectangle rect = new Rectangle(0,0,width,height);

			for (int i = 0; i < points.size(); i++) {

				curr = (ContinuousMappingPoint)points.get(i);

				g2.setPaint(Color.black);
				g2.drawString(Double.toString(curr.getValue()), xoff+width+width, yoff+(i*height)+3);

				if ( i+1 < points.size() ) 
					next = (ContinuousMappingPoint)points.get(i+1); 
				else
					next = null;

				if ( i == 0 ) {
					g2.setPaint( (Color)(curr.getRange().lesserValue) ); 
					rect.setBounds(xoff,yoff-height,width,height);
					g2.fill(rect);
				} 

				if ( next != null ) {
					GradientPaint gp = new GradientPaint(xoff,yoff+(i*height), (Color)curr.getRange().equalValue,xoff,yoff+((i+1)*height),(Color)next.getRange().equalValue);		
					g2.setPaint(gp);
					rect.setBounds(xoff,yoff+(i*height),width,height);
					g2.fill(rect);
				} else {
					g2.setPaint((Color)(curr.getRange().greaterValue));
					rect.setBounds(xoff,yoff+(i*height),width,height);
					g2.fill(rect);
				}
			}
		}
	}

	private class ObjectPanel extends JPanel {
		public ObjectPanel() {
        		setBackground(Color.white);

			Object[][] data = new Object[points.size()+2][2];
			Object[] columnNames = new Object[2];

			columnNames[0] = "Visual Representation";
			columnNames[1] = "Attribute Value";

			ContinuousMappingPoint curr = null;

			for ( int i = 0; i < points.size(); i++ ) {

				curr = (ContinuousMappingPoint)points.get(i);

				if ( i == 0 ) {
					data[i][0] = curr.getRange().lesserValue; 
					data[i][1] = "< " + Double.toString(curr.getValue());
				}

				data[i+1][0] = curr.getRange().equalValue;
				data[i+1][1] = "= " + Double.toString(curr.getValue());

				if ( i == points.size()-1 ) {
					data[i+2][0] = curr.getRange().greaterValue; 
					data[i+2][1] = "> "+ Double.toString(curr.getValue());
				}
			}

			add( new LegendTable(data,columnNames) );
		}
	}
}

