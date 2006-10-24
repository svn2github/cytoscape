package GOlorize.BiNGO;


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
/** Modified by Olivier Garcia (23/10/2006) :
 *  Changes :   Constructor that allows interactions with GOlorize (parameter GoBin, to send the BiNGO results to 
 *              GOlorize).
 *              Management of BioDataServer datas if available and choosen by user (parsing phase is cancelled and 
 *              the annotations are remapped by ZRemapAnnotation).
 * 
 */

import java.io.*;
import java.util.*;
import java.math.BigDecimal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.CyNetwork;
//import cytoscape.data.GraphObjAttributes;
import cytoscape.data.annotation.*;
import cytoscape.data.annotation.readers.*;
import cytoscape.data.readers.*;
import cytoscape.Cytoscape;

import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

/***********************************************************************
 * SettingsPanelActionListener.java     
 * --------------------------------
 *
 * Steven Maere & Karel Heymans (c) March 2005
 *		
 * Class that is the listener for the BiNGO-button on the settingspanel.
 * It collects all kinds of information: the ontology and annotation
 * file, the alpha, which distribution and correction will be used, ...
 * It also redirects the vizualisation and the making of a file with
 * information. It also redirects calculation of the p-values and
 * corrected p-values.
 ***********************************************************************/

 public class SettingsPanelActionListener implements ActionListener {




  /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/

		
	private TextOrGraphPanel textOrGraphPanel ;	
	/** JComboBox with the possible distributions.*/
  	private JComboBox testBox;
	/** JComboBox with the possible corrections.*/
	private JComboBox correctionBox;
	/** JComboBox with the category visualization options.*/
	private JComboBox categoriesBox;
	/** JComboBox with the options against what cluster should be tested.*/
	private JComboBox clusterVsBox;
	/** JTextField for the significance level.*/
	private JTextField alphaField;
	/** JTextField for the cluster name.*/
	private JTextField nameField;
	/** radiobuttons for choosing over- or underrepresentation */
	private OverUnderPanel overUnderPanel ;
	/** radiobuttons for choosing visualization/no vizualization */
	private VizPanel vizPanel ;
	/** SettingsOpenPanel for choosing the type of gene identifier used */
	private TypeOfIdentifierPanel typeOfIdentifierPanel;
	/** SettingsOpenPanel for choosing the annotation*/
	private ChooseAnnotationPanel annotationPanel;
	/** ChooseOntologyPanel for choosing the ontology*/
	private ChooseOntologyPanel ontologyPanel;
	/** the settingspanel.*/
	private SettingsPanel settingsPanel;
	/** the SettingsSavePanel for choosing the name and place for the data file.*/	
	private SettingsSavePanel dataPanel;
	/** the annotation.*/
	private Annotation annotation;
	/** the ontology.*/
	private Ontology ontology;
	/** the GO termID synonyms list compiled from the ontology*/
	private HashMap synonymHash ;
 	/** name of currently analyzed cluster ; used for creating attribute names and visual styles */
	private String networkName ;
	/** constant string for the none-label in the combobox.*/
	private final String NONE = "---";
	/** constant string for the name Hypergeometric Distribution.*/
	private final String HYPERGEOMETRIC = "Hypergeometric test";
	/** constant string for the name Binomial Test.*/
	private final String BINOMIAL = "Binomial test";
	/** constant string for the Benjamini & Hochberg FDR correction.*/
	private final String BENJAMINI_HOCHBERG_FDR = "Benjamini & Hochberg False Discovery Rate (FDR) correction";
	/** constant string for the Bonferroni FWER correction.*/
	private final String BONFERRONI = "Bonferroni Family-Wise Error Rate (FWER) correction";
	/** constant string for the loadcorrect of the filechooser.*/
	private final String LOADCORRECT = "LOADCORRECT";
  	/** constant string for the checking of numbers of categories, all categories.*/
  	private final String CATEGORY_ALL = "All categories";
  	/** constant string for the checking of numbers of categories, before correction.*/
  	private final String CATEGORY_BEFORE_CORRECTION = "Overrepresented categories before correction";
  	/** constant string for the checking of numbers of categories, after correction.*/
 	private final String CATEGORY_CORRECTION = "Overrepresented categories after correction";
	/** constant strings for the checking versus option.*/
	private final String GRAPH = "Test cluster versus network";
	private final String GENOME = "Test cluster versus whole annotation";
	private final String OVERSTRING = "Overrepresentation";
    private final String UNDERSTRING = "Underrepresentation";
	private static String VIZSTRING = "Visualization";
    private static String NOVIZSTRING = "No Visualization";

	private CalculateTestTask test ;
	private JProgressBar progressBar;
	private JFrame frame ;
	private JPanel panel ;
	/** parser for annotation in function of chosen ontology */
	private AnnotationParser annParser ;
	/** BiNGO directory path*/
	private String bingoDir ;
        private ZChooseBioDataServerAnnotation bdsAnnot;
        
        
        private GOlorize.GoBin goBin;

	/*--------------------------------------------------------------
      CONSTRUCTOR.
      --------------------------------------------------------------*/

	/**
	 * Constructor with all the settings of the settingspanel as arguments.
	 *
	 * @param settingsPanel the settingsPanel itself
	 */
    public SettingsPanelActionListener (SettingsPanel settingsPanel,GOlorize.GoBin goB) {
        
        this.goBin = goB;
        
        this.settingsPanel = settingsPanel;
		this.textOrGraphPanel = settingsPanel.getTextOrGraphPanel() ;
		this.typeOfIdentifierPanel = settingsPanel.getTypeOfIdentifierPanel() ;
		this.overUnderPanel = settingsPanel.getOverUnderPanel() ;
		this.vizPanel = settingsPanel.getVizPanel() ;
        this.testBox = settingsPanel.getTestBox();
        this.correctionBox = settingsPanel.getCorrectionBox();
        this.alphaField = settingsPanel.getAlphaField();
        this.categoriesBox = settingsPanel.getCategoriesBox();
        this.clusterVsBox = settingsPanel.getClusterVsBox();
        this.annotationPanel = settingsPanel.getAnnotationPanel();
        this.ontologyPanel = settingsPanel.getOntologyPanel();
        this.dataPanel = settingsPanel.getDataPanel();
        this.nameField = settingsPanel.getNameField();
		this.bingoDir = settingsPanel.getBingoDir() ;
                
                this.bdsAnnot=settingsPanel.getBDSPanel();
    }





	/*-------------------------------------Cytoscape-v2.2-------------------------
      LISTENER-PART.
      --------------------------------------------------------------*/

	/**
	 * action that is performed when the BiNGO-button is clicked.
	 *
	 * @param event BiNGO-button clicked.
	 */
    public void actionPerformed (ActionEvent event) {
         
		String status = "OK" ;
	    
		//checking cluster name.
		if(nameField.getText().trim().equals("")) {
	    	JOptionPane.showMessageDialog(settingsPanel,"Please choose a cluster name " );
			status = "FALSE" ;	
		}
		else{
			Set networkSet = Cytoscape.getNetworkSet() ;
			Iterator it = networkSet.iterator() ;
			while(it.hasNext()){
				CyNetwork c = (CyNetwork) it.next() ;
				if(nameField.getText().trim().equals(c.getTitle())){
					JOptionPane.showMessageDialog(settingsPanel,"A network with this name already exists in Cytoscape. Please choose another cluster name " );
					status = "FALSE" ;
				}
			}
		}	
		
		if(status.equals("OK")){
			// checking whether, if we select nodes from the network, the network and the selected nodes exist
			if(textOrGraphPanel.graphButtonChecked()){ 	
       			// get the network object from the window.
				//CyNetworkView graphView = Cytoscape.getCurrentNetworkView();  
				CyNetwork network = Cytoscape.getCurrentNetwork(); 				
        		// can't continue if either of these is null
				//200905 changed : accept input from (large) network without view loaded
				if (network == null) {JOptionPane.showMessageDialog(settingsPanel,"Please load a network.");
					status = "FALSE" ;
				}
				// put up a dialog if there are no selected nodes
				else if (network.getFlaggedNodes().size() == 0) {
					JOptionPane.showMessageDialog(settingsPanel,"Please select one or more nodes.");
					status = "FALSE" ;
				}
			}
			// testing whether, if nodes are selected from text area, there is something valid in the text area 
			else if(!textOrGraphPanel.graphButtonChecked()){
				if(!nameField.getText().trim().equals("batch")){
					Vector canonicalNameVector = getSelectedCanonicalNamesFromTextArea() ;
					if(canonicalNameVector.size() == 0){
						JOptionPane.showMessageDialog(settingsPanel,"Please paste one or more genes in the text field.");
						status = "FALSE" ;
					}	
				}		
			}
		}	
		
   		if(status.equals("OK")){
    		// distribution selected?
			if (testBox.getSelectedItem().equals(NONE)){
				if (!correctionBox.getSelectedItem().equals(NONE)){	
					JOptionPane.showMessageDialog(settingsPanel,"Multiple testing correction not possible without test selection...");
					status = "FALSE" ;	
				}	
				//allow no testing... anything is allowed in alpha field for now
				//JOptionPane.showMessageDialog(settingsPanel,"Please select a distribution.");
				//status = "FALSE" ; 
			}
			else {
			// checking of alpha is a decimal between 0 and 1.
				boolean alphaIncorrect = false;
				try {
					if (new BigDecimal(alphaField.getText()).compareTo(new BigDecimal("1")) >= 0 ||
						new BigDecimal(alphaField.getText()).compareTo(new BigDecimal("0")) <= 0)
						alphaIncorrect = true;
				}
				catch (Exception ex){
					alphaIncorrect = true;
				}
				if (alphaIncorrect){
					JOptionPane.showMessageDialog(settingsPanel,"Please input a valid significance level (i.e. a decimal number between 0 and 1).");
					status = "FALSE" ;	
				}
			}	
		}	
	
		if(status.equals("OK")){
			// category-option selected?
			if (categoriesBox.getSelectedItem().equals(NONE)){
				JOptionPane.showMessageDialog(settingsPanel,"Please select what categories should be visualized.");
				status = "FALSE" ;	
			}
			// checking number of categories option
			else if (categoriesBox.getSelectedItem().equals(CATEGORY_CORRECTION) &&
				correctionBox.getSelectedItem().equals(NONE)){
				JOptionPane.showMessageDialog(settingsPanel,
					"The option 'Overrepresented categories after correction'" + "\n"
					+ "at the category box requires the selection of a" + "\n"
					+ "correction in the correction box.");
				status = "FALSE" ;	
			}
			else if (categoriesBox.getSelectedItem().equals(CATEGORY_BEFORE_CORRECTION) &&
				testBox.getSelectedItem().equals(NONE)){
				JOptionPane.showMessageDialog(settingsPanel,
					"The option 'Overrepresented categories before correction'" + "\n"
					+ "at the category box requires at least the selection of a" + "\n"
					+ "test in the test box.");
				status = "FALSE" ;	
			}	
			// testing versus-option selected?
			else if (clusterVsBox.getSelectedItem().equals(NONE)){
				JOptionPane.showMessageDialog(settingsPanel,
					"Please select against what reference the cluster must be tested.");
				status = "FALSE" ;	
			}
			// testing consistency of text area node selection mode with versus-option
			else if (clusterVsBox.getSelectedItem().equals(GRAPH) && !textOrGraphPanel.graphButtonChecked()){
				//CyNetworkView graphView = Cytoscape.getCurrentNetworkView();  
				CyNetwork network = Cytoscape.getCurrentNetwork(); 				
				// can't continue if either of these is null
				//200905 changed : accept input from (large) network without view loaded
				if (network == null) {
					JOptionPane.showMessageDialog(settingsPanel,"Please load a network.");
					status = "FALSE" ;	
				}
			}
		}
		if(status.equals("OK")){
                    
                    if (! this.bdsAnnot.isMemoryChoiceEnabled() ) {
			// annotation file selected?
			if (annotationPanel.getFile() == null){
				JOptionPane.showMessageDialog(settingsPanel, "Please select an annotation file.");
				status = "FALSE" ;	
			}
			// ontology file selected?
			else if (ontologyPanel.getFile() == null){
				JOptionPane.showMessageDialog(settingsPanel, "Please select an ontology file.");
				status = "FALSE" ;	
			}
			else{
				annParser = new AnnotationParser(annotationPanel, ontologyPanel, typeOfIdentifierPanel.getCheckedButton(), settingsPanel, bingoDir) ;
				IndeterminateJobCalculator tc = new IndeterminateJobCalculator(annParser) ;
				tc.run() ;
				/*annParser.start(false) ;
				JOptionPane.showMessageDialog(settingsPanel,"Check3");
				*/
				if(annParser.getStatus() == false){
					status = "FALSE" ;	
				}
				else{
					annotation = annParser.getAnnotation() ;
					ontology = annParser.getOntology() ;
                                        
				}	
				
				if(annParser.getOrphans()){
  	 			JOptionPane.showMessageDialog(settingsPanel,
						"WARNING : Some category labels in the annotation file" + "\n" +
						"are not defined in the ontology. Please check the compatibility of" + "\n" + 
						"these files. For now, these labels will be ignored and calculations" + "\n" +
						"will proceed.");
				}
			}
                    }
                    
                    else {
                        annotation = bdsAnnot.getAnnotation();
                        ontology = bdsAnnot.getOntology();
                        ZRemapAnnotation zra = new ZRemapAnnotation(annotation,ontology);
                        
                        annotation=zra.run();
                    }
                    
		}
	
		if(status.equals("OK")){	
			// datafile save option selected ?
			if(dataPanel.checked()){				
				//does file exist already ?
				File sel = new File(dataPanel.getFileDir(), nameField.getText().trim() + ".bgo") ;
				if(sel.exists()){
					int choice = JOptionPane.showConfirmDialog(settingsPanel,"File " + nameField.getText().trim() + ".bgo already exists. Overwrite (y/n)?\nIf not, choose a different cluster name.", "confirm", JOptionPane.YES_NO_OPTION) ;//.getValue() ;
					if(choice != JOptionPane.YES_OPTION){
						dataPanel.reset(); 
						status = "FALSE" ;
					}	
				}	
				// is file name for data file correct?		
				String saveDataFileString = dataPanel.isFileNameLegal(nameField.getText().trim() + ".bgo");				
				if(!saveDataFileString.equals(LOADCORRECT)){
					JOptionPane.showMessageDialog(settingsPanel,"Data File: " + saveDataFileString);
					status = "FALSE" ;	
				}
	    	}
		}

		// passed all tests.
		if(status.equals("OK")){	
			Vector selectedNodes ;
			Vector allNodes ;
					
			//split graph input and text input
		
			boolean consistencyCheck = true;
			if(textOrGraphPanel.graphButtonChecked()){
				//graph
				//CyNetworkView graphView = Cytoscape.getCurrentNetworkView();  
				CyNetwork network = Cytoscape.getCurrentNetwork(); 	
						
				selectedNodes = getSelectedCanonicalNamesFromNetwork(network); 
				if(clusterVsBox.getSelectedItem().equals(GRAPH)){	
					allNodes = getAllCanonicalNamesFromNetwork(network);
				}
				else if(clusterVsBox.getSelectedItem().equals(GENOME)){
					allNodes = getAllCanonicalNamesFromAnnotation();
				}
				else{allNodes = getAllCanonicalNamesFromAnnotation();}
				
				if(consistencyCheck){			
					int [] testData = getClassificationsFromVector(selectedNodes);
					boolean noElementsInTestData = false;
					// testing whether there are elements in sample data array.
					try{
						int firstElement = testData[0];
					}
					catch(Exception ex){
						noElementsInTestData = true;
					}
					if (!noElementsInTestData){
						this.networkName = nameField.getText().trim() ; 
						performCalculations(selectedNodes,allNodes);
					}	
					else{
						JOptionPane.showMessageDialog(settingsPanel,
						"The selected annotation does not produce any" + "\n" +
						"classifications for the selected nodes." + "\n" +
						"Maybe you chose the wrong type of gene identifier ?");
					}
				}	
			}	
			else{
				// split simple mode and batch mode
				
				if(!nameField.getText().trim().equals("batch")){
			
					//text
					selectedNodes = getSelectedCanonicalNamesFromTextArea(); 
					if(clusterVsBox.getSelectedItem().equals(GRAPH)){	
						//CyNetworkView graphView = Cytoscape.getCurrentNetworkView();  
						CyNetwork network = Cytoscape.getCurrentNetwork(); 		
						allNodes = getAllCanonicalNamesFromNetwork(network);
						if (!allNodes.containsAll(selectedNodes)){
							consistencyCheck = false ;
							JOptionPane.showMessageDialog(settingsPanel,
								"Some nodes in the text input panel do not exist in the network." + "\n" +
								"Option 'Test Cluster versus Graph' is not allowed.");
						}
					}
					else if(clusterVsBox.getSelectedItem().equals(GENOME)){
						allNodes = getAllCanonicalNamesFromAnnotation();
					}
					else{allNodes = getAllCanonicalNamesFromAnnotation();}
				
					if(consistencyCheck){			
						int [] testData = getClassificationsFromVector(selectedNodes);
						boolean noElementsInTestData = false;
						// testing whether there are elements in sample data array.
						try{
							int firstElement = testData[0];
						}
						catch(Exception ex){
							noElementsInTestData = true;
						}
						if (!noElementsInTestData){
							this.networkName = nameField.getText().trim() ; 
							performCalculations(selectedNodes,allNodes);
						}	
						else{
							JOptionPane.showMessageDialog(settingsPanel,
								"The selected annotation does not produce any" + "\n" +
								"classifications for the selected nodes." + "\n" +
								"Maybe you chose the wrong type of gene identifier ?");
						}
					}	
				}
				else{
					String textNodes = textOrGraphPanel.getInputText() ;
					String [] clusters = textNodes.split("batch") ;
					if(clusters.length == 0){
						JOptionPane.showMessageDialog(settingsPanel,
								"Please separate your clusters " + "\n" +
								"with the 'batch' keyword." + "\n");
					}
					else{
						for(int i = 0 ; i < clusters.length ; i++){
							consistencyCheck = true ;
							selectedNodes = getBatchClusterFromTextArea(clusters[i]); 
							if(clusterVsBox.getSelectedItem().equals(GRAPH)){	
								//CyNetworkView graphView = Cytoscape.getCurrentNetworkView();  
								CyNetwork network = Cytoscape.getCurrentNetwork(); 		
								allNodes = getAllCanonicalNamesFromNetwork(network);
								if (!allNodes.containsAll(selectedNodes)){
									consistencyCheck = false ;
									JOptionPane.showMessageDialog(settingsPanel,
										"Some nodes in the text input panel do not exist in the network." + "\n" +
										"Option 'Test Cluster versus Graph' is not allowed.");
								}
							}
							else if(clusterVsBox.getSelectedItem().equals(GENOME)){
								allNodes = getAllCanonicalNamesFromAnnotation();
							}
							else{allNodes = getAllCanonicalNamesFromAnnotation();}
				
							if(consistencyCheck){			
								int [] testData = getClassificationsFromVector(selectedNodes);
								boolean noElementsInTestData = false;
								// testing whether there are elements in sample data array.
								try{
									int firstElement = testData[0];
								}
								catch(Exception ex){
									noElementsInTestData = true;
								}
								if (!noElementsInTestData){
									performCalculations(selectedNodes,allNodes);
								}	
								else{
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

	/**
	* method that gets the canonical names from the selected cluster.	
	*
	* @return vector containing the canonical names.
	*/
	public Vector getSelectedCanonicalNamesFromNetwork(CyNetwork network){  
		// vector for storing the canonical names
 		Vector canonicalNameVector = new Vector();
		// iterate over every node view to get the canonical names.
		for (Iterator i = network.getFlaggedNodes().iterator(); i.hasNext(); ) {
			// getting next NodeView
			//NodeView nView = (NodeView)i.next();
			// first get the corresponding node
			Node node = (Node)i.next();
			// gets the canonical name of the given node from the attributes object
 			String canonicalName = node.getIdentifier().toUpperCase();  
			if (canonicalName != null && canonicalName.length() != 0){
				canonicalNameVector.add(canonicalName);
			}
		}
		return canonicalNameVector;
	}
	
	/**
	 * method that gets the canonical names from text input.
	 *
	 * @return vector containing the canonical names.
	 */
	public Vector getSelectedCanonicalNamesFromTextArea(){  		
		String textNodes = textOrGraphPanel.getInputText() ;
		String [] nodes = textNodes.split("\\s+") ;
		// vector for storing the canonical names
		Vector canonicalNameVector = new Vector();
		// iterate over every node view to get the canonical names.
		for (int i = 0; i < nodes.length ; i++) {
			if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase()))
				canonicalNameVector.add(nodes[i].toUpperCase());
		}
		return canonicalNameVector;
	}

	/**
	 * method that gets the canonical names from text input in batch mode.
	 *
	 * @return vector containing the canonical names of batch instance.
	 */
	
	public Vector getBatchClusterFromTextArea(String textNodes){  		

		String [] nodes = textNodes.split("\\s+") ;
		// vector for storing the canonical names
		Vector canonicalNameVector = new Vector();
		// iterate over every node view to get the canonical names.
		int j = 0 ;
		while(nodes[j].equals("")){j++;}
		this.networkName = nodes[j] ;
		for (int i = j; i < nodes.length ; i++) {
			if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase()))
				canonicalNameVector.add(nodes[i].toUpperCase());
		}
		return canonicalNameVector;
	}
	

	/**
	 * method that gets the canonical names from the whole graph.
	 *
	 * @return vector vector containing the canonical names from the network.
	 */
	public Vector getAllCanonicalNamesFromNetwork(CyNetwork network){
		// vector for storing the canonical names
		Vector canonicalNameVector = new Vector();
		// iterate over every node view to get the canonical names.
		for (Iterator i = network.nodesIterator(); i.hasNext(); ) {
			// getting next NodeView
			//NodeView nView = (NodeView)i.next();
			// first get the corresponding node 
			Node node = (Node)i.next();
			// gets the canonical name of the given node from the attributes object
            String canonicalName = node.getIdentifier().toUpperCase();
			if (canonicalName != null && canonicalName.length() != 0)
				canonicalNameVector.add(canonicalName);
		}
		return canonicalNameVector;
	}
	
	/**
	 * method that gets the canonical names for the wole annotation.
	 *
	 * @return vector vector containing the canonical names.
	 */	
	public Vector getAllCanonicalNamesFromAnnotation(){
		String [] nodes = annotation.getNames() ;
		// vector for storing the canonical names
		Vector canonicalNameVector = new Vector();
		// iterate over every node view to get the canonical names.
		for (int i = 0; i < nodes.length ; i++) {
			if (nodes[i] != null && nodes[i].length() != 0)
				canonicalNameVector.add(nodes[i].toUpperCase());
		}
		return canonicalNameVector;
	}

	/**
	 * Method that gets the classifications from a vector of canonical names.
	 *
	 * @param canonicalNameVector vector of canonical names.
	 * @return int[] classifications.
	 */
	public int [] getClassificationsFromVector (Vector canonicalNameVector){
		// vector for the classifications.
		Vector classificationsVector = new Vector();
		// array for go labels.
		int [] goLabelsName;

		for (int i = 0; i < canonicalNameVector.size(); i++){
			goLabelsName = annotation.getClassifications(canonicalNameVector.get(i) + "");
			for (int t = 0; t < goLabelsName.length; t++)
				classificationsVector.add(goLabelsName[t] + "");
		}

		int [] classifications = new int [classificationsVector.size()];
		for (int i = 0; i < classifications.length; i++){
			classifications[i] = Integer.parseInt(classificationsVector.get(i).toString());
		}
		return classifications;
	}
	
	/**
	 * Method that redirects the calculations of the distribution and the correction. 
	 * Redirects the visualization of the network and
	 * redirects the making of a file with the interesting data.
	 */
		
	public void performCalculations(Vector selectedNodes, Vector allNodes){

		HashMap testMap = null;
		HashMap correctionMap = null;
		HashMap mapSmallX = null;
		HashMap mapSmallN = null;
		int bigX = 0;
		int bigN = 0;

		// distributions.
		// --------------
		// hypergeometric distribution.
		
		if (testBox.getSelectedItem().equals(NONE)){
					DistributionCount dc = new DistributionCount(annotation, ontology, selectedNodes, allNodes);
					dc.countSmallN();
					dc.countSmallX();
					dc.countBigN();
					dc.countBigX();
					mapSmallN = dc.getHashMapSmallN();
					mapSmallX = dc.getHashMapSmallX();
					bigN = dc.getBigN();
					bigX = dc.getBigX();
		}
		else {
			if (testBox.getSelectedItem().equals(HYPERGEOMETRIC)){
				if(overUnderPanel.getCheckedButton().equals(OVERSTRING)){
					test = new HypergeometricTestCalculate(
												selectedNodes,
												allNodes,
												annotation,
												ontology);
				}	
				else{
					test = new HypergeometricTestCalculateUnder(
												selectedNodes,
												allNodes,
												annotation,
												ontology);
				}	
			}
			else if (testBox.getSelectedItem().equals(BINOMIAL)){
				if(overUnderPanel.getCheckedButton().equals(OVERSTRING)){
					test = new BinomialTestCalculate(
											selectedNodes,
											allNodes,
											annotation,
											ontology);
				}
				else{
			   		test = new BinomialTestCalculateUnder(
												selectedNodes,
												allNodes,
												annotation,
												ontology);
				}	
			}

			TestCalculator tc = new TestCalculator(test) ;
			tc.run() ;

			testMap  = test.getTestMap();
			mapSmallX = test.getMapSmallX();
			mapSmallN = test.getMapSmallN();
			bigX = test.getBigX();
			bigN = test.getBigN();

			// corrections.
			// ------------
		
		
			MultipleTestingCorrection mtc = new MultipleTestingCorrection(alphaField.getText(),testMap,correctionBox.getSelectedItem().toString());
			mtc.calculate();
			correctionMap = mtc.getCorrectionMap();

		}
		
		DisplayBiNGOWindow display;
		CreateBiNGOFile file;
                
                
                
                        
                        
		// the initializing of the visualization and the making of the file
		/*if (testBox.getSelectedItem().equals(NONE)){
			if(vizPanel.getCheckedButton().equals(VIZSTRING)){
			  display = new DisplayBiNGOWindow(testMap,
											mapSmallX,
											mapSmallN,
											bigX,
											bigN,
											alphaField.getText(),
											ontology,
											networkName,
											dataPanel,
											categoriesBox.getSelectedItem()+"");
			  // displaying the BiNGO CyNetwork.
			  display.makeWindow();
		    }
			if(dataPanel.checked()){
				file = new CreateBiNGOFile(testMap,
											mapSmallX,
											mapSmallN,
											bigX,
											bigN,
											alphaField.getText(),
											annotation,
											ontology,
											annotationPanel.getFile().toString(),
											ontologyPanel.getFile().toString(),
											testBox.getSelectedItem()+"",
											dataPanel.getFileDir(),
											networkName + ".bgo",
											clusterVsBox.getSelectedItem()+"",
											categoriesBox.getSelectedItem()+"",
											selectedNodes);
				file.makeFile();
				
				//show resulting File in window
				File f = new File(dataPanel.getFileDir(),networkName + ".bgo") ;
				String fileName = f.getAbsolutePath() ;
				TextFileReader reader = new TextFileReader (fileName);
      			reader.read ();
      			String fullText = reader.getText();
				JTextArea textArea = new JTextArea(20, 60);
				JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				textArea.setEditable(false);
				textArea.append(fullText);
				textArea.setCaretPosition(0);
				JFrame window = new JFrame(nameField.getText().trim() + " BiNGO Results");
				window.getContentPane().add(new JPanel().add(scrollPane));
				window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
				window.pack(); 
				// for top right position of the output panel.      
		    	window.setLocation(screenSize.width - window.getWidth(),0);
				window.setVisible(true);
				window.setResizable(true);				
			}
		}	
		
		if (correctionMap == null){
			if(vizPanel.getCheckedButton().equals(VIZSTRING)){
			  display = new DisplayBiNGOWindow(testMap,
											mapSmallX,
											mapSmallN,
											bigX,
											bigN,
											alphaField.getText(),
											ontology,
											networkName,
											dataPanel,
											categoriesBox.getSelectedItem()+"");
			  // displaying the BiNGO CyNetwork.
			  display.makeWindow();
		    }
			if(dataPanel.checked()){
				file = new CreateBiNGOFile(testMap,
											mapSmallX,
											mapSmallN,
											bigX,
											bigN,
											alphaField.getText(),
											annotation,
											ontology,
											annotationPanel.getFile().toString(),
											ontologyPanel.getFile().toString(),
											testBox.getSelectedItem()+"",
											dataPanel.getFileDir(),
											networkName + ".bgo",
											clusterVsBox.getSelectedItem()+"",
											categoriesBox.getSelectedItem()+"",
											selectedNodes);
				file.makeFile();
				
				//show resulting File in window
				File f = new File(dataPanel.getFileDir(),networkName + ".bgo") ;
				String fileName = f.getAbsolutePath() ;
				TextFileReader reader = new TextFileReader (fileName);
      			reader.read ();
      			String fullText = reader.getText();
				JTextArea textArea = new JTextArea(20, 60);
				JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				textArea.setEditable(false);
				textArea.append(fullText);
				textArea.setCaretPosition(0);
				JFrame window = new JFrame(nameField.getText().trim() + " BiNGO Results");
				window.getContentPane().add(new JPanel().add(scrollPane));
				window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
				window.pack(); 
				// for top right position of the output panel.      
		    	window.setLocation(screenSize.width - window.getWidth(),0);
				window.setVisible(true);
				window.setResizable(true);				
			}
		}
		else{*/
			if(vizPanel.getCheckedButton().equals(VIZSTRING)){
				display = new DisplayBiNGOWindow(testMap,
											correctionMap,
											mapSmallX,
											mapSmallN,
											bigX,
											bigN,
											alphaField.getText(),
											ontology,
											networkName,
											dataPanel,             
											categoriesBox.getSelectedItem()+"");
			
				// displaying the BiNGO CyNetwork.
				display.makeWindow();
			}
                
                
                
                
//////////////////////////JE RAJOUTE MES TRUCS ICI ET SOUS LE TAS DE COMMENTAIRES
                        
                            goBin.createResultTab(testMap,
								correctionMap,
								mapSmallX,
								mapSmallN,
                                                                bigX,
								bigN,
								alphaField.getText(),
								annotation,
								ontology,
								annotationPanel.getFile().toString(),
								ontologyPanel.getFile().toString(),	
								testBox.getSelectedItem()+"",
								correctionBox.getSelectedItem()+"",
								overUnderPanel.getCheckedButton()+"",
								dataPanel.getFileDir(),
								networkName + ".bgo",
								clusterVsBox.getSelectedItem()+"",
								categoriesBox.getSelectedItem()+"",
								selectedNodes,
                                                                Cytoscape.getCurrentNetwork(),
                                                                Cytoscape.getCurrentNetworkView());
                        
                
                
                
                
                
                
                
			if(dataPanel.checked()){
				file = new CreateBiNGOFile(testMap,
										   correctionMap,
										   mapSmallX,
										   mapSmallN,
										   bigX,
										   bigN,
										   alphaField.getText(),
										   annotation,
										   ontology,
										   annotationPanel.getFile().toString(),
										   ontologyPanel.getFile().toString(),	
										   testBox.getSelectedItem()+"",
										   correctionBox.getSelectedItem()+"",
										   overUnderPanel.getCheckedButton()+"",
										   dataPanel.getFileDir(),
										   networkName + ".bgo",
										   clusterVsBox.getSelectedItem()+"",
										   categoriesBox.getSelectedItem()+"",
										   selectedNodes);
				file.makeFile();
				
				//show resulting File in window
				File f = new File(dataPanel.getFileDir(),networkName + ".bgo") ;
				String fileName = f.getAbsolutePath() ;
				TextFileReader reader = new TextFileReader (fileName);
      			reader.read ();
      			String fullText = reader.getText();
				JTextArea textArea = new JTextArea(20, 60);
				JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				textArea.setEditable(false);
				textArea.append(fullText);
				textArea.setCaretPosition(0);
				JFrame window = new JFrame(networkName + " BiNGO Results");
				window.getContentPane().add(new JPanel().add(scrollPane));
				window.pack(); 
				window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
				// for top right position of the output panel.      
		    	window.setLocation(screenSize.width - window.getWidth(), 0);
				window.setVisible(true);
				window.setResizable(true);
			}
		}
	//}
}
