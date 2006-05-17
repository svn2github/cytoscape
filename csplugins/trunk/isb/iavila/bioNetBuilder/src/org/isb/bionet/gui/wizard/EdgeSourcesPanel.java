/**
 *
 */
package org.isb.bionet.gui.wizard;

import java.awt.event.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;

import org.apache.xmlrpc.XmlRpcException;
import org.isb.bionet.CyNetUtils;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.datasource.synonyms.*;
import org.isb.bionet.gui.*;

import utils.MyUtils;
import utils.UserPasswordDialog;

import java.sql.SQLException;
import java.util.*;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.util.SwingWorker;
import cytoscape.util.IndeterminateProgressBar;

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
     * A vector with NCBI taxids as Strings of species for which to
     * get interactions
     */
    protected Vector taxids;
    
    /**
     * A table from source name to boolean, only sources that require a password are in this table
     */
    protected Hashtable authenticatedEdgeSources;
    
    /**
     * 
     * @param interactions_client
     * @param tax_ids a vector with NCBI taxids as Strings of species for which to
     * get interactions
     * @param nodes
     *            a Vector of Strings representing nodes, possibly null, used to
     *            estimate number of edges when starting nodes are set
     */
    public EdgeSourcesPanel(
            InteractionDataClient interactions_client,
            SynonymsClient synonyms_client,
            Vector tax_ids,
            Vector nodeIds) {
        
        this.interactionsClient = interactions_client;
        this.synonymsClient = synonyms_client;
        this.taxids = tax_ids;
        this.sourceToDialog = new Hashtable();
        this.buttonToTextField = new Hashtable();
        this.buttonToCheckBox = new Hashtable();
        this.sourceToCheckBox = new Hashtable();
        this.nodes = nodeIds;
        this.authenticatedEdgeSources = new Hashtable();
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
     * Sets the taxids for which to get interactions from the interactions sources
     * 
     * @param tax_ids a vector with NCBI taxids as Strings of species for which to
     * get interactions
     */
    public void setTaxids (Vector tax_ids){
        this.taxids = tax_ids;
    }
    
    
    /**
     * @return whether or not the user selected the first neighbors method
     * (this method is only available if there are starting nodes)
     */
    public boolean isFirstNeighborsSelected (){
        return this.fnCB.isSelected();
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
     * @return true if the given source requires a password and has been authenticated, 
     * or if the source does not require a password
     *
     */
    public boolean isSourceAuthenticated (String source_class){
        //System.out.println("authenticatedEdgeSources.contains(" + source_class + ") = " + this.authenticatedEdgeSources.contains(source_class));
        if(this.authenticatedEdgeSources.containsKey(source_class)){
            //System.out.println("his.authenticatedEdgeSources.get(source_class) =" + this.authenticatedEdgeSources.get(source_class));
            return( (Boolean)this.authenticatedEdgeSources.get(source_class) ).booleanValue();
        }
        return true;
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
     * Gets the human readable name of the interactions source
     * @param source_class fully specified Class name of the interactions source
     * @return the human readable name of the interactions source
     */
    public String getSourceName (String source_class){
        return (String)this.sourceToName.get(source_class);
    }

    /**
     * Recalculated number of edges for all selected data sources
     */
    protected void estimateNumEdges () {
        
        // Get the text field for each SELECTED source
        Hashtable sourceClassToField = new Hashtable();
        Iterator it = this.buttonToCheckBox.keySet().iterator();
        int numSelectedSources = 0;
        while(it.hasNext()){
            JButton b = (JButton)it.next();
            String sourceClass = (String)this.buttonToSourceClass.get(b);
            if(sourceClass != null){
                JTextField tf = (JTextField)this.buttonToTextField.get(b);
                //System.out.println("isSourceAuthenticated(" + sourceClass + ") = " + isSourceAuthenticated(sourceClass));
                if(isSourceSelected(sourceClass) && isSourceAuthenticated(sourceClass)){
                    sourceClassToField.put(sourceClass,tf);
                    numSelectedSources++;
                }else{
                    //  Not selected, so set its edge number to 0 and skip
                    tf.setText(Integer.toString(0));
                } 
            }
        }//while it.hasNext
       
        // If we have starting nodes, get their alternate IDs
        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        Vector startingNodes = new Vector();
        if(this.nodes != null && this.nodes.size() > 0){
            startingNodes.addAll(this.nodes);
            it = this.nodes.iterator();
            while(it.hasNext()){
                String gi = (String)it.next();
                String alternateGis = (String)nodeAtts.getStringAttribute(gi,CyNetUtils.ALTERNATE_UID_ATT);
                if(alternateGis == null) continue;
                String [] allGis = alternateGis.split("|");
                for(int i = 0; i < allGis.length; i++)
                    if(!startingNodes.contains(allGis[i])) startingNodes.add(allGis[i]);        
            }//while it
        }
        
        //System.err.println("startingNodes.size() = " + startingNodes.size() + " and this.nodes = " + this.nodes);
        
        // Get the taxid for the interactions
        if(this.taxids == null || taxids.size() == 0) return;  
        String species = (String)taxids.get(0); // for now get the first one
        
        // Store the nodeIDs if there is more than one source selected and there are starting nodes
        // This is so that we can find connecting interactions between ALL nodes from different data sources
        HashSet nodesToConnect = null;
        if(numSelectedSources  > 1 && this.nodes != null && this.nodes.size() > 0)
            nodesToConnect = new HashSet();
        
        // Get interactions for each source
        Hashtable sourceClassToArgs = new Hashtable(); // to be used later
        it = sourceClassToField.keySet().iterator();
        
        while(it.hasNext()){
            
            String sourceClass = (String)it.next();
            // Get the arguments for this source
            InteractionsSourceGui dialog = (InteractionsSourceGui) this.sourceToDialog.get(sourceClass);
            Hashtable args = new Hashtable();
            if(dialog != null){
                args = dialog.getArgsTable();
            }
            sourceClassToArgs.put(sourceClass,args); // to be used later
            
            Vector sourceInteractions = null;
           
            if(startingNodes.size() == 0){
               int numInteractions = 0;
                try{
                    if(args.size() > 0){
                        numInteractions = this.interactionsClient.getNumAllInteractions(species, args, sourceClass);
                    }else{        
                        numInteractions = this.interactionsClient.getNumAllInteractions(species, sourceClass);
                    }
                }catch(Exception e){e.printStackTrace();}
                
                JTextField tf = (JTextField)sourceClassToField.get(sourceClass);
                tf.setText(Integer.toString(numInteractions)); 
                
            }else{
                // We have starting nodes
                Vector adjacentNodes = null;
                if(this.fnCB.isSelected()){
                    // Get their first neighbors
                    try{
                        // fnCB can only be selected if this.nodes has elements
                        //System.err.println("getting fn, this.nodes = " + this.nodes);
                            if(args.size() > 0){
                                adjacentNodes = this.interactionsClient.getFirstNeighbors(this.nodes,species,args,sourceClass);
                            }else{
                                adjacentNodes = this.interactionsClient.getFirstNeighbors(this.nodes,species, sourceClass);
                            }
                    }catch(Exception e){e.printStackTrace();}
                    
                }
                   
                // If firstNeighbors is selected, and we have startingNodes, then we want to find the edges connecting
                // the nodes in first neighbors and starting nodes: CAVEAT: The nodes in startingNodes could be a subset of
                // selected nodes in a network. Connecting edges would only be found for these selected nodes, not for the
                // whole network.
                    
                Vector nodesToConnectForSource = new Vector(); 
                nodesToConnectForSource.addAll(startingNodes);
                if(adjacentNodes != null){
                    // make sure we don't have repeated nodes in nodesToConnect
                    // this means that fnCB is selected
                    adjacentNodes.removeAll(startingNodes);
                    nodesToConnectForSource.addAll(adjacentNodes);
                }
               
                // LEFT HERE.
                // At this point we have the nodes, can we check if they have alternate ids, without having to get the connecting interactions???
                
                try{
                    // This takes a long time...
                    if(args.size() > 0){
                        sourceInteractions = 
                            (Vector)this.interactionsClient.getConnectingInteractions(nodesToConnectForSource, species, args, sourceClass);
                    }else{
                        sourceInteractions = 
                            (Vector)this.interactionsClient.getConnectingInteractions(nodesToConnectForSource, species, sourceClass);
                    }
                }catch(Exception e){e.printStackTrace();}
                
                // Accumulate the new nodeIDs if needed:
                if(nodesToConnect != null){
                    boolean alternateIdsExist = false;
                    Iterator it2 = sourceInteractions.iterator();
                    int oldSize = nodesToConnect.size();
                    while(it2.hasNext()){
                        Hashtable interaction = (Hashtable)it2.next();
                        String id1 = (String)interaction.get(InteractionsDataSource.INTERACTOR_1);
                        String id2 = (String)interaction.get(InteractionsDataSource.INTERACTOR_2);
                        Vector id1alternates = (Vector)interaction.get(InteractionsDataSource.INTERACTOR_1_IDS);
                        Vector id2alternates = (Vector)interaction.get(InteractionsDataSource.INTERACTOR_2_IDS);
                        nodesToConnect.add(id1);
                        nodesToConnect.add(id2);
                        if(id1alternates != null) nodesToConnect.addAll(id1alternates);
                        if(id2alternates != null) nodesToConnect.addAll(id2alternates);
                    }//while it
                    
                    if(nodesToConnect.size() > oldSize) alternateIdsExist = true;
                    args.put("alternateIDs",new Boolean(alternateIdsExist));
                    if(!alternateIdsExist){
                        JTextField tf = (JTextField)sourceClassToField.get(sourceClass);
                        tf.setText(Integer.toString(sourceInteractions.size()));
                    }
                }else{
                    // we have starting nodes and only one edge source
                    JTextField tf = (JTextField)sourceClassToField.get(sourceClass);
                    tf.setText(Integer.toString(sourceInteractions.size()));
                }
                 
            }//else
                
        }//while it
        
        // Finally, connect nodes from different data sources if necessary
        if(nodesToConnect != null){
            nodesToConnect.addAll(this.nodes);
            it = sourceClassToArgs.keySet().iterator();       
            
            while(it.hasNext()){
                String sourceClass = (String)it.next();
                Hashtable args= (Hashtable)sourceClassToArgs.get(sourceClass);
                Boolean alternateIDs = (Boolean)args.get("alternateIDs");
                if(!alternateIDs.booleanValue()) continue;
                int numInteractions = 0;
                try{
                    if(args.size() > 0){  
                        numInteractions = this.interactionsClient.getNumConnectingInteractions(new Vector(nodesToConnect), species, args, sourceClass);
                    }else{
                        numInteractions = this.interactionsClient.getNumConnectingInteractions(new Vector(nodesToConnect), species, sourceClass);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                JTextField tf = (JTextField)sourceClassToField.get(sourceClass);
                tf.setText(Integer.toString(numInteractions));
            }//while it
        }
        
    }//estimateNumEdges

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
            
            boolean enabled = this.taxids.size() > 0;
            
            final JButton button = new JButton(buttonName + "...");
            
            //TODO: An automated way of finding which class is the GUI for a source and creating it
            
            if(buttonName.equals(HPRDInteractionsSource.NAME)){
                final HPRDGui hDialog = new HPRDGui();
                this.sourceToDialog.put(sourceClass,hDialog);
                button.addActionListener(
                        new AbstractAction() {
                           public void actionPerformed(ActionEvent event) {
                               HPRDGui hDialog = (HPRDGui) sourceToDialog
                                       .get(sourceClass);
                               hDialog.pack();
                               hDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                               hDialog.setVisible(true);
                           }// actionPerformed
                       });// AbstractAction
                boolean requiresPassword = false;
                try{
                    //System.out.println("Calling callSourceMethod ( "+sourceClass +", requiresPassword, new Vector()...");
                    Boolean rp = (Boolean)this.interactionsClient.callSourceMethod(sourceClass,"requiresPassword",new Vector());
                    //System.out.println("got " + rp);
                    requiresPassword = rp.booleanValue();
                }catch(Exception e){
                    e.printStackTrace();
                    requiresPassword = true;
                }
               if(requiresPassword){
                   this.authenticatedEdgeSources.put(sourceClass,Boolean.FALSE);
                   //System.out.println("---------------authenticatedEdgeSources.get("+sourceClass+") = " + this.authenticatedEdgeSources.get(sourceClass));
               }
            }else if(buttonName.equals(DipInteractionsSource.NAME)){
                final DipGui dDialog = new DipGui();
                this.sourceToDialog.put(sourceClass,dDialog);
                button.addActionListener(
                        new AbstractAction() {
                           public void actionPerformed(ActionEvent event) {
                               DipGui dDialog = (DipGui) sourceToDialog
                                       .get(sourceClass);
                               dDialog.pack();
                               dDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                               dDialog.setVisible(true);
                           }// actionPerformed
                       });// AbstractAction
                
            }else if(buttonName.equals(BindInteractionsSource.NAME)){
                final BindGui bDialog = new BindGui();
                this.sourceToDialog.put(sourceClass,bDialog);
                button.addActionListener(
                        new AbstractAction() {
                           public void actionPerformed(ActionEvent event) {
                               BindGui bDialog = (BindGui) sourceToDialog
                                       .get(sourceClass);
                               bDialog.pack();
                               bDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                               bDialog.setVisible(true);
                           }// actionPerformed
                       });// AbstractAction
            }else if (buttonName.equals(ProlinksInteractionsSource.NAME)) {
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
            final String sourceClass = (String)this.buttonToSourceClass.get(button);
            
            c.gridwidth = 1; // reset to the default
            JCheckBox cb = new JCheckBox();
            gridbag.setConstraints(cb, c);
            gridLayoutPanel.add(cb);
            if(sourceClass != null) this.sourceToCheckBox.put(sourceClass, cb);
            
            gridbag.setConstraints(button, c);
            gridLayoutPanel.add(button);

            cb.setSelected(button.isEnabled());
            
            // check box action performed
            cb.addActionListener(new AbstractAction() {
                public void actionPerformed(ActionEvent event) {
                    JCheckBox source = (JCheckBox) event.getSource();
                    if(source.isSelected()){
                       // see if the selected source has been authenticated
                        if(sourceClass != null && !isSourceAuthenticated(sourceClass)){
                            // call a method that asks for a password and sends it to the server
                            boolean ok = false;
                            UserPasswordDialog passDialog = new UserPasswordDialog("Enter password for data source");
                            passDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                            passDialog.setVisible(true);
                            try{
                                Vector args = new Vector();
                                args.add(passDialog.getUserName());
                                String password = new String(passDialog.getPassword());
                                args.add(password);
                                Boolean OK = (Boolean)interactionsClient.callSourceMethod(sourceClass,"authenticate",args);
                                ok = OK.booleanValue();
                            }catch(Exception e){e.printStackTrace();}
                            //System.out.println("SourceClass " + sourceClass + " authenticated: " + ok);
                            if(!ok){
                                JOptionPane.showMessageDialog(EdgeSourcesPanel.this,"Unauthorised user and password.","Not authorised.",JOptionPane.ERROR_MESSAGE);
                                button.setEnabled(false);
                            }else{
                                authenticatedEdgeSources.put(sourceClass,Boolean.TRUE);
                                button.setEnabled(true);
                            }
                          }else{
                            button.setEnabled(source.isSelected());  
                        }
                    }
                    
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
                    
                    IndeterminateProgressBar pBar =
                        new IndeterminateProgressBar(Cytoscape.getDesktop(),"BioNetBuilder","Please wait while number of edges are calculated...");
                    
                    public void actionPerformed (ActionEvent event){
                        final SwingWorker worker = new SwingWorker (){
                            
                            public Object construct (){
                                pBar.pack();
                               // pBar.setLocationRelativeTo(EdgeSourcesPanel.this.)
                                pBar.setLocation(EdgeSourcesPanel.this.getLocationOnScreen());
                                pBar.setVisible(true);
                                estimateNumEdges();
                                return null;
                            }//construct
                            
                            public void finished (){
                                pBar.setVisible(false);
                            }//finished
                        };//SwingWorker
                        
                        worker.start();
                        
                    }//actionPerformed
                    
                }//AbstractAction
        );//addActionListener
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