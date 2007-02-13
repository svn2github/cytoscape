
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

package cytoscape.data.readers;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.readers.MetadataEntries;
import cytoscape.data.readers.MetadataParser;

import junit.framework.TestCase;

import java.net.URISyntaxException;

import java.util.Map;


/**
 * Test cases for MetadataParser.<br>
 *
 * @author kono
 *
 */
public class MetadataParserTest extends TestCase {
	MetadataParser mdp;
	CyNetwork network;

	protected void setUp() throws Exception {
		super.setUp();
		network = Cytoscape.getCurrentNetwork();
		mdp = new MetadataParser(network);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testMakeNewMetadataMap() {
		Map newMap = mdp.makeNewMetadataMap();
		assertNotNull(newMap);
		assertEquals(MetadataEntries.values().length, newMap.size());
		assertEquals("http://www.cytoscape.org/", newMap.get(MetadataEntries.SOURCE.toString()));
		assertEquals("N/A", newMap.get(MetadataEntries.IDENTIFIER.toString()));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testSetMetadata() {
		System.out.println("### Metadata Parser is testing with network: " + network.getTitle()
		                   + " ###");

		mdp.setMetadata(MetadataEntries.SOURCE, "Gene Ontology");
		mdp.setMetadata(MetadataEntries.DESCRIPTION, "DAG created form OBO file.");

		Map metadata = Cytoscape.getNetworkAttributes()
		                        .getMapAttribute(network.getIdentifier(),
		                                         mdp.DEFAULT_NETWORK_METADATA_LABEL);
		assertNotNull(metadata);
		assertEquals("Gene Ontology", metadata.get(MetadataEntries.SOURCE.toString()));
		assertEquals("DAG created form OBO file.",
		             metadata.get(MetadataEntries.DESCRIPTION.toString()));
	}
}
