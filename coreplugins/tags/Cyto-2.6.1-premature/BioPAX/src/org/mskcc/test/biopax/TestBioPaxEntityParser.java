// $Id: TestBioPaxEntityParser.java,v 1.3 2006/06/15 22:06:02 grossb Exp $
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

import org.jdom.Element;

import org.mskcc.biopax_plugin.util.biopax.BioPaxEntityParser;
import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;
import org.mskcc.biopax_plugin.util.links.ExternalLink;

import java.io.FileReader;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Tests the BioPaxEntity Parser Class.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxEntityParser extends TestCase {
	/**
	 * Tests the BioPax Entity Parser class.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testBioPaxEntityParser() throws Exception {
		FileReader fileReader = new FileReader("testData/biopax_complex.owl");
		final BioPaxUtil bpUtil = new BioPaxUtil(fileReader);
		HashMap rdfMap = bpUtil.getRdfResourceMap();
		Element protein = (Element) rdfMap.get("CPATH-124");
		BioPaxEntityParser entityParser = new BioPaxEntityParser(protein, rdfMap);
		assertEquals("protein", entityParser.getType());
		assertEquals("UniProt:P01375 Tumor necrosis factor precursor "
		             + "(TNF-alpha) (Tumor necrosis factor ligand superfamily "
		             + "member 2) (TNF-a) (Cachectin)", entityParser.getName());
		assertEquals("TNF", entityParser.getShortName());

		ArrayList synList = entityParser.getSynonymList();
		assertEquals(3, synList.size());
		assertEquals("TNF", (String) synList.get(0));
		assertEquals("TNFA", (String) synList.get(1));
		assertEquals("TNFSF2", (String) synList.get(2));
		assertEquals("Homo sapiens", entityParser.getOrganismName());
		assertEquals(9606, entityParser.getOrganismTaxonomyId());
		assertTrue(entityParser.getComment().startsWith("FUNCTION: Cytokine that binds"));
		assertEquals("see http://www.amaze.ulb.ac.be/", entityParser.getAvailability());

		ArrayList unificationRefs = entityParser.getUnificationXRefs();
		assertEquals(1, unificationRefs.size());

		ExternalLink link = (ExternalLink) unificationRefs.get(0);
		assertEquals("UniProt", link.getDbName());
		assertEquals("P01375", link.getId());

		ArrayList relationshipRefs = entityParser.getRelationshipXRefs();
		assertEquals(2, relationshipRefs.size());
		link = (ExternalLink) relationshipRefs.get(0);
		assertEquals("GO", link.getDbName());
		assertEquals("0008624", link.getId());
		link = (ExternalLink) relationshipRefs.get(1);
		assertEquals("GO", link.getDbName());
		assertEquals("0006919", link.getId());

		ArrayList xrefs = entityParser.getAllXRefs();
		assertEquals(5, xrefs.size());
	}
}
