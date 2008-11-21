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
package org.mskcc.test.biopax;

import junit.framework.TestCase;
import org.jdom.Element;
import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Tests the BioPaxUtil Class.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxUtil extends TestCase {
	/**
	 * Tests the BioPAX Utility Class.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testUtil() throws Exception {
		FileReader fileReader = new FileReader("src/test/resources/testData/biopax_sample1.owl");
		BioPaxUtil bpUtil = new BioPaxUtil(fileReader);

		// Validate Number of Entities
		List physicalList = bpUtil.getPhysicalEntityList();
		List interactionList = bpUtil.getInteractionList();
		List pathwayList = bpUtil.getPathwayList();

		assertEquals(8, physicalList.size());
		assertEquals(4, interactionList.size());
		assertEquals(1, pathwayList.size());

		//  Try Getting an Element via its RDF ID
		HashMap map = bpUtil.getRdfResourceMap();
		Element e = (Element) map.get("catalysis43");
		assertEquals("catalysis", e.getName());

		HashMap pathwayMembershipMap = bpUtil.getPathwayMembershipMap();
		ArrayList list = (ArrayList) pathwayMembershipMap.get("catalysis43");
		String pathwayId = (String) list.get(0);
		assertEquals("pathway50", pathwayId);

		list = (ArrayList) pathwayMembershipMap.get("smallMolecule10");
		pathwayId = (String) list.get(0);
		assertEquals("pathway50", pathwayId);
	}

	/**
	 * Tests the BioPAX Utility Class on a second sample data file.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testUtil2() throws Exception {
		FileReader fileReader = new FileReader("src/test/resources/testData/biopax_complex.owl");
		final BioPaxUtil bpUtil = new BioPaxUtil(fileReader);
		HashMap pathwayMembershipMap = bpUtil.getPathwayMembershipMap();
		ArrayList list = (ArrayList) pathwayMembershipMap.get("CPATH-124");
		assertEquals(null, list);
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
		FileReader fileReader = new FileReader("src/test/resources/testData/Apoptosis_modified.owl");
		final BioPaxUtil bpUtil = new BioPaxUtil(fileReader);
		HashMap pathwayMembershipMap = bpUtil.getPathwayMembershipMap();
		ArrayList list = (ArrayList) pathwayMembershipMap.get("Pubmed_7530336");
		assertEquals(2, list.size());

		String pathwayId = (String) list.get(0);
		assertEquals("Apoptosis", pathwayId);
		pathwayId = (String) list.get(1);
		assertEquals("Apoptosis2", pathwayId);

		list = (ArrayList) pathwayMembershipMap.get("cell");
		assertEquals(2, list.size());
		pathwayId = (String) list.get(0);
		assertEquals("Apoptosis", pathwayId);
		pathwayId = (String) list.get(1);
		assertEquals("Apoptosis2", pathwayId);
	}

	/**
	 * Tests the BioPAX Utility Class on a fourth sample data file.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testUtil4() throws Exception {
		FileReader fileReader = new FileReader("src/test/resources/testData/biopax_sample1.owl");
		final BioPaxUtil bpUtil = new BioPaxUtil(fileReader);
		HashMap pathwayMembershipMap = bpUtil.getPathwayMembershipMap();
		ArrayList list = (ArrayList) pathwayMembershipMap.get("protein45");
		assertEquals(1, list.size());
	}
}
