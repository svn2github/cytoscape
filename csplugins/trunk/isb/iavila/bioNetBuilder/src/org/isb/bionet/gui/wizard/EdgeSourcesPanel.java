/**
 *
 */
package org.isb.bionet.gui.wizard;

import java.awt.event.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.datasource.synonyms.*;
import org.isb.bionet.gui.*;

import java.util.*;

import cytoscape.*;

/**
 * 
 * @author iavila
 * 
 */
public class EdgeSourcesPanel extends JPanel {

    /**
     * The fully specified edge source class to its human friendly name for
     * buttons
     */
    protected Map sourceToName;

    /**
     * If a data source has been selected, it's fully specified calss will be in
     * this Map with a Vector of species as a value
     */
    protected Map sourceToSpecies;

    /**
     * A Map from an edge data source's fully described class to the dialog that
     * contains its parameters
     */
    protected Map sourceToDialog;

    /**
     * The client to which to make requests
     */
    protected InteractionDataClient interactionsClient;

    /**
     * JButton to JTextField to display number of edges
     */
    protected Map buttonToTextField;

    /**
     * JButton to JCheckBox to select data sources
     */
    protected Map buttonToCheckBox;
    
    /**
     * A map from fully specified edge source class to its check box
     */
    protected Map sourceToCheckBox;

    /**
     * JButton to String describing fully specified class of data source
     */
    protected Map buttonToSourceClass;

    /**
     * Possibly null
     */
    protected Vector nodes;

    /**
     * Used to display Cytoscape loaded networks
     */
    protected CyNetworksDialog netsDialog;
    
    /**
     * The client for gene synonyms
     */
    protected SynonymsClient synonymsClient;
    
    /**
     * The first neighbors checkbox
     */
    protected JCheckBox fnCB;
    
    /**
     * 
     * @param interactions_client
     * @param sourceToSelectedSpecies
     *            Map from fully specified data source class to a Vector of its
     *            selected species (Vector of Strings)
     * @param nodes
     *            a Vector of Strings representing nodes, possibly null, used to
     *            estimate number of edges when starting nodes are set
     */
    public EdgeSourcesPanel(
            InteractionDataClient interactions_client,
            SynonymsClient synonyms_client,
            Map sourceToSelectedSpecies,
            Vector nodeIds) {
        
        this.interactionsClient = interactions_client;
        this.synonymsClient = synonyms_client;
        this.sourceToSpecies = sourceToSelectedSpecies;
        this.sourceToDialog = new Hashtable();
        this.buttonToTextField = new Hashtable();
        this.buttonToCheckBox = new Hashtable();
        this.sourceToCheckBox = new Hashtable();
        this.nodes = nodeIds;
        create();
    }
    
    /**
     * 
     * @param nodeIds Vector of Strings
     */
    public void setNodes (Vector nodeIds){
        this.nodes = nodeIds;
        if(this.nodes.size() > 0)
            this.fnCB.setEnabled(true);
        else{
            this.fnCB.setSelected(false);
            this.fnCB.setEnabled(false);
        }
    }
    
    
    /**
     * @return whether or not the user selected the first neighbors method
     * (this method is only available if there are starting nodes)
     */
    public boolean isFirstNeighborsSelected (){
        return this.fnCB.isSelected();
    }
    
    
    /**
     * 
     * @param sourceToSelectedSpecies a Map from fully specified data source's classes to Vectors of Strings representing species for the data sources
     */
    public void setSourcesToSpecies (Map sourceToSelectedSpecies){
        this.sourceToSpecies = sourceToSelectedSpecies;
    }

    /**
     * @return A Map from an edge data source's fully described class to the
     *         dialog that contains its parameters
     */
    public Map getSourcesDialogs() {
        return this.sourceToDialog;
    }
    
    /**
     * 
     * @param source_class the fully specified class of the source
     * @return 
     */
    public boolean isSourceSelected (String source_class){
        JCheckBox cb = (JCheckBox)this.sourceToCheckBox.get(source_class);
        if(cb != null) return cb.isSelected();
        return false;
    }

    /**
     * @param buttonName
     *            the human friendly name of the button to enable
     * @param enabled
     *            true or false
     */
    public void setSourceButtonEnabled(String buttonName, boolean enabled) {
        Iterator it = this.buttonToSourceClass.keySet().iterator();
        while (it.hasNext()) {
            JButton button = (JButton) it.next();
            String actualName = button.getText();
            if (actualName.startsWith(buttonName)) {
                button.setEnabled(enabled);
                JCheckBox checkBox = (JCheckBox) this.buttonToCheckBox
                        .get(button);
                checkBox.setSelected(true);
                return;
            }
        }// while it.hasNext
    }

    /**
     * 
     * @return the CyNetworks to be used as sources of edges
     */
    public CyNetwork[] getSelectedNetworks() {
        return this.netsDialog.getSelectedNetworks();
    }

    /**
     * Recalculated number of edges for all selected data sources
     */
    protected void estimateNumEdges () {
        
        Hashtable sourceClassToField = new Hashtable();
        Iterator it = this.buttonToCheckBox.keySet().iterator();
        while(it.hasNext()){
            JButton b = (JButton)it.next();
            if(b.isEnabled()){
                String sourceClass = (String)this.buttonToSourceClass.get(b);
                sourceClassToField.put(sourceClass,this.buttonToTextField.get(b));
            }
        }//while it.hasNext
        
        Hashtable sourceClassToArgs = new Hashtable();
        Hashtable sourceClassToSpecies = new Hashtable();
       
        int numSources = sourceClassToField.size();
        HashSet nodeIDs = null;
        // Store the nodeIDs of the found edges if there is more than one source selected and there are starting nodes
        // This is so that we can find connecting interactions between ALL nodes from different data sources
        if(numSources > 1 && this.nodes != null && this.nodes.size() > 0)
            nodeIDs = new HashSet();
        
        it = sourceClassToField.keySet().iterator();
        while(it.hasNext()){
            
            String sourceClass = (String)it.next();
            
            List sourceSpecies = (List)this.sourceToSpecies.get(sourceClass);
            if(sourceSpecies == null || sourceSpecies.size() == 0) continue;
            
            String species = (String)sourceSpecies.get(0);
            sourceClassToSpecies.put(sourceClass,species);
            
            JDialog dialog = (JDialog) this.sourceToDialog.get(sourceClass);
            
            Hashtable args = new Hashtable();
            if(sourceClass.equals(ProlinksInteractionsSource.class.getName())){
                ProlinksGui prolinksGui = (ProlinksGui)dialog;
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
              
            
            }else if(sourceClass.equals(KeggInteractionsSource.class.getName())){
            
                KeggGui kDialog = (KeggGui)dialog;
                int threshold = kDialog.getThreshold();
                boolean oneEdge = kDialog.createOneEdgePerCompound();
                args = new Hashtable();
                args.put(KeggInteractionsSource.THRESHOLD_KEY,new Integer(threshold));
                args.put(KeggInteractionsSource.EDGE_PER_CPD_KEY, new Boolean(oneEdge));
                System.out.println("------- KEGG settings (estimateNumEdges)----------");
                System.out.println("threshold = " + threshold);
                System.out.println("oneEdgePerCpd = " + oneEdge);
                System.out.println("species = " + sourceSpecies);
                System.out.println("---------------------------------------------------");
                
            }
              
            sourceClassToArgs.put(sourceClass,args);
            
            Vector sourceInteractions = null;
            try{
                
                if(this.nodes == null || this.nodes.size() == 0){
                    
                    if(args.size() > 0){
                        sourceInteractions = (Vector)this.interactionsClient.getAllInteractions(species, args);
                    }else{
                        sourceInteractions = (Vector)this.interactionsClient.getAllInteractions(species);
                    }
                
                }else{

                    Vector adjacentNodes = null;
                    
                    if(this.fnCB.isSelected()){
                        // fnCB can only be selected if this.nodes has elements
                            if(args.size() > 0)
                                adjacentNodes = this.interactionsClient.getFirstNeighbors(this.nodes,species,args);
                            else
                                adjacentNodes = this.interactionsClient.getFirstNeighbors(this.nodes,species);
                    }
                   
                    // If firstNeighbors is selected, and we have startingNodes, then we want to find the edges connecting
                    // the nodes in first neighbors and starting nodes: CAVEAT: The nodes in startingNodes could be a subset of
                    // selected nodes in a network. Connecting edges would only be found for these selected nodes, not for the
                    // whole network.
                    
                    Vector nodesToConnect = this.nodes;
                    if(adjacentNodes != null){
                        // make sure we don't have repeated nodes in nodesToConnect
                        // this means that fnCB is selected
                        adjacentNodes.removeAll(this.nodes);
                        nodesToConnect.addAll(adjacentNodes);
                    }
                    if(args.size() > 0){
                        sourceInteractions = (Vector)this.interactionsClient.getConnectingInteractions(nodesToConnect, species, args);
                    }else{
                        sourceInteractions = (Vector)this.interactionsClient.getConnectingInteractions(nodesToConnect, species);
                    }
                }//else
                
                // Accumulate the new nodeIDs if needed:
                if(nodeIDs != null){
                    Iterator it2 = sourceInteractions.iterator();
                    while(it2.hasNext()){
                        Hashtable interaction = (Hashtable)it2.next();
                        String id1 = (String)interaction.get(InteractionsDataSource.INTERACTOR_1);
                        String id2 = (String)interaction.get(InteractionsDataSource.INTERACTOR_2);
                        nodeIDs.add(id1);
                        nodeIDs.add(id2);
                    }//while it
                }else{
                   // Write the number of interactions for the source
                    JTextField tf = (JTextField)sourceClassToField.get(sourceClass);
                    tf.setText(Integer.toString(sourceInteractions.size()));  
                }
            
                
            }catch (Exception ex){
                ex.printStackTrace();
            }
            
        }//while it
        
        // Finally, connect nodes from different data sources if necessary
        if(nodeIDs != null){
            nodeIDs.addAll(this.nodes);
            it = sourceClassToArgs.keySet().iterator();
            while(it.hasNext()){
                String sourceClass = (String)it.next();
                Hashtable args= (Hashtable)sourceClassToArgs.get(sourceClass);
                String species = (String)sourceClassToSpecies.get(sourceClass);
                Vector sourceInteractions = null;
                try{
                    if(args.size() > 0){
                        sourceInteractions = (Vector)this.interactionsClient.getConnectingInteractions(new Vector(nodeIDs), species, args);
                    }else{
                        sourceInteractions = (Vector)this.interactionsClient.getConnectingInteractions(new Vector(nodeIDs), species);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                JTextField tf = (JTextField)sourceClassToField.get(sourceClass);
                tf.setText(Integer.toString(sourceInteractions.size()));
            }//while it
        }
        
    } 

    protected void create() {

        // Create buttons and select them if the user has selected them as data
        // sources
        try {
            this.sourceToName = this.interactionsClient.getSourcesNames();
        } catch (Exception e) {
            e.printStackTrace();
            this.sourceToName = new Hashtable();
        }

        this.buttonToSourceClass = new HashMap();
        Iterator it = this.sourceToName.keySet().iterator();
        while (it.hasNext()) {
          
            final String sourceClass = (String) it.next();
            //System.out.print("-----------" + sourceClass + "-------------\n");
            String buttonName = (String) this.sourceToName.get(sourceClass);
            boolean enabled = this.sourceToSpecies.containsKey(sourceClass);
            final JButton button = new JButton(buttonName + "...");
            
            if (buttonName.equals(ProlinksInteractionsSource.NAME)) {
                final  ProlinksGui pDialog = new ProlinksGui();
                this.sourceToDialog.put(sourceClass, pDialog);
                button.addActionListener(
                 new AbstractAction() {
                    public void actionPerformed(ActionEvent event) {
                        ProlinksGui pDialog = (ProlinksGui) sourceToDialog
                                .get(sourceClass);
                        pDialog.pack();
                        pDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                        pDialog.setVisible(true);
                        // Dialog is modal, so we get back when the user closes
                        // it:
                        //estimateNumEdges(button);
                    }// actionPerformed
                });// AbstractAction
            } else if(buttonName.equals(KeggInteractionsSource.NAME)){
               final KeggGui kDialog = new KeggGui(this.interactionsClient,KeggInteractionsSource.DEFAULT_THRESHOLD);
               this.sourceToDialog.put(sourceClass, kDialog);
                button.addActionListener(
                 new AbstractAction() {
                    public void actionPerformed(ActionEvent event) {
                        KeggGui kDialog = (KeggGui) sourceToDialog
                                .get(sourceClass);
                        kDialog.pack();
                        kDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                        kDialog.setVisible(true);
                        // Dialog is modal, so we get back when the user closes
                        // it:
                        //estimateNumEdges(button);
                    }// actionPerformed
                });// AbstractAction
            }//KEGG
            
            button.setEnabled(enabled);
            this.buttonToSourceClass.put(button, sourceClass);
          
        }// while it

        JButton netsButton = new JButton("Loaded Networks...");
        netsButton.addActionListener(
                
                new AbstractAction() {

                public void actionPerformed(ActionEvent event) {
                    if (netsDialog == null) {
                        netsDialog = new CyNetworksDialog();
                    }
                    netsDialog.update();
                    netsDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                    netsDialog.pack();
                    netsDialog.setVisible(true);
                    // netsDialog is modal
                    CyNetwork[] nets = netsDialog.getSelectedNetworks();
                    int numEdges = 0;
                    for (int i = 0; i < nets.length; i++) {
                        numEdges += nets[i].getEdgeCount();
                    }// for i
                    JButton source = (JButton) event.getSource();
                    ((JTextField) buttonToTextField.get(source)).setText(Integer
                            .toString(numEdges));
                }// actionPerformed
            }// AbstractAction
                );
        
        netsButton.setEnabled(false);
        this.buttonToSourceClass.put(netsButton,null);

       
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       
        GridBagLayout gridbag = new GridBagLayout();
        JPanel gridLayoutPanel = new JPanel();
        gridLayoutPanel.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.ipadx = 5;
        Component emptyBox = Box.createHorizontalGlue();
        gridbag.setConstraints(emptyBox, c);
        gridLayoutPanel.add(emptyBox);

        JLabel sourceLabel = new JLabel("Edge Source");
        gridbag.setConstraints(sourceLabel, c);
        gridLayoutPanel.add(sourceLabel);

        c.gridwidth = GridBagConstraints.REMAINDER; // end row

        JLabel stats = new JLabel("Num Edges");
        gridbag.setConstraints(stats, c);
        gridLayoutPanel.add(stats);

        c.fill = GridBagConstraints.HORIZONTAL;
        it = this.buttonToSourceClass.keySet().iterator();
        
        while (it.hasNext()) {
            
            final JButton button = (JButton) it.next();
            String sourceClass = (String)this.buttonToSourceClass.get(button);
            
            c.gridwidth = 1; // reset to the default
            JCheckBox cb = new JCheckBox();
            gridbag.setConstraints(cb, c);
            gridLayoutPanel.add(cb);
            if(sourceClass != null) this.sourceToCheckBox.put(sourceClass, cb);
            
            gridbag.setConstraints(button, c);
            gridLayoutPanel.add(button);

            cb.setSelected(button.isEnabled());

            cb.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent event) {
                    JCheckBox source = (JCheckBox) event.getSource();
                    button.setEnabled(source.isSelected());
                }
            });

            c.gridwidth = GridBagConstraints.REMAINDER;
            JTextField edgesNum = new JTextField(4);
            edgesNum.setText("0");

            this.buttonToTextField.put(button, edgesNum);
            this.buttonToCheckBox.put(button, cb);

            edgesNum.setEditable(false);
            gridbag.setConstraints(edgesNum, c);
            gridLayoutPanel.add(edgesNum);
        }// while it buttons
        
        JButton numEdgesButton = new JButton("Calculate number of edges from selected databases");
        numEdgesButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        estimateNumEdges();
                    }
                    
                }
        );
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        fnCB = new JCheckBox("Add first neighbors of nodes");
        fnCB.setSelected(false);
        if(this.nodes.size() == 0){
            fnCB.setEnabled(false);
        }
        gridbag.setConstraints(fnCB, c);
        gridLayoutPanel.add(fnCB);
        gridbag.setConstraints(numEdgesButton, c);
        gridLayoutPanel.add(numEdgesButton);
        
        // set layout for this panel and add the two main panels
        GridBagLayout gbl = new GridBagLayout();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.ipady = 25;
        setLayout(gbl);
        
        gbl.setConstraints(gridLayoutPanel,c);
        add(gridLayoutPanel);
        
    }// create
    
}