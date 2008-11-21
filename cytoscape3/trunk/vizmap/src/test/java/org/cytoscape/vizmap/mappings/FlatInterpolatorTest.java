/*
  File: FlatInterpolatorTest.java

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

// FlatInterpolatorTest.java

//----------------------------------------------------------------------------
// $Revision: 9565 $
// $Date: 2007-02-13 11:36:50 -0800 (Tue, 13 Feb 2007) $
// $Author: mes $
//----------------------------------------------------------------------------
package org.cytoscape.vizmap.mappings;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.*;


//----------------------------------------------------------------------------
/**
 *
 */
public class FlatInterpolatorTest extends TestCase {
	//----------------------------------------------------------------------------
	/**
	 * Creates a new FlatInterpolatorTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public FlatInterpolatorTest(String name) {
		super(name);
	}

	//----------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	//----------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	//----------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testFunction() throws Exception {
		Double d1 = new Double(1.0);
		Double d2 = new Double(2.0);
		Double dMid = new Double(1.5);
		Color c1 = new Color(0, 0, 0);
		Color c2 = new Color(255, 255, 255);

		FlatInterpolator li = new FlatInterpolator();
		Object returnVal = li.getRangeValue(d1, c1, d2, c2, dMid);
		assertTrue(returnVal == c1);

		FlatInterpolator li2 = new FlatInterpolator(FlatInterpolator.LOWER);
		returnVal = li2.getRangeValue(d1, c1, d2, c2, dMid);
		assertTrue(returnVal == c1);

		FlatInterpolator li3 = new FlatInterpolator(FlatInterpolator.UPPER);
		returnVal = li3.getRangeValue(d1, c1, d2, c2, dMid);
		assertTrue(returnVal == c2);
	}

	//---------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(FlatInterpolatorTest.class));
	}

	//----------------------------------------------------------------------------
}
