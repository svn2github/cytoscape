package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that is the listener for the BiNGO-button on the settingspanel.
 * * It collects all kinds of information: the ontology and annotation
 * * file, the alpha, which distribution and correction will be used, ...
 * * It also redirects the vizualisation and the making of a file with
 * * information. It also redirects calculation of the p-values and
 * * corrected p-values.  
 **/

import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;
import cytoscape.data.readers.TextFileReader;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import giny.model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



/**
 * ********************************************************************
 * SettingsPanelActionListener.java
 * --------------------------------
 * <p/>
 * Steven Maere & Karel Heymans (c) March 2005
 * <p/>
 * Class that is the listener for the BiNGO-button on the settingspanel.
 * It collects all kinds of information: the ontology and annotation
 * file, the alpha, which distribution and correction will be used, ...
 * It also redirects the vizualisation and the making of a file with
 * information. It also redirects calculation of the p-values and
 * corrected p-values.
 * *********************************************************************
 */

public class SettingsPanelActionListener implements ActionListener {

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/
    private SettingsPanel settingsPanel;
    private BingoParameters params;
    private BiNGO.GOlorize.GoBin goBin ;
    private JTaskConfig config;
    private CyNetworkView startNetworkView;
    private CyNetwork startNetwork;
    
//    private ZChooseBioDataServerAnnotation bdsAnnot;

    /**
     * constant string for the none-label in the combobox.
     */
    private final String NONE = BingoAlgorithm.NONE;
    private HashSet<String> ecCodes ;
    private HashMap<String,HashSet<String>> redundantIDs = new HashMap<String,HashSet<String>>() ;
    private boolean consistencyCheck = true;
    
    /**
     * constant strings for the checking versus option.
     */
    private final String GRAPH = BingoAlgorithm.GRAPH;
    private final String GENOME = BingoAlgorithm.GENOME;
    private final String VIZSTRING = BingoAlgorithm.VIZSTRING;

    /**
     * constant string for the none-label in the combobox.
     */
    private final String CATEGORY_BEFORE_CORRECTION = BingoAlgorithm.CATEGORY_BEFORE_CORRECTION;
    private final String CATEGORY_CORRECTION = BingoAlgorithm.CATEGORY_CORRECTION;



    /*--------------------------------------------------------------
      CONSTRUCTOR.
      --------------------------------------------------------------*/

    /**
     * Constructor with all the settings of the settingspanel as arguments.
     */
    public SettingsPanelActionListener(BingoParameters params, SettingsPanel settingsPanel) {
        this.params = params;
        this.settingsPanel = settingsPanel;
 //       this.bdsAnnot=settingsPanel.getBDSPanel();
        this.goBin = null;
        this.startNetworkView = null;
        this.startNetwork = null;
        ecCodes = new HashSet();
        ecCodes.add("IEA"); ecCodes.add("ISS"); ecCodes.add("TAS"); ecCodes.add("IDA"); ecCodes.add("IGI"); ecCodes.add("IMP"); ecCodes.add("IEP"); ecCodes.add("ND");
        ecCodes.add("RCA"); ecCodes.add("IPI"); ecCodes.add("NAS"); ecCodes.add("IC"); ecCodes.add("NR");
    }

    /*--------------------------------------------------------------
    LISTENER-PART.
    --------------------------------------------------------------*/

    /**
     * action that is performed when the BiNGO-button is clicked.
     *
     * @param event BiNGO-button clicked.
     */
    public void actionPerformed(ActionEvent event) {

        //Initialize a Task to perform all the tasks
        //  Configure JTask
        config = new JTaskConfig();

        //  Show Cancel/Close Buttons
        config.displayCancelButton(true);
        config.displayStatus(true);

        String status = updateParameters();
        HashSet noClassificationsSet = new HashSet() ;
        redundantIDs = new HashMap<String,HashSet<String>>() ;

        // passed all tests.
        if (status.equals("OK")) {

            //split graph input and text input
            consistencyCheck = true;
            if (params.getTextOrGraph()) {
                //graph
                startNetworkView = Cytoscape.getCurrentNetworkView();
                startNetwork = startNetworkView.getNetwork();
                CyNetwork network = Cytoscape.getCurrentNetwork();

                params.setSelectedNodes(getSelectedCanonicalNamesFromNetwork(network));
                if (params.getReferenceSet().equals(GRAPH)) {
                    params.setAllNodes(getAllCanonicalNamesFromNetwork(network));
                } else if (params.getReferenceSet().equals(GENOME)) {
                    params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
                } else {
                    params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
                }

                if (consistencyCheck) {
                    int [] testData = getClassificationsFromVector(params.getSelectedNodes(),noClassificationsSet);
                    boolean noElementsInTestData = false;
                    // testing whether there are elements in sample data array.
                    try {
                        int firstElement = testData[0];
                    }
                    catch (Exception ex) {
                        noElementsInTestData = true;
                    }
                    if (!noElementsInTestData) {
                        performCalculations(params.getSelectedNodes(), params.getAllNodes(), noClassificationsSet);
                    } else {
                        JOptionPane.showMessageDialog(settingsPanel,
                                "The selected annotation does not produce any" + "\n" +
                                        "classifications for the selected nodes." + "\n" +
                                        "Maybe you chose the wrong type of gene identifier ?");
                    }
                }
            } else {
                // split simple mode and batch mode

                if (!params.getCluster_name().equals("batch")) {

                    //text

                    if (params.getReferenceSet().equals(GRAPH)) {
                        //CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
                        CyNetwork network = Cytoscape.getCurrentNetwork();
                        params.setAllNodes(getAllCanonicalNamesFromNetwork(network));
                        HashSet<String> selection = getSelectedCanonicalNamesFromTextArea();
                        //conformize names...
                        params.setSelectedNodes(conformize(selection,params.getAllNodes())); 
                        if (!params.getAllNodes().containsAll(params.getSelectedNodes())) {
                            consistencyCheck = false;
                            JOptionPane.showMessageDialog(settingsPanel,
                                    "Some nodes in the text input panel do not exist in the network." + "\n" +
                                            "Option 'Test Cluster versus Graph' is not allowed.");
                        }
                    } else if (params.getReferenceSet().equals(GENOME)) {
                        params.setSelectedNodes(getSelectedCanonicalNamesFromTextArea());
                        params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
                    } else {
                        params.setSelectedNodes(getSelectedCanonicalNamesFromTextArea());
                        params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
                    }

                    if (consistencyCheck) {
                        int [] testData = getClassificationsFromVector(params.getSelectedNodes(),noClassificationsSet);
                        boolean noElementsInTestData = false;
                        // testing whether there are elements in sample data array.
                        try {
                            int firstElement = testData[0];
                        }
                        catch (Exception ex) {
                            noElementsInTestData = true;
                        }
                        if (!noElementsInTestData) {
                            performCalculations(params.getSelectedNodes(), params.getAllNodes(), noClassificationsSet);
                        } else {
                            JOptionPane.showMessageDialog(settingsPanel,
                                    "The selected annotation does not produce any" + "\n" +
                                            "classifications for the selected nodes." + "\n" +
                                            "Maybe you chose the wrong type of gene identifier ?");
                        }
                    }
                } else {
                    String textNodes = params.getTextInput();
                    String [] clusters = textNodes.split("batch");
                    if (clusters.length == 0) {
                        JOptionPane.showMessageDialog(settingsPanel,
                                "Please separate your clusters " + "\n" +
                                        "with the 'batch' keyword." + "\n");
                    } else {
                        if (params.getReferenceSet().equals(GRAPH)) {
                            //CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
                            CyNetwork network = Cytoscape.getCurrentNetwork();
                            params.setAllNodes(getAllCanonicalNamesFromNetwork(network));                           
                        } else if (params.getReferenceSet().equals(GENOME)) {
                            params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
                        } else {
                            params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
                        }
                        for (int i = 0; i < clusters.length; i++) {
                            consistencyCheck = true;
                            HashSet<String> selection = getBatchClusterFromTextArea(clusters[i]);
                            //conformize names...
                            params.setSelectedNodes(conformize(selection,params.getAllNodes()));  
                            if (params.getReferenceSet().equals(GRAPH) && !params.getAllNodes().containsAll(params.getSelectedNodes())) {
                                consistencyCheck = false;
                                JOptionPane.showMessageDialog(settingsPanel,
                                    "Some nodes in the text input panel do not exist in the network." + "\n" +
                                            "Option 'Test Cluster versus Graph' is not allowed.");
                            }
                            if (consistencyCheck) {
                                int [] testData = getClassificationsFromVector(params.getSelectedNodes(), noClassificationsSet);
                                boolean noElementsInTestData = false;
                                // testing whether there are elements in sample data array.
                                try {
                                    int firstElement = testData[0];
                                }
                                catch (Exception ex) {
                                    noElementsInTestData = true;
                                }
                                if (!noElementsInTestData) {
                                    performCalculations(params.getSelectedNodes(), params.getAllNodes(), noClassificationsSet);
                                } else {
                                    JOptionPane.showMessageDialog(settingsPanel,
                                            "The selected annotation does not produce any" + "\n" +
                                                    "classifications for the selected nodes." + "\n" +
                                                    "Maybe you chose the wrong type of gene identifier ?");
                                }
                            }
                        }                        
                    }
                }
            }
        }
    }


    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/
    private String openResourceFile(String name) {
        return getClass().getResource("/"+name).toString();
    }
    
    public HashSet<String> conformize(HashSet<String> selection, HashSet<String> allNodes){
        HashSet<String> conformizedSelection = new HashSet<String>();
        for(String s: selection){
            boolean ok = false;
            for(String s2: allNodes){                   
                if(params.getAlias().get(s) != null && params.getAlias().get(s2) != null){
                    if(params.getAlias().get(s).equals(params.getAlias().get(s2))){
                        conformizedSelection.add(s2);
                        ok = true;
                        /*if(!s.equals(s2)){
                            System.out.println(s + "\t" + s2);
                        }*/
                        break;
                    } 
                }
            }
            if(ok == false){
                conformizedSelection.add(s);
            }
        }
        return conformizedSelection;
    }
    
    public String updateParameters() {
        String status = "OK";
        params.setOverOrUnder(settingsPanel.getOverUnderPanel().getCheckedButton());
        params.setVisualization(settingsPanel.getVizPanel().getCheckedButton());
        params.setCluster_name(settingsPanel.getNameField().getText().trim());

        //checking cluster name.
        if (params.getCluster_name().equals("")) {
            JOptionPane.showMessageDialog(settingsPanel, "Please choose a cluster name ");
            status = "FALSE";
        } else {
            Set networkSet = Cytoscape.getNetworkSet();
            Iterator it = networkSet.iterator();
            while (it.hasNext()) {
                CyNetwork c = (CyNetwork) it.next();
                if (params.getCluster_name().equals(c.getTitle())) {
                    JOptionPane.showMessageDialog(settingsPanel, "A network with this name already exists in Cytoscape. Please choose another cluster name ");
                    status = "FALSE";
                }
            }
        }

        params.setTextOrGraph(settingsPanel.getTextOrGraphPanel().graphButtonChecked());
        if (status.equals("OK")) {
            // checking whether, if we select nodes from the network, the network and the selected nodes exist
            if (params.isTextOrGraph()) {
                // get the network object from the window.
                //CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
                CyNetwork network = Cytoscape.getCurrentNetwork();
                // can't continue if either of these is null
                //200905 changed : accept input from (large) network without view loaded
                if (network == null) {
                    JOptionPane.showMessageDialog(settingsPanel, "Please load a network.");
                    status = "FALSE";
                }
                // put up a dialog if there are no selected nodes
                else if (network.getSelectedNodes().size() == 0) {
                    JOptionPane.showMessageDialog(settingsPanel, "Please select one or more nodes.");
                    status = "FALSE";
                }
            }
            // testing whether, if nodes are selected from text area, there is something valid in the text area
            else if (!params.isTextOrGraph()) {
                //set the text from the window to the variable
                params.setTextInput(settingsPanel.getTextOrGraphPanel().getInputText());
                if (params.getTextInput() == null) {
                    JOptionPane.showMessageDialog(settingsPanel, "Please paste one or more genes in the text field.");
                    status = "FALSE";
                }
            }
        }

        //significance cut-off
        params.setCorrection((String) settingsPanel.getCorrectionBox().getSelectedItem());
        params.setTest((String) settingsPanel.getTestBox().getSelectedItem());
        params.setSignificance(new BigDecimal(settingsPanel.getAlphaField().getText()));
        if (status.equals("OK")) {
            // distribution selected?
            if (params.getTest().equals(NONE)) {
                settingsPanel.getAlphaField().setText("1.00") ;
                params.setSignificance(new BigDecimal("1.00"));
                if (!params.getCorrection().equals(NONE)) {
                    JOptionPane.showMessageDialog(settingsPanel, "Multiple testing correction not possible without test selection...");
                    status = "FALSE";
                }
            } else {
                // checking of alpha is a decimal between 0 and 1.
                boolean alphaIncorrect = false;
                try {
                    if (params.getSignificance().compareTo(new BigDecimal("1")) >= 0 ||
                            params.getSignificance().compareTo(new BigDecimal("0")) <= 0)
                        alphaIncorrect = true;

                }
                catch (Exception ex) {
                    alphaIncorrect = true;
                }
                if (alphaIncorrect) {
                    JOptionPane.showMessageDialog(settingsPanel, "Please input a valid significance level (i.e. a decimal number between 0 and 1).");
                    status = "FALSE";
                }
            }
        }

        //categories
        params.setCategory((String) settingsPanel.getCategoriesBox().getSelectedItem());
        params.setReferenceSet((String) settingsPanel.getClusterVsBox().getSelectedItem());
        if (status.equals("OK")) {
            // category-option selected?
            if (params.getCategory().equals(NONE)) {
                JOptionPane.showMessageDialog(settingsPanel, "Please select what categories should be visualized.");
                status = "FALSE";
            }
            // checking number of categories option
            else if (params.getCategory().equals(CATEGORY_CORRECTION) &&
                    params.getCorrection().equals(NONE)) {
                JOptionPane.showMessageDialog(settingsPanel,
                        "The option 'Overrepresented categories after correction'" + "\n"
                                + "at the category box requires the selection of a" + "\n"
                                + "correction in the correction box.");
                status = "FALSE";
            } else if (params.getCategory().equals(CATEGORY_BEFORE_CORRECTION) &&
                    params.getTest().equals(NONE)) {
                JOptionPane.showMessageDialog(settingsPanel,
                        "The option 'Overrepresented categories before correction'" + "\n"
                                + "at the category box requires at least the selection of a" + "\n"
                                + "test in the test box.");
                status = "FALSE";
            }
            // testing versus-option selected?
            else if (params.getReferenceSet().equals(NONE)) {
                JOptionPane.showMessageDialog(settingsPanel,
                        "Please select against what reference the cluster must be tested.");
                status = "FALSE";
            }
            // testing consistency of text area node selection mode with versus-option
            else if (params.getReferenceSet().equals(GRAPH) && !params.isTextOrGraph()) {
                //CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
                CyNetwork network = Cytoscape.getCurrentNetwork();
                // can't continue if either of these is null
                //200905 changed : accept input from (large) network without view loaded
                if (network == null) {
                    JOptionPane.showMessageDialog(settingsPanel, "Please load a network.");
                    status = "FALSE";
                }
            }
        }
        
        //annotation and ontology files
        
      //  if (! this.bdsAnnot.isMemoryChoiceEnabled() ) {
        
        params.setSpecies(settingsPanel.getAnnotationPanel().getSelection());

        //Get the specified species and the find out what is its corresponding file name
        String specified_species = settingsPanel.getAnnotationPanel().getSpecifiedSpecies();
        if(specified_species.equals(BingoAlgorithm.CUSTOM)){
            params.setAnnotationFile(settingsPanel.getAnnotationPanel().getSelection());
        }
        else{
            //get the file name for this species.
            String annot_filename = params.getSpeciesFilename(specified_species);
            params.setAnnotationFile(openResourceFile(annot_filename));
        }
        if(settingsPanel.getOntologyPanel().getSpecifiedOntology().equals(BingoAlgorithm.CUSTOM)){
          params.setOntologyFile(settingsPanel.getOntologyPanel().getSelection());
        }
        else{
          params.setOntologyFile(openResourceFile(settingsPanel.getOntologyPanel().getSelection()));
        }
        params.setAnnotation_default(settingsPanel.getAnnotationPanel().getDefault());
        params.setOntology_default(settingsPanel.getOntologyPanel().getDefault());
        HashSet deleteCodes = new HashSet();
        String tmp = settingsPanel.getEcField().getText().trim();
        String[] codes = tmp.split("\\s+");
        for(int i = 0; i<codes.length; i++){
          if(codes[i].length() != 0){  
            if(ecCodes.contains(codes[i].toUpperCase())){
                deleteCodes.add(codes[i].toUpperCase());
            }
            else{
                JOptionPane.showMessageDialog(settingsPanel, "Evidence code " + codes[i].toUpperCase() + " does not exist");
                status = "FALSE";
            }
          }  
        }
        params.setDeleteCodes(deleteCodes);
  //      params.setIdentifier(settingsPanel.getTypeOfIdentifierPanel().getCheckedButton());
        if (status.equals("OK")) {
            // annotation file selected?
            if (params.getAnnotationFile() == null) {
                JOptionPane.showMessageDialog(settingsPanel, "Please select an annotation file.");
                status = "FALSE";
            }
            // ontology file selected?
            else if (params.getOntologyFile() == null) {
                JOptionPane.showMessageDialog(settingsPanel, "Please select an ontology file.");
                status = "FALSE";
            } else {
                AnnotationParser annParser = params.initializeAnnotationParser();
                boolean success = TaskManager.executeTask(annParser, config);
                 if (annParser.getStatus()) {
                    params.setAnnotation(annParser.getAnnotation());
                    params.setOntology(annParser.getOntology());
                    params.setAlias(annParser.getAlias());
                    if (annParser.getOrphans()) {
                        JOptionPane.showMessageDialog(settingsPanel,
                                "WARNING : Some category labels in the annotation file" + "\n" +
                                    "are not defined in the ontology. Please check the compatibility of" + "\n" +
                                    "these files. For now, these labels will be ignored and calculations" + "\n" +
                                    "will proceed.");
                    }
                }
                else{
                    status = "FALSE";
                }
            }
        }
  /*      }
        //if using annotation/ontology from Cytoscape
        else{
               params.setAnnotation(bdsAnnot.getAnnotation());
               params.setAnnotation_default(false);
               params.setAnnotationFile(null);
               params.setOntology(bdsAnnot.getOntology());
               params.setOntology_default(false);
               params.setOntologyFile(null);
               AnnotationParser annParser = params.initializeAnnotationParser();
               boolean success = TaskManager.executeTask(annParser, config);
                 if (annParser.getStatus()) {
                    params.setAnnotation(annParser.getAnnotation());
                    params.setOntology(annParser.getOntology());
                 }
                 if (params.getAnnParser().getOrphans()) {
                    JOptionPane.showMessageDialog(settingsPanel,
                            "WARNING : Some category labels in the annotation" + "\n" +
                                    "are not defined in the ontology. Please check the compatibility of" + "\n" +
                                    "the ontology and annotation files loaded into Cytoscape." + "\n" +
                                    "For now, these labels will be ignored and calculations" + "\n" +
                                    "will proceed.");
                 }
        }
  */
        //Data panel
        params.setFileoutput(settingsPanel.getDataPanel().checked());
        if (status.equals("OK")) {
            // datafile save option selected ?
            if (params.isFileoutput()) {
                //does file exist already ?
                params.setFileoutput_dir(settingsPanel.getDataPanel().getFileDir());
                File sel = new File(settingsPanel.getDataPanel().getFileDir(), params.getCluster_name() + ".bgo");
                if (sel.exists()) {
                    int choice = JOptionPane.showConfirmDialog(settingsPanel, "File " + params.getCluster_name() + ".bgo already exists. Overwrite (y/n)?\nIf not, choose a different cluster name.", "confirm", JOptionPane.YES_NO_OPTION);//.getValue() ;
                    if (choice != JOptionPane.YES_OPTION) {
                        settingsPanel.getDataPanel().reset();
                        status = "FALSE";
                    }
                }
                // is file name for data file correct?
                String saveDataFileString = settingsPanel.getDataPanel().isFileNameLegal(params.getCluster_name() + ".bgo");
                if (!saveDataFileString.equals("LOADCORRECT")) {
                    JOptionPane.showMessageDialog(settingsPanel, "Data File: " + saveDataFileString);
                    status = "FALSE";
                }
            }
        }

        //return if all parameters were set.
        return status;
    }

    /**
     * method that gets the canonical names from the selected cluster.
     *
     * @return vector containing the canonical names.
     */
    public HashSet getSelectedCanonicalNamesFromNetwork(CyNetwork network) {
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
        // iterate over every node view to get the canonical names.
        for (Iterator i = network.getSelectedNodes().iterator(); i.hasNext();) {
            // getting next NodeView
            //NodeView nView = (NodeView)i.next();
            // first get the corresponding node
            Node node = (Node) i.next();
            // gets the canonical name of the given node from the attributes object
            String canonicalName = node.getIdentifier().toUpperCase();
            if (canonicalName != null && canonicalName.length() != 0 && !canonicalNameVector.contains(canonicalName)) {
                if(mapNames.contains(params.getAlias().get(node.getIdentifier().toUpperCase()))){
                    redundantIDs.put(node.getIdentifier().toUpperCase(), (HashSet<String>) params.getAlias().get(node.getIdentifier().toUpperCase()));
                    /*int opt = JOptionPane.showOptionDialog(settingsPanel,
                                "WARNING : The test set contains multiple identifiers for the gene/protein " + "\n" + 
                                    node.getIdentifier().toUpperCase() + ". If you press 'Yes', the redundant identifier will be ignored " + "\n" + 
                                    "and calculations will proceed. Press 'No' to abort calculations.","WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    if(opt == JOptionPane.NO_OPTION){
                       consistencyCheck = false;
                    }*/
                }
                //else{
                    if(params.getAlias().get(node.getIdentifier().toUpperCase()) != null){
                        mapNames.add((HashSet<String>) params.getAlias().get(node.getIdentifier().toUpperCase()));
                    }
                    canonicalNameVector.add(canonicalName);
                //}
            }
        }
        return canonicalNameVector;
    }

    /**
     * method that gets the canonical names from text input.
     *
     * @return HashSet containing the canonical names.
     */
    public HashSet getSelectedCanonicalNamesFromTextArea() {
        String textNodes = params.getTextInput();
        String [] nodes = textNodes.split("\\s+");
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
        // iterate over every node view to get the canonical names.
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase())){
                if(mapNames.contains(params.getAlias().get(nodes[i].toUpperCase()))){
                    redundantIDs.put(nodes[i].toUpperCase(), (HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                    /*int opt = JOptionPane.showOptionDialog(settingsPanel,
                                "WARNING : The test set contains multiple identifiers for the gene/protein " + "\n" + 
                                    nodes[i].toUpperCase() + ". If you press 'Yes', the redundant identifier will be ignored " + "\n" + 
                                    "and calculations will proceed. Press 'No' to abort calculations.","WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    if(opt == JOptionPane.NO_OPTION){
                       consistencyCheck = false;
                    }*/
                }
                //else{
                    if(params.getAlias().get(nodes[i].toUpperCase()) != null){
                        mapNames.add((HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                    }    
                    canonicalNameVector.add(nodes[i].toUpperCase());                    
                //}
            }      
        }
        return canonicalNameVector;
    }

    /**
     * method that gets the canonical names from text input in batch mode.
     *
     * @return vector containing the canonical names of batch instance.
     */

    public HashSet getBatchClusterFromTextArea(String textNodes) {

        String [] nodes = textNodes.split("\\s+");
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
        // iterate over every node view to get the canonical names.
        // first term is cluster name...
        int j = 0;
        while (nodes[j].equals("")) {
            j++;
        }
        params.setCluster_name(nodes[j]);
        for (int i = j+1; i < nodes.length; i++) {
            if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase())){
                if(mapNames.contains(params.getAlias().get(nodes[i].toUpperCase()))){
                    redundantIDs.put(nodes[i].toUpperCase(), (HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                    /*int opt = JOptionPane.showOptionDialog(settingsPanel,
                                "WARNING : The test set contains multiple identifiers for the gene/protein " + "\n" + 
                                    nodes[i].toUpperCase() + ". If you press 'Yes', the redundant identifier will be ignored " + "\n" + 
                                    "and calculations will proceed. Press 'No' to abort calculations.","WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    if(opt == JOptionPane.NO_OPTION){
                       consistencyCheck = false;
                    }*/
                }
                else{   
                    if(params.getAlias().get(nodes[i].toUpperCase()) != null){
                      mapNames.add((HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
                    }
                    canonicalNameVector.add(nodes[i].toUpperCase());
                }
            }     
        }                
        
        return canonicalNameVector;
    }


    /**
     * method that gets the canonical names from the whole graph.
     *
     * @return HashSet containing the canonical names from the network.
     */
    public HashSet getAllCanonicalNamesFromNetwork(CyNetwork network) {
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
        // iterate over every node view to get the canonical names.
        for (Iterator i = network.nodesIterator(); i.hasNext();) {
            // getting next NodeView
            //NodeView nView = (NodeView)i.next();
            // first get the corresponding node
            Node node = (Node) i.next();
            // gets the canonical name of the given node from the attributes object
            String canonicalName = node.getIdentifier().toUpperCase();
            if (canonicalName != null && (canonicalName.length() != 0) && !canonicalNameVector.contains(canonicalName)){
                if(mapNames.contains(params.getAlias().get(node.getIdentifier().toUpperCase()))){
                    redundantIDs.put(node.getIdentifier().toUpperCase(), (HashSet<String>) params.getAlias().get(node.getIdentifier().toUpperCase()));
                    int opt = JOptionPane.showOptionDialog(settingsPanel,
                                "WARNING : The network contains multiple identifiers for the gene/protein " + "\n" + 
                                    node.getIdentifier().toUpperCase() + ". If you press 'Yes', the redundant identifier will be ignored " + "\n" + 
                                    "and calculations will proceed. Press 'No' to abort calculations.","WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,null,null);
                    if(opt == JOptionPane.NO_OPTION){
                       consistencyCheck = false;
                    }
                }
                else{
                    if(params.getAlias().get(node.getIdentifier().toUpperCase()) != null){
                        mapNames.add((HashSet<String>) params.getAlias().get(node.getIdentifier().toUpperCase()));
                    }
                    canonicalNameVector.add(canonicalName);
                }
            }     
        }                
        
        return canonicalNameVector;
    }

    /**
     * method that gets the canonical names for the whole annotation.
     *
     * @return HashSet containing the canonical names.
     */
    public HashSet getAllCanonicalNamesFromAnnotation(HashSet selectedNodes) {
        String [] nodes = params.getAnnotation().getNames();
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null && (nodes[i].length() != 0)){
                canonicalNameVector.add(nodes[i].toUpperCase());
            }      
        }
        
        //replace canonical names in reference set that match one of the canonical names in the selected cluster, to get rid of e.g. splice variants if the non-splice-specific gene is part of the selection, and to avoid conflicts between names in ref set and selection
        HashMap<String,HashSet<String>> alias = params.getAlias();
        Iterator it2 = selectedNodes.iterator();
        while(it2.hasNext()){
            String name = it2.next() + "" ;
            HashSet tmp = alias.get(name);
            if(tmp != null){
                Iterator it = tmp.iterator();
                while(it.hasNext()){
                    canonicalNameVector.remove(it.next() + "");
                }
                //add selected node name
                canonicalNameVector.add(name);
            }
        }
        return canonicalNameVector;
    }

    /**
     * Method that gets the classifications from a HashSet of canonical names.
     *
     * @param canonicalNameVector HashSet of canonical names.
     * @return int[] classifications.
     */
    public int [] getClassificationsFromVector(HashSet canonicalNameVector, HashSet noClassificationsSet) {
        // HashSet for the classifications.
        HashSet classificationsVector = new HashSet();
        HashMap<String,HashSet<String>> alias = params.getAlias();
        // array for go labels.
        int [] goLabelsName;
        Iterator it2 = canonicalNameVector.iterator();
        while(it2.hasNext()){     
            String name = it2.next() + "";
            HashSet identifiers = alias.get(name) ;
            HashSet cls = new HashSet();
            // array for go labels.
            if(identifiers != null){
                Iterator it = identifiers.iterator() ;
                while(it.hasNext()){      
                    goLabelsName = params.getAnnotation().getClassifications(it.next() + "");
                    for (int t = 0; t < goLabelsName.length; t++){
                        cls.add(goLabelsName[t] + "");
                    }
                }
            }
            if(cls.size() == 0){
                  noClassificationsSet.add(name);
            }
            Iterator it3 = cls.iterator();
            while(it3.hasNext()){
                  classificationsVector.add(it3.next() + "");
            }
        }      
        int [] classifications = new int [classificationsVector.size()];
        it2 = classificationsVector.iterator();
        int i = 0;
        while(it2.hasNext()){
            classifications[i] = Integer.parseInt(it2.next() + "");
            i++;
        }
        return classifications;
    }

    /**
     * Method that redirects the calculations of the distribution and the correction.
     * Redirects the visualization of the network and
     * redirects the making of a file with the interesting data.
     */

    public void performCalculations(HashSet selectedNodes, HashSet allNodes, HashSet noClassificationsSet) {

        HashMap testMap = null;
        HashMap correctionMap = null;
        HashMap mapSmallX = null;
        HashMap mapSmallN = null;
        HashMap mapBigX = null;
        HashMap mapBigN = null;

        BingoAlgorithm algorithm = new BingoAlgorithm(params);

        CalculateTestTask test = algorithm.calculate_distribution();
        
            boolean success = TaskManager.executeTask(test, config);
            testMap = test.getTestMap();
            CalculateCorrectionTask correction = algorithm.calculate_corrections(testMap);
            if ((correction != null)&&(!params.getTest().equals(NONE))) {
                success = TaskManager.executeTask(correction, config);
                correctionMap = correction.getCorrectionMap();
            }
            mapSmallX = test.getMapSmallX();
            mapSmallN = test.getMapSmallN();
            mapBigX = test.getMapBigX();
            mapBigN = test.getMapBigN();

        DisplayBiNGOWindow display;
        CreateBiNGOFile file;

        if (params.getVisualization().equals(VIZSTRING)) {
            display = new DisplayBiNGOWindow(testMap,
                    correctionMap,
                    mapSmallX,
                    mapSmallN,
                    mapBigX,
                    mapBigN,
                    params.getSignificance().toString(),
                    params.getOntology(),
                    params.getCluster_name(),
                    params.getCategory() + "");

            // displaying the BiNGO CyNetwork.
            display.makeWindow();
        }
        if(goBin == null){
           goBin = new BiNGO.GOlorize.GoBin(settingsPanel, startNetworkView);
        }
        
        if(params.getAnnotationFile() == null){
            params.setAnnotationFile("Cytoscape loaded annotation: "+ params.getAnnotation().toString());
        }
        
        if(params.getOntologyFile() == null){
            params.setOntologyFile("Cytoscape loaded ontology: " + params.getOntology().toString());
        }
        
        goBin.createResultTab(testMap,
                    correctionMap,
                    mapSmallX,
                    mapSmallN,
                    mapBigX,
                    mapBigN,
                    params.getSignificance().toString(),
                    params.getAnnotation(),
                    params.getAlias(),
                    params.getOntology(),
                    params.getAnnotationFile().toString(),
                    params.getOntologyFile().toString(),
                    params.getTest() + "",
                    params.getCorrection() + "",
                    params.getOverOrUnder() + "",
                    params.getFileoutput_dir(),
                    params.getCluster_name() + ".bgo",
                    params.getReferenceSet() + "",
                    params.getCategory() + "",
                    selectedNodes,
                    startNetwork,
                    startNetworkView);
        
        if (params.isFileoutput()) {
            file = new CreateBiNGOFile(testMap,
                    correctionMap,
                    mapSmallX,
                    mapSmallN,
                    mapBigX,
                    mapBigN,
                    params.getSignificance().toString(),
                    params.getAnnotation(),
                    params.getDeleteCodes(),
                    params.getAlias(),
                    params.getOntology(),
                    params.getAnnotationFile().toString(),
                    params.getOntologyFile().toString(),
                    params.getTest() + "",
                    params.getCorrection() + "",
                    params.getOverOrUnder() + "",
                    params.getFileoutput_dir(),
                    params.getCluster_name() + ".bgo",
                    params.getReferenceSet() + "",
                    params.getCategory() + "",
                    selectedNodes,
                    noClassificationsSet);
            file.makeFile();
            
            if(params.getTest().equals(NONE) && params.getCorrection().equals(NONE)){
                CreateAnnotationFile file2 = new CreateAnnotationFile(
                    params.getAnnotation(),
                    params.getAlias(),
                    params.getOntology(),
                    params.getFileoutput_dir(),
                    params.getCluster_name() + ".anno",
                    selectedNodes);
                file2.makeFile();
            }

            //show resulting File in window
      /*      File f = new File(params.getFileoutput_dir(), params.getCluster_name() + ".bgo");
            String fileName = f.getAbsolutePath();
            TextFileReader reader = new TextFileReader(fileName);
            reader.read();
            String fullText = reader.getText();
            JTextArea textArea = new JTextArea(20, 60);
            JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            textArea.setEditable(false);
            textArea.append(fullText);
            textArea.setCaretPosition(0);
            JFrame window = new JFrame(params.getCluster_name() + " BiNGO Results");
            window.getContentPane().add(new JPanel().add(scrollPane));
            window.pack();
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // for top right position of the output panel.
            window.setLocation(screenSize.width - window.getWidth(), 0);
            window.setVisible(true);
            window.setResizable(true);*/

        }

    }

}
