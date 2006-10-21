package org.cytoscape.coreplugin.psi_mi.plugin;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import java.util.ArrayList;

import org.cytoscape.coreplugin.psi_mi.util.ContentReader;
import org.cytoscape.coreplugin.psi_mi.util.DataServiceException;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiTwoFiveToInteractions;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapperException;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiOneToInteractions;
import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapToCytoscape;

public class PsiMiPlugIn extends CytoscapePlugin {

    public PsiMiPlugIn() throws Exception {
        loadPsi1();
        loadPsi2();
    }

    private void loadPsi1() throws Exception {
        //  First, get some interactions from sample data file.
        ArrayList interactions = new ArrayList();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/psi_sample1.xml");

        //  Map from PSI One to DataService Interaction Objects.
        MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions
                (xml, interactions);
        mapper1.doMapping();

        //  Now Map to Cytocape Network Objects.
        CyNetwork network = Cytoscape.createNetwork("psi_1");
        MapToCytoscape mapper2 = new MapToCytoscape
                (interactions, network, MapToCytoscape.MATRIX_VIEW);
        mapper2.doMapping();
    }

    private void loadPsi2() throws DataServiceException, MapperException {
        //  First, get some interactions from sample data file.
        ArrayList interactions = new ArrayList();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/psi_sample_2_5_2.xml");

        //  Map from PSI to DataService Interaction Objects.
        MapPsiTwoFiveToInteractions mapper1 = new MapPsiTwoFiveToInteractions
                (xml, interactions);
        mapper1.doMapping();

        //  Create CyNetwork
        CyNetwork network = Cytoscape.createNetwork("psi_2_5");

        //  Now map interactions to cyNetwork.
        MapToCytoscape mapper2 = new MapToCytoscape
                (interactions, network, MapToCytoscape.SPOKE_VIEW);
        mapper2.doMapping();
    }
}
