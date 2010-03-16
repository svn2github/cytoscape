/*
 File: GlobalAppearance.java

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import java.awt.Color;


/**
 * Objects of this class hold data describing global appearance attributes of
 * the graph window.
 */
public class GlobalAppearance {
	
	private enum GlobalAppearenceName {
		BACKGROUND_COLOR("Background Color"),
		NODE_SELECTION_COLOR("Node Selection Color"),
		NODE_REVERSE_SELECTION_COLOR("Node Reverse Selection Color"),
		EDGE_SELECTION_COLOR("Edge Selection Color"),
		EDGE_REVERSE_SELECTION_COLOR("Edge Reverse Selection Color");

		private String name;
		private static String[] names;

		static {
			names = new String[GlobalAppearenceName.values().length];
			int i = 0;
			for(GlobalAppearenceName ganame: GlobalAppearenceName.values()) {
				names[i] = ganame.getName();
				i++;
			}
		}
		
		private GlobalAppearenceName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public static String[] getAllNames() {
			return names;
		}
	}

	private Color backgroundColor;
	@Deprecated
	private Color sloppySelectionColor;
	private Color nodeSelectionColor;
	private Color nodeReverseSelectionColor;
	private Color edgeSelectionColor;
	private Color edgeReverseSelectionColor;

	/**
	 * Creates a new GlobalAppearance object.
	 */
	public GlobalAppearance() {
	}
	
	protected static String[] getCalculatorNames() {
		return GlobalAppearenceName.getAllNames();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setBackgroundColor(Color c) {
		backgroundColor = c;
	}

	/**
	 *  Do not use this.
	 *
	 * @return  DOCUMENT ME!
	 * @deprecated Will be removed 5/2008
	 */
	@Deprecated
	public Color getSloppySelectionColor() {
		return sloppySelectionColor;
	}

	/**
	 *  Do not use this.
	 *
	 * @param c DOCUMENT ME!
	 * @deprecated Will be removed 5/2008
	 */
	@Deprecated
	public void setSloppySelectionColor(Color c) {
		sloppySelectionColor = c;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getNodeSelectionColor() {
		return nodeSelectionColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setNodeSelectionColor(Color c) {
		nodeSelectionColor = c;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getNodeReverseSelectionColor() {
		return nodeReverseSelectionColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setNodeReverseSelectionColor(Color c) {
		nodeReverseSelectionColor = c;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getEdgeSelectionColor() {
		return edgeSelectionColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setEdgeSelectionColor(Color c) {
		edgeSelectionColor = c;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getEdgeReverseSelectionColor() {
		return edgeReverseSelectionColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setEdgeReverseSelectionColor(Color c) {
		edgeReverseSelectionColor = c;
	}
}
