// $Id: TestBioPaxUtil.java,v 1.8 2006/06/15 22:06:02 grossb Exp $
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
package cytoscape.coreplugins.biopax;

import junit.framework.TestCase;

import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level2.interaction;
import org.biopax.paxtools.model.level2.pathway;
import org.biopax.paxtools.model.level2.physicalEntity;

import cytoscape.coreplugins.biopax.util.BioPaxUtil;


/**
 * Tests the BioPaxUtil Class.
 *
 * @author Ethan Cerami; updated by Igor Rodchenkov
 */
public class TestBioPaxUtil extends TestCase {
	/**
	 * Tests the BioPAX Utility Class.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testUtil() throws Exception {
		Model model = BioPaxUtil.readFile("test-resources/biopax_sample1.owl");
		// Validate Number of Entities
		assertNotNull(model);
		assertEquals(8, model.getObjects(physicalEntity.class).size());
		assertEquals(4, model.getObjects(interaction.class).size());
		assertEquals(1, model.getObjects(pathway.class).size());

		String pathways = BioPaxUtil.getParentPathwayName(model.getByID("catalysis43")).toString();
		assertTrue(pathways.contains("pathway50"));
	}

	/**
	 * Tests the BioPAX Utility Class on a second sample data file.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testUtil2() throws Exception {
		Model model = BioPaxUtil.readFile("test-resources/biopax_complex.owl");
		String pathways = BioPaxUtil.getParentPathwayName(model.getByID("CPATH-124")).toString();
		assertTrue(pathways.length() == 0);
	}

	/**
	 * Tests the BioPAX Utility Class on a third sample data file.
	 * <p/>
	 * This tests a specially modified version of the Apoptosis pathway
	 * that contains one real pathway, and one "dummy" duplicate pathway.
	 * <p/>
	 * It provides a test case for entities, which can be members of multiple
	 * pathways at once.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testUtil3() throws Exception {
		Model model = BioPaxUtil.readFile("test-resources/Apoptosis_modified.owl");
		String pathways = BioPaxUtil.getParentPathwayName(model.getByID("Pubmed_7530336")).toString();
		assertTrue(pathways.length() > 0);

		assertTrue(pathways.contains("Apoptosis"));
		assertTrue(pathways.contains("Apoptosis2"));
		
		pathways = BioPaxUtil.getParentPathwayName(model.getByID("cell")).toString();
		assertTrue(pathways.contains("Apoptosis"));
		assertTrue(pathways.contains("Apoptosis2"));
	}

	/**
	 * Tests the BioPAX Utility Class on a fourth sample data file.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testUtil4() throws Exception {
		Model model = BioPaxUtil.readFile("test-resources/biopax_sample1.owl");
		String pathways = BioPaxUtil.getParentPathwayName(model.getByID("protein45")).toString();
		assertTrue(pathways.length() > 0);
	}
}
