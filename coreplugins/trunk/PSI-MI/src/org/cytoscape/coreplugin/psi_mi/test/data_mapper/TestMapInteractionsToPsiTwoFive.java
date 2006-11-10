package org.cytoscape.coreplugin.psi_mi.test.data_mapper;

import junit.framework.TestCase;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.math.BigInteger;

import org.cytoscape.coreplugin.psi_mi.util.ContentReader;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiOneToInteractions;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapInteractionsToPsiTwoFive;
import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapToCytoscape;
import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapFromCytoscape;
import org.cytoscape.coreplugin.psi_mi.schema.mi25.*;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 * Tests MapInteractionsToPsiTwoFive.
 *
 * @author Ethan Cerami
 */
public class TestMapInteractionsToPsiTwoFive extends TestCase {

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

        //  First map PSI-MI Level 1 to interaction objects
        MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions (xml, interactions);
        mapper1.doMapping();
        assertEquals (6, interactions.size());

        //  Second, map to Cytoscape objects
        CyNetwork network = Cytoscape.createNetwork("network1");
        MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.SPOKE_VIEW);
        mapper2.doMapping();
        addToCyNetwork (mapper2, network);

        //  Verify Number of Nodes and Number of Edges
        int nodeCount = network.getNodeCount();
        int edgeCount = network.getEdgeCount();
        assertEquals(7, nodeCount);
        assertEquals(6, edgeCount);

        //  Third, map back to interaction Objects
        MapFromCytoscape mapper3 = new MapFromCytoscape (network);
        mapper3.doMapping();
        interactions = mapper3.getInteractions();
        assertEquals (6, interactions.size());

        //  Fourth, map to PSI-MI Level 2.5
        MapInteractionsToPsiTwoFive mapper4 = new MapInteractionsToPsiTwoFive (interactions);
        mapper4.doMapping();

        EntrySet entrySet = mapper4.getPsiXml();
        validateInteractors(entrySet.getEntry().get(0).getInteractorList());

        validateInteractions(entrySet.getEntry().get(0).getInteractionList());
        StringWriter writer = new StringWriter();
        JAXBContext jc = JAXBContext.newInstance(
                "org.cytoscape.coreplugin.psi_mi.schema.mi25");
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(entrySet, writer);

        // System.out.println(writer.toString());

        //  Verify that XML indentation is turned on.
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<entrySet version=\"5\" level=\"2\" xmlns=\"net:sf:psidev:mi\">\n" +
                "    <entry>\n" +
                "        <interactorList>";
        assertTrue("XML Indentation Test has failed.  ",
               writer.toString().startsWith(expected));
    }

    private void addToCyNetwork(MapToCytoscape mapper, CyNetwork cyNetwork) {
        //  Add new nodes/edges to network
        int nodeIndices[] = mapper.getNodeIndices();
        int edgeIndices[] = mapper.getEdgeIndices();
        for (int i=0; i<nodeIndices.length; i++) {
            cyNetwork.addNode(nodeIndices[i]);
        }
        for (int i=0; i<edgeIndices.length; i++) {
            cyNetwork.addEdge(edgeIndices[i]);
        }
    }

    /**
     * Validates Interactor Objects.
     *
     * @param interactorList Castor InteractorList Object.
     */
    private void validateInteractors(EntrySet.Entry.InteractorList interactorList) {
        InteractorElementType interactor = interactorList.getInteractor().get(0);
        NamesType name = interactor.getNames();
        assertEquals("YHR119W", name.getShortLabel());
        assertTrue(name.getFullName().startsWith("Gene has a SET or TROMO"));

        InteractorElementType.Organism organism = interactor.getOrganism();
        assertEquals(4932, organism.getNcbiTaxId());
        assertEquals("baker's yeast", organism.getNames().getShortLabel());
        assertEquals("Saccharomyces cerevisiae",
                organism.getNames().getFullName());

        assertEquals(0, interactor.getId());

        XrefType xrefType = interactor.getXref();
        DbReferenceType xref = xrefType.getPrimaryRef();
        assertEquals("Entrez GI", xref.getDb());
        assertEquals("529135", xref.getId());

        xref = xrefType.getSecondaryRef().get(0);
        assertEquals("RefSeq GI", xref.getDb());
        assertEquals("6321911", xref.getId());

        String sequence = interactor.getSequence();
        assertTrue(sequence.startsWith("MNTYAQESKLRLKTKIGAD"));
    }

    /**
     * Validates Interaction Objects.
     *
     * @param interactionList Castor Interaction Object.
     */
    private void validateInteractions(EntrySet.Entry.InteractionList interactionList) {
        InteractionElementType interaction = interactionList.getInteraction().get(3);
        InteractionElementType.ExperimentList expList = interaction.getExperimentList();
        ExperimentType expType = (ExperimentType)
                expList.getExperimentRefOrExperimentDescription().get(0);
        BibrefType bibRef = expType.getBibref();
        XrefType xref = bibRef.getXref();
        DbReferenceType primaryRef = xref.getPrimaryRef();
        assertEquals("pubmed", primaryRef.getDb());
        assertEquals("11283351", primaryRef.getId());

        CvType cvType = expType.getInteractionDetectionMethod();
        NamesType name = cvType.getNames();
        assertEquals("classical two hybrid", name.getShortLabel());
        xref = cvType.getXref();
        primaryRef = xref.getPrimaryRef();
        assertEquals("PSI-MI", primaryRef.getDb());
        assertEquals("MI:0018", primaryRef.getId());

        InteractionElementType.ParticipantList pList = interaction.getParticipantList();
        ParticipantType participant = pList.getParticipant().get(0);
        Integer ref = participant.getInteractorRef();
        assertEquals(new Integer(2), ref);

        //  Verify Interaction XRefs.
        xref = interaction.getXref();
        primaryRef = xref.getPrimaryRef();
        String db = primaryRef.getDb();
        String id = primaryRef.getId();
        assertEquals ("DIP", db);
        assertEquals ("61E", id);
    }
}
