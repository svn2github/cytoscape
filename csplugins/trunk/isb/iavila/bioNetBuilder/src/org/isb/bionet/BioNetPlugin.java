/**
 * 
 */
package org.isb.bionet;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.data.Semantics;
import org.isb.xmlrpc.client.*;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.gui.*;
import org.isb.bionet.gui.wizard.*;

import java.lang.Exception;
import java.util.*;

/**
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class BioNetPlugin extends CytoscapePlugin {

    /**
     * Constructor
     */
    public BioNetPlugin() {

        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
                new AbstractAction("Build a Biological Network...") {
                    public void actionPerformed(ActionEvent e) {
                        InteractionDataClient interactionsClient = null;
                        try {
                                    
                            interactionsClient = (InteractionDataClient) DataClientFactory
                                    .getClient("interactions");
                            if (interactionsClient != null) {
                                System.out
                                        .println("Successfully got an InteractionDataClient!!!");
                            } else {
                                System.out
                                        .println(":-) Could not get an InteractionDataClient!!!");
                            }

                            interactionsClient
                                    .addSource("org.isb.bionet.datasource.interactions.ProlinksInteractionsSource");
                            System.out.println(interactionsClient.getSources());
                           
                            NetworkBuilderWizard wizard = new NetworkBuilderWizard(interactionsClient);
                            wizard.startWizard();
                            
                        }catch (Exception ex){
                              ex.printStackTrace();
                        }
                            
                    }// actionPerformed
                });
    }// BioNetPlugin

}// BioNetPlugin
