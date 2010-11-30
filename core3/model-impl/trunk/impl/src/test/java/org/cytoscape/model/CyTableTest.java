/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.model;


import java.util.HashMap;

import org.cytoscape.equations.BooleanList;
import org.cytoscape.equations.EqnCompiler;
import org.cytoscape.equations.Equation;
import org.cytoscape.equations.Interpreter;
import org.cytoscape.equations.StringList;
import org.cytoscape.equations.internal.EqnCompilerImpl;
import org.cytoscape.equations.internal.EqnParserImpl;
import org.cytoscape.equations.internal.interpreter.InterpreterImpl;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.internal.CyTableImpl;


public class CyTableTest extends AbstractCyTableTest {
	private final EqnCompiler compiler = new EqnCompilerImpl(new EqnParserImpl());

	public void setUp() {
		eventHelper = new DummyCyEventHelper();
		final Interpreter interpreter = new InterpreterImpl();
		table = new CyTableImpl("homer", "SUID", Long.class, true, eventHelper, interpreter);
		attrs = table.getRow(1L);
	}

	public void tearDown() {
		table = null;
		attrs = null;
	}

	public void testSetEquation() {
		table.createColumn("someDouble", Double.class);
		table.createColumn("someOtherDouble", Double.class);

		compiler.compile("=6/3", new HashMap<String, Class>());
		final Equation eqn = compiler.getEquation();
		attrs.set("someDouble", eqn);

		assertTrue(attrs.isSet("someDouble", Double.class));
		assertEquals(2.0, attrs.get("someDouble", Double.class).doubleValue());
	}

	public void testSetEquationWithIncompatibleEquationReturnType() {
		table.createColumn("someDouble", Double.class);
		table.createColumn("someOtherDouble", Double.class);

		compiler.compile("=\"String\"", new HashMap<String, Class>());
		final Equation eqn = compiler.getEquation();
		try {
			attrs.set("someDouble", eqn);
			fail();
		} catch (IllegalArgumentException e) {
			/* Intentionally empty! */
		}
	}

	public void testCreateList() {
		table.createListColumn("booleanList", Boolean.class);
		attrs.set("booleanList", new BooleanList());
		final BooleanList nonEmptyList = new BooleanList(true, false);
		attrs.set("booleanList", nonEmptyList);
		assertEquals(attrs.getList("booleanList", Boolean.class), nonEmptyList);
	}

	public void testSetListWithACompatibleEquation() {
		table.createListColumn("stringList", String.class);
		attrs.set("stringList", new StringList());
		compiler.compile("=SLIST(\"one\",\"two\")", new HashMap<String, Class>());
		final Equation eqn = compiler.getEquation();
		attrs.set("stringList", eqn);
		final StringList expectedList = new StringList("one", "two");
		assertEquals(attrs.getList("stringList", String.class), expectedList);
	}
}
