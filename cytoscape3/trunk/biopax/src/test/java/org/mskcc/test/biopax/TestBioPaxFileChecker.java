// $Id: TestBioPaxFileChecker.java,v 1.3 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
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
package org.mskcc.test.biopax;

import junit.framework.TestCase;
import org.mskcc.biopax_plugin.util.biopax.BioPaxFileChecker;
import org.mskcc.biopax_plugin.util.net.WebFileConnect;

import java.io.File;
import java.io.StringReader;


/**
 * Tests the BioPAX File Checker.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxFileChecker extends TestCase {
	/**
	 * Tests the File Checker.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testFileCheckerValid() throws Exception {
		String str = WebFileConnect.retrieveDocument(new File("src/test/resources/testData/biopax_sample1.owl"));
		StringReader reader = new StringReader(str);
		BioPaxFileChecker checker = new BioPaxFileChecker(reader);
		assertTrue(checker.isProbablyBioPaxFile());
	}

	/**
	 * Tests the File Checker.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testFileCheckerInvalid1() throws Exception {
		String str = WebFileConnect.retrieveDocument(new File("src/test/resources/testData/psi_sample1.xml"));
		StringReader reader = new StringReader(str);
		BioPaxFileChecker checker = new BioPaxFileChecker(reader);
		assertTrue(!checker.isProbablyBioPaxFile());
	}

	/**
	 * Tests the File Checker.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testFileCheckerInvalid2() throws Exception {
		String str = WebFileConnect.retrieveDocument(new File("src/test/resources/testData/soft1.txt"));
		StringReader reader = new StringReader(str);
		BioPaxFileChecker checker = new BioPaxFileChecker(reader);
		assertTrue(!checker.isProbablyBioPaxFile());
	}
}
