
package org.isb.iavila.ontology;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.isb.xmlrpc.client.DataClientFactory;
import org.isb.iavila.ontology.gui.*;
import org.isb.iavila.ontology.xmlrpc.*;
import cytoscape.Cytoscape;
import cytoscape.plugin.*;

public class OntologyPlugin extends CytoscapePlugin {
    
    protected GOClient goClient;
    protected CytoscapeGODialog goDialog;
    
    /**
     * Constructor
     */
    public OntologyPlugin (){
        
        try{
            this.goClient = (GOClient)DataClientFactory.getClient("geneOntology");
            if (this.goClient != null) {
                System.out.println("Successfully got a GOClient!!!");
                this.goDialog = new CytoscapeGODialog(this.goClient);
            } else {
                System.out.println("Could not get a GOClient!!!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }//catch
        
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
                new AbstractAction("Gene Ontology...") {
                    
                    public void actionPerformed(ActionEvent e) {
        
                        if(goDialog != null){
                            goDialog.pack();
                            goDialog.setLocationRelativeTo(Cytoscape.getDesktop());
                            goDialog.setVisible(true);
                      }else{
                          JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                  "No Gene Ontology Client available","Error",
                                  JOptionPane.ERROR_MESSAGE);
                      }//else
                      
                    }// actionPerformed
                });
    }//OntologyPlugin
    
}
