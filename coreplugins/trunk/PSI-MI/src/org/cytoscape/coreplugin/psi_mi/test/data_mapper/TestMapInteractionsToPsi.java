/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.cytoscape.coreplugin.psi_mi.test.data_mapper;

import junit.framework.TestCase;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.*;

import java.io.File;
import java.util.ArrayList;

import org.cytoscape.coreplugin.psi_mi.data_mapper.MapInteractionsToPsiOne;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiTwoFiveToInteractions;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.util.ContentReader;

/**
 * Tests MapInteractionsToPsiOne.
 *
 * @author Ethan Cerami
 */
public class TestMapInteractionsToPsi extends TestCase {

    /**
     * Tests Mapper with Sample PSI Data File.
     *
     * @throws Exception All Exceptions.
     */
    public void testMapper() throws Exception {
        File file = new File("testData/psi_sample1.xml");
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent(file.toString());
        ArrayList interactions = new ArrayList();
        MapPsiTwoFiveToInteractions mapFromPsi = new MapPsiTwoFiveToInteractions
                (xml, interactions);
        mapFromPsi.doMapping();

        //MapInteractionsToPsiOne mapToPsiOne = new MapInteractionsToPsiOne(interactions);
        //mapToPsiOne.doMapping();
       /* EntrySet entrySet = mapToPsiOne.getPsiXml();

        validateInteractors(entrySet.getEntry(0).getInteractorList());
        validateInteractions(entrySet.getEntry(0).getInteractionList());

        StringWriter writer = new StringWriter();
        entrySet.marshal(writer);

        //  Verify that XML indentation is turned on.
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<entrySet level=\"1\" version=\"1\" "
                + "xmlns=\"net:sf:psidev:mi\">\n"
                + "    <entry>\n"
                + "        <interactorList>\n";
        assertTrue("XML Indentation Test has failed.  "
                + "This test probably failed because you updated to a new "
                + "version of Castor.  If so, you need to unjar the castor.jar "
                + "file, modify the castor.properties file to turn indentation "
                + "on, and then rejar it.",
                writer.toString().startsWith(expected));

                */
    }

    /**
     * Tests Mapper with a Minimal Set of Data.
     *
     * @throws Exception All Exceptions.
     */
    public void testMapperWithBareBonesData() throws Exception {
        ArrayList interactions = new ArrayList();
        org.cytoscape.coreplugin.psi_mi.model.Interaction interaction = new org.cytoscape.coreplugin.psi_mi.model. Interaction();
        Interactor interactorA = new Interactor();
        interactorA.setName("A");
        Interactor interactorB = new Interactor();
        interactorB.setName("B");
        ArrayList interactors = new ArrayList();
        interactors.add(interactorA);
        interactors.add(interactorB);
        interaction.setInteractors(interactors);
        interactions.add(interaction);

        //MapInteractionsToPsiOne mapToPsiOne = new MapInteractionsToPsiOne(interactions);
        //mapToPsiOne.doMapping();
    /*    EntrySet entrySet = mapToPsiOne.getPsiXml();

        StringWriter writer = new StringWriter();
        entrySet.marshal(writer);

        assertEquals(true, entrySet.isValid());*/
    }

    /**
     * Validates Interactor Objects.
     *
     * @param interactorList Castor InteractorList Object.
     */
    private void validateInteractors(EntrySet.Entry.InteractorList interactorList) {
  /*      ProteinInteractorType interactor =
                interactorList.getProteinInteractor(0);
        NamesType name = interactor.getNames();
        assertEquals("YHR119W", name.getShortLabel());
        assertTrue(name.getFullName().startsWith("Gene has a SET or TROMO"));

        Organism organism = interactor.getOrganism();
        assertEquals(4932, organism.getNcbiTaxId());
        assertEquals("baker's yeast", organism.getNames().getShortLabel());
        assertEquals("Saccharomyces cerevisiae",
                organism.getNames().getFullName());

        assertEquals("YHR119W", interactor.getId());

        XrefType xrefType = interactor.getXref();
        DbReferenceType xref = xrefType.getPrimaryRef();
        assertEquals("Entrez GI", xref.getDb());
        assertEquals("529135", xref.getId());

        xref = xrefType.getSecondaryRef(0);
        assertEquals("RefSeq GI", xref.getDb());
        assertEquals("6321911", xref.getId());

        String sequence = interactor.getSequence();
        assertTrue(sequence.startsWith("MNTYAQESKLRLKTKIGAD"));*/
    }

    /**
     * Validates Interaction Objects.
     *
     * @param interactionList Castor Interaction Object.
     */
    private void validateInteractions(EntrySet.Entry.InteractionList interactionList) {
      /*  InteractionElementType interaction = interactionList.getInteraction(0);
        ExperimentList expList = interaction.getExperimentList();
        ExperimentListItem expItem = expList.getExperimentListItem(0);
        ExperimentType expType = expItem.getExperimentDescription();
        BibrefType bibRef = expType.getBibref();
        XrefType xref = bibRef.getXref();
        DbReferenceType primaryRef = xref.getPrimaryRef();
        assertEquals("pubmed", primaryRef.getDb());
        assertEquals("11283351", primaryRef.getId());

        CvType cvType = expType.getInteractionDetection();
        NamesType name = cvType.getNames();
        assertEquals("classical two hybrid", name.getShortLabel());
        xref = cvType.getXref();
        primaryRef = xref.getPrimaryRef();
        assertEquals("PSI-MI", primaryRef.getDb());
        assertEquals("MI:0018", primaryRef.getId());

        ParticipantList pList = interaction.getParticipantList();
        ProteinParticipantType participant = pList.getProteinParticipant(0);
        ProteinParticipantTypeChoice choice =
                participant.getProteinParticipantTypeChoice();
        RefType ref = choice.getProteinInteractorRef();
        String reference = ref.getRef();
        assertEquals("YCR038C", reference);

        //  Verify Interaction XRefs.
        xref = interaction.getXref();
        primaryRef = xref.getPrimaryRef();
        String db = primaryRef.getDb();
        String id = primaryRef.getId();
        assertEquals ("CPATH", db);
        assertEquals ("1", id);*/
    }
}
