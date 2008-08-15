/*
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
package org.cytoscape.coreplugin.psi_mi.test.data_mapper;

import junit.framework.TestCase;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiOneToInteractions;
import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.Interaction;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.util.ContentReader;

import java.io.File;
import java.util.ArrayList;


/**
 * Tests the MapPsiOneToInteractions Mapper.
 *
 * @author Ethan Cerami
 */
public class TestMapPsiOneToInteractions extends TestCase {
	/**
	 * Test the PSI Mapper, Case 1.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testPsiMapper1() throws Exception {
		File file = new File("src/test/resources/testData/psi_sample2.xml");
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent(file.toString());
		ArrayList interactions = new ArrayList();
		MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
		mapper.doMapping();

		//  Validate Interaction at index = 0.
		Interaction interaction = (Interaction) interactions.get(0);
		validateSample1(interaction);
	}

	/**
	 * Test the PSI Mapper, Case 2.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testPsiMapper2() throws Exception {
		File file = new File("src/test/resources/testData/yeast_normalised.xml");
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent(file.toString());
		ArrayList interactions = new ArrayList();
		MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
		mapper.doMapping();

		//  Validate Interaction at index = 0.
		Interaction interaction = (Interaction) interactions.get(0);
		validateSample2(interaction);
	}

	/**
	 * Test the PSI Mapper, Case 3.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testPsiMapper3() throws Exception {
		File file = new File("src/test/resources/testData/yeast_denormalised.xml");
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent(file.toString());
		ArrayList interactions = new ArrayList();
		MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
		mapper.doMapping();

		//  Validate Interaction at index = 0.
		Interaction interaction = (Interaction) interactions.get(0);
		validateSample3(interaction);
	}

	/**
	 * Test the PSI Mapper with Sample DIP Data.
	 * Tests Specifically for Changes in ShortLabel Data.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testDipData1() throws Exception {
		File file = new File("src/test/resources/testData/dip_sample.xml");
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent(file.toString());
		ArrayList interactions = new ArrayList();
		MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
		mapper.doMapping();

		Interaction interaction = (Interaction) interactions.get(0);
		ArrayList interactors = interaction.getInteractors();

		//  The first interactor does not have a short label.
		//  In the absence of a short label, the SwissProt.
		//  These tests verifies this fact.
		Interactor interactor = (Interactor) interactors.get(0);
		String name = interactor.getName();
		assertEquals("P06139", name);

		interactor = (Interactor) interactors.get(1);
		name = interactor.getName();
		assertEquals("major prion PrP-Sc protein precursor", name);

		//  Verify Interaction Xrefs
		ExternalReference[] refs = interaction.getExternalRefs();
		assertEquals(1, refs.length);
		assertEquals("DIP", refs[0].getDatabase());
		assertEquals("61E", refs[0].getId());
	}

	/**
	 * Test the PSI Mapper with Sample DIP Data.
	 * Tests Specifically for Multiple Experimental Results
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testDipData2() throws Exception {
		File file = new File("src/test/resources/testData/dip_sample.xml");
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent(file.toString());
		ArrayList interactions = new ArrayList();
		MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
		mapper.doMapping();

		// The sample data file has one PSI interaction.
		// with 5 experimentDescriptions.  This will map to
		// 5 data service Interaction objects.
		assertEquals(5, interactions.size());

		Interaction interaction = (Interaction) interactions.get(0);
		this.validateDipInteractions(interaction, "11821039", "Genetic", "PSI", "MI:0045");
		interaction = (Interaction) interactions.get(1);
		this.validateDipInteractions(interaction, "9174345", "x-ray crystallography", "PSI",
		                             "MI:0114");
		interaction = (Interaction) interactions.get(4);
		this.validateDipInteractions(interaction, "10089390", "x-ray crystallography", "PSI",
		                             "MI:0114");
	}

	/**
	 * Tests sample cPath File.
	 * @throws Exception All Errors.
	 */
	public void testcPathData() throws Exception {
		File file = new File("src/test/resources/testData/cpath_p53.xml");
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent(file.toString());
		ArrayList interactions = new ArrayList();
		MapPsiOneToInteractions mapper = new MapPsiOneToInteractions(xml, interactions);
		mapper.doMapping();
		assertEquals(10, interactions.size());
	}

	/**
	 * Validates Specific Interaction.
	 */
	private void validateSample1(Interaction interaction) {
		assertEquals("11283351", interaction.getAttribute(InteractionVocab.PUB_MED_ID));
		assertEquals("classical two hybrid",
		             interaction.getAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME));
		assertEquals("MI:0018",
		             interaction.getAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_ID));

		ArrayList interactors = interaction.getInteractors();
		assertEquals(4, interactors.size());

		Interactor interactor0 = (Interactor) interactors.get(0);
		Interactor interactor1 = (Interactor) interactors.get(1);
		Interactor interactor2 = (Interactor) interactors.get(2);
		Interactor interactor3 = (Interactor) interactors.get(3);

		assertEquals("A", interactor0.getName());
		assertEquals("B", interactor1.getName());
		assertEquals("C", interactor2.getName());
		assertEquals("D", interactor3.getName());

		String fullName0 = (String) interactor0.getAttribute(InteractorVocab.FULL_NAME);
		String fullName1 = (String) interactor1.getAttribute(InteractorVocab.FULL_NAME);
		String fullName2 = (String) interactor2.getAttribute(InteractorVocab.FULL_NAME);
		String fullName3 = (String) interactor3.getAttribute(InteractorVocab.FULL_NAME);
		assertTrue(fullName0.startsWith("Gene has a SET"));
		assertTrue(fullName1.startsWith("Kinesin-related"));
		assertTrue(fullName2.startsWith("SH3-domain"));
		assertTrue(fullName3.startsWith("SH3-domain"));
	}

	private void validateDipInteractions(Interaction interaction, String expectedPmid,
	                                     String expectedSystemName, String expectedDbName,
	                                     String expectedDbId) {
		String pmid = (String) interaction.getAttribute(InteractionVocab.PUB_MED_ID);
		String expName = (String) interaction.getAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
		String dbName = (String) interaction.getAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_DB);
		String dbId = (String) interaction.getAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_ID);
		assertEquals(expectedPmid, pmid);
		assertEquals(expectedSystemName, expName);
		assertEquals(expectedDbName, dbName);
		assertEquals(expectedDbId, dbId);

		ArrayList interactors = interaction.getInteractors();
		assertEquals(2, interactors.size());

		Interactor interactor = (Interactor) interactors.get(0);
		assertEquals("P06139", interactor.getName());
		interactor = (Interactor) interactors.get(1);
		assertEquals("major prion PrP-Sc protein precursor", interactor.getName());
	}

	/**
	 * Validates Specific Interaction.
	 */
	private void validateSample2(Interaction interaction) {
		ArrayList interactors = interaction.getInteractors();
		Interactor interactor0 = (Interactor) interactors.get(0);
		assertEquals("MAK10", interactor0.getName());

		Interactor interactor1 = (Interactor) interactors.get(1);
		assertEquals("MAK3", interactor1.getName());
	}

	/**
	 * Validates Specific Interaction.
	 */
	private void validateSample3(Interaction interaction) {
		ArrayList interactors = interaction.getInteractors();
		Interactor interactor0 = (Interactor) interactors.get(0);
		assertEquals("MAK10", interactor0.getName());

		Interactor interactor1 = (Interactor) interactors.get(1);
		assertEquals("MAK3", interactor1.getName());
	}

	/**
	 * Outputs Interaction (used for debugging purposes).
	 */
	private void outputInteraction(Interaction interaction) {
		System.out.println("Interaction:");

		String pubMedID = (String) interaction.getAttribute(InteractionVocab.PUB_MED_ID);
		String expSystem = (String) interaction.getAttribute(InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
		System.out.println(".. PubMedID:  " + pubMedID);
		System.out.println(".. Experimental System:  " + expSystem);

		ArrayList interactors = interaction.getInteractors();

		for (int i = 0; i < interactors.size(); i++) {
			Interactor interactor = (Interactor) interactors.get(i);
			outputInteractor(interactor);
		}
	}

	/**
	 * Outputs Interactor.
	 */
	private void outputInteractor(Interactor interactor) {
		System.out.println(".. Interactor:  " + interactor.getName());
		System.out.println("..... Description:  " + interactor.getDescription());
	}
}
