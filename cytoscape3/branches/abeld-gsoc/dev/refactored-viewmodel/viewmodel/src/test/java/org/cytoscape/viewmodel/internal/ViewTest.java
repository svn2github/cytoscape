

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

package org.cytoscape.viewmodel.internal;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.viewmodel.*;
import org.cytoscape.viewmodel.internal.*;

import java.util.*;
import java.awt.Color;


public class ViewTest extends TestCase {

	View<String> view;
	VisualProperty<Color> nodeColor;
	VisualProperty<Double> edgeWidth;

	public void setUp() {
		view = new RowOrientedViewImpl("homer");
		nodeColor = new NodeColorVisualProperty();
		edgeWidth = new EdgeWidthVisualProperty();
	}

	public void tearDown() {
		view = null;
		nodeColor = null;
		edgeWidth = null;
	}


	public void testGetSource() {
		assertTrue( view.getSource().equals("homer") );
	}

	public void testGetDefaultVisualProperty() {
		Color c = view.getVisualProperty(nodeColor);
		assertEquals( "expect color blue", Color.BLUE, c );
	}

	public void testSetVisualProperty() {
		view.setVisualProperty(nodeColor,Color.RED);
		Color c = view.getVisualProperty(nodeColor);
		assertEquals( "expect color red",  Color.RED, c );
	}

	public void testSetValueLock() {
		view.setVisualProperty(nodeColor,Color.GREEN);
		Color c = view.getVisualProperty(nodeColor);
		assertEquals( "expect color green",  Color.GREEN, c );

		view.setValueLock(nodeColor, true);
		assertTrue( view.isValueLocked(nodeColor) );
		assertFalse( view.isValueLocked(edgeWidth) );

		view.setVisualProperty(nodeColor,Color.RED);
		c = view.getVisualProperty(nodeColor);
		assertEquals( "expect color green",  Color.GREEN, c );
		
		view.setValueLock(nodeColor, false);
		assertFalse( view.isValueLocked(nodeColor) );
		assertFalse( view.isValueLocked(edgeWidth) );

		view.setVisualProperty(nodeColor,Color.RED);
		c = view.getVisualProperty(nodeColor);
		assertEquals( "expect color red",  Color.RED, c );
	}
}
