/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.vizmap;

import org.cytoscape.vizmap.icon.NodeIcon;
import org.cytoscape.vizmap.icon.VisualPropertyIcon;

import org.cytoscape.view.ShapeFactory;

import org.cytoscape.view.NodeView;
import org.cytoscape.view.renderers.ShapeRenderer;
import org.cytoscape.view.renderers.NodeRenderer;

import java.awt.Shape;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

/**
 * This is a replacement for ShapeNodeRealizer.java
 *
 * @since Cytoscape 2.5
 * @version 0.7
 * @author kono
 *
 */
public enum NodeRenderers {
    SIMPLE_SHAPE(new ShapeRenderer("simple shape"), "simple shape"),
    SIMPLE_SHAPE2(new ShapeRenderer("simple shape2"), "simple shape2");
    
	private NodeRenderer nodeRenderer;
	private String name;
	private static Map<Integer, Shape> nodeShapes = ShapeFactory.getNodeShapes();

	private NodeRenderers(NodeRenderer renderer, String name) {
		this.nodeRenderer = renderer;
		this.name = name;
	}

	public NodeRenderer getRenderer(){
		return nodeRenderer;
	}
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static String[] valuesAsString() {
		final int length = values().length;
		final String[] nameArray = new String[length];

		for (int i = 0; i < length; i++)
			nameArray[i] = values()[i].getRendererName();

		return nameArray;
	}

	/**
	 * Get name of the shape.
	 *
	 * @return DOCUMENT ME!
	 */
	public String getRendererName() {
		return name;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Map<Object, Icon> getIconSet() {
		Map<Object, Icon> nodeShapeIcons = new HashMap<Object, Icon>();

		for (NodeRenderers shape : values()) {
			NodeIcon icon = new NodeIcon(nodeShapes.get(1),
			                             VisualPropertyIcon.DEFAULT_ICON_SIZE,
			                             VisualPropertyIcon.DEFAULT_ICON_SIZE, shape.getRendererName());
			nodeShapeIcons.put(shape, icon);
		}

		return nodeShapeIcons;
	}
}
