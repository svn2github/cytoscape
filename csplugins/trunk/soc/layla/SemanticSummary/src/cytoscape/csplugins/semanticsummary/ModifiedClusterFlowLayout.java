/*
 File: ModifiedFlowLayout.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary;

import java.awt.*;

/**
 * A modified version of FlowLayout that allows containers using this
 * Layout to behave in a reasonable manner when placed inside a
 * JScrollPane
 * 
 * Also modified so that the preferred width is the width of the largest contained
 * component.
   
 * @author Babu Kalakrishnan
 * @author Layla Oesper
 */
public class ModifiedClusterFlowLayout extends FlowLayout {
	  public ModifiedClusterFlowLayout() {
	     super();
	  }

	  public ModifiedClusterFlowLayout(int align) {
	     super(align);
	  }

	  public ModifiedClusterFlowLayout(int align, int hgap, int vgap) {
	     super(align, hgap, vgap);
	  }
	  
	  public Dimension minimumLayoutSize(Container target) {
	     // Size of largest component, so we can resize it in
	     // either direction with something like a split-pane.
	     return computeMinSize(target);
	  }

	  public Dimension preferredLayoutSize(Container target) {
	     return computeSize(target);
	  }

	  private Dimension computeSize(Container target) {
	     synchronized (target.getTreeLock()) {
	        int hgap = getHgap();
	        int vgap = getVgap();
	        //int w = target.getWidth();

	        // Let this behave like a regular FlowLayout (single row)
	        // if the container hasn't been assigned any size yet
	        
	        Insets insets = target.getInsets();
	        if (insets == null)
	           insets = new Insets(0, 0, 0, 0);
	        int reqdWidth = 0;
	        
	        int maxwidth = this.getMaxWidth(target);
	        maxwidth = maxwidth - 2*hgap;
	        
	        if (maxwidth == 0)
	        	maxwidth = Integer.MAX_VALUE - insets.left - insets.right;
	        
	        int n = target.getComponentCount();
	        int x = 0;
	        int y = insets.top + vgap; // FlowLayout starts by adding vgap, so do that here too.
	        int rowHeight = 0;
	        
	        for (int i = 0; i < n; i++) {
	           Component c = target.getComponent(i);
	           if (c.isVisible()) {
	              Dimension d = c.getPreferredSize();
	              if ((x == 0) || ((x + d.width) <= maxwidth)) {
	                 // fits in current row.
	                 if (x > 0) {
	                    x += hgap;
	                 }
	                 x += d.width;
	                 rowHeight = Math.max(rowHeight, d.height);
	              }
	              else {
	                 // Start of new row
	                 x = d.width;
	                 y += vgap + rowHeight;
	                 rowHeight = d.height;
	              }
	              reqdWidth = Math.max(reqdWidth, x);
	           }
	        }
	        y += rowHeight;
	        y += insets.bottom;
	        y += vgap; //add to bottom too
	        return new Dimension(reqdWidth+insets.left+insets.right, y);
	     }
	  }//computeSize

	  private Dimension computeMinSize(Container target) {
	     synchronized (target.getTreeLock()) {
	        int minx = Integer.MAX_VALUE;
	        int miny = Integer.MIN_VALUE;
	        boolean found_one = false;
	        int n = target.getComponentCount();

	        for (int i = 0; i < n; i++) {
	           Component c = target.getComponent(i);
	           if (c.isVisible()) {
	              found_one = true;
	              Dimension d = c.getPreferredSize();
	              minx = Math.min(minx, d.width);
	              miny = Math.min(miny, d.height);
	           }
	        }
	        if (found_one) {
	           return new Dimension(minx, miny);
	        }
	        return new Dimension(0, 0);
	     }
	  }//computeSize
	  
	  
	  private int getMaxWidth(Container target)
	  {
		  int maxWidth = 0;
		  int n = target.getComponentCount();
		  for (int i = 0; i < n; i++) 
		  {
	           Component c = target.getComponent(i);
	           if (c.isVisible()) 
	           {
	        	   int curWidth = c.getPreferredSize().width;
	        	   maxWidth = Math.max(curWidth, maxWidth);
	           }
		  }
		  return maxWidth;
	  }
	  

	}//ModifiedFlowLayout
