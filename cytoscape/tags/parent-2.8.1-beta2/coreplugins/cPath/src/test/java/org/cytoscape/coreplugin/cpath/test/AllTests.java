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
package org.cytoscape.coreplugin.cpath.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.cytoscape.coreplugin.cpath.test.protocol.ProtocolSuite;
import org.cytoscape.coreplugin.cpath.test.task.TaskSuite;


/**
 * Runs All JUnit Tests.
 *
 * @author Ethan Cerami
 */
public class AllTests extends TestCase {
	/**
	 * The suite method kicks off all of the tests.
	 *
	 * @return junit.framework.Test
	 */
	public static Test suite() {
		//  Organize all suites into one master suite.
		TestSuite suite = new TestSuite();
		suite.addTest(TaskSuite.suite());
		suite.addTest(ProtocolSuite.suite());
		suite.setName("Cytoscape cPath Plugin Tests");

		return suite;
	}

	/**
	 * Runs all Cytoscape Unit Tests.
	 *
	 * @param args Command Line Arguments. use -ui to run the JUnit Graphical
	 *             interface.
	 */
	public static void main(String[] args) {
		if ((args.length > 0) && (args[0] != null) && args[0].equals("-ui")) {
			String[] newargs = { "org.cytoscape.coreplugin.cpath.test.AllTests", "-noloading" };
			junit.swingui.TestRunner.main(newargs);
		} else {
			junit.textui.TestRunner.run(suite());
		}
	}
}
