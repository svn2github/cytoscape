
/*
  File: CytoPanelName.java

  Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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


package cytoscape.view.cytopanels;

import javax.swing.SwingConstants;

/** 
 * An enum that maps names to compass directions.
 */
public enum CytoPanelName {
	NORTH("Top Panel",SwingConstants.NORTH),
	SOUTH("Data Panel",SwingConstants.SOUTH),
	EAST("Results Panel",SwingConstants.EAST),
	WEST("Management Panel",SwingConstants.WEST),
	SOUTH_WEST("Control Panel",SwingConstants.SOUTH_WEST),
	;

	private String title;
	private int compassDirection;

	private CytoPanelName(String title, int compassDirection) {
		this.title = title;
		this.compassDirection = compassDirection;
	}

	public String getTitle() {
		return title;
	}

	public int getCompassDirection() {
		return compassDirection;
	}

	public static String getTitle(int dir) {
		for (CytoPanelName cpn : values()) 
			if ( dir == cpn.getCompassDirection() )
				return cpn.getTitle();
		return null;
	}
}
