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

import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.DiscreteRangeImpl;
import org.cytoscape.view.model.Range;

public class FontTwoDVisualProperty extends AbstractVisualProperty<Font> {

	private static final Range<Font> FONT_RANGE;

	static {
		final Set<Font> fontSet = new HashSet<Font>();
		//TODO: register all available system fonts here.
		FONT_RANGE = new DiscreteRangeImpl<Font>(Font.class,
				fontSet);
	}

	public FontTwoDVisualProperty(final Font def, final String id,
			final String name, final Class<?> targetDataType) {
		super(def, FONT_RANGE, id, name, targetDataType);
	}

	public String toSerializableString(final Font value) {
		// TODO:
		return value.toString();
	}

	public Font parseSerializableString(final String text) {
		Font font = null;
		
		if (text != null) {
			// e.g. "Monospaced,plain,12"
            String name = text.replaceAll("(\\.[bB]old)?,[a-zA-Z]+,\\d+(\\.\\d+)?", "");

            boolean bold = text.matches("(?i).*\\.bold,[a-zA-Z]+,.*");
            int style = bold ? Font.BOLD : Font.PLAIN;
            int size = 12;

            String sSize = text.replaceAll(".+,[^,]+,", "");
            
            try {
                size = Integer.parseInt(sSize);
            } catch (NumberFormatException nfe) {
                // TODO: log/warning
            }

            font = new Font(name, style, size);
        }
		
		return font;
	}

	private static Set<Font> getSystemFonts() {
		//TODO: implement this.
		final Set<Font> fontSet = new HashSet<Font>();
		return fontSet;
	}
}
