/*
 File: BypassHelper.java 
 
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
/*
  File: BypassHelper.java

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

// BypassHelper.java
package cytoscape.visual;

import giny.view.Label;

import java.awt.Color;
import java.awt.Font;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

public class BypassHelperTest extends TestCase {
	
	CyNode homer;
	CyNode marge;
	CyEdge lisa;
	CyAttributes nodeAttrs;
	CyAttributes edgeAttrs;
	String id;

	public BypassHelperTest(String name) {
		super(name);
	}

	public void setUp() {

		homer = Cytoscape.getCyNode("homer", true);
		marge = Cytoscape.getCyNode("marge", true);
		lisa = Cytoscape.getCyEdge(homer, marge, Semantics.INTERACTION, "pp",
				true);
		nodeAttrs = Cytoscape.getNodeAttributes();
		edgeAttrs = Cytoscape.getEdgeAttributes();
		id = homer.getIdentifier();

		nodeAttrs.setAttribute(id, "node.fillColor", "25,31,244");
		nodeAttrs.setAttribute(id, "node.borderColor", "junk");
		nodeAttrs.setAttribute(id, "node.size", "22.0");
		nodeAttrs.setAttribute(id, "node.labelPosition", "NE,W,c,13,-1");
		nodeAttrs.setAttribute(id, "node.font", "SansSerif,italic,10");
		nodeAttrs.setAttribute(id, "node.lineType", "dashed2");
		nodeAttrs.setAttribute(id, "node.shape", "DIAMOND");

		edgeAttrs.setAttribute(lisa.getIdentifier(), "edge.sourceArrow",
				"WHITE_DELTA");
	}

	public void testGetBypass() {

		Object o = null;

		o = BypassHelper
				.getBypass(nodeAttrs, id, "node.fillColor", Color.class);
		System.out.println("id " + id);
		System.out.println("o " + o.toString());
		assertTrue("color equals", ((Color) o).equals(new Color(25, 31, 244)));

		o = BypassHelper.getBypass(nodeAttrs, id, "node.size", Double.class);
		assertEquals("size equals", 22.0, ((Double) o).doubleValue(), 0.0001);

		o = BypassHelper.getBypass(nodeAttrs, id, "node.labelPosition",
				LabelPosition.class);
		System.out.println("lab pos " + o.toString());
		LabelPosition nlp = new LabelPosition(Label.NORTHEAST, Label.WEST,
				Label.JUSTIFY_CENTER, 13, -1);
		System.out.println("new lab pos " + nlp.toString());
		assertTrue("label position equals", nlp.equals((LabelPosition) o));

		o = BypassHelper.getBypass(nodeAttrs, id, "node.font", Font.class);
		assertTrue("font equals", (new Font("SansSerif", Font.ITALIC, 10))
				.equals((Font) o));

		o = BypassHelper.getBypass(nodeAttrs, id, "node.lineType",
				LineType.class);
		assertTrue("linetype equals", LineType.DASHED_2.equals((LineType) o));

		o = BypassHelper.getBypass(nodeAttrs, id, "node.shape", Byte.class);
		assertEquals("shape equals", (byte) 7, ((Byte) o).byteValue());

		// o =
		// BypassHelper.getBypass(edgeAttrs,lisa.getIdentifier(),"edge.sourceArrow",Arrow.class);
		// assertTrue("arrow equals",Arrow.WHITE_DELTA == (Arrow)o);

		o = BypassHelper.getBypass(edgeAttrs, lisa.getIdentifier(),
				"edge.sourceArrow", Arrow.class);
		assertTrue("arrow equals", ArrowShape.DELTA == ((Arrow) o).getShape());

	}

	public void testGetColorBypass() {
		Color c = BypassHelper.getColorBypass(nodeAttrs, id, "node.fillColor");
		assertTrue("color equals", c.equals(new Color(25, 31, 244)));

		c = BypassHelper.getColorBypass(nodeAttrs, id, "node.borderColor");
		System.out.print("should be null: ");
		System.out.println(c);
		assertNull("border color null ", c);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(BypassHelperTest.class));
	}
}
