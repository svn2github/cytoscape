// $Id: TestExternalLinkUtil.java,v 1.11 2006/06/15 22:07:49 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross.
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package cytoscape.coreplugins.biopax;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import cytoscape.coreplugins.biopax.mapper.TestBioPaxToCytoscapeMapper;
import cytoscape.coreplugins.biopax.util.TestExternalLinkUtil;
import cytoscape.coreplugins.biopax.util.TestWebFileConnect;


/**
 * Runs all Unit Tests for the BioPAX Plugin.
 *
 * @author Ethan Cerami.
 */
public class AllTest extends TestCase {
	/**
	 * Master Test Suite.
	 *
	 * @return Test Suite to run.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestBioPaxUtil.class);
		suite.addTestSuite(TestBioPaxToCytoscapeMapper.class);
		suite.addTestSuite(TestBioPaxImportWrapper.class);
		suite.addTestSuite(TestExternalLinkUtil.class);
		suite.addTestSuite(TestWebFileConnect.class);
		suite.setName("Quick Find Tests");

		return suite;
	}

	/**
	 * Run the all tests method.
	 *
	 * @param args java.lang.String[]
	 * @throws Exception All Errors.
	 */
	public static void main(String[] args) throws Exception {
		if ((args.length > 0) && (args[0] != null) && args[0].equals("-ui")) {
			String[] newargs = { "csplugins.test.AllTest", "-noloading" };
			junit.swingui.TestRunner.main(newargs);
		} else {
			junit.textui.TestRunner.run(suite());
		}
	}
}
