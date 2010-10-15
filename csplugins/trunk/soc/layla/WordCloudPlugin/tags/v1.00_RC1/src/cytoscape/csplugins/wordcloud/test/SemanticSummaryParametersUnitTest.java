/*
 File: SemanticSummaryParametersUnitTest.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.wordcloud.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import cytoscape.csplugins.wordcloud.CloudParameters;
import cytoscape.csplugins.wordcloud.SemanticSummaryParameters;

/**
 * This class tests the functionality of the SemanticSummaryParameters class.
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class SemanticSummaryParametersUnitTest extends TestCase {


	SemanticSummaryParameters params = new SemanticSummaryParameters();
	String cloudName = "Test Cloud";
	String nonName = "Other Cloud";
	CloudParameters cloudParams = new CloudParameters();
	CloudParameters nonParams = new CloudParameters();
	
	
	@Before
	public void setUp() {
		cloudParams.setCloudName(cloudName);
		nonParams.setCloudName(nonName);
	}

	@Test
	public void testAddCloud() {
		
		CloudParameters retrieved = params.getCloud(cloudName);
		assertTrue(retrieved == null);
		
		params.addCloud(cloudName, cloudParams);
		retrieved = params.getCloud(cloudName);
		assertTrue(params.containsCloud(cloudName));
		assertTrue(retrieved.equals(cloudParams));
	}

	@Test
	public void testRemoveCloud() {
		
		params.addCloud(cloudName,cloudParams);
		assertTrue(params.containsCloud(cloudName));
		params.removeCloud(nonName);
		assertTrue(params.containsCloud(cloudName));
		params.removeCloud(cloudName);
		assertFalse(params.containsCloud(cloudName));
	}

	@Test
	public void testNetworkChanged() {
		params.addCloud(cloudName,cloudParams);
		params.addCloud(nonName, nonParams);
		cloudParams.setRatiosInitialized(true);
		cloudParams.setSelInitialized(true);
		cloudParams.setCountInitialized(true);
		nonParams.setRatiosInitialized(true);
		nonParams.setSelInitialized(true);
		nonParams.setCountInitialized(true);
		params.networkChanged();
		assertFalse(cloudParams.getRatiosInitialized());
		assertFalse(cloudParams.getSelInitialized());
		assertFalse(cloudParams.getCountInitialized());
		assertFalse(nonParams.getRatiosInitialized());
		assertFalse(nonParams.getSelInitialized());
		assertFalse(nonParams.getCountInitialized());
	}

	@Test
	public void testGetNextCloudName() {
		String nextName = params.getNextCloudName();
		assertEquals(nextName, "Cloud_1");
		nextName = params.getNextCloudName();
		assertEquals(nextName, "Cloud_2");
	}
}
