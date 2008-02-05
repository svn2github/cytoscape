/*
  File: NodeAppearanceTest.java

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

// NodeAppearanceTest.java

//----------------------------------------------------------------------------
// $Revision: 8412 $
// $Date: 2006-10-06 13:46:32 -0700 (Fri, 06 Oct 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.readers.CyAttributesReader;

import cytoscape.util.FileUtil;

import cytoscape.visual.*;

import cytoscape.visual.calculators.*;

import cytoscape.visual.mappings.*;

import cytoscape.visual.ui.*;

import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;

import giny.view.NodeView;

//----------------------------------------------------------------------------
import junit.framework.*;

import java.awt.Color;
import java.awt.Font;

import java.io.*;

import java.util.Map;
import java.util.Properties;


// Note that much of NodeAppearance is tested in NodeAppearanceCalculatorTest.
/**
 *
 */
public class NodeAppearanceTest extends TestCase {
	double SLACK = 0.00001;

	/**
	 * Creates a new NodeAppearanceTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public NodeAppearanceTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testNodeSizeLocked() {
		NodeAppearance na = new NodeAppearance();

		// by default size is locked
		assertEquals("node uniform size", 35.0, ((Double)na.get(VisualPropertyType.NODE_SIZE)).doubleValue(), SLACK);
	// TODO
//		assertEquals("node width", 35.0, ((Double)na.get(VisualPropertyType.NODE_WIDTH)).doubleValue(), SLACK);
		assertEquals("node height", 35.0, ((Double)na.get(VisualPropertyType.NODE_HEIGHT)).doubleValue(), SLACK);

		na.setNodeSizeLocked(false);

		assertEquals("node uniform size", 35.0, ((Double)na.get(VisualPropertyType.NODE_SIZE)).doubleValue(), SLACK);
		assertEquals("node width", 70.0, ((Double)na.get(VisualPropertyType.NODE_WIDTH)).doubleValue(), SLACK);
		assertEquals("node height", 30.0, ((Double)na.get(VisualPropertyType.NODE_HEIGHT)).doubleValue(), SLACK);

		na.set(VisualPropertyType.NODE_WIDTH,40.0);

		assertEquals("node uniform size", 35.0, ((Double)na.get(VisualPropertyType.NODE_SIZE)).doubleValue(), SLACK);
		assertEquals("node width", 40.0, ((Double)na.get(VisualPropertyType.NODE_WIDTH)).doubleValue(), SLACK);

		na.set(VisualPropertyType.NODE_HEIGHT,50.0);

		assertEquals("node uniform size", 35.0, ((Double)na.get(VisualPropertyType.NODE_SIZE)).doubleValue(), SLACK);
		assertEquals("node heigth", 50.0, ((Double)na.get(VisualPropertyType.NODE_HEIGHT)).doubleValue(), SLACK);

		na.setNodeSizeLocked(true);

		na.set(VisualPropertyType.NODE_WIDTH,20.0);

		assertEquals("node uniform size", 35.0, ((Double)na.get(VisualPropertyType.NODE_SIZE)).doubleValue(), SLACK);
		assertEquals("node width", 35.0, ((Double)na.get(VisualPropertyType.NODE_WIDTH)).doubleValue(), SLACK);
		na.setNodeSizeLocked(false);
		assertEquals("node width", 20.0, ((Double)na.get(VisualPropertyType.NODE_WIDTH)).doubleValue(), SLACK);

		na.setNodeSizeLocked(true);
		na.set(VisualPropertyType.NODE_HEIGHT,80.0);

		assertEquals("node uniform size", 35.0, ((Double)na.get(VisualPropertyType.NODE_SIZE)).doubleValue(), SLACK);
		assertEquals("node heigth", 35.0, ((Double)na.get(VisualPropertyType.NODE_HEIGHT)).doubleValue(), SLACK);
		na.setNodeSizeLocked(false);
		assertEquals("node heigth", 80.0, ((Double)na.get(VisualPropertyType.NODE_HEIGHT)).doubleValue(), SLACK);
	}
}
