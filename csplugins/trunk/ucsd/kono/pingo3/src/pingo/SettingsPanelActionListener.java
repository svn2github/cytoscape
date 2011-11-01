package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 * * Authors: Steven Maere
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.RichVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;

import BiNGO.AnnotationParser;
import BiNGO.BingoAlgorithm;

/**
 * ********************************************************************
 * SettingsPanelActionListener.java --------------------------------
 * <p/>
 * Steven Maere (c) 2010
 * <p/>
 * Class that is the listener for the PiNGO-button on the settingspanel. It
 * collects all kinds of information: the ontology and annotation file, the
 * alpha, which distribution and correction will be used, ... It also redirects
 * the vizualisation and the making of a file with information. It also
 * redirects calculation of the p-values and corrected p-values.
 * *********************************************************************
 */

public class SettingsPanelActionListener implements ActionListener {

	private SettingsPanel settingsPanel;
	private PingoParameters params;
	private pingo.GOlorize.GoBin goBin;
	private CyNetworkView startNetworkView;
	private CyNetwork startNetwork;

	/**
	 * constant string for the none-label in the combo box.
	 */
	private final String NONE = BingoAlgorithm.NONE;
	private Set<String> ecCodes;
	private boolean consistencyCheck = true;

	/**
	 * constant strings for the checking versus option.
	 */
	private final String GRAPH = BingoAlgorithm.GRAPH;
	private final String GENOME = BingoAlgorithm.GENOME;
	private final String VIZSTRING = BingoAlgorithm.VIZSTRING;

	private Map<String, Gene> geneMap = new HashMap<String, Gene>();

	private final CyPluginAdapter adapter;

	/**
	 * Constructor with all the settings of the settingspanel as arguments.
	 */
	public SettingsPanelActionListener(PingoParameters params, SettingsPanel settingsPanel,
			final CyPluginAdapter adapter) {
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
	 * action that is performed when the PiNGO-button is clicked.
	 * 
	 * @param event
	 *            PiNGO-button clicked.
	 */
	public void actionPerformed(ActionEvent event) {

		boolean status = false;
		try {
			status = updateParameters();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// passed all tests.
		if (status == true) {
			try {
				// Cytoscape graph input and file input
				consistencyCheck = true;
				// read in reference sets and make gene map.
				if (params.getReferenceSet().equals(GRAPH)) {
					if (params.isCytoscapeInput()) {
						final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();
						params.setRefSet(getRefGenesFromCyNetwork(network));
					} else {
						params.setRefSet(getRefGenesFromGraphFile(params.getGraphFile()));
					}
				} else if (params.getReferenceSet().equals(GENOME)) {
					params.setRefSet(getRefGenesFromAnnotation());
				} else {
					params.setRefSet(getRefGenesFromReferenceSet(params.getReferenceSet()));
				}

				params.setGeneMap(geneMap);

				if (consistencyCheck == true) {
					if (params.isCytoscapeInput()) {
						// graph
						startNetworkView = adapter.getCyApplicationManager().getCurrentNetworkView();
						startNetwork = adapter.getCyApplicationManager().getCurrentNetwork();
						params.setGraph(getCytoscapeNetwork(startNetwork));
					} else {
						params.setGraph(importGraph(params.getGraphFile()));
					}
				}
				if (consistencyCheck == true) {
					performCalculations();
				}

			} catch (Exception e) {
				JOptionPane.showMessageDialog(settingsPanel, e, "PiNGO Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	private String openResourceFile(String name) {
		return getClass().getResource("/" + name).toString();
	}

	public boolean updateParameters() throws IOException, InterruptedException {
		params.setOverOrUnder(BingoAlgorithm.OVERSTRING);
		params.setVisualization(settingsPanel.getVizPanel().getVizMode());
		params.setTabMode(settingsPanel.getVizPanel().getTabMode());
		params.setStarMode(settingsPanel.getVizPanel().getStarMode());
		params.setCluster_name(settingsPanel.getNameField().getText().trim());
		params.setGeneDescriptionFile(settingsPanel.getGeneDescriptionPanel().getSelection());

		// checking cluster name.
		if (params.getCluster_name().equals("")) {
			JOptionPane.showMessageDialog(settingsPanel, "Please choose a descriptive name for your analysis.");
			return false;
		}

		final Set<CyNetwork> networkSet = adapter.getCyNetworkManager().getNetworkSet();
		for (CyNetwork network : networkSet) {
			if (params.getCluster_name().equals(network.getCyRow().get(CyTableEntry.NAME, String.class))) {
				JOptionPane.showMessageDialog(settingsPanel,
						"A network with this name already exists in Cytoscape. Please choose another cluster name ");
				return false;
			}
		}

		// testing whether there is something valid in the text area

		// set the text from the window to the variable
		params.setGraphFile(settingsPanel.getGraphPanel().getSelection());
		params.setStartGoCats(settingsPanel.getStartGoCatInputField().getText());
		params.setFilterGoCats(settingsPanel.getFilterGoCatInputField().getText());
		params.setTargetGoCats(settingsPanel.getTargetGoCatInputField().getText());
		if (params.getTargetGoCats() == null || !params.getTargetGoCats().matches("\\s*\\d+(\\s+\\d+)*\\s*")) {
			JOptionPane.showMessageDialog(settingsPanel,
					"Please paste one or more NUMERIC target GO category labels in the text field.");
			return false;
		}

		if (settingsPanel.getGraphPanel().getSelection().equals(settingsPanel.CURRENTGRAPH)) {
			params.setCytoscapeInput(true);
		} else {
			params.setCytoscapeInput(false);
		}
		// checking whether, if we select the graph from Cytoscape, a network
		// exists
		if (params.isCytoscapeInput()) {
			// get the network object from the window.
			final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();
			if (network == null) {
				JOptionPane.showMessageDialog(settingsPanel, "Please load a network.");
				return false;
			}
		}
		// else check whether graph file exists
		else {
			try {
				BufferedReader test = new BufferedReader(new FileReader(params.getGraphFile()));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(settingsPanel, "Graph file does not exist or can't be read.");
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

		// Data panel
		params.setFileoutput(settingsPanel.getDataPanel().getEnabled());
		// datafile save option selected ?
		if (params.isFileoutput()) {
			// does file exist already ?
			params.setFileoutput_dir(settingsPanel.getDataPanel().getFileDir());
			File sel = new File(settingsPanel.getDataPanel().getFileDir(), params.getCluster_name() + ".pgo");
			if (sel.exists()) {
				int choice = JOptionPane.showConfirmDialog(settingsPanel, "File " + params.getCluster_name()
						+ ".pgo already exists. Overwrite (y/n)?\nIf not, choose a different file name.", "confirm",
						JOptionPane.YES_NO_OPTION);// .getValue() ;
				if (choice != JOptionPane.YES_OPTION) {
					settingsPanel.getDataPanel().reset();
					return false;
				}
			}
			// is file name for data file correct?
			String saveDataFileString = settingsPanel.getDataPanel().isFileNameLegal(params.getCluster_name() + ".pgo");
			if (!saveDataFileString.equals("LOADCORRECT")) {
				JOptionPane.showMessageDialog(settingsPanel, "Data File: " + saveDataFileString);
				return false;
			}
		}

		// simultaneously check whether annotation, ontology, refset, namespace,
		// deleteCode settings are changed, if so, change params.status to false

		// categories

		if (params.getReferenceSet() == null
				|| !params.getReferenceSet().equals((String) settingsPanel.getClusterVsPanel().getSelection())) {
			params.setStatus(false);
		}
		params.setReferenceSet((String) settingsPanel.getClusterVsPanel().getSelection());

		// testing versus-option selected?
		if (params.getReferenceSet().equals(NONE)) {
			JOptionPane.showMessageDialog(settingsPanel,
					"Please select against what reference the enrichment will be tested.");
			return false;
		}

		// annotation and ontology files

		// if (! this.bdsAnnot.isMemoryChoiceEnabled() ) {

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
		// previous call, PregoParameters.status is false and AnnotationParser
		// should be called again
		if (params.getStatus() == false) {
			AnnotationParser annParser = params.initializeAnnotationParser();
			annParser.calculate();
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

		// check whether input GO cats are present in ontology
		if (!params.getStartGoCats().equals("")) {
			String[] startGoCats = params.getStartGoCats().split("\\s+");
			for (String s : startGoCats) {
				if (!params.getOntology().containsTerm(new Integer(s))) {
					JOptionPane.showMessageDialog(settingsPanel, "WARNING : Category " + s
							+ " is not defined in the ontology. Please choose another start GO category.");
					return false;
				}
			}
		}
		if (!params.getFilterGoCats().equals("")) {
			String[] filterGoCats = params.getFilterGoCats().split("\\s+");
			for (String s : filterGoCats) {
				if (!params.getOntology().containsTerm(new Integer(s))) {
					JOptionPane.showMessageDialog(settingsPanel, "WARNING : Category " + s
							+ " is not defined in the ontology. Please choose another filter GO category.");
					return false;
				}
			}
		}
		if (!params.getTargetGoCats().equals("")) {
			String[] targetGoCats = params.getTargetGoCats().split("\\s+");
			for (String s : targetGoCats) {
				if (!params.getOntology().containsTerm(new Integer(s))) {
					JOptionPane.showMessageDialog(settingsPanel, "WARNING : Category " + s
							+ " is not defined in the ontology. Please choose another target GO category.");
					return false;
				}
			}
		}
		// return if all parameters were set.
		return true;
	}

	/**
	 * method that gets the canonical names from the whole graph.
	 * 
	 * @return HashSet containing the canonical names from the network.
	 */
	public HashSet<Gene> getRefGenesFromCyNetwork(CyNetwork network) {
		// HashSet for storing the canonical names
		HashSet refGenes = new HashSet<Gene>();
		geneMap = new HashMap<String, Gene>();
		// iterate over every node view to get the canonical names.
		int count = 0;
		final List<CyNode> nodes = network.getNodeList();
		for (CyNode node : nodes) {
			// gets the canonical name of the given node from the attributes
			// object
			String name = node.getCyRow().get(CyTableEntry.NAME, String.class);
			if (name != null && (name.length() != 0)) {
				Gene g = new Gene(name, count);
				refGenes.add(g);
				geneMap.put(name, g);
				count++;
			}
		}
		return refGenes;
	}

	public HashSet<Gene> getRefGenesFromGraphFile(String graphFile) {
		// HashSet for storing the canonical names
		HashSet refGenes = new HashSet<Gene>();
		geneMap = new HashMap<String, Gene>();

		// parse graph file
		BufferedReader bufferedReader;
		String line;
		HashSet<String> names = new HashSet<String>();
		int count = 0;

		try {
			bufferedReader = new BufferedReader(new FileReader(graphFile));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			// //omit header
			// bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] tokens = line.trim().split("\\t");
				String name1 = tokens[0].trim().toUpperCase();
				String name2 = tokens[1].trim().toUpperCase();
				if (!geneMap.containsKey(name1) && name1 != null && (name1.length() != 0)) {
					Gene g = new Gene(name1, count);
					refGenes.add(g);
					geneMap.put(name1, g);
					count++;
				}
				if (!geneMap.containsKey(name2) && name2 != null && (name2.length() != 0)) {
					Gene g = new Gene(name2, count);
					refGenes.add(g);
					geneMap.put(name2, g);
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return refGenes;
	}

	/**
	 * method that gets the canonical names for the whole annotation.
	 * 
	 * @return HashSet containing the canonical names.
	 */
	public HashSet<Gene> getRefGenesFromAnnotation() {
		String[] nodes = params.getAnnotation().getNames();
		// HashSet for storing the canonical names
		HashSet<Gene> refGenes = new HashSet<Gene>();
		geneMap = new HashMap<String, Gene>();
		int count = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null && (nodes[i].length() != 0)) {
				Gene g = new Gene(nodes[i].toUpperCase(), count);
				refGenes.add(g);
				geneMap.put(nodes[i].toUpperCase(), g);
				count++;
			}
		}

		// replace genes/names in reference set that match one of the names in
		// the graph, to avoid conflicts between names in ref set and selection,
		// remove all double mappings caused by e.g. splice variants
		HashSet<String> graphNames = new HashSet<String>();

		if (params.isCytoscapeInput()) {
			final CyNetwork network = adapter.getCyApplicationManager().getCurrentNetwork();
			final List<CyNode> nodeList = network.getNodeList();
			for (CyNode n : nodeList)
				graphNames.add(n.getCyRow().get(CyTableEntry.NAME, String.class));
		} else {
			graphNames = importGraphNodeNames(params.getGraphFile());
		}
		int nr = geneMap.size();
		for (String name : graphNames) {
			final Set<String> tmp = params.getAlias().get(name);
			if (tmp != null) {
				for (String s : tmp) {
					refGenes.remove(geneMap.get(s));
					geneMap.remove(s);
				}
			}
			// even if graph gene is undefined in annotation-derived refSet
			// (e.g. because it has no annotation), a gene will be created here.
			Gene g = new Gene(name, nr);
			refGenes.add(g);
			geneMap.put(name, g);
			nr++;
		}
		return refGenes;
	}

	/**
	 * method that gets the canonical names for a reference set.
	 * 
	 * @return HashSet containing the canonical names.
	 */
	public HashSet<Gene> getRefGenesFromReferenceSet(String refSet) {
		HashSet<String> nodes = parseReferenceSet(refSet);
		// HashSet for storing the genes
		HashSet<Gene> refGenes = new HashSet<Gene>();
		geneMap = new HashMap<String, Gene>();
		int count = 0;
		for (String s : nodes) {
			if (s.length() != 0 && !geneMap.containsKey(s)) {
				Gene g = new Gene(s, count);
				refGenes.add(g);
				count++;
				geneMap.put(s, g);
			}
		}

		return refGenes;
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
	 * method that gets the network from the Cytoscape graph.
	 * 
	 * @return HashMap<Gene,HashMap<Gene,Double>> representing the graph.
	 */
	public Map<Gene, Map<Gene, Double>> getCytoscapeNetwork(final CyNetwork network) {

		final Map<Gene, Map<Gene, Double>> G = new HashMap<Gene, Map<Gene, Double>>();

		// iterate over every edge.
		final List<CyEdge> edges = network.getEdgeList();
		for (final CyEdge edge : edges) {
			final CyNode node1 = edge.getSource();
			final CyNode node2 = edge.getTarget();
			// gets the canonical name of the given node from the attributes
			// object
			final String name1 = node1.getCyRow().get(CyTableEntry.NAME, String.class);
			final String name2 = node2.getCyRow().get(CyTableEntry.NAME, String.class);

			if (name1 != null && (name1.length() != 0) && name2 != null && (name2.length() != 0)) {
				if (!geneMap.containsKey(name1)) {
					int opt = JOptionPane.showOptionDialog(settingsPanel,
							"WARNING : The reference set does not contain " + name1
									+ ".\n If you press 'Yes', this gene will be ignored \n"
									+ "and calculations will proceed. Press 'No' to abort calculations.", "WARNING",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
					if (opt == JOptionPane.NO_OPTION) {
						consistencyCheck = false;
						return null;
					}
				} else if (!geneMap.containsKey(name2)) {
					int opt = JOptionPane.showOptionDialog(settingsPanel,
							"WARNING : The reference set does not contain " + name2
									+ ".\n If you press 'Yes', this gene will be ignored \n"
									+ "and calculations will proceed. Press 'No' to abort calculations.", "WARNING",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
					if (opt == JOptionPane.NO_OPTION) {
						consistencyCheck = false;
						return null;
					}
				} else {
					Gene gene1 = geneMap.get(name1);
					Gene gene2 = geneMap.get(name2);
					if (!gene1.equals(gene2)) {
						if (!G.containsKey(gene1)) {
							G.put(gene1, new HashMap<Gene, Double>());
						}
						G.get(gene1).put(gene2, new Double(0));
						if (!G.containsKey(gene2)) {
							G.put(gene2, new HashMap<Gene, Double>());
						}
						G.get(gene2).put(gene1, new Double(0));
					}
				}
			}
		}

		return G;
	}

	/**
	 * method that gets the network from a graph file.
	 * 
	 * @return HashMap<Gene,HashMap<Gene,Double>> representing the graph.
	 */

	public Map<Gene, Map<Gene, Double>> importGraph(String graphFile) {
		// parse graph file
		BufferedReader bufferedReader;
		String line;
		final Map<Gene, Map<Gene, Double>> G = new HashMap<Gene, Map<Gene, Double>>();

		try {
			bufferedReader = new BufferedReader(new FileReader(graphFile));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			// //omit header
			// bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.trim().startsWith("#")) {
					String[] tokens = line.trim().split("\\t");
					if (tokens.length < 2) {
						JOptionPane
								.showMessageDialog(
										settingsPanel,
										"The input network file does not seem to have the right format. \n Please specify a tab-delimited text file with at least two columns.");
						consistencyCheck = false;
						return null;
					}
					String name1 = tokens[0].trim().toUpperCase();
					String name2 = tokens[1].trim().toUpperCase();
					if (!geneMap.containsKey(name1)) {
						int opt = JOptionPane.showOptionDialog(settingsPanel,
								"WARNING : The reference set does not contain " + name1
										+ ".\n If you press 'Yes', this gene will be ignored \n"
										+ "and calculations will proceed. Press 'No' to abort calculations.",
								"WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
						if (opt == JOptionPane.NO_OPTION) {
							consistencyCheck = false;
							return null;
						}
					} else if (!geneMap.containsKey(name2)) {
						int opt = JOptionPane.showOptionDialog(settingsPanel,
								"WARNING : The reference set does not contain " + name2
										+ ".\n If you press 'Yes', this gene will be ignored \n"
										+ "and calculations will proceed. Press 'No' to abort calculations.",
								"WARNING", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
						if (opt == JOptionPane.NO_OPTION) {
							consistencyCheck = false;
							return null;
						}
					} else {
						Gene gene1 = geneMap.get(name1);
						Gene gene2 = geneMap.get(name2);
						if (!gene1.equals(gene2)) {
							if (!G.containsKey(gene1)) {
								G.put(gene1, new HashMap<Gene, Double>());
							}
							G.get(gene1).put(gene2, new Double(0));
							if (!G.containsKey(gene2)) {
								G.put(gene2, new HashMap<Gene, Double>());
							}
							G.get(gene2).put(gene1, new Double(0));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return G;

	}

	public HashSet<String> importGraphNodeNames(String graphFile) {
		// parse graph file
		BufferedReader bufferedReader;
		String line;
		HashSet<String> names = new HashSet<String>();

		try {
			bufferedReader = new BufferedReader(new FileReader(graphFile));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			// //omit header
			// bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] tokens = line.trim().split("\\t");
				String name1 = tokens[0].trim().toUpperCase();
				String name2 = tokens[1].trim().toUpperCase();
				names.add(name1);
				names.add(name2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return names;

	}

	/**
	 * Method that redirects the calculations of the distribution and the
	 * correction. Redirects the visualization of the network and redirects the
	 * making of a file with the interesting data.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */

	public void performCalculations() throws InterruptedException, IOException {
		PingoAnalysis gcb = new PingoAnalysis(params);
		ModuleNetwork M = gcb.getModuleNetwork();
		if (M.moduleSet == null) {
			JOptionPane.showMessageDialog(settingsPanel, "There are no test genes in the network after filtering.");
		} else {
			if (params.getTabMode().equals(VizPanel.TABSTRING)) {
				String[] goCats = params.getTargetGoCats().split("\\s+");
				for (String s : goCats) {
					Integer cat = new Integer(s);
					Map<PingoAnalysis.TestInstance, Double> pvals = gcb.getPvalues(cat);
					Map<PingoAnalysis.TestInstance, Integer> smallX = gcb.getSmallX(cat);
					Map<PingoAnalysis.TestInstance, Integer> smallN = gcb.getSmallN(cat);
					Map<PingoAnalysis.TestInstance, Integer> bigX = gcb.getBigX(cat);
					Map<PingoAnalysis.TestInstance, Integer> bigN = gcb.getBigN(cat);
					Map<PingoAnalysis.TestInstance, Set<Gene>> neighbors = gcb.getNeighbors(cat);
					DisplayPingoWindow display;
					Set<Gene> predictedGenesSet = new HashSet<Gene>();
					Set<Gene> graphNodes = new HashSet<Gene>();
					Map<Gene, Map<Gene, Double>> graph = new HashMap<Gene, Map<Gene, Double>>();
					for (PingoAnalysis.TestInstance t : pvals.keySet()) {
						predictedGenesSet.add(M.geneMap.get(t.m.name));
						graphNodes.add(M.geneMap.get(t.m.name));
						for (Gene g : neighbors.get(t)) {
							if (!graph.containsKey(M.geneMap.get(t.m.name))) {
								graph.put(M.geneMap.get(t.m.name), new HashMap<Gene, Double>());
							}
							graph.get(M.geneMap.get(t.m.name)).put(g, 0.0);
							graphNodes.add(g);
						}
					}
					// if not starmode, add interconnections between neighbors
					if (params.getStarMode().equals(VizPanel.NOSTARSTRING)) {
						for (Gene g1 : graphNodes) {
							for (Gene g2 : graphNodes) {
								// G is double-linked hashmap
								if ((M.G.containsKey(g1) && M.G.get(g1).containsKey(g2))
										&& (graph.containsKey(g2) && !graph.get(g2).containsKey(g1))) {
									if (!graph.containsKey(g1)) {
										graph.put(g1, new HashMap<Gene, Double>());
									}
									graph.get(g1).put(g2, 0.0);
								}
							}
						}
					}

					if (params.getVisualization().equals(VIZSTRING) && params.isCytoscapeInput()) {

						final Set<CyNode> selectedNodesSet = new HashSet();
						final Collection<View<CyNode>> nodeViews = startNetworkView.getNodeViews();
						for (final View<CyNode> nodeView : nodeViews) {
							final String node = nodeView.getModel().getCyRow().get(CyTableEntry.NAME, String.class);
							if (graphNodes.contains(M.geneMap.get(node))) {
								selectedNodesSet.add(nodeView.getModel());
							}
						}

						final Set<CyEdge> selectedEdgesSet = new HashSet<CyEdge>();
						final Collection<View<CyEdge>> edgeViews = startNetworkView.getEdgeViews();
						for (View<CyEdge> edgeView : edgeViews) {
							final String sourceNode = edgeView.getModel().getSource().getCyRow()
									.get(CyTableEntry.NAME, String.class);
							final String targetNode = edgeView.getModel().getTarget().getCyRow()
									.get(CyTableEntry.NAME, String.class);
							if (graphNodes.contains(M.geneMap.get(sourceNode))
									&& graphNodes.contains(M.geneMap.get(targetNode))) {
								if (params.getStarMode().equals(VizPanel.STARSTRING)) {
									if (predictedGenesSet.contains(M.geneMap.get(sourceNode))
											|| predictedGenesSet.contains(M.geneMap.get(targetNode))) {
										selectedEdgesSet.add(edgeView.getModel());
									}
								} else {
									selectedEdgesSet.add(edgeView.getModel());
								}
							}
						}

						this.setSelected(startNetworkView.getModel().getNodeList(), false);
						this.setSelected(startNetworkView.getModel().getEdgeList(), false);
						this.setSelected(selectedNodesSet, true);
						this.setSelected(selectedEdgesSet, true);

						startNetworkView.updateView();
						newWindowSelectedNodesEdges(s);

					} else if (params.getVisualization().equals(VIZSTRING)) {
						display = new DisplayPingoWindow(M, graph, pvals, smallX, smallN, bigX, bigN, params
								.getSignificance().toString(), params.getCluster_name() + "_" + s, adapter);

						// displaying the BiNGO CyNetwork.
						display.makeWindow();
					}
					if ((goBin == null) || goBin.isWindowClosed())
						goBin = new pingo.GOlorize.GoBin(settingsPanel, startNetworkView, adapter);

					CyNetwork curNetwork = adapter.getCyApplicationManager().getCurrentNetwork();
					CyNetworkView curNetworkView = adapter.getCyApplicationManager().getCurrentNetworkView();
					goBin.createResultTab(M, pvals, smallX, smallN, bigX, bigN, neighbors, params.getCluster_name()
							+ "_" + s, params.getAnnotationFile().toString(), params.getOntologyFile().toString(),
							params.getTest() + "", params.getCorrection() + "", curNetwork, curNetworkView);
				}
			} else {
				String[] goCats = params.getTargetGoCats().split("\\s+");

				Map<PingoAnalysis.TestInstance, Double> pvals = new HashMap<PingoAnalysis.TestInstance, Double>();
				Map<PingoAnalysis.TestInstance, Integer> smallX = new HashMap<PingoAnalysis.TestInstance, Integer>();
				Map<PingoAnalysis.TestInstance, Integer> smallN = new HashMap<PingoAnalysis.TestInstance, Integer>();
				Map<PingoAnalysis.TestInstance, Integer> bigX = new HashMap<PingoAnalysis.TestInstance, Integer>();
				Map<PingoAnalysis.TestInstance, Integer> bigN = new HashMap<PingoAnalysis.TestInstance, Integer>();
				Map<PingoAnalysis.TestInstance, Set<Gene>> neighbors = new HashMap<PingoAnalysis.TestInstance, Set<Gene>>();
				DisplayPingoWindow display;
				Set<Gene> predictedGenesSet = new HashSet<Gene>();
				Set<Gene> graphNodes = new HashSet<Gene>();
				Map<Gene, Map<Gene, Double>> graph = new HashMap<Gene, Map<Gene, Double>>();
				for (String s : goCats) {
					Integer cat = new Integer(s);
					pvals.putAll(gcb.getPvalues(cat));
					smallX.putAll(gcb.getSmallX(cat));
					smallN.putAll(gcb.getSmallN(cat));
					bigX.putAll(gcb.getBigX(cat));
					bigN.putAll(gcb.getBigN(cat));
					neighbors.putAll(gcb.getNeighbors(cat));
				}

				for (PingoAnalysis.TestInstance t : pvals.keySet()) {
					predictedGenesSet.add(M.geneMap.get(t.m.name));
					graphNodes.add(M.geneMap.get(t.m.name));
					for (Gene g : neighbors.get(t)) {
						if (!graph.containsKey(M.geneMap.get(t.m.name))) {
							graph.put(M.geneMap.get(t.m.name), new HashMap<Gene, Double>());
						}
						graph.get(M.geneMap.get(t.m.name)).put(g, 0.0);
						graphNodes.add(g);
					}
				}

				// if not starmode, add interconnections between neighbors
				if (params.getStarMode().equals(VizPanel.NOSTARSTRING)) {
					for (Gene g1 : graphNodes) {
						for (Gene g2 : graphNodes) {
							// G is double-linked hashmap
							if ((M.G.containsKey(g1) && M.G.get(g1).containsKey(g2))
									&& (graph.containsKey(g2) && !graph.get(g2).containsKey(g1))) {
								if (!graph.containsKey(g1)) {
									graph.put(g1, new HashMap<Gene, Double>());
								}
								graph.get(g1).put(g2, 0.0);
							}
						}
					}
				}

				if (params.getVisualization().equals(VIZSTRING) && params.isCytoscapeInput()) {

					final Set<CyNode> selectedNodesSet = new HashSet<CyNode>();
					final Collection<View<CyNode>> nodeViews = startNetworkView.getNodeViews();
					for (final View<CyNode> nodeView : nodeViews) {
						final String node = nodeView.getModel().getCyRow().get(CyTableEntry.NAME, String.class);
						if (graphNodes.contains(M.geneMap.get(node))) {
							selectedNodesSet.add(nodeView.getModel());
						}
					}
					final Set<CyEdge> selectedEdgesSet = new HashSet<CyEdge>();
					final Collection<View<CyEdge>> edgeViews = startNetworkView.getEdgeViews();
					for (View<CyEdge> edgeView : edgeViews) {
						final String sourceNode = edgeView.getModel().getSource().getCyRow()
								.get(CyTableEntry.NAME, String.class);
						final String targetNode = edgeView.getModel().getTarget().getCyRow()
								.get(CyTableEntry.NAME, String.class);
						if (graphNodes.contains(M.geneMap.get(sourceNode))
								&& graphNodes.contains(M.geneMap.get(targetNode))) {
							if (params.getStarMode().equals(VizPanel.STARSTRING)) {
								if (predictedGenesSet.contains(M.geneMap.get(sourceNode))
										|| predictedGenesSet.contains(M.geneMap.get(targetNode))) {
									selectedEdgesSet.add(edgeView.getModel());
								}
							} else {
								selectedEdgesSet.add(edgeView.getModel());
							}
						}
					}

					this.setSelected(startNetworkView.getModel().getNodeList(), false);
					this.setSelected(startNetworkView.getModel().getEdgeList(), false);
					this.setSelected(selectedNodesSet, true);
					this.setSelected(selectedEdgesSet, true);

					startNetworkView.updateView();
					newWindowSelectedNodesEdges("");

				} else if (params.getVisualization().equals(VIZSTRING)) {
					display = new DisplayPingoWindow(M, graph, pvals, smallX, smallN, bigX, bigN, params
							.getSignificance().toString(), params.getCluster_name(), adapter);

					// displaying the BiNGO CyNetwork.
					display.makeWindow();
				}
				if ((goBin == null) || goBin.isWindowClosed()) {
					goBin = new pingo.GOlorize.GoBin(settingsPanel, startNetworkView, adapter);
				}

				goBin.createResultTab(M, pvals, smallX, smallN, bigX, bigN, neighbors, params.getCluster_name(), params
						.getAnnotationFile().toString(), params.getOntologyFile().toString(), params.getTest() + "",
						params.getCorrection() + "", adapter.getCyApplicationManager().getCurrentNetwork(), adapter
								.getCyApplicationManager().getCurrentNetworkView());
			}
		}
	}

	public void newWindowSelectedNodesEdges(final String name) {
		// keep ref to current state
		CyNetwork current_network = startNetwork;
		CyNetworkView current_network_view = startNetworkView;

		if (current_network == null)
			return;

		final Set<CyNode> nodes = this.getSelected(current_network.getNodeList());
		final Set<CyEdge> edges = this.getSelected(current_network.getEdgeList());

		String networkName;
		if (!name.isEmpty())
			networkName = params.getCluster_name() + "_" + name;
		else
			networkName = params.getCluster_name();

		final CyNetwork new_network = adapter.getCyNetworkFactory().getInstance();
		new_network.getCyRow().set(CyTableEntry.NAME, networkName);
		// Add nodes
		final Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		for (CyNode node : nodes) {
			final String nodeName = node.getCyRow().get(CyTableEntry.NAME, String.class);
			CyNode newNode = nodeMap.get(nodeName);
			if (newNode == null) {
				newNode = new_network.addNode();
				newNode.getCyRow().set(CyTableEntry.NAME, nodeName);
				nodeMap.put(nodeName, newNode);
			}
		}

		// Add edges
		final Map<String, CyEdge> edgeMap = new HashMap<String, CyEdge>();
		for (CyEdge edge : edges) {
			final String edgeName = edge.getCyRow().get(CyTableEntry.NAME, String.class);
			CyEdge newEdge = edgeMap.get(edgeName);
			if (newEdge == null) {
				final String sourceName = edge.getSource().getCyRow().get(CyTableEntry.NAME, String.class);
				final String targetName = edge.getTarget().getCyRow().get(CyTableEntry.NAME, String.class);
				newEdge = new_network.addEdge(nodeMap.get(sourceName), nodeMap.get(targetName), true);
				newEdge.getCyRow().set(CyTableEntry.NAME, edgeName);
				newEdge.getCyRow().set(CyEdge.INTERACTION, edge.getCyRow().get(CyEdge.INTERACTION, String.class));
				edgeMap.put(edgeName, newEdge);
			}
		}

		final CyNetworkView new_network_view = adapter.getCyNetworkViewFactory().getNetworkView(new_network);

		String vsName = "default";

		// keep the node positions
		if (current_network_view != null) {
			final List<CyNode> nodeList = new_network.getNodeList();

			for (final CyNode node : nodeList) {
				final View<CyNode> nvOriginal = current_network_view.getNodeView(node);
				final View<CyNode> nv = new_network_view.getNodeView(node);
				final Double x = nvOriginal.getVisualProperty(RichVisualLexicon.NODE_X_LOCATION);
				final Double y = nvOriginal.getVisualProperty(RichVisualLexicon.NODE_Y_LOCATION);
				nv.setVisualProperty(RichVisualLexicon.NODE_X_LOCATION, x);
				nv.setVisualProperty(RichVisualLexicon.NODE_Y_LOCATION, y);
			}

			new_network_view.fitContent();

			// Set visual style
			final VisualStyle newVS = adapter.getVisualMappingManager().getVisualStyle(current_network_view);

			if (newVS != null)
				vsName = newVS.getTitle();

			adapter.getVisualMappingManager().setVisualStyle(newVS, new_network_view);
		}

	}

	private void setSelected(final Collection<? extends CyTableEntry> objects, final Boolean value) {
		for (CyTableEntry obj : objects)
			obj.getCyRow().set(CyNetwork.SELECTED, value);
	}

	private final <T extends CyTableEntry> Set<T> getSelected(final Collection<T> objects) {
		final Set<T> selected = new HashSet<T>();
		for (T obj : objects) {
			if (obj.getCyRow().get(CyNetwork.SELECTED, Boolean.class))
				selected.add(obj);
		}

		return selected;
	}

}
