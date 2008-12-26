
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

package org.cytoscape.presentation.internal;

import org.cytoscape.presentation.TextNodeRenderer;

import org.cytoscape.viewmodel.Renderer;
import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.VisualProperty;

import java.util.HashSet;
import java.util.Set;


/**
 *
 */
public class TextNodeRendererImpl implements TextNodeRenderer, Renderer {
	private static final VisualProperty<String> nodeLabel = new VisualPropertyImpl<String>("NODE_LABEL",
	                                                                                       "node label (string)",
	                                                                                       "default label",
	                                                                                       String.class,
	                                                                                       VisualProperty.NODE);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String render(final View<?> view) {
		final String label = view.getVisualProperty(nodeLabel);

		return label;
	}

	/**
	 * Returns the Set of VisualPropertys supported by this Renderer.
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set<VisualProperty<?>> getVisualProperties() {
		final Set<VisualProperty<?>> ret = new HashSet<VisualProperty<?>>();
		ret.add(nodeLabel);

		return ret;
	}

	/**
	 * Given a String, returns a VisualProperty object.
	 *
	 * @param s  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualProperty<?> parseVisualProperty(final String s) {
		throw new RuntimeException("can't happen");
	}

	/**
	 * Returns a string suitable for parsing by {Renderer#parseVisualProperty(String s)}.
	 *
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getVisualPropertyString(final VisualProperty<?> vp) {
		throw new RuntimeException("can't happen");
	}
}
