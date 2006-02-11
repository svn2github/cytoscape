/**
 * 
 */
package org.isb.bionet;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import cytoscape.*;
import cytoscape.plugin.*;
import org.isb.xmlrpc.client.*;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.datasource.synonyms.*;
import org.isb.bionet.gui.wizard.*;
import org.isb.iavila.ontology.xmlrpc.*;
import java.lang.Exception;

/**
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class BioNetPlugin extends CytoscapePlugin {

    protected InteractionDataClient interactionsClient;
    protected GOClient goClient;
    protected SynonymsClient synClient;

    protected NetworkBuilderWizard wizard;

    /**
     * Constructor
     */
    public BioNetPlugin() {

       // XmlRpc.setDebug(true);
        
        try {

            this.interactionsClient = (InteractionDataClient) DataClientFactory
                    .getClient("interactions");
            

            if (this.interactionsClient != null) {
                System.out
                        .println("Successfully got an InteractionDataClient!!!");
            } else {
                System.out
                        .println("Could not get an InteractionDataClient!!!");
            }
            
            this.goClient = (GOClient)DataClientFactory.getClient("geneOntology");
            if (this.goClient != null) {
                System.out
                        .println("Successfully got a GOClient!!!");
            } else {
                System.out
                        .println("Could not get a GOClient!!!");
            }
            
            this.synClient = (SynonymsClient)DataClientFactory.getClient("synonyms");
            if (this.synClient != null) {
                System.out
                        .println("Successfully got a SynonymsClient!!!");
            } else {
                System.out
                        .println("Could not get a SynonymsClient!!!");
            }
 
            this.interactionsClient.addSource(ProlinksInteractionsSource.class.getName());
            this.interactionsClient.addSource(KeggInteractionsSource.class.getName());
            this.interactionsClient.addSource(BindInteractionsSource.class.getName());
            this.interactionsClient.addSource(DipInteractionsSource.class.getName());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.wizard = new NetworkBuilderWizard(this.synClient, this.interactionsClient, this.goClient);

        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
                new AbstractAction("Build a Biological Network...") {
                    public void actionPerformed(ActionEvent e) {
                        wizard.startWizard();
                    }// actionPerformed
                });
    }// BioNetPlugin

}// BioNetPlugin
