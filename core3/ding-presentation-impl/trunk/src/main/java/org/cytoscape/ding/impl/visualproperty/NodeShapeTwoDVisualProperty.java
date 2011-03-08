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
package org.cytoscape.ding.impl.visualproperty;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.cytoscape.ding.NodeShape;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.DiscreteRangeImpl;
import org.cytoscape.view.model.Range;

public class NodeShapeTwoDVisualProperty extends
		AbstractVisualProperty<NodeShape> {

	private static final Range<NodeShape> NODE_SHAPE_RANGE;
	
	/** key -> valid_cytoscape_key */
	private static final Map<String, String> shapeKeys = new Hashtable<String, String>();

	static {
		final Set<NodeShape> shapeSet = new HashSet<NodeShape>();
		for (final NodeShape shape : NodeShape.values())
			shapeSet.add(shape);
		NODE_SHAPE_RANGE = new DiscreteRangeImpl<NodeShape>(NodeShape.class,
				shapeSet);
		
		// Let's be nice and also support regular and Cytoscape XGMML shapes?
		shapeKeys.put("SQUARE", "RECT");
		shapeKeys.put("RECTANGLE", "RECT");
		shapeKeys.put("BOX", "RECT");
		shapeKeys.put("ROUNDRECT", "ROUND_RECT");
		shapeKeys.put("ROUND_RECTANGLE", "ROUND_RECT");
		shapeKeys.put("RHOMBUS", "PARALLELOGRAM");
		shapeKeys.put("V", "VEE");
		shapeKeys.put("CIRCLE", "ELLIPSE");
		shapeKeys.put("VER_ELLIPSIS", "ELLIPSE");
		shapeKeys.put("HOR_ELLIPSIS", "ELLIPSE");
	}

	public NodeShapeTwoDVisualProperty(final NodeShape def, final String id,
			final String name) {
		super(def, NODE_SHAPE_RANGE, id, name, CyNode.class);
	}

	public String toSerializableString(final NodeShape value) {
		return value.toString();
	}

	public NodeShape parseSerializableString(final String text) {
		NodeShape shape = null;

		if (text != null) {
			String key = text.trim().toUpperCase();
			String validKey = shapeKeys.get(key);
			
			if (validKey == null)
				validKey = key;
			
			shape = NodeShape.valueOf(validKey);
		}

		return shape;
	}
}
