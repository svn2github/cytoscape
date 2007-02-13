/*
  File: ThesaurusFlatFileReaderTest.java

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

// ThesaurusFlatFileReaderTest

//------------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data.synonyms.readers;

import cytoscape.AllTests;

import cytoscape.data.synonyms.*;
import cytoscape.data.synonyms.readers.ThesaurusFlatFileReader;

import junit.framework.*;

import org.jdom.*;

import org.jdom.input.*;

import org.jdom.output.*;

//------------------------------------------------------------------------------
import java.io.*;

import java.util.*;


//------------------------------------------------------------------------------
/**
 * test the ThesaurusFlatFileReader class
 */
public class ThesaurusFlatFileReaderTest extends TestCase {
	//------------------------------------------------------------------------------
	/**
	 * Creates a new ThesaurusFlatFileReaderTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public ThesaurusFlatFileReaderTest(String name) {
		super(name);
	}

	//------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	//------------------------------------------------------------------------------
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	//------------------------------------------------------------------------------
	/**
	 *  read a small sample yeast thesaurus file
	 */
	public void testReadSmallYeastThesaurus() throws Exception {
		AllTests.standardOut("testReadSmallYeastThesaurus");

		String filename = "testData/yeastSmall.txt";

		//  if (AllTests.runAllTests()) {
		//     filename =
		//      "src/cytoscape/data/synonyms/readers/unitTests/sampleData/yeastSmall.txt";
		//  }
		ThesaurusFlatFileReader reader = new ThesaurusFlatFileReader(filename);
		Thesaurus thesaurus = reader.getThesaurus();

		assertTrue(thesaurus.getSpecies().equals("Saccharomyces cerevisiae"));
		assertTrue(thesaurus.nodeLabelCount() == 8);

		// the values of these next 3 arrays are extracted, by hand, from the flat file
		String[] canonical = {
		                         "YHR047C", "YBL074C", "YKL106W", "YLR027C", "YGL119W", "YBR236C",
		                         "YKL112W", "YMR072W"
		                     };
		String[] common = { "AAP1'", "AAR2", "AAT1", "AAT2", "ABC1", "ABD1", "ABF1", "ABF2" };
		int[] alternateNameCount = { 1, 0, 0, 1, 0, 0, 3, 0 };

		for (int i = 0; i < canonical.length; i++) {
			assertTrue(thesaurus.getCommonName(canonical[i]).equals(common[i]));
			assertTrue(thesaurus.getNodeLabel(common[i]).equals(canonical[i]));

			String[] alternateNames = thesaurus.getAlternateCommonNames(canonical[i]);

			if (i == 6) { // do a name-by-name comparison for this orf with 3 alternate names

				String orf = canonical[i];
				assertTrue(orf.equals("YKL112W"));
				assertTrue(alternateNames[0].equals("BAF1"));
				assertTrue(alternateNames[1].equals("OBF1"));
				assertTrue(alternateNames[2].equals("REB2"));
			} // i == 6

			for (int j = 0; j < alternateNames.length; j++)
				AllTests.standardOut(canonical[i] + " -> " + alternateNames[j]);

			assertTrue(alternateNames.length == alternateNameCount[i]);
		}
	} // testReadSmallYeastThesaurus
	  //-------------------------------------------------------------------------

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(ThesaurusFlatFileReaderTest.class));
	} // main
	  //------------------------------------------------------------------------------
} // class ThesaurusFlatFileReaderTest
