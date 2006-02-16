/**
 *
 */
package org.isb.bionet.gui.wizard;

import java.awt.event.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

import java.io.File;
import java.util.*;

import org.isb.bionet.gui.*;
import org.isb.bionet.gui.taxonomy.TaxonomySearchDialog;
import org.isb.iavila.ontology.gui.*;
import org.isb.iavila.ontology.xmlrpc.*;
import org.isb.bionet.datasource.synonyms.*;

import utils.MyUtils;
import cytoscape.*;
import cytoscape.data.Semantics;

public class NodeSourcesPanel extends JPanel {

    
    // The sources of nodes:
    public static final String USER_LIST = "user_list";
    public static final String NETS = "network";
    public static final String TAXONOMY = "taxonomy";
    public static final String ANNOTS = "annotation";
    
    
    protected File myListFile;
    protected Vector myListNodes;
    protected CyNetworksDialog netsDialog;
    protected TaxonomySearchDialog taxonomyDialog;
    protected JTextField listNodes, annotsNodes, netsNodes, taxonomyNodes;
    protected CytoscapeGODialog annotationsDialog;
    protected String [] annotationNodeIDs = new String[0];
    protected JCheckBox useAnnotations;
    protected JCheckBox useList;
    protected JCheckBox useNets;
    protected JCheckBox useSelectedNodes;
    protected JCheckBox useTaxonomy;
    
    protected GOClient goClient;
    protected SynonymsClient synClient;
    
    /**
     *  Creates a panel with node sources
     */
    public NodeSourcesPanel (GOClient go_client, SynonymsClient synonyms_client){
        this.goClient = go_client;
        this.synClient = synonyms_client;
        create();
    }
    
    /**
     * @return if a file has been selected, it returns it, returns null otherwise
     */
    public File getMyListFile (){
        return this.myListFile;
    }
    
    /**
     * 
     * @return a table from one of USER_LIST:<listfile name>,NETS:<network name>,TAXONOMY,ANNOTS to Vectors of node IDs
     */
    public Hashtable getSelectedSourceToNodesTable (){
       
        Hashtable table = new Hashtable();
       
        if(useList.isSelected()){
            Vector myListNodes = getNodesFromMyList();
            if(myListNodes != null) table.put(USER_LIST + ":" + this.myListFile.getName(), myListNodes);
        }
       
        CyNetwork [] nodeNets = getSelectedNetworks();
        if(nodeNets != null && useNets.isSelected()){
            if(!this.useSelectedNodes.isSelected()){
                for(int i = 0; i < nodeNets.length; i++){
                    Iterator it = nodeNets[i].nodesIterator();
                    Vector netNodes = new Vector();
                    while(it.hasNext()){
                        CyNode node = (CyNode)it.next();
                        String nodeName = Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME);
                        if(!netNodes.contains(nodeName))
                            netNodes.add(nodeName);
                    }//while it
                    if(netNodes.size() > 0)
                        table.put(NETS + ":" + nodeNets[i].getTitle(), netNodes);
                }//for i
            }else{
                //get the selected nodes
                for(int i = 0; i < nodeNets.length; i++){
                    Iterator it = nodeNets[i].getFlaggedNodes().iterator();
                    Vector netNodes = new Vector();
                    while(it.hasNext()){
                        CyNode node = (CyNode)it.next();
                        String nodeName = Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME);
                        if(!netNodes.contains(nodeName))
                            netNodes.add(nodeName);
                    }//while it
                    if(netNodes.size() > 0)
                        table.put(NETS + ":" + nodeNets[i].getTitle(), netNodes);
                }//for i
            
            }
           
        }// if nodeNets != null
        
        if(this.useAnnotations.isSelected()){
            if(this.annotationNodeIDs != null && this.annotationNodeIDs.length > 0){
                Vector annotsVector = new Vector();
                for(int i = 0; i < this.annotationNodeIDs.length; i++) annotsVector.add(this.annotationNodeIDs[i]);
                table.put(ANNOTS,annotsVector);
            }
        }
        
        if(this.useTaxonomy.isSelected()){
          Vector taxonomyVector = new Vector(this.taxonomyDialog.getGeneIDs());
          table.put(TAXONOMY, taxonomyVector);
        }
        
        return table;
    }
    
    
    /**
     * 
     * @return a Vector of gene ids from the user selected sources
     */
    public Vector getAllNodes (){
       
        // TODO: Do checks: selected networks species must match to the species the user selected in this sesion
        // TODO: Nodes from annotations!
      
        Vector startingNodes = new Vector();
        if(useList.isSelected()){
            Vector myListNodes = getNodesFromMyList();
            if(myListNodes != null) startingNodes.addAll(myListNodes);
        }
       
        CyNetwork [] nodeNets = getSelectedNetworks();
        if(nodeNets != null && useNets.isSelected()){
            if(!this.useSelectedNodes.isSelected()){
                for(int i = 0; i < nodeNets.length; i++){
                    Iterator it = nodeNets[i].nodesIterator();
                    while(it.hasNext()){
                        CyNode node = (CyNode)it.next();
                        String nodeName = Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME);
                        if(!startingNodes.contains(nodeName))
                            startingNodes.add(nodeName);
                    }//while it
                }//for i
            }else{
                //get the selected nodes
                for(int i = 0; i < nodeNets.length; i++){
                    Iterator it = nodeNets[i].getFlaggedNodes().iterator();
                    while(it.hasNext()){
                        CyNode node = (CyNode)it.next();
                        String nodeName = Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME);
                        if(!startingNodes.contains(nodeName))
                            startingNodes.add(nodeName);
                    }//while it
                }//for i
            
            }
        }// if nodeNets != null
        
        if(this.useAnnotations.isSelected()){
            for(int i = 0; i < this.annotationNodeIDs.length; i++){
                startingNodes.add(this.annotationNodeIDs[i]);
            }
        }
        
        if(this.useTaxonomy.isSelected()){
          startingNodes.addAll(this.taxonomyDialog.getGeneIDs());
        }
        
        return startingNodes;
    }
    
    /**
     * 
     * @return the CyNetworks to be used as sources for nodes
     */
    public CyNetwork [] getSelectedNetworks (){
        if(this.netsDialog != null)
            return this.netsDialog.getSelectedNetworks();
        return new CyNetwork[0];
    }
    
    public Vector getNodesFromMyList (){
        return this.myListNodes;
    }
    
    /**
     * 
     * @return a Vector with the node names in "myList" file
     */
    protected Vector translateNodesToGis (Vector nodeIDs){
        
        // Accepted id types are orf, gi, and refseq
        // Bin the input ids into these 3 types and conver them
        HashSet gis = new HashSet();
        Vector orfs = new Vector();
        Vector refseqs = new Vector();
        
        Iterator it = nodeIDs.iterator();
        while(it.hasNext()){
            String id = (String) it.next();
            if(id.startsWith(SynonymsSource.GI_ID)){
                gis.add(id);
            }else if(id.startsWith(SynonymsSource.REFSEQ_ID)){
                refseqs.add(id);
            }else if(id.startsWith(SynonymsSource.ORF_ID)){
                orfs.add(id);
            }
        }
        
        Hashtable rsToGi = new Hashtable();
        Hashtable orfToGi = new Hashtable();
        
        try{
            if(refseqs.size() > 0)
                rsToGi = this.synClient.getSynonyms(SynonymsSource.REFSEQ_ID, refseqs, SynonymsSource.GI_ID);
            if(orfs.size() > 0)
                orfToGi = this.synClient.getSynonyms(SynonymsSource.ORF_ID,orfs,SynonymsSource.GI_ID);
        }catch(Exception e){ e.printStackTrace();}
        
        // Get all the gis
        it = rsToGi.keySet().iterator();
        while(it.hasNext()){
            gis.addAll ( (Vector) rsToGi.get(it.next()) );
        }
        
        it = orfToGi.keySet().iterator();
        while(it.hasNext()){
            gis.addAll( (Vector) orfToGi.get(it.next()) );
        }
        
        return new Vector(gis);
        
    }
    
    /**
     * Gets the taxonomy search dialog that other GUIs can use
     * 
     * @return a TaxonomySearchDialog
     */
    public TaxonomySearchDialog getTaxonomySearchDialog (){
        if(this.taxonomyDialog == null)
            this.taxonomyDialog = new TaxonomySearchDialog(this.synClient);
        return this.taxonomyDialog;
    }
    
    /**
     * Gets the annotations dialog that other GUIs can use
     * 
     * @return a CytoscapeGODialog
     */
    public CytoscapeGODialog getCytoscapeGoDialog (){
        if(this.annotationsDialog == null) createAnnotationsDialog(this.goClient);
        return this.annotationsDialog;
    }
    
    /**
     * Updated the number of nodes from selected networks
     */
    public void updateNumNodesFromNetwork (){
        int numNodes = 0;
        
        
        
        if(useSelectedNodes.isSelected()){
            CyNetwork [] nets = netsDialog.getSelectedNetworks();
            if(nets == null) return;
            for(int i = 0; i < nets.length; i++){
                Set flaggedNodes = nets[i].getFlaggedNodes();
                if(flaggedNodes != null)
                    numNodes += flaggedNodes.size();
            }//for i
        }else{
            CyNetwork [] nets = netsDialog.getSelectedNetworks();
            if(nets == null) return;
            for(int i = 0; i < nets.length; i++){
                numNodes += nets[i].getNodeCount();
            }//for i
        }
        netsNodes.setText(Integer.toString(numNodes));
    }
    
    /**
     * Updated the number of nodes from the taxonomy dialog
     */
    public void updateNumNodesFromTaxonomy (){
        int numNodes = 0;
        if(useTaxonomy.isSelected()){
          numNodes = this.taxonomyDialog.getGeneIDs().size();
        }
        this.taxonomyNodes.setText(Integer.toString(numNodes));
    }
    
    /**
     * Creates the panel
     */
    protected void create() {
        
        //CREATE BUTTONS
        
        // Annotations button
        final JButton annotsButton = new JButton("Nodes with selected annotations...");
        annotsButton.setEnabled(false);
        annotsButton.addActionListener(new AbstractAction(){
            
            public void actionPerformed (ActionEvent event){
                //JOptionPane.showMessageDialog(NodeSourcesPanel.this, "Not implemented yet!", "Oops!", JOptionPane.ERROR_MESSAGE);
                if(annotationsDialog == null){
                    createAnnotationsDialog(goClient);
                }
                annotationsDialog.pack();
                annotationsDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                annotationsDialog.setVisible(true);
                // The dialog is modal
               //annotationNodeIDs = annotationsDialog.getGenesWithTerms();
               //System.out.println("There are " + annotationNodeIDs.length + " nodes from annotations.");
            }//actionPerformed
            
        });
        
        // Nodes from list
        final JButton listButton = new JButton("Nodes from my list...");
        final JFileChooser fileChooser = new JFileChooser();
        listButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        int returnVal = fileChooser.showOpenDialog(NodeSourcesPanel.this);
                        if(returnVal == JFileChooser.APPROVE_OPTION) {
                            myListFile = fileChooser.getSelectedFile();
                            try{
                                myListNodes = MyUtils.ReadFileLines(myListFile.getAbsolutePath());
                                myListNodes = translateNodesToGis(myListNodes);
                                int numRead = myListNodes.size();
                                listNodes.setText(Integer.toString(numRead));
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(NodeSourcesPanel.this, "Could not read nodes in file " + 
                                        myListFile.getName() + "!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }// APPROVE_OPTION
                    }//actionPerformed
                    
                }//AbstractAction
        );
        listButton.setEnabled(false);
        
        // Nodes from networks
        final JButton netsButton  =  new JButton("Nodes from loaded networks...");
        netsButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed (ActionEvent event){
                        if(netsDialog == null){
                            netsDialog = new CyNetworksDialog();
                        }
                        netsDialog.update();
                        netsDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                        netsDialog.pack();
                        netsDialog.setVisible(true);
                        // netsDialog is modal
                        updateNumNodesFromNetwork();
                    }//actionPerformed
                }//AbstractAction
        );
        netsButton.setEnabled(false);
        
        // Nodes from taxonomy
        
        if(this.taxonomyDialog == null){
            this.taxonomyDialog = new TaxonomySearchDialog(synClient);
        }
        
        final JButton taxButton = new JButton("Nodes from NCBI Taxonomy...");
        taxButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed (ActionEvent event){
                        
                        if(taxonomyDialog.isVisible()){
                            taxonomyDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                        }else{
                            taxonomyDialog.pack();
                            taxonomyDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                            taxonomyDialog.setVisible(true);
                            updateNumNodesFromTaxonomy();
                        }
                    }//actionPerformed
                }//AbstractAction
        );
        
        // Add check boxes and buttons, first, column names
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagLayout gridbag = new GridBagLayout();
        this.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.ipadx = 5;
        Component emptyBox = Box.createHorizontalGlue();
        gridbag.setConstraints(emptyBox, c);
        this.add(emptyBox);

        JLabel use = new JLabel("Node Source");
        gridbag.setConstraints(use, c);
        this.add(use);

        c.gridwidth = GridBagConstraints.REMAINDER; // end row

        JLabel stats = new JLabel("Num Nodes");
        gridbag.setConstraints(stats, c);
        this.add(stats);

        c.gridwidth = 1; // reset to the default

        // Annotations
        this.useAnnotations = new JCheckBox();
        this.useAnnotations.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        annotsButton.setEnabled(source.isSelected());
                    }
                }
        );
        this.useAnnotations.setSelected(false);
        gridbag.setConstraints(useAnnotations, c);
        this.add(useAnnotations);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(annotsButton, c);
        this.add(annotsButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.annotsNodes = new JTextField(4);
        this.annotsNodes.setText("0");
        this.annotsNodes.setEditable(false);
        gridbag.setConstraints(this.annotsNodes, c);
        this.add(this.annotsNodes);

        c.gridwidth = 1;
        
        //List nodes
        c.fill = GridBagConstraints.NONE;
        this.useList = new JCheckBox();
        useList.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        listButton.setEnabled(source.isSelected());
                    }
                }
        );
        useList.setSelected(false);
        gridbag.setConstraints(useList, c);
        this.add(useList);

        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        gridbag.setConstraints(listButton, c);
        this.add(listButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.listNodes = new JTextField(4);
        this.listNodes.setEditable(false);
        this.listNodes.setText("0");
        gridbag.setConstraints(this.listNodes, c);
        this.add(this.listNodes);

        c.gridwidth = 1;
        
        // Taxonomy nodes
        c.fill = GridBagConstraints.NONE;
        
        this.useTaxonomy = new JCheckBox();
        useTaxonomy.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        taxButton.setEnabled(source.isSelected());
                    }
                }
        );
        useTaxonomy.setSelected(false);
        gridbag.setConstraints(useTaxonomy, c);
        this.add(useTaxonomy);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(taxButton, c);
        taxButton.setEnabled(false);
        this.add(taxButton);
        
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.taxonomyNodes = new JTextField(4);
        this.taxonomyNodes.setEditable(false);
        this.taxonomyNodes.setText("0");
        gridbag.setConstraints(this.taxonomyNodes, c);
        this.add(this.taxonomyNodes);

        c.gridwidth = 1;
        
        //Network nodes
        c.fill = GridBagConstraints.NONE;
        this.useNets = new JCheckBox();
        useNets.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        netsButton.setEnabled(source.isSelected());
                        useSelectedNodes.setEnabled(useNets.isSelected());
                        if(netsDialog == null){
                            netsDialog = new CyNetworksDialog();
                        }
                        if(source.isSelected()){
                            netsDialog.update();
                            netsDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                            netsDialog.pack();
                            netsDialog.setVisible(true);
                            updateNumNodesFromNetwork();
                            }
                    }
                }
        );
        useNets.setSelected(false);
        gridbag.setConstraints(useNets, c);
        this.add(useNets);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(netsButton, c);
        this.add(netsButton);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.netsNodes = new JTextField(4);
        this.netsNodes.setEditable(false);
        this.netsNodes.setText("0");
        gridbag.setConstraints(this.netsNodes, c);
        this.add(this.netsNodes);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
         
        this.useSelectedNodes = new JCheckBox("Use selected nodes in networks");
        if(this.useNets.isSelected())
            this.useSelectedNodes.setEnabled(true);
        else
            this.useSelectedNodes.setEnabled(false);
        this.useSelectedNodes.addActionListener(
                
                new AbstractAction(){
                    public void actionPerformed (ActionEvent e){
                      updateNumNodesFromNetwork();
                    }
                });
        
        gridbag.setConstraints(this.useSelectedNodes,c);
        this.add(this.useSelectedNodes);
        
    }
    
   /**
    * Creates a CycotscapeGODialog with custom buttons
    * @param go_client
    */ 
    protected void createAnnotationsDialog (GOClient go_client){
        JPanel buttonsPanel = new JPanel();
        JButton ok = new JButton("OK");
        ok.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        System.out.println("--------- OK action performed -------------");
                        System.out.println(annotationsDialog.getSelectedSpecies());
                        annotationNodeIDs = annotationsDialog.getGenesWithTerms();
                        System.out.println("num genes = " + annotationNodeIDs.length);
                       // JOptionPane.showMessageDialog(annotationsDialog,"There are "+annotationNodeIDs.length+" genes with the selected annotations.","" +
                       //         "BioNet Builder", JOptionPane.INFORMATION_MESSAGE);
                        annotsNodes.setText(Integer.toString(annotationNodeIDs.length));
                        annotationsDialog.dispose();
                    }
                    
                }
        );
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        annotationsDialog.dispose();
                    }
                }
        );
        buttonsPanel.add(ok);
        buttonsPanel.add(cancel);
        
        this.annotationsDialog = new CytoscapeGODialog(go_client,buttonsPanel);
        //this.annotationsDialog.setModal(true);
        this.annotationsDialog.setRecursive(true);
    }
}