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
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Set;

import org.isb.bionet.gui.*;
import org.isb.bionet.gui.taxonomy.TaxonomySearchDialog;
import org.isb.iavila.ontology.gui.*;
import org.isb.iavila.ontology.xmlrpc.*;
import org.isb.bionet.datasource.synonyms.*;

import utils.MyUtils;
import cytoscape.*;
import cytoscape.data.Semantics;

public class NodeSourcesPanel extends JPanel {

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
    
    /**
     *  Creates a panel with node sources
     */
    public NodeSourcesPanel (GOClient go_client, SynonymsClient synonyms_client){
        create(go_client, synonyms_client);
    }
    
    /**
     * @return if a file has been selected, it returns it, returns null otherwise
     */
    public File getMyListFile (){
        return this.myListFile;
    }
    
    /**
     * 
     * @return a Vector of gene ids from the user selected sources
     */
    public Vector getAllNodes (){
        Vector myListNodes = getNodesFromMyList();
        CyNetwork [] nodeNets = getSelectedNetworks();
        // TODO: Do checks: selected networks species must match to the species the user selected in this sesion
        // TODO: Nodes from annotations!
        Vector startingNodes = new Vector();
        if(myListNodes != null && useList.isSelected()){
            startingNodes.addAll(myListNodes);
        }
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
            // TODO: Add nodes from taxonomy
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
    
    /**
     * 
     * @return a Vector with the node names in "myList" file
     */
    public Vector getNodesFromMyList (){
        return this.myListNodes;
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
     * Creates the panel
     */
    protected void create(final GOClient go_client, final SynonymsClient synonyms_client) {
        
        //CREATE BUTTONS
        
        // Annotations button
        final JButton annotsButton = new JButton("Nodes with selected annotations...");
        annotsButton.setEnabled(false);
        annotsButton.addActionListener(new AbstractAction(){
            
            public void actionPerformed (ActionEvent event){
                //JOptionPane.showMessageDialog(NodeSourcesPanel.this, "Not implemented yet!", "Oops!", JOptionPane.ERROR_MESSAGE);
                if(annotationsDialog == null){
                    createAnnotationsDialog(go_client);
                }
                annotationsDialog.pack();
                annotationsDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                annotationsDialog.setVisible(true);
                // The dialog is modal
               annotationNodeIDs = annotationsDialog.getGenesWithTerms();
               System.out.println("There are " + annotationNodeIDs.length + " nodes from annotations.");
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
        final JButton taxButton = new JButton("Nodes from NCBI Taxonomy...");
        taxButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed (ActionEvent event){
                        if(taxonomyDialog == null){
                            taxonomyDialog = new TaxonomySearchDialog(synonyms_client);
                        }
                        if(taxonomyDialog.isVisible()){
                            taxonomyDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                        }else{
                            taxonomyDialog.pack();
                            taxonomyDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                            taxonomyDialog.setVisible(true);
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
                        int [] newNodes = annotationsDialog.createNodes();
                        ArrayList canonicals = new ArrayList();
                        for(int i = 0; i < newNodes.length; i++){
                            CyNode node = (CyNode)Cytoscape.getRootGraph().getNode(newNodes[i]);
                            String canonical = Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME);
                            canonicals.add(canonical);
                        }
                        annotationNodeIDs = (String[])canonicals.toArray(new String[canonicals.size()]);
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
        
    }
}