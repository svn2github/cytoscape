/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.util;

import junit.framework.*;

import java.util.Properties;


/**
 *
 */
public class PropUtilTest extends TestCase {

	Properties props;
	protected void setUp() throws Exception {
		props = new Properties();
	}

	public void testGoodInt() {
		props.setProperty("good-int-1","123");

		assertEquals(123,PropUtil.getInt(props,"good-int-1",999));
	}

	public void testBadInt() {
		props.setProperty("bad-int-1"," 4");
		props.setProperty("bad-int-2","5 ");
		props.setProperty("bad-int-3"," 6 ");
		props.setProperty("bad-int-4","homer");
		props.setProperty("bad-int-5"," x");
		props.setProperty("bad-int-6","5.04 ");
		props.setProperty("bad-int-7"," 6.0 ");

		assertEquals(999,PropUtil.getInt(props,"bad-int-1",999));
		assertEquals(999,PropUtil.getInt(props,"bad-int-2",999));
		assertEquals(999,PropUtil.getInt(props,"bad-int-3",999));
		assertEquals(999,PropUtil.getInt(props,"bad-int-4",999));
		assertEquals(999,PropUtil.getInt(props,"bad-int-5",999));
		assertEquals(999,PropUtil.getInt(props,"bad-int-6",999));
		assertEquals(999,PropUtil.getInt(props,"bad-int-7",999));
	}


	public void testTrueGood() {
		props.setProperty("true-good-1","true");
		props.setProperty("true-good-2"," TRUE");
		props.setProperty("true-good-3","trUe ");
		props.setProperty("true-good-4","  True ");

		assertTrue(PropUtil.getBoolean(props,"true-good-1",false));
		assertTrue(PropUtil.getBoolean(props,"true-good-2",false));
		assertTrue(PropUtil.getBoolean(props,"true-good-3",false));
		assertTrue(PropUtil.getBoolean(props,"true-good-4",false));
	}

	public void testFalseGood() {
		props.setProperty("false-good-1","false");
		props.setProperty("false-good-2"," FALSE");
		props.setProperty("false-good-3","False ");
		props.setProperty("false-good-4","  false ");

		assertFalse(PropUtil.getBoolean(props,"false-good-1",true));
		assertFalse(PropUtil.getBoolean(props,"false-good-2",true));
		assertFalse(PropUtil.getBoolean(props,"false-good-3",true));
		assertFalse(PropUtil.getBoolean(props,"false-good-4",true));
	}

	public void testYesGood() {
		props.setProperty("true-good-1","yes");
		props.setProperty("true-good-2"," YES");
		props.setProperty("true-good-3","yEs ");
		props.setProperty("true-good-4","  YeS ");

		assertTrue(PropUtil.getBoolean(props,"true-good-1",false));
		assertTrue(PropUtil.getBoolean(props,"true-good-2",false));
		assertTrue(PropUtil.getBoolean(props,"true-good-3",false));
		assertTrue(PropUtil.getBoolean(props,"true-good-4",false));
	}

	public void testNoGood() {
		props.setProperty("false-good-1","no");
		props.setProperty("false-good-2"," NO");
		props.setProperty("false-good-3","No ");
		props.setProperty("false-good-4","  nO ");

		assertFalse(PropUtil.getBoolean(props,"false-good-1",true));
		assertFalse(PropUtil.getBoolean(props,"false-good-2",true));
		assertFalse(PropUtil.getBoolean(props,"false-good-3",true));
		assertFalse(PropUtil.getBoolean(props,"false-good-4",true));
	}

	public void testTrueBad() {
		props.setProperty("true-bad-1","truex");
		props.setProperty("true-bad-2","true x");
		props.setProperty("true-bad-3","true!");

		// we should be getting the default in each case
		assertFalse(PropUtil.getBoolean(props,"true-bad-1",false));
		assertTrue(PropUtil.getBoolean(props,"true-bad-2",true));  
		assertFalse(PropUtil.getBoolean(props,"true-bad-3",false));
	}

	public void testFalseBad() {
		props.setProperty("false-bad-1","falsex");
		props.setProperty("false-bad-2","false x");
		props.setProperty("false-bad-3","false!");

		// we should be getting the default in each case
		assertFalse(PropUtil.getBoolean(props,"false-bad-1",false));
		assertTrue(PropUtil.getBoolean(props,"false-bad-2",true));  
		assertFalse(PropUtil.getBoolean(props,"false-bad-3",false));
	}

}
