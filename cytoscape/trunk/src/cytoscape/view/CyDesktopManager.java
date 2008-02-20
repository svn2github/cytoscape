/*
  File: CyNodeView.java

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
package cytoscape.view;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import java.awt.Dimension;

/**
 *
  */
public class CyDesktopManager extends DefaultDesktopManager  {
	
	public static int ARRANGE_GRID = 0; // TILED
	public static int ARRANGE_CASCADE = 2;
	public static int ARRANGE_HORIZONTAL = 3;
	public static int ARRANGE_VERTICAL = 4;
	protected static JDesktopPane desktop;
	private CyDesktopManager() {
	}
	
	//Minimises all windows that are iconifiable
	public static void minizeWindows() {
		
	}

	// Restore all minimised windows
	public static void restoreWindows() {
		
	}
	
	//Closes all open windows
	public static void closeAllWindows() {
		
	}
	
	//Get the desktop we are managing
	public static JDesktopPane getDesktop() {
		return desktop;
	}

	//Set the desktop we are managing
	public static void setDesktop(JDesktopPane pDesktop) {
		desktop = pDesktop;
	}
	
	// Arrange all windows in the desktop according to the given style
	public static void arrangeFrames(int pStyle) {
		Dimension desktopSize = desktop.getSize();
		
		JInternalFrame[] allFrames = desktop.getAllFrames();
		
		int frameCount = allFrames.length; 
		if ( frameCount == 0) {
			return;
		}

		if (pStyle == CyDesktopManager.ARRANGE_CASCADE) {
			int delta_x = 20;
			int delta_y = 20;
			
			int w = desktopSize.width - delta_x * (frameCount-1);
			int h = desktopSize.height - delta_y *(frameCount-1);
			
			for (int i=0; i<frameCount; i++) {
				// put the newest frame on the top 
				allFrames[frameCount -1-i].setBounds(delta_x * i, delta_y*i, w, h);
			}
		}
		else if (pStyle == CyDesktopManager.ARRANGE_GRID) {
			
			int maxCol = (new Double(Math.ceil(Math.sqrt(frameCount)))).intValue();
			int minCol = (new Double(Math.floor(Math.sqrt(frameCount)))).intValue();
			
			//System.out.println("mincol, maxCol = " + minCol + ","+maxCol);
			
			if (minCol == maxCol) {
				int w = desktopSize.width/minCol;
				int h = desktopSize.height/minCol;
				for (int i=0; i< frameCount; i++) {
					int col = minCol - i/minCol-1;
					int row = minCol-1 - i%minCol;		
					allFrames[frameCount-i-1].setBounds(col*w, row*h, w, h);
				}
			}
			else {
				System.out.println("TODO: Not defined yet");
				
				
				
				
				
				
				
				
				
			}
			
			
			
		}
		else if (pStyle == CyDesktopManager.ARRANGE_HORIZONTAL) {
			
			int x = 0;
			int[] y = new int[frameCount];
			int w = desktopSize.width;
			int h = desktopSize.height/frameCount;
			
			for (int i=0; i< frameCount; i++) {
				y[i] = h * i;
				allFrames[i].setBounds(x, y[i], w, h);
			}
		}
		else if (pStyle == CyDesktopManager.ARRANGE_VERTICAL) {
			int[] x = new int[frameCount];
			int y = 0;
			int w = desktopSize.width/allFrames.length;
			int h = desktopSize.height;
			
			for (int i=0; i< frameCount; i++) {
				x[i] = w * i;
				allFrames[i].setBounds(x[i], y, w, h);
			}
		}
	}
	
}

