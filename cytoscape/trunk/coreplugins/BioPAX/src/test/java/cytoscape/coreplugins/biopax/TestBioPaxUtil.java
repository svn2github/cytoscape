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

import java.io.FileInputStream;

import junit.framework.TestCase;

import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.Interaction;
import org.biopax.paxtools.model.level3.Pathway;
import org.biopax.paxtools.model.level3.PhysicalEntity;

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
		Model model = BioPaxUtil.read(new FileInputStream(getClass().getResource("/biopax_sample1.owl").getFile()));
		// Validate Number of Entities
		assertNotNull(model);
		assertEquals(8, model.getObjects(PhysicalEntity.class).size());
		assertEquals(4, model.getObjects(Interaction.class).size());
		assertEquals(1, model.getObjects(Pathway.class).size());
	}

	/**
	 * Tests the BioPAX Utility Class on a second sample data file.
	 *
	 * @throws Exception All Exceptions.
	 */
	/* TODO re-factor because BioPaxUtil.getParentPathwayName is removed
	public void testUtil2() throws Exception {
		Model model = BioPaxUtil.readFile("src/test/resources/biopax_complex.owl");
		Set<String> pathways = BioPaxUtil.getParentPathwayName(
				model.getByID("http://cbio.mskcc.org/cpath#CPATH-124"), model);
		System.out.println("PARENT PROCESSes NAMES: " + pathways);
		assertTrue(pathways.size()==0);
	}
	/

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
	/* TODO re-factor because BioPaxUtil.getParentPathwayName is removed
	public void testUtil3() throws Exception {
		Model model = BioPaxUtil.readFile("src/test/resources/Apoptosis.owl");
		String pathways = BioPaxUtil.getParentPathwayName(
				model.getByID("http://www.biopax.org/examples/apoptosis#Pubmed_7530336"), model)
				.toString();
		System.out.println("PARENT PW NAMES: " + pathways);
		assertTrue(pathways.contains("FasL/ CD95L signaling"));
	}
	*/

}
