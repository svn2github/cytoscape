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
                            
                            // TESTING FOR NOW
                            
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

                            
                            //ProlinksGui prolinksGui = new ProlinksGui();
                            //prolinksGui.pack();
                            //prolinksGui.setLocationRelativeTo(Cytoscape.getDesktop());
                            //prolinksGui.setVisible(true);
                            
                            Hashtable args = new Hashtable();
                            Vector methods = new Vector();
                            methods.add(ProlinksInteractionsSource.PP);
                            args.put(InteractionsDataSource.INTERACTION_TYPE,
                                    methods);
                            args.put(ProlinksInteractionsSource.PVAL,
                                    new Double(0.00005));
                            args.put(InteractionsDataSource.DIRECTED, Boolean.TRUE);

                            String species = "Saccharomyces_cerevisiae";
                            CyNetwork network = Utils.makeNewNetwork(
                                    interactionsClient.getAllInteractions(
                                            species, args), "myNet");
                            Cytoscape.getDesktop().setFocus("myNet");
                            
                            System.out.println("After getAllInteractions, network has " + network.getNodeCount() + " nodes and " + network.getEdgeCount() + " edges.");
                            
                            List nodes = Cytoscape.getCyNodesList();
                            Iterator it = nodes.iterator();
                            Vector nodeNames = new Vector();
                            while(it.hasNext()){
                                CyNode node = (CyNode)it.next();
                                String name = (String)Cytoscape.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
                                nodeNames.add(name);
                            }//while it.hasNext
                            
                            Vector vOfv = interactionsClient.getAdjacentInteractions(nodeNames, species, args);
                            it = vOfv.iterator();
                            
                            while(it.hasNext()){
                                Utils.addInteractionsToNetwork(network, (Vector)it.next());
                                System.out.println("After addInteractionsToNetwork, network has " + network.getNodeCount() + " nodes and " + network.getEdgeCount() + " edges.");
                            }
                            
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }// catch
                    }// actionPerformed
                });
    }// BioNetPlugin

}// BioNetPlugin
