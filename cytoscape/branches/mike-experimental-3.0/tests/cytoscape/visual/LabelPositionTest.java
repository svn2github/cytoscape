/*
  File: LabelPositionTest.java

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
package cytoscape.visual;

import org.cytoscape.Edge;
import org.cytoscape.GraphPerspective;
import org.cytoscape.Node;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.readers.CyAttributesReader;

import cytoscape.util.FileUtil;

import cytoscape.visual.*;

import cytoscape.visual.calculators.*;

import cytoscape.visual.mappings.*;

import cytoscape.visual.ui.*;

import org.cytoscape.Edge;
import org.cytoscape.Node;
import org.cytoscape.RootGraph;

import giny.view.Label;

import junit.framework.*;

import java.awt.Color;
import java.awt.Font;

import java.io.*;

import java.util.Map;
import java.util.Properties;


/**
 *
 */
public class LabelPositionTest extends TestCase {
	/**
	 * Creates a new LabelPositionTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public LabelPositionTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testEquals() {
		System.out.println("begin testEquals");

		LabelPosition l1 = new LabelPosition();
		l1.setTargetAnchor(Label.NORTHWEST);
		l1.setLabelAnchor(Label.CENTER);
		l1.setJustify(Label.JUSTIFY_LEFT);
		l1.setOffsetX(17.0);
		l1.setOffsetY(-19.0);

		LabelPosition l2 = new LabelPosition(Label.NORTHWEST, Label.CENTER, Label.JUSTIFY_LEFT,
		                                     17.0, -19.0);
		System.out.println("label 1\n" + l1.toString());
		System.out.println("label 2\n" + l2.toString());
		assertTrue("positions equal", l1.equals(l2));
		assertTrue("position doesn't equal a color", !l1.equals(Color.black));
		assertTrue("position doesn't equal null", !l1.equals(null));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testParse() {
		System.out.println("begin testParse");

		LabelPosition l1 = LabelPosition.parse("C,NW,l,17,-19.0");
		LabelPosition l2 = new LabelPosition(Label.CENTER, Label.NORTHWEST, Label.JUSTIFY_LEFT,
		                                     17.0, -19.0);
		System.out.println("label 1\n" + l1.toString());
		System.out.println("label 2\n" + l2.toString());
		assertTrue("positions equal", l1.equals(l2));

		LabelPosition l3 = LabelPosition.parse("junk");
		assertNull(l3);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(LabelPositionTest.class));
	}
}
