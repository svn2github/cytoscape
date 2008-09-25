
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.view.model;

/**
 * Used to define the position of a label relative to 
 * a target object.
 * <p>
 * We should probably have get X offset and get Y offset as well.  Ugh.
 */
public interface LabelPosition extends Saveable {
	/**
	 * This identifies where on the target object the label will be drawn.
	 *
	 * @return The {@link AnchorLocation} on the target object.
	 */
	public AnchorLocation getTargetAnchor();

	/**
	 * This identifies where label will be drawn relative to to the label's bounding box
	 * once the  target {@link AnchorLocation} has been identified.
	 *
	 * @return The {@link AnchorLocation} on the label bounding box.
	 */
	public AnchorLocation getLabelAnchor();

	/**
	 * This identifies how the text should be justified within the bounding box that the
	 * label is drawn in.
	 *
	 * @return The {@link LabelJustify} that defines how the label will be drawn within it's
	 *         bounding box.
	 */
	public LabelJustify getJustify();
}
