
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
package org.cytoscape.model;

import com.clarkware.junitperf.TimedTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.cytoscape.model.internal.CyDataTableImpl;


/**
 * Created by IntelliJ IDEA. User: skillcoy Date: Sep 19, 2008 Time: 3:04:03 PM To change this
 * template use File | Settings | File Templates.
 */
public class TimedLoadDataTableTest extends TestCase {
	private CyDataTable dataTable;
	private static final int TOTAL_ROWS = 20000;
	private static final long MAX_TIME_MILLIS = 1000;
	private static final String[] COL_NAMES = new String[] {
	                                             "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
	                                             "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
	                                             "U", "V", "W", "X", "Y", "Z"
	                                         };

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static Test suite() {
		Test test = new TimedLoadDataTableTest("testLoadTable");
		Test timedTest = new TimedTest(test, MAX_TIME_MILLIS);

		return timedTest;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
		TestResult result = junit.textui.TestRunner.run(suite());
	}

	/**
	 * Creates a new TimedAddNodeTest object.
	 *
	 * @param name DOCUMENT ME!
	 */
	public TimedLoadDataTableTest(String name) {
		super(name);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void setUp() {
		dataTable = new CyDataTableImpl(null, "foobar", true);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
		dataTable = null;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testLoadTable() {
		Class<?> colClass = String.class;

		for (int i = 0; i < COL_NAMES.length; i++)
			dataTable.createColumn(COL_NAMES[i], colClass, false);

		for (int i = 0; i < TOTAL_ROWS; i++) {
			CyRow row = dataTable.addRow();
			//assertNotNull(row);

			for (String colName : COL_NAMES) {
				row.set(colName, "foo bar");
			}
		}

		// TODO might be useful to be able to ask of a table how many rows & columns it has
	}
}
