package org.isb.iavila.ontology.gui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.*;
import org.isb.iavila.ontology.*;
import org.isb.iavila.ontology.xmlrpc.*;
import cytoscape.*;
import cytoscape.CyNode;
import cytoscape.data.Semantics;
//import cytoscape.data.CytoscapeData;

public class CytoscapeGODialog extends JDialog {

    /**
     * The name of the attribute used to save GO node attributes
     */
    public static final String ATTRIBUTE_NAME = "GO_Terms";

    protected GOViewer goViewer;

    protected GOClient goClient;

    protected JDialog speciesDialog;

    protected JList speciesList;

    protected JTextField spField;

    protected GOSpecies selectedSpecies;
    
    protected JRadioButton recursiveRadioButton;

    /**
     * Creates the JFrame
     * 
     * @param go_client
     *            the GOClient to use
     */
    public CytoscapeGODialog (GOClient go_client) {
        this(go_client, null);
    }

    /**
     * 
     * @param go_client
     *            the GOClient to use
     * @param buttons_panel
     *            the panel that will be located in the BorderLayout.SOUTH
     *            section of the dialog
     */
    public CytoscapeGODialog (GOClient go_client, JPanel buttons_panel) {
        setTitle("Cytoscape GO");
        this.goClient = go_client;
        this.recursiveRadioButton = new JRadioButton("Recursive");
        this.recursiveRadioButton.setSelected(true);
        create(buttons_panel);
    }

    /**
     * 
     * @return the user selected GOSpecies
     */
    public GOSpecies getSelectedSpecies () {
        return this.selectedSpecies;
    }
    
    /**
     * 
     * @param taxid NCBI taxid as a String parsable as an int
     */
    public void setSelectedSpeciesTaxid (String taxid){
        Hashtable info = null;
        try{
            info = this.goClient.getSpeciesWithID(taxid);
        }catch (Exception e){ e.printStackTrace();}
        
        int id = Integer.parseInt((String)info.get(GOHandler.SPECIES_ID));
        String genus = (String)info.get(GOHandler.GENUS);
        String species = (String)info.get(GOHandler.SPECIES);
        String commonName = (String)info.get(GOHandler.SP_COMMON_NAME);
        GOSpecies gSpecies = new GOSpecies(id, genus, species, commonName);
        setSelectedSpecies(gSpecies);
    }

    /**
     * @param sp
     */
    protected void setSelectedSpecies (GOSpecies sp) {
        this.selectedSpecies = sp;
        this.spField.setText(sp.toString());
    }
    
    
   
    /**
     * Sets whether or not operations should be recursive. For example, if true, createNodes will return nodes
     * with the selected terms and their descendant terms as well.
     * @param recursive
     */
    public void setRecursive (boolean recursive){
        this.recursiveRadioButton.setSelected(recursive);
    }
    
    /**
     * Gets whether or not operations should be recursive. For example, if true, createNodes will return nodes
     * with the selected terms and their descendant terms as well.
     */
    
    public boolean getRecursive (){
        return this.recursiveRadioButton.isSelected();
    }

    /**
     * Creates the GUI
     * 
     * @param buttons_panel
     *            the panel that goes at the bottom of the dialog
     */
    protected void create (JPanel buttons_panel) {
        getContentPane().setLayout(new BorderLayout());

        // 1. Select species
        JPanel spPanel = createSpeciesPanel();
        getContentPane().add(spPanel, BorderLayout.NORTH);

        // 2. Select terms
        this.goViewer = new GOViewer(this.goClient);
        // this.goViewer.setPreferredSize(new Dimension(100,250)); //TODO:
        // adjust this
        getContentPane().add(this.goViewer, BorderLayout.CENTER);

        // 3. Things you can do in Cytoscape with the above
        if (buttons_panel == null) {
            JPanel defaultPanel = createButtonPanel();
            getContentPane().add(defaultPanel, BorderLayout.SOUTH);
        } else {
            getContentPane().add(buttons_panel, BorderLayout.SOUTH);
        }

    }

    /**
     * Creates the species query panel
     * 
     * @return
     */
    protected JPanel createSpeciesPanel () {

        JLabel label = new JLabel("Species that contain:");
        this.spField = new JTextField();
        spField.setColumns(20);
        JButton searchButton = new JButton("Search...");
        searchButton.addActionListener(

        new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                String pattern = spField.getText();
                if (pattern != null && pattern.length() > 0) {
                    try {
                        pattern.trim();
                        Vector species = goClient.getSpeciesLike(pattern);
                        displaySpeciesDialog(species);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(
                                    CytoscapeGODialog.this,
                                    "Please enter a species (common or canonical name, can be incomplete)",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                }
            }// actionPerformed

        }// AbstractAction

                );
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(this.spField);
        panel.add(searchButton);

        return panel;
    }

    /**
     * Creates the default buttons panel with the following buttons: Create
     * Nodes, Select Nodes, Create Node Attribute
     * 
     * @return
     */
    // Todo: Need to use SwingWorker -iliana
    protected JPanel createButtonPanel() {
        JPanel bPanel = new JPanel();
        JButton createNodesButton = new JButton("Create Nodes...");
        createNodesButton.addActionListener(new AbstractAction() {

            public void actionPerformed(ActionEvent action) {
               
                // String netName = askForNetworkName();
                String netName = "GO NET";
                createNetwork(netName);
            }

        });
        JButton selectNodesButton = new JButton("Select Nodes");
        selectNodesButton.addActionListener(new AbstractAction() {

            public void actionPerformed(ActionEvent action) {
                selectNodes(false);
            }

        });
        JButton attributeButton = new JButton("Create Node Attribute");
        attributeButton.addActionListener(

        new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                attachNodeAttribute();
            }
        });
        
        bPanel.add(createNodesButton);
        bPanel.add(selectNodesButton);
        bPanel.add(attributeButton);
        bPanel.add(this.recursiveRadioButton);
        return bPanel;
    }

    /**
     * Shows the results of a species query
     * 
     * @param species
     *            the result of the query (Vector of GOSpecies objects)
     */
    protected void displaySpeciesDialog(Vector species) {

        if (this.speciesDialog == null) {
            this.speciesDialog = createSpeciesDialog(species);
        } else {
            updateSpeciesList(species);
        }

        this.speciesDialog.pack();
        this.speciesDialog.setLocationRelativeTo(this);
        this.speciesDialog.setVisible(true);

    }

    /**
     * Sorts species by their GOSpecies.toString() values, and then sets the
     * this.speciesList to the new species results
     * 
     * @param species
     */
    protected void updateSpeciesList(Vector species) {
        TreeSet sortedSpecies = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                GOSpecies s1 = (GOSpecies) o1;
                GOSpecies s2 = (GOSpecies) o2;
                return s1.toString().compareTo(s2.toString());
            }

            public boolean equals(Object obj) {
                return obj == this;
            }
        });

        Iterator it = species.iterator();
        while (it.hasNext()) {
            Hashtable table = (Hashtable) it.next();
            int id = Integer.parseInt((String) table.get(GOHandler.SPECIES_ID));
            String genus = (String) table.get(GOHandler.GENUS);
            String sp = (String) table.get(GOHandler.SPECIES);
            String commonName = (String) table.get(GOHandler.SP_COMMON_NAME);
            GOSpecies goSP = new GOSpecies(id, genus, sp, commonName);
            sortedSpecies.add(goSP);
        }
        this.speciesList.removeAll();
        this.speciesList.setListData(new Vector(sortedSpecies));
    }

    /**
     * Creates the dialog that shows the results of a species query
     * 
     * @param species
     *            Vector of GOSpecies objects
     * @return the dialog
     */
    protected JDialog createSpeciesDialog(Vector species) {
        final JDialog dialog = new JDialog();
        dialog.setTitle("Matched Species");
        dialog.getContentPane().setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel label = 
            new JLabel("<html>Select one of the following species that matched your query.</html>");
        topPanel.add(label);

        dialog.getContentPane().add(topPanel, BorderLayout.NORTH);

        this.speciesList = new JList();
        updateSpeciesList(species);
        JScrollPane scrollPane = new JScrollPane(this.speciesList);
        scrollPane.setPreferredSize(new Dimension(80, 100));

        dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                GOSpecies gsp = (GOSpecies) speciesList.getSelectedValue();
                if (gsp == null) {
                    // message????
                } else {
                    setSelectedSpecies(gsp);
                }
                dialog.dispose();
            }// actionPerformed

        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(

        new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                dialog.dispose();
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        return dialog;

    }

    // ----------- Public methods that should be in a separate class
    
    /**
     * @return an array of gene ids that are annotated with the selected terms for the selected species
     */
    public String [] getGenesWithTerms (boolean useAndOperator){
        OntologyTerm[] terms = this.goViewer.getSelectedTerms();

        Vector termIDs = new Vector();
        for (int i = 0; i < terms.length; i++) {
            termIDs.add(Integer.toString(terms[i].getID()));
        }// for

        String spID = Integer.toString(this.selectedSpecies.getID());
        Hashtable termToGenes = null;
        Vector genes = null;
        try {
            if(!useAndOperator)
                termToGenes = goClient.getGenesWithTerms(termIDs, spID, this.recursiveRadioButton.isSelected());
            else
                genes = goClient.getGenesWithTermsIntersection(termIDs,spID,this.recursiveRadioButton.isSelected());
            //  System.out.println(termToGenes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        if(useAndOperator) return (String []) genes.toArray(new String[genes.size()]);
        
        Iterator it = termToGenes.keySet().iterator();
        HashSet geneIDs = new HashSet();
        while (it.hasNext()) {
            String termID = (String) it.next();
            genes = (Vector) termToGenes.get(termID);
            Iterator it2 = genes.iterator();
            while (it2.hasNext()) {
                String gene = (String) it2.next();
                geneIDs.add(gene);
            }// while it2
        }// while it.hasNext
        return (String[])geneIDs.toArray(new String[geneIDs.size()]);
    }
    
    
    /**
     * Creates nodes that are annotated with the selected ontology terms and
     * belog to the selected species
     * 
     * @return the new CyNodes
     */
    public Collection createNodes(boolean useAndOperator) {

        String [] genes = getGenesWithTerms(useAndOperator);
        
        HashSet nodes = new HashSet();
        for (int i = 0; i < genes.length; i++) {
            String termID = genes[i];
            CyNode node = (CyNode) Cytoscape.getCyNode(termID, true);
            nodes.add(node);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),Semantics.SPECIES,this.selectedSpecies.toString());
        }//for
        return nodes;
    }

    /**
     * Creates nodes in the currently selected network (or it creates a new one)
     * that are annotated with the selected ontology terms and belog to the
     * selected species
     */
    // TODO: Ask the user for network options (create nodes in current net? name
    // of new net? etc).
    public void createNetwork(String net_name) {
        Collection nodes = createNodes(false);
        
        if (nodes.size() == 0) {
            JOptionPane.showMessageDialog(this,
                    "There are no nodes annotated with the selected terms.",
                    "Cytoscape GO", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
            
        int answer = JOptionPane.showConfirmDialog(this, "There are " + nodes.size()
                + " nodes annotated with the selected terms. Create nodes?",
                "Cytoscape GO", JOptionPane.YES_NO_OPTION);
            
        if(answer != JOptionPane.YES_OPTION) return;
        
        CyNetwork net = Cytoscape.getNetwork(net_name);
        if (net == null || net.getIdentifier().equals("0")) {
            net = Cytoscape.createNetwork(nodes, new ArrayList(), net_name);
            Cytoscape.getDesktop().setFocus(net.getIdentifier());
        } else {
            int [] nodeIndices = new int [nodes.size()];
            Iterator it = nodes.iterator();
            int i = 0;
            while(it.hasNext()){
                nodeIndices[i] = ((CyNode)it.next()).getRootGraphIndex();
                i++;
            }
            net.restoreNodes(nodeIndices);          
        }

    }

    /**
     * Selects nodes in the currently selected network that are annotated with
     * the selected ontology terms and belog to the selected species
     */
    public void selectNodes(boolean useAndOperator) {
        
        String [] genes = getGenesWithTerms(useAndOperator);
        
        HashSet nodes = new HashSet();
        for (int i = 0; i < genes.length; i++) {
            String termID = genes[i];
            CyNode node = (CyNode) Cytoscape.getCyNode(termID, false);
            nodes.add(node);
        }//for
        
        if (nodes.size() == 0) {
            JOptionPane.showMessageDialog(this,
                    "There are no nodes annotated with the selected terms.",
                    "Cytoscape GO", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        CyNetwork net = Cytoscape.getCurrentNetwork();
        if (net == null || net.getIdentifier().equals("0")) {
            JOptionPane.showMessageDialog(this,
                    "There are no networks present.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            
            // DOES NOT WORK:
            //Cytoscape.getCurrentNetworkView().setSelected((CyNode[])nodes.toArray(new CyNode[nodes.size()]));
            //Cytoscape.getCurrentNetworkView().setSelected((NodeView[])nodeViews.toArray(new NodeView[nodeViews.size()]));
            // THIS WORKS:
            net.setFlaggedNodes(nodes,true);
            //Cytoscape.getCurrentNetwork().setFlaggedNodes(nodes, true);
        }

    }

    /**
     * Creates a node attribute for each selected term, and annotates nodes with
     * that term
     * 
     */
    public void attachNodeAttribute() {
        OntologyTerm[] terms = this.goViewer.getSelectedTerms();

        Vector termIDs = new Vector();
        for (int i = 0; i < terms.length; i++) {
            termIDs.add(Integer.toString(terms[i].getID()));
        }// for

        String spID = Integer.toString(this.selectedSpecies.getID());
        Hashtable termToGenes = null;
        Hashtable termsInfo = null;
        try {
            termToGenes = goClient.getGenesWithTerms(termIDs, spID, this.recursiveRadioButton.isSelected());
            termsInfo = goClient.getTermsInfo(new Vector(termToGenes.keySet()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Iterator it = termToGenes.keySet().iterator();
        ArrayList nodes = new ArrayList();
        //Test new CytoscapeData:
        //CytoscapeData cyData = Cytoscape.getCurrentNetwork().getNodeData(); 
        //cyData.initializeAttributeType(ATTRIBUTE_NAME, CytoscapeData.TYPE_STRING);
        
        while (it.hasNext()) {
            String termID = (String) it.next();
            Vector genes = (Vector) termToGenes.get(termID);
            Hashtable termInfo = (Hashtable) termsInfo.get(termID);
            String termName = (String) termInfo.get(GOHandler.TERM_NAME);
            Iterator it2 = genes.iterator();
            while (it2.hasNext()) {
                String gene = (String) it2.next();
                CyNode node = (CyNode) Cytoscape.getCyNode(gene, false);
                nodes.add(node);   
                String termList = (String)Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(),ATTRIBUTE_NAME);
                if (termList != null) {
                    // Make sure we are not adding repeated terms:
                    String[] setTerms = termList.split(",");
                    boolean found = false;
                    for (int i = 0; i < setTerms.length; i++) {
                        if (setTerms.equals(termName)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        termList += "," + termName;
                } else {
                    termList = termName;
                }
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),ATTRIBUTE_NAME,termList);
            }// while it2
        }// while it.hasNext
        //System.out.println("Done setting attribuets!!!!");
        if (nodes.size() == 0) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "<html>There are no nodes annotated with the selected terms."+
                            "<br>Node attributes were not created.</html>",
                            "Cytoscape GO", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

}