/*
  File: TestContinuousMappingWriter.java

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
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.continuous.ContinuousMappingReader;
import cytoscape.visual.mappings.continuous.ContinuousMappingWriter;

import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;

import junit.framework.TestCase;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Properties;


/**
 * Tests the ContinuousMappingWriter Class.
 */
public class TestContinuousMappingWriter extends TestCase {
	/**
	 * Tests the ContinuousMappingWriter Class.
	 * @throws Exception All Exceptions.
	 */
	public void testWriter() throws Exception {
		//  Read in a Properties File
		String baseKey = "nodeColorCalculator.RedGreen2.mapping";
		ValueParser parser = new ColorParser();
		InputStream in = TestContinuousMappingReader.getDataFile();
		Properties properties = new Properties();
		properties.load(in);

		ContinuousMappingReader reader = new ContinuousMappingReader(properties, baseKey, parser);
		ArrayList points = reader.getPoints();

		//  Now write out a set of Propeties.
		ContinuousMappingWriter writer = new ContinuousMappingWriter(points, baseKey,
		                                                             reader
		                                                                   .getControllingAttributeName(),
		                                                             reader.getInterpolator());

		//  Test a samplint of properties.
		Properties newProps = writer.getProperties();

		//  Test Boundary Range Value
		String bv0Lesser = newProps.getProperty(baseKey + ".bv0.lesser");
		assertEquals("255,0,0", bv0Lesser);

		//  Test Interpolator
		String interp = newProps.getProperty(baseKey + ".interpolator");
		assertEquals("LinearNumberToColorInterpolator", interp);

		//  Test Controlling Attribute
		String type = newProps.getProperty(baseKey + ".controller");
		assertEquals("expression", type);
	}
}
