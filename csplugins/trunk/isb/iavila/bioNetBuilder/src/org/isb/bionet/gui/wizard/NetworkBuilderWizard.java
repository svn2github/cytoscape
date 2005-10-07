package org.isb.bionet.gui.wizard;

import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.datasource.synonyms.*;
import org.isb.iavila.ontology.xmlrpc.*;
import org.isb.bionet.gui.ProlinksGui;
import org.isb.bionet.CyNetUtils;
import cytoscape.*;

/**
 * 
 * @author iavila
 * TODO: "Estimate" buttons to calculate num nodes and edges
 */
public class NetworkBuilderWizard {
    
    // Clients
    protected SynonymsClient synonymsClient;
    protected InteractionDataClient interactionsClient;
    protected GOClient goClient;
    
    // Panels and dialogs
    protected List dialogs;
    protected SpeciesPanel speciesPanel;
    protected NodeSourcesPanel nodeSourcesPanel;
    protected EdgeSourcesPanel edgeSourcesPanel;
    protected NetworkSettingsPanel networkPanel;
    
    // Bookkeeping
    protected int currentStep;
    protected boolean onLastStep = false;
    
    // Actions
    protected AbstractAction DEFAULT_BACK_ACTION = new AbstractAction(){
            
            public void actionPerformed (ActionEvent e){
                displayStep(currentStep-1);
            }
    };
    
    protected AbstractAction DEFAULT_NEXT_ACTION = new AbstractAction(){
                
                public void actionPerformed (ActionEvent e){
                    displayStep(currentStep+1);
                }
        
    };
    
    protected AbstractAction FINISH_ACTION;
    
    /**
     * 
     * @param 
     */
    public NetworkBuilderWizard (SynonymsClient synonyms_client,
                InteractionDataClient interactions_client, GOClient go_client){
        this.synonymsClient = synonyms_client;
        this.interactionsClient = interactions_client;
        this.goClient = go_client;
        FINISH_ACTION = new AbstractAction (){
            public void actionPerformed (ActionEvent event){
                createNetwork();
                JDialog currentDialog = (JDialog)dialogs.get(currentStep);
                currentDialog.setVisible(false);
            }
            
        };
        createDialogs();
    }//constructor
    
    /**
     * Starts the wizard.
     */
    public void startWizard (){
        this.onLastStep = false;
        displayStep(0);
    }//startWizard
    
    /**
     * Displays the dialog at position step in this.dialogs
     * @param step
     */
    protected void displayStep (int step){      
        JDialog prevDialog = (JDialog)this.dialogs.get(this.currentStep);
        this.currentStep = step;
        if(this.currentStep == this.dialogs.size()-1){
            this.onLastStep = true;
        }else{
            this.onLastStep = false;
        }
        JDialog dialog = (JDialog)this.dialogs.get(this.currentStep);
        dialog.setLocationRelativeTo(prevDialog);
        if(prevDialog.isVisible()){
            prevDialog.setVisible(false);
        }
        dialog.setVisible(true);
    }//dsiplayStep
    
    /**
     * Creates all the dialogs in order of steps
     */
    public void createDialogs (){
        this.dialogs = new ArrayList();
        
        this.currentStep = -1;
        
        // Create the dialog for selecting species
        this.currentStep++;
        JDialog speciesDialog = createSpeciesDialog();
        this.dialogs.add(this.currentStep, speciesDialog);
        
        // Create the dialog to select nodes
        this.currentStep++;
        JDialog nodesDialog = createNodeSourcesDialog();
        this.dialogs.add(this.currentStep, nodesDialog);
        
        // Create the dialog to select edges
        this.currentStep++;
        JDialog edgesDialog = createEdgeSourcesDialog();
        this.dialogs.add(this.currentStep, edgesDialog);
        
        // Create the dialog for network settings
        this.currentStep++;
        this.onLastStep = true;
        JDialog netDialog = createNetworkSettingsDialog();
        this.dialogs.add(this.currentStep,netDialog);
    }//start
    
    
    /**
     * Creates a dialog with a BorderLayout, the SOUTH portion of the dialog contains wizard buttons (back, next, cancel)
     * @return a JDialog
     */
    protected JDialog createWizardDialog (AbstractAction backAction, AbstractAction nextAction){
        
        JDialog dialog = new JDialog(Cytoscape.getDesktop());
        dialog.setTitle("BioNetwork Builder");
        dialog.setSize(400, 400);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel buttons = createWizardButtons(backAction, nextAction);
        
        panel.add(buttons, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        
        return dialog;
        
    }//createWizardDialog
    
    /**
     * Creates Back, Next, and Cancel buttons
     * @return
     */
    protected JPanel createWizardButtons (AbstractAction backAction, AbstractAction nextAction){
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        JButton back = new JButton("< Back");
        if(backAction != null){
            back.addActionListener(backAction);
        }else{
            back.setEnabled(false);
        }
 
        JButton next = new JButton("Next >");
        next.addActionListener(nextAction);
        
        if(this.onLastStep){
            next.setText("Finish");
        }
        
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new AbstractAction(){
            public void actionPerformed (ActionEvent event){
                JDialog dialog = (JDialog)dialogs.get(currentStep);
                dialog.dispose();
            }
        });
        
        panel.add(back);
        panel.add(next);
        panel.add(cancel);
        
        return panel;
    }//createWizardButtons
    
    
    protected JPanel createExplanationPanel (String explanation){
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(explanation);
        panel.add(label);
        return panel;
    }
    
    //TODO: Each data source should have a dialog that pops-up with the species
    //TODO: Explanation: must choose same species for each data source
    //TODO: In the Synonyms db, have a species table? This would solve the above two items
    protected JDialog createSpeciesDialog (){
        
        AbstractAction back, next;
        if(this.currentStep == 0){
            back = null;
        }else{
            back = DEFAULT_BACK_ACTION;
        }
        
        next = new AbstractAction (){
            public void actionPerformed (ActionEvent event){
                Map table = (Map)speciesPanel.getSourcesSelectedSpecies();
                if(table.size() == 0){
                    JOptionPane.showMessageDialog(speciesPanel,"Please select a species", "Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    // enable edge sources in the edges dialog
                    if(edgeSourcesPanel == null)
                        return;
                    Map sourceToName = (Map)speciesPanel.getSourcesNames();
                    Map sourcesToSpecies = speciesPanel.getSourcesSelectedSpecies();
                    edgeSourcesPanel.setSourcesToSpecies(sourcesToSpecies);
                    Iterator it = table.keySet().iterator();
                    while(it.hasNext()){
                        String name = (String)sourceToName.get(it.next());
                        edgeSourcesPanel.setSourceButtonEnabled(name, true);
                    }//while it
                    
                    // TODO: Disable the ones that are not used!
                    
                    
                    if(onLastStep){
                        FINISH_ACTION.actionPerformed(event);
                    }else{
                        DEFAULT_NEXT_ACTION.actionPerformed(event);
                    }
                  }//else
            }//actionPerformed
        };
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = 
            createExplanationPanel("<html><br>Select a species for your biological network from your<br>desired data sources.<br></html>");
        dialog.getContentPane().add(explanation, BorderLayout.NORTH);
        
        try{
            Hashtable sourceToSp = this.interactionsClient.getSupportedSpeciesForEachSource();
            Hashtable  sourceToName = this.interactionsClient.getSourcesNames();
            this.speciesPanel = new SpeciesPanel(sourceToSp, sourceToName);
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.speciesPanel,
                    "<html>There was an error while attempting to obtain supported species!<br>"+ e.getMessage() +"<br></html>", 
                    "Error",JOptionPane.ERROR_MESSAGE);
            Hashtable emptyTable = new Hashtable();
            this.speciesPanel = new SpeciesPanel(emptyTable, emptyTable);
        }
       
        dialog.getContentPane().add(this.speciesPanel, BorderLayout.CENTER);
        
        return dialog;
        
    }//createSpeciesDialog
    
    
    protected JDialog createNodeSourcesDialog (){
        
        AbstractAction back, next;
        if(this.currentStep == 0){
            back = null;
        }else{
            back = DEFAULT_BACK_ACTION;
        }
        
        
        next = new AbstractAction (){
            
            public void actionPerformed (ActionEvent event){
               // if()
                Vector nodes = nodeSourcesPanel.getAllNodes();
               
                if(edgeSourcesPanel != null){
                    edgeSourcesPanel.setNodes(nodes);
                    if(nodes.size() > 0){
                        edgeSourcesPanel.setEdgeMethod(InteractionsDataSource.CONNECTING_EDGES);
                    }else{
                        edgeSourcesPanel.setEdgeMethod(InteractionsDataSource.ALL_EDGES);
                    }
                    // This takes too long, so there is a "Calculate number of edges" button instead
                    //edgeSourcesPanel.estimateNumEdges();
                }// edgeSourcesPanel != null
                
                if(onLastStep){
                    FINISH_ACTION.actionPerformed(event);
                }else{
                    DEFAULT_NEXT_ACTION.actionPerformed(event);
                }
                
            }//actionPerformed
            
        };
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = 
            createExplanationPanel("<html><br>Select the sources for the nodes in your biological network.<br>"+
                    "Advanced settings for some sources are available if you"+
                    "<br>press the source's corresponding button.<br><br>"+
                    "If you don't select any node sources, then nodes will be"+
                    "<br>created automatically when edges are created (next step).<br>" +
                    "</html>"); 
        
        dialog.getContentPane().add(explanation,BorderLayout.NORTH);
        
        this.nodeSourcesPanel = new NodeSourcesPanel(this.goClient);
        
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bigPanel.add(this.nodeSourcesPanel);
       
        dialog.getContentPane().add(bigPanel, BorderLayout.CENTER);
        
        return dialog;
        
    }
    
    protected JDialog createEdgeSourcesDialog (){
        
        AbstractAction back, next;
        if(this.currentStep == 0){
            back = null;
        }else{
            back = DEFAULT_BACK_ACTION;
        }
        
        // Data sources contain default parameters, so even if the user does not
        // change anything here, we are OK
        if(this.onLastStep){
            next =  FINISH_ACTION;
        }else{
            next = DEFAULT_NEXT_ACTION;
        }
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = createExplanationPanel(
                "<html><br>The edge sources that you selected when specifying species<br>"+
                "are available here.<br>"+
                "You can set their parameters by pressing on their corresponding<br>buttons.<br></htlm>"
        ); 
        
        dialog.getContentPane().add(explanation,BorderLayout.NORTH);
        
        Map sourcesToSpecies;
        
        if(this.speciesPanel != null){
            sourcesToSpecies = this.speciesPanel.getSourcesSelectedSpecies();
        }else{
           sourcesToSpecies = new Hashtable();
        }
        
        Vector nodes = null;
        int method;
        if(this.nodeSourcesPanel != null){
            nodes = this.nodeSourcesPanel.getAllNodes();
            method = InteractionsDataSource.CONNECTING_EDGES;
        }else{
            method = InteractionsDataSource.ALL_EDGES;
        }
        
        this.edgeSourcesPanel = 
            new EdgeSourcesPanel(this.interactionsClient, this.synonymsClient, 
                    sourcesToSpecies, nodes, method);
        
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bigPanel.add(this.edgeSourcesPanel);
       
        dialog.getContentPane().add(bigPanel, BorderLayout.CENTER);
        
        return dialog;
        
    }
    
    protected JDialog createNetworkSettingsDialog (){
        
        AbstractAction back, next;
        
        if(this.currentStep == 0){
            back = null;
        }else{
            back = DEFAULT_BACK_ACTION;
        }

        
        next = new AbstractAction (){
            public void actionPerformed (ActionEvent event){
                String name = networkPanel.getNetworkName();
                if(name == null || name.length() == 0){
                    JOptionPane.showMessageDialog(networkPanel,"Please enter a name for your network.", "Error", JOptionPane.ERROR_MESSAGE);
                }else{
                    if(onLastStep){
                        FINISH_ACTION.actionPerformed(event);
                    }else{
                        DEFAULT_NEXT_ACTION.actionPerformed(event);
                    }
                 }//else
            }//actionPerformed
        };//AbstractAction
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = createExplanationPanel(
                "<html><br>Set parameters for your new biological network.<br></html>"
        );
        
        dialog.getContentPane().add(explanation, BorderLayout.NORTH);
        
        this.networkPanel = new NetworkSettingsPanel();
        
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bigPanel.add(this.networkPanel);
       
        dialog.getContentPane().add(bigPanel, BorderLayout.CENTER);
        
        return dialog;
    }
    
    
    // TODO: Organize this better, this is quick and dirty, maybe a new class???
    // TODO: Need to pass to the interactions source handler a list of genes with id's that it understands (synonyms handler)
    protected void createNetwork (){
        // 1. Get the species for each edge data source
        Map sourceToSpecies = this.speciesPanel.getSourcesSelectedSpecies();
        Map sourceToNames = this.speciesPanel.getSourcesNames();
        
        // 2. Get the starting nodes for the network (if any)
        Vector startingNodes = this.nodeSourcesPanel.getAllNodes();
        
        
        // 3. Get the edge data source parameter settings
        Map sourceToSettings = this.edgeSourcesPanel.getSourcesDialogs();
        Iterator it = sourceToSettings.keySet().iterator();
        
        // 4. Get the network name
        String netName = this.networkPanel.getNetworkName();
        
        // 5. Iterate over all the edge data sources and accumulate interactions
        Vector interactions = new Vector();
        while(it.hasNext()){
            
            String sourceClass = (String)it.next();
            if(!this.edgeSourcesPanel.isSourceSelected(sourceClass)) continue;
          
            List sourceSpecies = (List)sourceToSpecies.get(sourceClass);
            if(sourceSpecies == null || sourceSpecies.size() == 0) continue;
            
            
            String species = (String)sourceSpecies.get(0);
            String sourceName = (String)sourceToNames.get(sourceClass);
            
      
            Hashtable args = new Hashtable();
            if(sourceName.equals(ProlinksInteractionsSource.NAME)){
                
                ProlinksGui prolinksGui = (ProlinksGui)sourceToSettings.get(sourceClass);
                Vector interactionTypes = prolinksGui.getSelectedInteractionTypes();
                double pvalTh = prolinksGui.getPval(false);
                System.out.println("------- Prolinks settings (createNetwork)----------");
                System.out.println("interactionTypes = " + interactionTypes);
                System.out.println("pval = " + pvalTh);
                System.out.println("species = " + sourceSpecies);
                System.out.println("---------------------------------------------------");
                
                if(pvalTh != 1){
                    args.put(ProlinksInteractionsSource.PVAL, new Double(pvalTh));
                }
                
                if(interactionTypes.size() < 4){
                    args.put(ProlinksInteractionsSource.INTERACTION_TYPE, interactionTypes);
                }
                
            //    params.add(args);
            
            }//if prolinks
                
            //String method;
            Vector sourceInteractions = null;
            try{
                if(startingNodes == null || startingNodes.size() == 0){
                    //method = "getAllInteractions";
                    if(args.size() > 0){
                        sourceInteractions = (Vector)this.interactionsClient.getAllInteractions(species, args);
                    }else{
                        sourceInteractions = (Vector)this.interactionsClient.getAllInteractions(species);
                    }
                }else{
                    if(args.size() > 0){
                        sourceInteractions = (Vector)this.interactionsClient.getConnectingInteractions(startingNodes, species, args);
                    }else{
                        sourceInteractions = (Vector)this.interactionsClient.getConnectingInteractions(startingNodes, species);
                    }
                 //   method = "getConnectingInteractions";
                }
                interactions.addAll(sourceInteractions);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            
            //try{
              //  Vector sourceInteractions = (Vector)this.interactionsClient.callSourceMethod(sourceClass,method,params);
                //TODO: Translate to GI ids
                //translateToGI(sourceInteractions, targetID);
                //interactions.addAll(sourceInteractions);
            //}catch (Exception e){
              //  e.printStackTrace();
           // }
            
        }//while it
        
        // 6. Make a network!
        CyNetwork newNet = CyNetUtils.makeNewNetwork(interactions, netName);
        
        //7. If requested, create Rosetta attribute
        if(this.networkPanel.createRosettaURLAttribute()){
            CyNetUtils.createRosettaURLNodeAttribute(newNet);
        }
        
        
    }//createNetwork
    
    /**
     * 
     * @param id an ID
     * @return one of:<br>
     * PROLINKS_ID, KEGG_ID, GI_ID, or ID_NOT_FOUND
     */
    public String getIdType (String id){
        String [] tokens = id.split(":");
        if(tokens.length == 0) return SynonymsSource.ID_NOT_FOUND;
        if(tokens[0].equals(SynonymsSource.PROLINKS_ID)) return SynonymsSource.PROLINKS_ID;
        if(tokens[0].equals(SynonymsSource.KEGG_ID)) return SynonymsSource.KEGG_ID;
        if(tokens[0].equals(SynonymsSource.GI_ID)) return SynonymsSource.GI_ID;
        return SynonymsSource.ID_NOT_FOUND;
    }
    
    //TODO: Remove?
    private void translateToGI (Vector interactions, String id_type){
     
        Iterator it = interactions.iterator();
        Set genes = new HashSet (); 
        while(it.hasNext()){
            Hashtable anInteraction = (Hashtable)it.next();
            String gene1 = (String)anInteraction.get(InteractionsDataSource.INTERACTOR_1);
            String gene2 = (String)anInteraction.get(InteractionsDataSource.INTERACTOR_2);
            genes.add(gene1);
            genes.add(gene2);
        }//while it.hasNext
        Hashtable synonyms = null;
        try{
           synonyms = this.synonymsClient.getSynonyms(id_type, new Vector(genes), SynonymsSource.GI_ID);
        }catch(Exception ex){
            System.err.println("There was a problem while translating gene names to GI_IDs!!!");
            ex.printStackTrace();
            return;
        }
        
        it = interactions.iterator();
        while(it.hasNext()){
            Hashtable anInteraction = (Hashtable)it.next();
            String gene1 = (String)anInteraction.get(InteractionsDataSource.INTERACTOR_1);
            String gene2 = (String)anInteraction.get(InteractionsDataSource.INTERACTOR_2);
            String gi1 = (String)synonyms.get(gene1);
            if(gi1 == null){
                System.err.println("No gi id found for " + gene1);
            }else{
                anInteraction.put(InteractionsDataSource.INTERACTOR_1, gi1);
            }
            String gi2 = (String)synonyms.get(gene2);
            if(gi2 == null){
                System.err.println("No gi id found for " + gene2);
            }else{
                anInteraction.put(InteractionsDataSource.INTERACTOR_2, gi2);
            }
                        
        }//while it.hasNext
        
    }
    
    
}//NetworkBuilderWizard