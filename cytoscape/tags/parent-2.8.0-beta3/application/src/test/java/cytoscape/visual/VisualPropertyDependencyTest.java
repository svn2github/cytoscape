/*
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

import java.util.Properties;
import junit.framework.*;
import static cytoscape.visual.VisualPropertyDependency.Definition.*;


public class VisualPropertyDependencyTest extends TestCase {
	double SLACK = 0.00001;
	VisualPropertyDependency deps;

	public VisualPropertyDependencyTest(String name) {
		super(name);
	}

	public void setUp() {
		deps = new VisualPropertyDependencyImpl();
	}

	public void testDefaults() {
		assertTrue( deps.check(NODE_SIZE_LOCKED) );
		assertTrue( !deps.check(ARROW_COLOR_MATCHES_EDGE) );
	}

	public void testSet() {
		deps.set(NODE_SIZE_LOCKED,true);
		assertTrue( deps.check(NODE_SIZE_LOCKED) );
		deps.set(NODE_SIZE_LOCKED,false);
		assertTrue( !deps.check(NODE_SIZE_LOCKED) );
	}

	public void testApplyProps() {
		Properties props = new Properties();
		String baseKey = "base";
		props.setProperty(baseKey + ".nodeSizeLocked", "false");

		deps.applyDefaultProperties(props,baseKey);

		assertTrue( !deps.check(NODE_SIZE_LOCKED) );
	}

	public void testGetOnlySetProps() {
		Properties props = deps.getDefaultProperties("base");
		assertNull( props.getProperty("base." + NODE_SIZE_LOCKED.getDefaultPropertyKey()));
		assertNull( props.getProperty("base." + ARROW_COLOR_MATCHES_EDGE.getDefaultPropertyKey()));
	}

	public void testGetSetProps() {
		deps.set(NODE_SIZE_LOCKED,false);
		Properties props = deps.getDefaultProperties("base");
		assertEquals("false", props.getProperty("base."+NODE_SIZE_LOCKED.getDefaultPropertyKey()));
	}
}
