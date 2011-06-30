/*
  File: TextJarReaderTest.java

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

// TextJarReaderTest.java
package cytoscape.data.readers;

import cytoscape.AllTests;

import cytoscape.data.readers.TextJarReader;

import junit.framework.*;

import java.io.IOException;

import java.net.URL;


/**
 *
 */
public class TextJarReaderTest extends TestCase {
	/**
	 * Creates a new TextJarReaderTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public TextJarReaderTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testOldSchool() throws Exception {
		TextJarReader reader = new TextJarReader("jar://vizmap.props");
		int count = reader.read();
		String text = reader.getText();
		assertTrue(text.length() > 0);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testNewSchool() throws Exception {
		// rather than hardcoding the url, do it this way because
		// we won't know where the jar file actually lives
		// While this may not appear like it's testing anything, it
		// is.  This only makes sure the url is constructed
		// properly.
		URL url = getClass().getResource("/vizmap.props");
		System.out.println("url: " + url.toString());

		if (url.toString().startsWith("file")) {
			try {
				TextJarReader reader = new TextJarReader(url.toString());
			} catch (IOException e) {
				System.out.println("We expect the following error since "
				                   + "TextJarReader only supports jar urls.");
				e.printStackTrace();
				assertTrue(1 == 1);

				return;
			}

			fail("didn't catch expected exception");
		} else {
			TextJarReader reader = new TextJarReader(url.toString());
			int count = reader.read();
			String text = reader.getText();
			System.out.println(text);
			assertTrue(text.length() > 0);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testBadURL() throws Exception {
		URL url = new URL("http://google.com");

		try {
			TextJarReader reader = new TextJarReader(url.toString());
		} catch (IOException ioe) {
			assertTrue(1 == 1);

			return;
		}

		fail("didn't catch an expected exception for url: " + url.toString());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(TextJarReaderTest.class));
	}
} // TextJarReaderTest
