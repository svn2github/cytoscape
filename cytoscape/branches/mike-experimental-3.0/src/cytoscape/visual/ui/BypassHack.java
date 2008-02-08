
/*
 File: BypassHack.java 

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
package cytoscape.visual.ui;

import cytoscape.GraphObject; 

/**
 * ARgh.  This is a horrible, horrible hack that allows LabelPosition to
 * be set dynamically for vizmap bypass as you drag the icon around in
 * the label position dialog.  This class provides access to the current
 * node (or edge) that has had it's context menu clicked.  This class allows
 * the dialog to get the node then set the bypass value as things move rather
 * than waiting for the user to click OK. This could be used by other 
 * VisualPropertyTypes in a similar fashion, but currently isn't.
 * <b>This code should NEVER propagate to newer versions of Cytoscape!!!</b>
 * This should be handled in a completely different way in the future.
 */
class BypassHack {
	private static GraphObject curr = null;

	static void setCurrentObject(GraphObject o) {
		curr = o;
	}

	static GraphObject getCurrentObject() {
		return curr;
	}

	static void finished() {
		curr = null;
	}
}
