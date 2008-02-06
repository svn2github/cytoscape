/*
 File: CyAttributesWriterTest.java

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
package cytoscape.data.writers;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesImpl;

import cytoscape.data.readers.CyAttributesReader;

import cytoscape.data.writers.CyAttributesWriter;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Tests the CyAttributesWriter Class.
 *
 * Note 2/6/2008 by kono:
 *   This code tests CyAttributesWriter2, not the original one.
 *
 */
public class CyAttributesWriterTest extends TestCase {
	/**
	 * Tests Writing out of Scalar Values.
	 *
	 * @throws IOException
	 *             IO Error.
	 */
	public void testWriterScalars() throws IOException {
		System.out.println("################## CyAttributesWriter2 Test start #######################");

		// Load test data.
		CyAttributes cyAttributes = new CyAttributesImpl();
		File file = new File("testData/galFiltered.nodeAttrs1");
		FileReader reader = new FileReader(file);
		CyAttributesReader.loadAttributes(cyAttributes, reader);

		// Write attributes
		StringWriter sw = new StringWriter();
		CyAttributesWriter2 cw2 = new CyAttributesWriter2(cyAttributes, "TestNodeAttribute1", sw);
		cw2.writeAttributes();

		String output = sw.toString();
		String[] lines = output.split(System.getProperty("line.separator"));
		Set<String> allLines = new TreeSet<String>();

		for (String line : lines) {
			System.out.println("Line = " + line);
			allLines.add(line);
		}

		if(sw != null) {
			sw.close();
			sw = null;
		}
		assertEquals(lines.length, allLines.size());
		assertTrue(allLines.contains("TestNodeAttribute1 (class=java.lang.Integer)"));
		assertTrue(allLines.contains("YDR309C = 1"));
		assertTrue(allLines.contains("YML024W = 2"));
		allLines = null;
		lines = null;

		System.out.println("################## CyAttributesWriter2 Test end #######################");
	}

	/**
	 * Tests Writing out of Lists.
	 *
	 * @throws IOException
	 *             IO Error.
	 */
	public void testWriteSimpleLists() throws IOException {
		
		System.out.println("################## CyAttributesWriter2 List Test start #######################");
		
		CyAttributes cyAttributes = new CyAttributesImpl();
		File file = new File("testData/implicitStringArray.attribute");
		FileReader reader = new FileReader(file);
		CyAttributesReader.loadAttributes(cyAttributes, reader);

		// Add a new item
		final List<String> list = new ArrayList<String>();
		list.add(new String("Apple"));
		list.add(new String("Orange"));
		list.add(new String("Banana"));

		cyAttributes.setListAttribute("ABC_123", "GO_molecular_function_level_4", list);

		StringWriter writer = new StringWriter();
		CyAttributesWriter2 writer2 = new CyAttributesWriter2(cyAttributes, "GO_molecular_function_level_4", writer);
		writer2.writeAttributes();

		String output = writer.toString();
		String[] lines = output.split(System.getProperty("line.separator"));
		Set<String> allLines = new TreeSet<String>();

		for (String line : lines) {
			System.out.println("Line = " + line);
			allLines.add(line);
		}
		
		if(writer != null) {
			writer.close();
			writer = null;
		}

		/*
		 * Order of entries are not important.
		 * Should focus on which attributes are actually saved.
		 */
		assertEquals(allLines.size(), lines.length);
		assertTrue(allLines.contains("GO_molecular_function_level_4 (class=java.lang.String)"));
		assertTrue(allLines.contains("HSD17B2 = (membrane::intracellular)"));
		assertTrue(allLines.contains("E2F4 = (DNA binding)"));
		assertTrue(allLines.contains("AP1G1 = (intracellular::clathrin adaptor::intracellular "
		                             + "transporter)"));
		assertTrue(allLines.contains("ABC_123 = (Apple::Orange::Banana)"));
		assertTrue(allLines.contains("CDH3 = (cell adhesion molecule)"));

		allLines = null;
		lines = null;
		
		System.out.println("################## CyAttributesWriter2 List Test end #######################");
	}

	/**
	 * Runs just this one unit test.
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(CyAttributesWriterTest.class);
	}
}
