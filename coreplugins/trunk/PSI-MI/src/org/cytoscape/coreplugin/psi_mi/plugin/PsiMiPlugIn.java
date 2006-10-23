package org.cytoscape.coreplugin.psi_mi.plugin;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.ImportHandler;

import java.util.ArrayList;

import org.cytoscape.coreplugin.psi_mi.util.ContentReader;
import org.cytoscape.coreplugin.psi_mi.util.DataServiceException;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiTwoFiveToInteractions;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapperException;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiOneToInteractions;
import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapToCytoscape;

public class PsiMiPlugIn extends CytoscapePlugin {

    /**
     * PSI-MI Plugin
     */
    public PsiMiPlugIn() {

        //  Register PsiMiFilter
        ImportHandler importHandler = new ImportHandler();
        importHandler.addFilter(new PsiMiFilter());
    }
}
