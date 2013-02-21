package bingo.internal;

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
 * * Description: Class that is the listener for the bingo-button on the settingspanel.
 * * It collects all kinds of information: the ontology and annotation
 * * file, the alpha, which distribution and correction will be used, ...
 * * It also redirects the vizualisation and the making of a file with
 * * information. It also redirects calculation of the p-values and
 * * corrected p-values.  
 **/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import bingo.internal.ui.SettingsPanel;


/**
 * ********************************************************************
 * SettingsPanelActionListener.java --------------------------------
 * <p/>
 * Steven Maere & Karel Heymans (c) March 2005
 * <p/>
 * Class that is the listener for the bingo-button on the settingspanel. It
 * collects all kinds of information: the ontology and annotation file, the
 * alpha, which distribution and correction will be used, ... It also redirects
 * the vizualisation and the making of a file with information. It also
 * redirects calculation of the p-values and corrected p-values.
 * *********************************************************************
 */

public class SettingsPanelActionListener implements ActionListener {

	private final CySwingAppAdapter adapter;

	private SettingsPanel settingsPanel;
	private BingoParameters params;
	private bingo.internal.GOlorize.GoBin goBin;

	private CyNetworkView startNetworkView;
	private CyNetwork startNetwork;

	/**
	 * constant string for the none-label in the combobox.
	 */
	private final String NONE = BingoAlgorithm.NONE;
	private Set<String> ecCodes;
	private Map<String, Set<String>> redundantIDs = new HashMap<String, Set<String>>();
	private boolean consistencyCheck = true;

	/**
	 * constant strings for the checking versus option.
	 */
	private static final String GRAPH = BingoAlgorithm.GRAPH;
	private static final String GENOME = BingoAlgorithm.GENOME;
	private static final String VIZSTRING = BingoAlgorithm.VIZSTRING;

	/**
	 * constant string for the none-label in the combobox.
	 */
	private static final String CATEGORY_BEFORE_CORRECTION = BingoAlgorithm.CATEGORY_BEFORE_CORRECTION;
	private static final String CATEGORY_CORRECTION = BingoAlgorithm.CATEGORY_CORRECTION;

	
	private TaskMonitor tMonitor;
	
	/**
	 * Constructor with all the settings of the settingspanel as arguments.
	 */
	public SettingsPanelActionListener(final BingoParameters params, SettingsPanel settingsPanel,
			final CySwingAppAdapter adapter) {
		this.adapter = adapter;
		this.params = params;
		this.settingsPanel = settingsPanel;
		this.goBin = null;
		this.startNetworkView = null;
		this.startNetwork = null;
		ecCodes = new HashSet<String>();
		ecCodes.add("IEA");
		ecCodes.add("ISS");
		ecCodes.add("TAS");
		ecCodes.add("IDA");
		ecCodes.add("IGI");
		ecCodes.add("IMP");
		ecCodes.add("IEP");
		ecCodes.add("ND");
		ecCodes.add("RCA");
		ecCodes.add("IPI");
		ecCodes.add("NAS");
		ecCodes.add("IC");
		ecCodes.add("NR");
	}

	/**
	 * action that is performed when the bingo-button is clicked.
	 * 
	 * @param event
	 *            bingo-button clicked.
	 */
	public void actionPerformed(ActionEvent event) {

		// Imports annotations, etc.
		boolean status = updateParameters();
		
		final Set noClassificationsSet = new HashSet();
		redundantIDs = new HashMap<String, Set<String>>();

		// passed all tests.
		if (status == true) {

			// split graph input and text input
			consistencyCheck = true;
			if (params.getTextOrGraph()) {
				startNetworkView = adapter.getCyApplicationManager().getCurrentNetworkView();
				startNetwork = startNetworkView.getModel();

				final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();

				params.setSelectedNodes(getSelectedCanonicalNamesFromNetwork(network));
				if (params.getReferenceSet().equals(GRAPH)) {
					params.setAllNodes(getAllCanonicalNamesFromNetwork(network));
				} else if (params.getReferenceSet().equals(GENOME)) {
					params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
				} else {
					params.setAllNodes(getAllCanonicalNamesFromReferenceSet(params.getReferenceSet(),
							params.getSelectedNodes()));
				}
				if (!params.getReferenceSet().equals(GENOME)
						&& !params.getAllNodes().containsAll(params.getSelectedNodes())) {
					consistencyCheck = false;
					JOptionPane.showMessageDialog(settingsPanel,
							"Some genes in the text input panel are not defined in the reference set. Please check your input settings"
									+ "\n");
				}

				if (consistencyCheck) {
					int[] testData = getClassificationsFromVector(params.getSelectedNodes(), noClassificationsSet);
					boolean noElementsInTestData = false;
					// testing whether there are elements in sample data array.
					try {
						int firstElement = testData[0];
					} catch (Exception ex) {
						noElementsInTestData = true;
					}
					if (!noElementsInTestData) {
						performCalculations(params.getSelectedNodes(), params.getAllNodes(), noClassificationsSet);
					} else {
						JOptionPane.showMessageDialog(settingsPanel, "The selected annotation does not produce any"
								+ "\n" + "classifications for the selected nodes." + "\n"
								+ "Maybe you chose the wrong type of gene identifier ?");
					}
				}
			} else {
				// split simple mode and batch mode
				if (!params.getCluster_name().equals("batch")) {
					if (params.getReferenceSet().equals(GRAPH)) {
						final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();
						params.setAllNodes(getAllCanonicalNamesFromNetwork(network));
						Set<String> selection = getSelectedCanonicalNamesFromTextArea();
						// conformize names...
						params.setSelectedNodes(conformize(selection, params.getAllNodes()));
					} else if (params.getReferenceSet().equals(GENOME)) {
						params.setSelectedNodes(getSelectedCanonicalNamesFromTextArea());
						params.setAllNodes(getAllCanonicalNamesFromAnnotation(params.getSelectedNodes()));
					} else {
						params.setSelectedNodes(getSelectedCanonicalNamesFromTextArea());
						params.setAllNodes(getAllCanonicalNamesFromReferenceSet(params.getReferenceSet(),
								params.getSelectedNodes()));
					}

					if (!params.getReferenceSet().equals(GENOME)
							&& !params.getAllNodes().containsAll(params.getSelectedNodes())) {
						consistencyCheck = false;
						JOptionPane.showMessageDialog(settingsPanel,
								"Some genes in the text input panel are not defined in the reference set. Please check your input settings"
										+ "\n");
					}

					if (consistencyCheck) {
						int[] testData = getClassificationsFromVector(params.getSelectedNodes(), noClassificationsSet);
						boolean noElementsInTestData = false;
						// testing whether there are elements in sample data
						// array.
						try {
							int firstElement = testData[0];
						} catch (Exception ex) {
							noElementsInTestData = true;
						}
						if (!noElementsInTestData) {
							performCalculations(params.getSelectedNodes(), params.getAllNodes(), noClassificationsSet);
						} else {
							JOptionPane.showMessageDialog(settingsPanel, "The selected annotation does not produce any"
									+ "\n" + "classifications for the selected nodes." + "\n"
									+ "Maybe you chose the wrong type of gene identifier ?");
						}
					}
				} else {
					String textNodes = params.getTextInput();
					String[] clusters = textNodes.split("batch");
					if (clusters.length == 0) {
						JOptionPane.showMessageDialog(settingsPanel, "Please separate your clusters " + "\n"
								+ "with the 'batch' keyword." + "\n");
					} else {
						if (params.getReferenceSet().equals(GRAPH)) {
							final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();
							params.setAllNodes(getAllCanonicalNamesFromNetwork(network));
						} else if (params.getReferenceSet().equals(GENOME)) {
							HashSet<String> allClusters = new HashSet<String>();
							for (int i = 0; i < clusters.length; i++) {
								HashSet<String> selection = getBatchClusterFromTextArea(clusters[i]);
								allClusters.addAll(selection);
							}
							params.setAllNodes(getAllCanonicalNamesFromAnnotation(allClusters));
						} else {
							HashSet<String> allClusters = new HashSet<String>();
							for (int i = 0; i < clusters.length; i++) {
								HashSet<String> selection = getBatchClusterFromTextArea(clusters[i]);
								allClusters.addAll(selection);
							}
							params.setAllNodes(getAllCanonicalNamesFromReferenceSet(params.getReferenceSet(),
									allClusters));
						}
						for (int i = 0; i < clusters.length; i++) {
							consistencyCheck = true;
							HashSet<String> selection = getBatchClusterFromTextArea(clusters[i]);
							// conformize names...
							if (params.getReferenceSet().equals(GRAPH)) {
								params.setSelectedNodes(conformize(selection, params.getAllNodes()));
							} else {
								params.setSelectedNodes(selection);
							}
							if (!params.getReferenceSet().equals(GENOME)
									&& !params.getAllNodes().containsAll(params.getSelectedNodes())) {
								consistencyCheck = false;
								JOptionPane.showMessageDialog(settingsPanel,
										"Some genes in the text input panel are not defined in the reference set. Please check your input settings"
												+ "\n");
							}
							
							if (consistencyCheck) {
								int[] testData = getClassificationsFromVector(params.getSelectedNodes(),
										noClassificationsSet);
								boolean noElementsInTestData = false;
								// testing whether there are elements in sample
								// data array.
								try {
									int firstElement = testData[0];
								} catch (Exception ex) {
									noElementsInTestData = true;
								}
								if (!noElementsInTestData) {
									performCalculations(params.getSelectedNodes(), params.getAllNodes(),
											noClassificationsSet);
								} else {
									JOptionPane.showMessageDialog(settingsPanel,
											"The selected annotation does not produce any" + "\n"
													+ "classifications for the selected nodes." + "\n"
													+ "Maybe you chose the wrong type of gene identifier ?");
								}
							}
						}
					}
				}
			}
		}
	}

	private String openResourceFile(String name) {
		return getClass().getResource("/" + name).toString();
	}

	public Set<String> conformize(Set<String> selection, Set<String> allNodes) {
		final Set<String> conformizedSelection = new HashSet<String>();
		for (String s : selection) {
			boolean ok = false;
			for (String s2 : allNodes) {
				if (params.getAlias().get(s) != null && params.getAlias().get(s2) != null) {
					if (params.getAlias().get(s).equals(params.getAlias().get(s2))) {
						conformizedSelection.add(s2);
						ok = true;
						break;
					}
				}
			}
			if (ok == false)
				conformizedSelection.add(s);
		}
		return conformizedSelection;
	}

	public boolean updateParameters() {
		boolean status = true;
		
		params.setOverOrUnder(settingsPanel.getOverUnderPanel().getCheckedButton());
		params.setVisualization(settingsPanel.getVizPanel().getCheckedButton());
		params.setCluster_name(settingsPanel.getNameField().getText().trim());

		// checking cluster name.
		if (params.getCluster_name().equals("")) {
			JOptionPane.showMessageDialog(settingsPanel, "Please choose a cluster name ");
			return false;
		}

		final Set<CyNetwork> networkSet = adapter.getCyNetworkManager().getNetworkSet();
		for (final CyNetwork network : networkSet) {
			//final String title = network.getCyRow().get(CyNetwork.NAME, String.class);
			final String title = network.getDefaultNetworkTable().getRow(network).get(CyNetwork.NAME, String.class);
			
			if (params.getCluster_name().equals(title)) {
				JOptionPane.showMessageDialog(settingsPanel,
						"A network with this name already exists in Cytoscape. Please choose another cluster name ");
				return false;
			}
		}

		params.setTextOrGraph(settingsPanel.getTextOrGraphPanel().graphButtonChecked());
		// checking whether, if we select nodes from the network, the network
		// and the selected nodes exist
		if (params.isTextOrGraph()) {
			// get the network object from the window.
			// CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
			final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();
			// can't continue if either of these is null
			// 200905 changed : accept input from (large) network without view
			// loaded
			if (network == null) {
				JOptionPane.showMessageDialog(settingsPanel, "Please load a network.");
				return false;
//			} else if (network.getSelectedNodes().size() == 0) {
			} else {
				// FIXME
				boolean selectedFound = false;
				final List<CyNode> nodes = network.getNodeList();
				for (CyNode node: nodes) {
					//if (node.getCyRow().get(CyNetwork.SELECTED, Boolean.class) == true) {
					if (network.getDefaultNodeTable().getRow(node).get(CyNetwork.SELECTED, Boolean.class) == true) {
						selectedFound = true;
						break;
					}
				}

				if (selectedFound == false) {
					// put up a dialog if there are no selected nodes
					JOptionPane.showMessageDialog(settingsPanel, "Please select one or more nodes.");
					return false;
				}
			}
		}
		// testing whether, if nodes are selected from text area, there is
		// something valid in the text area
		if (!params.isTextOrGraph()) {
			// set the text from the window to the variable
			params.setTextInput(settingsPanel.getTextOrGraphPanel().getInputText());
			if (params.getTextInput() == null) {
				JOptionPane.showMessageDialog(settingsPanel, "Please paste one or more genes in the text field.");
				return false;
			}
		}

		// significance cut-off
		params.setCorrection((String) settingsPanel.getCorrectionBox().getSelectedItem());
		params.setTest((String) settingsPanel.getTestBox().getSelectedItem());
		params.setSignificance(new BigDecimal(settingsPanel.getAlphaField().getText()));
		// distribution selected?
		if (params.getTest().equals(NONE)) {
			settingsPanel.getAlphaField().setText("1.00");
			params.setSignificance(new BigDecimal("1.00"));
			if (!params.getCorrection().equals(NONE)) {
				JOptionPane.showMessageDialog(settingsPanel,
						"Multiple testing correction not possible without test selection...");
				return false;
			}
		} else {
			// checking of alpha is a decimal between 0 and 1.
			boolean alphaIncorrect = false;
			try {
				if (params.getSignificance().compareTo(new BigDecimal("1")) >= 0
						|| params.getSignificance().compareTo(new BigDecimal("0")) <= 0)
					alphaIncorrect = true;

			} catch (Exception ex) {
				alphaIncorrect = true;
			}
			if (alphaIncorrect) {
				JOptionPane.showMessageDialog(settingsPanel,
						"Please input a valid significance level (i.e. a decimal number between 0 and 1).");
				return false;
			}
		}

		// category-option selected?
		params.setCategory((String) settingsPanel.getCategoriesBox().getSelectedItem());
		if (params.getCategory().equals(NONE)) {
			JOptionPane.showMessageDialog(settingsPanel, "Please select what categories should be visualized.");
			return false;
		}
		// checking number of categories option
		if (params.getCategory().equals(CATEGORY_CORRECTION) && params.getCorrection().equals(NONE)) {
			JOptionPane.showMessageDialog(settingsPanel, "The option 'Overrepresented categories after correction'"
					+ "\n" + "at the category box requires the selection of a" + "\n"
					+ "correction in the correction box.");
			return false;
		}
		if (params.getCategory().equals(CATEGORY_BEFORE_CORRECTION) && params.getTest().equals(NONE)) {
			JOptionPane.showMessageDialog(settingsPanel, "The option 'Overrepresented categories before correction'"
					+ "\n" + "at the category box requires at least the selection of a" + "\n"
					+ "test in the test box.");
			return false;
		}

		// Data panel
		params.setFileoutput(settingsPanel.getDataPanel().checked());
		// datafile save option selected ?
		if (params.isFileoutput()) {
			// does file exist already ?
			params.setFileoutput_dir(settingsPanel.getDataPanel().getFileDir());
			File sel = new File(settingsPanel.getDataPanel().getFileDir(), params.getCluster_name() + ".bgo");
			if (sel.exists()) {
				int choice = JOptionPane.showConfirmDialog(settingsPanel, "File " + params.getCluster_name()
						+ ".bgo already exists. Overwrite (y/n)?\nIf not, choose a different cluster name.", "confirm",
						JOptionPane.YES_NO_OPTION);// .getValue() ;
				if (choice != JOptionPane.YES_OPTION) {
					settingsPanel.getDataPanel().reset();
					return false;
				}
			}
			// is file name for data file correct?
			String saveDataFileString = settingsPanel.getDataPanel().isFileNameLegal(params.getCluster_name() + ".bgo");
			if (!saveDataFileString.equals("LOADCORRECT")) {
				JOptionPane.showMessageDialog(settingsPanel, "Data File: " + saveDataFileString);
				return false;
			}
		}

		if (params.getReferenceSet() == null
				|| !params.getReferenceSet().equals((String) settingsPanel.getClusterVsPanel().getSelection())) {
			params.setStatus(false);
		}
		params.setReferenceSet((String) settingsPanel.getClusterVsPanel().getSelection());

		// testing versus-option selected?
		if (params.getReferenceSet().equals(NONE)) {
			JOptionPane.showMessageDialog(settingsPanel,
					"Please select against what reference the cluster must be tested.");
			return false;
		}
		// testing consistency of text area node selection mode with
		// versus-option
		else if (params.getReferenceSet().equals(GRAPH) && !params.isTextOrGraph()) {
			// CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
			
			final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();
			// can't continue if either of these is null
			// 200905 changed : accept input from (large) network without view
			// loaded
			if (network == null) {
				JOptionPane.showMessageDialog(settingsPanel, "Please load a network.");
				return false;
			}
		}

		// Get the specified species and the find out what is its corresponding
		// file name
		String specified_species = settingsPanel.getAnnotationPanel().getSpecifiedSpecies();
		if (specified_species.equals(BingoAlgorithm.CUSTOM)) {
			if (params.getAnnotationFile() == null
					|| !params.getAnnotationFile().equals(settingsPanel.getAnnotationPanel().getSelection())) {
				params.setStatus(false);
			}
			params.setAnnotationFile(settingsPanel.getAnnotationPanel().getSelection());
			params.setAnnotation_default(settingsPanel.getAnnotationPanel().getDefault());
			params.setSpecies(settingsPanel.getAnnotationPanel().getSelection());
		} else {
			// get the file name for this species.
			String annot_filename = params.getSpeciesFilename(specified_species);
			if (params.getAnnotationFile() == null
					|| !params.getAnnotationFile().equals(openResourceFile(annot_filename))) {
				params.setStatus(false);
			}
			params.setAnnotationFile(openResourceFile(annot_filename));
			params.setAnnotation_default(settingsPanel.getAnnotationPanel().getDefault());
			params.setSpecies(settingsPanel.getAnnotationPanel().getSelection());
		}
		if (settingsPanel.getOntologyPanel().getSpecifiedOntology().equals(BingoAlgorithm.CUSTOM)) {
			if (params.getOntologyFile() == null
					|| !params.getOntologyFile().equals(settingsPanel.getOntologyPanel().getSelection())) {
				params.setStatus(false);
			}
			params.setOntologyFile(settingsPanel.getOntologyPanel().getSelection());
			params.setOntology_default(settingsPanel.getOntologyPanel().getDefault());
			if (settingsPanel.getNamespacePanel().choiceBox.isEnabled()) {
				if (params.getNameSpace() == null
						|| !params.getNameSpace().equals(settingsPanel.getNamespacePanel().getSelection())) {
					params.setStatus(false);
				}
				params.setNameSpace(settingsPanel.getNamespacePanel().getSelection());
			} else {
				params.setNameSpace(BingoAlgorithm.NONE);
			}
		} else {
			if (params.getOntologyFile() == null
					|| !params.getOntologyFile().equals(
							openResourceFile(settingsPanel.getOntologyPanel().getSelection()))) {
				params.setStatus(false);
			}
			params.setOntologyFile(openResourceFile(settingsPanel.getOntologyPanel().getSelection()));
			params.setOntology_default(settingsPanel.getOntologyPanel().getDefault());
			params.setNameSpace(BingoAlgorithm.NONE);
		}

		HashSet deleteCodes = new HashSet();
		String tmp = settingsPanel.getEcField().getText().trim();
		String[] codes = tmp.split("\\s+");
		for (int i = 0; i < codes.length; i++) {
			if (codes[i].length() != 0) {
				if (ecCodes.contains(codes[i].toUpperCase())) {
					deleteCodes.add(codes[i].toUpperCase());
				} else {
					JOptionPane.showMessageDialog(settingsPanel, "Evidence code " + codes[i].toUpperCase()
							+ " does not exist");
					return false;
				}
			}
		}
		if (params.getDeleteCodes() == null || !params.getDeleteCodes().equals(deleteCodes)) {
			params.setStatus(false);
		}
		params.setDeleteCodes(deleteCodes);

		// probably superfluous
		// annotation file selected?
		if (params.getAnnotationFile() == null) {
			JOptionPane.showMessageDialog(settingsPanel, "Please select an annotation file.");
			return false;
		}
		// probably superfluous
		// ontology file selected?
		else if (params.getOntologyFile() == null) {
			JOptionPane.showMessageDialog(settingsPanel, "Please select an ontology file.");
			return false;
		}

		// in case something annotation/ontology-related went wrong in a
		// previous call, bingoParameters.status is false and AnnotationParser
		// should be called again
		if (params.getStatus() == false) {
			AnnotationParser annParser = params.initializeAnnotationParser();
			//TaskManager taskManager = adapter.getTaskManager();
			System.out.println("Calling annotation parser...");
			//taskManager.execute(new GenericTaskFactory(annParser));
			try {
				annParser.calculate();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Calling annotation parser...DONE!!");
			if (annParser.getStatus()) {
				params.setAnnotation(annParser.getAnnotation());
				params.setOntology(annParser.getOntology());
				params.setAlias(annParser.getAlias());
				if (annParser.getOrphans()) {
					JOptionPane.showMessageDialog(settingsPanel,
							"WARNING : Some category labels in the annotation file" + "\n"
									+ "are not defined in the ontology. Please check the compatibility of" + "\n"
									+ "these files. For now, these labels will be ignored and calculations" + "\n"
									+ "will proceed.");
				}
				// only way to set status true is to pass annotation parse step
				params.setStatus(true);
			} else {
				params.setStatus(false);
				return false;
			}
		}
		// return if all parameters were set.
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
		final Set<Set<String>> mapNames = new HashSet<Set<String>>();
		// iterate over every node view to get the canonical names.
		final List<CyNode> nodes = network.getNodeList();
		for (CyNode node: nodes) {
			//if (node.getCyRow().get(CyNetwork.SELECTED, Boolean.class) == false)
			if (network.getDefaultNodeTable().getRow(node).get(CyNetwork.SELECTED, Boolean.class) == false)
				continue;

			// gets the canonical name of the given node from the attributes object
			//final String canonicalName = node.getCyRow().get(CyNetwork.NAME, String.class);
			final String canonicalName = network.getDefaultNodeTable().getRow(node).get(CyNetwork.NAME, String.class);
			
//			String canonicalName = node.getIdentifier().toUpperCase();
			if (canonicalName != null && canonicalName.length() != 0 && !canonicalNameVector.contains(canonicalName)) {
				if (mapNames.contains(params.getAlias().get(canonicalName))) {
					redundantIDs.put(canonicalName,
							(HashSet<String>) params.getAlias().get(canonicalName));
					/*
					 * int opt = JOptionPane.showOptionDialog(settingsPanel,
					 * "WARNING : The test set contains multiple identifiers for the gene/protein "
					 * + "\n" + node.getIdentifier().toUpperCase() +
					 * ". If you press 'Yes', the redundant identifier will be ignored "
					 * + "\n" +
					 * "and calculations will proceed. Press 'No' to abort calculations."
					 * ,"WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.
					 * WARNING_MESSAGE,null,null,null); if(opt ==
					 * JOptionPane.NO_OPTION){ consistencyCheck = false; }
					 */
				}
				if (params.getAlias().get(canonicalName) != null) {
					mapNames.add((HashSet<String>) params.getAlias().get(canonicalName));
				}
				canonicalNameVector.add(canonicalName);
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
		String[] nodes = textNodes.split("\\s+");
		// HashSet for storing the canonical names
		HashSet canonicalNameVector = new HashSet();
		HashSet<HashSet<String>> mapNames = new HashSet<HashSet<String>>();
		// iterate over every node view to get the canonical names.
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase())) {
				if (mapNames.contains(params.getAlias().get(nodes[i].toUpperCase()))) {
					redundantIDs.put(nodes[i].toUpperCase(),
							(HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
					/*
					 * int opt = JOptionPane.showOptionDialog(settingsPanel,
					 * "WARNING : The test set contains multiple identifiers for the gene/protein "
					 * + "\n" + nodes[i].toUpperCase() +
					 * ". If you press 'Yes', the redundant identifier will be ignored "
					 * + "\n" +
					 * "and calculations will proceed. Press 'No' to abort calculations."
					 * ,"WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.
					 * WARNING_MESSAGE,null,null,null); if(opt ==
					 * JOptionPane.NO_OPTION){ consistencyCheck = false; }
					 */
				}
				// else{
				if (params.getAlias().get(nodes[i].toUpperCase()) != null) {
					mapNames.add((HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
				}
				canonicalNameVector.add(nodes[i].toUpperCase());
				// }
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

		String[] nodes = textNodes.split("\\s+");
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
		for (int i = j + 1; i < nodes.length; i++) {
			if (nodes[i] != null && nodes[i].length() != 0 && !canonicalNameVector.contains(nodes[i].toUpperCase())) {
				if (mapNames.contains(params.getAlias().get(nodes[i].toUpperCase()))) {
					redundantIDs.put(nodes[i].toUpperCase(),
							(HashSet<String>) params.getAlias().get(nodes[i].toUpperCase()));
					/*
					 * int opt = JOptionPane.showOptionDialog(settingsPanel,
					 * "WARNING : The test set contains multiple identifiers for the gene/protein "
					 * + "\n" + nodes[i].toUpperCase() +
					 * ". If you press 'Yes', the redundant identifier will be ignored "
					 * + "\n" +
					 * "and calculations will proceed. Press 'No' to abort calculations."
					 * ,"WARNING",JOptionPane.YES_NO_OPTION,JOptionPane.
					 * WARNING_MESSAGE,null,null,null); if(opt ==
					 * JOptionPane.NO_OPTION){ consistencyCheck = false; }
					 */
				} else {
					if (params.getAlias().get(nodes[i].toUpperCase()) != null) {
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
	public Set getAllCanonicalNamesFromNetwork(CyNetwork network) {
		// HashSet for storing the canonical names
		Set canonicalNameVector = new HashSet();
		Set<Set<String>> mapNames = new HashSet<Set<String>>();
		// iterate over every node view to get the canonical names.
		final List<CyNode> nodes = network.getNodeList();
		for (CyNode node: nodes) {
			// gets the canonical name of the given node from the attributes object
			//String canonicalName = node.getCyRow().get(CyNetwork.NAME, String.class);
			String canonicalName = network.getDefaultNodeTable().getRow(node).get(CyNetwork.NAME, String.class);
			
			if (canonicalName != null && (canonicalName.length() != 0) && !canonicalNameVector.contains(canonicalName)) {
				if (mapNames.contains(params.getAlias().get(canonicalName))) {
					redundantIDs.put(canonicalName,
							(HashSet<String>) params.getAlias().get(canonicalName));
					int opt = JOptionPane.showOptionDialog(settingsPanel,
							"WARNING : The network contains multiple identifiers for the gene/protein " + "\n"
									+ canonicalName
									+ ". If you press 'Yes', the redundant identifier will be ignored " + "\n"
									+ "and calculations will proceed. Press 'No' to abort calculations.", "WARNING",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
					if (opt == JOptionPane.NO_OPTION) {
						consistencyCheck = false;
					}
				} else {
					if (params.getAlias().get(canonicalName) != null) {
						mapNames.add((HashSet<String>) params.getAlias().get(canonicalName));
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
	public Set getAllCanonicalNamesFromAnnotation(Set selectedNodes) {
		String[] nodes = params.getAnnotation().getNames();
		// HashSet for storing the canonical names
		HashSet canonicalNameVector = new HashSet();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null && (nodes[i].length() != 0)) {
				canonicalNameVector.add(nodes[i].toUpperCase());
			}
		}

		// replace canonical names in reference set that match one of the
		// canonical names in the selected cluster, to get rid of e.g. splice
		// variants if the non-splice-specific gene is part of the selection,
		// and to avoid conflicts between names in ref set and selection
		Map<String, Set<String>> alias = params.getAlias();
		Iterator it2 = selectedNodes.iterator();
		while (it2.hasNext()) {
			String name = it2.next() + "";
			Set tmp = alias.get(name);
			if (tmp != null) {
				Iterator it = tmp.iterator();
				while (it.hasNext()) {
					canonicalNameVector.remove(it.next() + "");
				}
				// add selected node name
				canonicalNameVector.add(name);
			}
		}
		return canonicalNameVector;
	}

	/**
	 * method that gets the canonical names for the whole annotation.
	 * 
	 * @return HashSet containing the canonical names.
	 */
	public Set getAllCanonicalNamesFromReferenceSet(String refSet, Set selectedNodes) {
		HashSet<String> nodes = parseReferenceSet(refSet);
		// HashSet for storing the canonical names
		HashSet canonicalNameVector = new HashSet();
		for (String s : nodes) {
			if (s.length() != 0) {
				canonicalNameVector.add(s.toUpperCase());
			}
		}

		// replace canonical names in reference set that match one of the
		// canonical names in the selected cluster, to get rid of e.g. splice
		// variants if the non-splice-specific gene is part of the selection,
		// and to avoid conflicts between names in ref set and selection
		Map<String, Set<String>> alias = params.getAlias();
		Iterator it2 = selectedNodes.iterator();
		while (it2.hasNext()) {
			String name = it2.next() + "";
			Set tmp = alias.get(name);
			if (tmp != null) {
				Iterator it = tmp.iterator();
				while (it.hasNext()) {
					canonicalNameVector.remove(it.next() + "");
				}
				// add selected node name
				canonicalNameVector.add(name);
			}
		}
		return canonicalNameVector;
	}

	public HashSet<String> parseReferenceSet(String refSetFile) {
		HashSet<String> refSet = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(refSetFile)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.trim().split("\t");
				refSet.add(tokens[0].trim().toUpperCase());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(settingsPanel, "Error reading reference file: " + e);
		}
		return refSet;
	}

	/**
	 * Method that gets the classifications from a HashSet of canonical names.
	 * 
	 * @param canonicalNameVector
	 *            HashSet of canonical names.
	 * @return int[] classifications.
	 */
	public int[] getClassificationsFromVector(Set canonicalNameVector, Set noClassificationsSet) {
		// HashSet for the classifications.
		Set classificationsVector = new HashSet();
		Map<String, Set<String>> alias = params.getAlias();
		// array for go labels.
		int[] goLabelsName;
		Iterator it2 = canonicalNameVector.iterator();
		while (it2.hasNext()) {
			String name = it2.next() + "";
			Set identifiers = alias.get(name);
			Set cls = new HashSet();
			// array for go labels.
			if (identifiers != null) {
				Iterator it = identifiers.iterator();
				while (it.hasNext()) {
					goLabelsName = params.getAnnotation().getClassifications(it.next() + "");
					for (int t = 0; t < goLabelsName.length; t++) {
						cls.add(goLabelsName[t] + "");
					}
				}
			}
			if (cls.size() == 0) {
				noClassificationsSet.add(name);
			}
			Iterator it3 = cls.iterator();
			while (it3.hasNext()) {
				classificationsVector.add(it3.next() + "");
			}
		}
		int[] classifications = new int[classificationsVector.size()];
		it2 = classificationsVector.iterator();
		int i = 0;
		while (it2.hasNext()) {
			classifications[i] = Integer.parseInt(it2.next() + "");
			i++;
		}
		return classifications;
	}

	/**
	 * Method that redirects the calculations of the distribution and the
	 * correction. Redirects the visualization of the network and redirects the
	 * making of a file with the interesting data.
	 */

	public void performCalculations(Set selectedNodes, Set allNodes, Set noClassificationsSet) {
		final PostProcessTask calc = new PostProcessTask(selectedNodes, allNodes, noClassificationsSet);
		// Execute task here!
		final TaskManager tm = adapter.getTaskManager();
		tm.execute((new GenericTaskFactory(calc)).createTaskIterator());
	}
	
	private final class PostProcessTask extends AbstractTask {
		
		private final Set selectedNodes; 
		private final Set allNodes;
		private final Set noClassificationsSet;
		
		PostProcessTask(Set selectedNodes, Set allNodes, Set noClassificationsSet) {
			this.selectedNodes = selectedNodes;
			this.allNodes = allNodes;
			this.noClassificationsSet = noClassificationsSet;
		}

		@Override
		public void run(TaskMonitor tm) throws Exception {
			Map testMap = null;
			Map correctionMap = null;
			Map mapSmallX = null;
			Map mapSmallN = null;
			Map mapBigX = null;
			Map mapBigN = null;

			BingoAlgorithm algorithm = new BingoAlgorithm(params);
			CalculateTestTask test = algorithm.calculate_distribution(tm);
			test.calculate();
			
			testMap = test.getTestMap();
			CalculateCorrectionTask correction = algorithm.calculate_corrections(testMap);
			
			if ((correction != null) && (!params.getTest().equals(NONE))) {
				correction.calculate();
				correctionMap = correction.getCorrectionMap();
			}
			mapSmallX = test.getMapSmallX();
			mapSmallN = test.getMapSmallN();
			mapBigX = test.getMapBigX();
			mapBigN = test.getMapBigN();

			DisplayBiNGOWindow display;
			CreateBiNGOFile file;

			if (params.getVisualization().equals(VIZSTRING)) {
				display = new DisplayBiNGOWindow(testMap, correctionMap, mapSmallX, mapSmallN, mapBigX, mapBigN, params
						.getSignificance().toString(), params.getOntology(), params.getCluster_name(), params.getCategory()
						+ "", adapter);

				// displaying the bingo CyNetwork.
				display.makeWindow();
			}
			if ((goBin == null) || goBin.isWindowClosed()) {
				goBin = new bingo.internal.GOlorize.GoBin(settingsPanel, startNetworkView, adapter);
			}

			if (params.getAnnotationFile() == null) {
				params.setAnnotationFile("Cytoscape loaded annotation: " + params.getAnnotation().toString());
			}

			if (params.getOntologyFile() == null) {
				params.setOntologyFile("Cytoscape loaded ontology: " + params.getOntology().toString());
			}

			goBin.createResultTab(testMap, correctionMap, mapSmallX, mapSmallN, mapBigX, mapBigN, params.getSignificance()
					.toString(), params.getAnnotation(), params.getAlias(), params.getOntology(), params
					.getAnnotationFile().toString(), params.getOntologyFile().toString(), params.getTest() + "",
					params.getCorrection() + "", params.getOverOrUnder() + "", params.getFileoutput_dir(),
					params.getCluster_name() + ".bgo", params.getReferenceSet() + "", params.getCategory() + "",
					selectedNodes, startNetwork, startNetworkView);

			if (params.isFileoutput()) {
				file = new CreateBiNGOFile(testMap, correctionMap, mapSmallX, mapSmallN, mapBigX, mapBigN, params
						.getSignificance().toString(), params.getAnnotation(), params.getDeleteCodes(), params.getAlias(),
						params.getOntology(), params.getAnnotationFile().toString(), params.getOntologyFile().toString(),
						params.getTest() + "", params.getCorrection() + "", params.getOverOrUnder() + "",
						params.getFileoutput_dir(), params.getCluster_name() + ".bgo", params.getReferenceSet() + "",
						params.getCategory() + "", selectedNodes, noClassificationsSet);
				file.makeFile();

				if (params.getTest().equals(NONE) && params.getCorrection().equals(NONE)) {
					CreateAnnotationFile file2 = new CreateAnnotationFile(params.getAnnotation(), params.getAlias(),
							params.getOntology(), params.getFileoutput_dir(), params.getCluster_name() + ".anno",
							selectedNodes);
					file2.makeFile();
				}
			}
		}
		
	}
}
