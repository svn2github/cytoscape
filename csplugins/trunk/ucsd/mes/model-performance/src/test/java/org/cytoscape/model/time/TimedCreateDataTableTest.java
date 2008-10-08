
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
package org.cytoscape.model.time;

import com.clarkware.junitperf.TimedTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.cytoscape.model.internal.CyDataTableImpl;
import org.cytoscape.model.*;
import org.cytoscape.*;


import java.util.Random;


/**
 * Created by IntelliJ IDEA. User: skillcoy Date: Sep 19, 2008 Time: 3:04:03 PM To change this
 * template use File | Settings | File Templates.
 */
public class TimedCreateDataTableTest extends TestCase {
	private CyDataTable dataTable;
	private static final int TOTAL_COLS = 50;
	private static final int TOTAL_ROWS = 100000;
	private static final long MAX_TIME_MILLIS = 1000;

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static Test suite() {
		Test test = new TimedCreateDataTableTest("testCreateTable");
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
	public TimedCreateDataTableTest(String name) {
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
	public void testCreateTable() {
		RandomColumnName randomCol = new RandomColumnName();
		Class<?> colClass = String.class;

		for (int i = 0; i < TOTAL_COLS; i++) {
			if ((i % 3) == 0)
				colClass = Integer.class;
			else if ((i % 5) == 0)
				colClass = Double.class;

			dataTable.createColumn(randomCol.getRandomName(), colClass, false);
		}

		for (int i = 0; i < TOTAL_ROWS; i++)
			dataTable.addRow();

		// TODO might be useful to be able to ask of a table how many rows & columns it has
	}

	private class RandomColumnName {
		private Random rn = new Random(5);

		private int rand(int lo, int hi) {
			int n = hi - lo + 1;
			int i = rn.nextInt() % n;

			if (i < 0)
				i = -i;

			return lo + i;
		}

		private String randomstring(int lo, int hi) {
			int n = rand(lo, hi);
			byte[] b = new byte[n];

			for (int i = 0; i < n; i++)
				b[i] = (byte) rand('a', 'z');

			return new String(b);
		}

		public String getRandomName() {
			return randomstring(5, 35);
		}
	}
}
