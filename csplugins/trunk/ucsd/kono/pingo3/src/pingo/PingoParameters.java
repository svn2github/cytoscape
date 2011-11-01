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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarFile;

import BiNGO.AnnotationParser;
import BiNGO.BingoAlgorithm;
import BiNGO.BingoParameters;
import BiNGO.ontology.Annotation;
import BiNGO.ontology.Ontology;

public class PingoParameters {

	// Pingo properties
	private Properties pingo_props;
	private BingoParameters bp;

	private String pingoDir;

	// parameters extracted from the pingo interface
	private String cluster_name;
	private String startGoCats;
	private String filterGoCats;
	private String targetGoCats;
	private String graphFile;
	private Boolean cytoscapeInput;
	private String test;
	private String visualization;
	private String tabMode;
	private String starMode;
	private String correction;
	private String overunder;
	private BigDecimal significance;
	private String category;
	private String referenceSet;
	private String species;
	private String geneDescriptionFile;
	// private String identifier;

	// annotation and ontology files
	private String annotationFile;
	private String ontologyFile;
	private Annotation annotation;
	private Ontology ontology;
	private String namespace;
	private Set deleteCodes;
	private Map<String, Set<String>> alias;
	private Map<Gene, Map<Gene, Double>> graph;
	private Map<String, Gene> geneMap;
	private Set<Gene> refSet;

	private AnnotationParser annParser;
	// indicates whether we are using the default files (if false then we are using custom files)
	private boolean annotation_default;
	private boolean ontology_default;

	private boolean fileoutput;
	private String fileoutput_dir;

	// Variables that hold the species, the identifiers and ontologies
	// That are available to use for any computation.
	// These are dictated by what is in the properties file.
	private TreeMap speciesfileHash;
	private TreeMap filespeciesHash;
	// private TreeMap identifierHash;
	private TreeMap ontologyHash;
	private TreeMap namespaceHash;
	private int number_species;
	private int number_ontology;
	private int number_namespaces;

	private boolean status = false;

	// stores list of selected nodes and the all nodes in the reference set
	private HashSet selectedNodes;
	private HashSet allNodes;

	public PingoParameters(String pingoDir) throws IOException {

		this.pingoDir = pingoDir;
		this.deleteCodes = new HashSet();

		pingo_props = new Properties();
		// Open the properties file, try custom properties in /plugins folder
		// first
		try {
			pingo_props.load(PropReader("pingo_gui.properties"));
			checkProps();
		} catch (Exception e) {
			try {
				pingo_props.load(JarReader("pingo_gui.properties"));
				checkProps();
			} catch (Exception e2) {
				// throw e;
				String propfile_path = openResourceFile("pingo_gui.properties");
				System.out.println("can't find default properties file" + propfile_path);
			}
		}
		initializeSpeciesHash();
		initializeOntologyHash();
		initializeNamespaceHash();

		this.bp = new BingoParameters(pingoDir);

		// Give default values to main parameters
		test = BingoAlgorithm.HYPERGEOMETRIC;
		correction = BingoAlgorithm.BENJAMINI_HOCHBERG_FDR;
		significance = new BigDecimal(0.05);
		category = BingoAlgorithm.CATEGORY_CORRECTION;
		referenceSet = BingoAlgorithm.GENOME;
		namespace = BingoAlgorithm.NONE;

	}

	private String openResourceFile(String name) {
		return getClass().getResource("/" + name).toString();
	}

	public InputStream JarReader(String name) throws IOException {
		URL url = getClass().getResource("/" + name);
		JarURLConnection juc = (JarURLConnection) url.openConnection();
		JarFile jarFile = juc.getJarFile();
		InputStream is = jarFile.getInputStream(jarFile.getJarEntry(name));
		return is;
	}

	public InputStream PropReader(String name) throws IOException {
		File propFile = new File(pingoDir, name);
		FileInputStream is = new FileInputStream(propFile);
		return is;
	}

	public OutputStream PropWriter(String name) throws IOException {
		File propFile = new File(pingoDir, name);
		FileOutputStream os = new FileOutputStream(propFile);
		return os;
	}

	public void storeParameterSettings() throws IOException {
		OutputStream os = PropWriter("pingo_gui.properties");
		pingo_props.store(os, "");
		os.close();
	}

	public void initializeSpeciesHash() {
		Object f;
		String property_value;
		this.speciesfileHash = new TreeMap();
		this.filespeciesHash = new TreeMap();
		number_species = 0;
		for (Enumeration e = pingo_props.propertyNames(); e.hasMoreElements();) {
			f = e.nextElement();

			// Check to see if the element is part of the species subset
			if (f.toString().indexOf("species.") != -1) {
				// .contains("species.")) {
				number_species++;
				property_value = pingo_props.getProperty(f.toString());
				String filename = property_value;
				// Take the & sign out key and replace with a space
				String key = f.toString().substring(8);
				String[] tokens_key = key.split("_");
				String formatted_key = "";
				int j = 0;
				for (; j < tokens_key.length - 1; j++) {
					formatted_key = formatted_key + tokens_key[j] + " ";
				}
				formatted_key = formatted_key + tokens_key[j];
				System.out.println(formatted_key);

				speciesfileHash.put(formatted_key, filename);
				filespeciesHash.put(filename, formatted_key);
			}
		}
	}

	public String getSpeciesFilename(String specified_species) {
		return (String) speciesfileHash.get(specified_species);
	}

	public String getSpeciesNameFromFilename(String filename) {
		return (String) filespeciesHash.get(filename);
	}

	public void initializeOntologyHash() {
		Object f;
		String property_value;
		this.ontologyHash = new TreeMap();
		number_ontology = 0;

		for (Enumeration e = pingo_props.propertyNames(); e.hasMoreElements();) {
			f = e.nextElement();

			// Check to see if the element is part of the identifier subset
			if (f.toString().indexOf("ontology.") != -1) {
				// (f.toString().contains("ontology.")) {
				number_ontology++;
				property_value = pingo_props.getProperty(f.toString());
				ontologyHash.put(f.toString().substring(9), property_value);
			}
		}
	}

	public void initializeNamespaceHash() {
		Object f;
		String property_value;
		this.namespaceHash = new TreeMap();
		number_namespaces = 0;

		for (Enumeration e = pingo_props.propertyNames(); e.hasMoreElements();) {
			f = e.nextElement();

			// Check to see if the element is part of the identifier subset
			if (f.toString().indexOf("namespace.") != -1) {
				// (f.toString().contains("ontology.")) {
				number_namespaces++;
				property_value = pingo_props.getProperty(f.toString());
				namespaceHash.put(f.toString().substring(9), property_value);
			}
		}
	}

	public String[] getSpeciesLabels() {
		int label_num = 0;
		String[] labels = new String[number_species + 2];
		// Add the None label
		labels[label_num] = BingoAlgorithm.NONE;
		label_num++;

		for (Iterator iter = speciesfileHash.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			labels[label_num] = (String) entry.getKey();
			label_num++;
		}
		// Add the Custom label
		labels[label_num] = BingoAlgorithm.CUSTOM;

		return labels;
	}

	/*
	 * Method to get the labels of the ontology files. The labels are the values
	 * in the Ontology TreeMap.
	 */
	public String[] getOntologyLabels() {
		int label_num = 0;
		String[] labels = new String[number_ontology + 2];
		// Add the None label
		labels[label_num] = BingoAlgorithm.NONE;
		label_num++;

		// ontologyHash.values()
		for (Iterator iter = ontologyHash.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			labels[label_num] = (String) entry.getValue();
			label_num++;
		}

		// Add the Custom label
		labels[label_num] = BingoAlgorithm.CUSTOM;

		return labels;
	}

	public String[] getNamespaceLabels() {
		int label_num = 0;
		String[] labels = new String[number_namespaces + 2];
		// Add the None label
		labels[label_num] = BingoAlgorithm.NONE;
		label_num++;

		// ontologyHash.values()
		for (Iterator iter = namespaceHash.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			labels[label_num] = (String) entry.getValue();
			label_num++;
		}

		// Add the Custom label
		labels[label_num] = BingoAlgorithm.CUSTOM;

		return labels;
	}

	public AnnotationParser getAnnParser() {
		return annParser;
	}

	public Set getDeleteCodes() {
		return deleteCodes;
	}

	public AnnotationParser initializeAnnotationParser() {
		annParser = new AnnotationParser(this.bp, new HashSet<String>());
		return annParser;
	}

	public AnnotationParser initializeAnnotationParser(HashSet<String> genes) {
		annParser = new AnnotationParser(this.bp, genes);
		return annParser;
	}

	public TreeMap getOntologyHash() {
		return ontologyHash;
	}

	public Map<String, Set<String>> getAlias() {
		return alias;
	}

	public boolean isAnnotation_default() {
		return annotation_default;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public String getStartGoCats() {
		return startGoCats;
	}

	public String getFilterGoCats() {
		return filterGoCats;
	}

	public String getTargetGoCats() {
		return targetGoCats;
	}

	public String getGeneDescriptionFile() {
		return geneDescriptionFile;
	}

	public Map<String, Gene> getGeneMap() {
		return geneMap;
	}

	public String getGraphFile() {
		return graphFile;
	}

	public String getSpecies() {
		return species;
	}

	public boolean isOntology_default() {
		return ontology_default;
	}

	public String getPingoDir() {
		return pingoDir;
	}

	public String getVisualization() {
		return visualization;
	}

	public String getTabMode() {
		return tabMode;
	}

	public String getStarMode() {
		return starMode;
	}

	public String getCategory() {
		return category;
	}

	public String getOverOrUnder() {
		return overunder;
	}

	public String getReferenceSet() {
		return referenceSet;
	}

	public String getAnnotationFile() {
		return annotationFile;
	}

	public String getOntologyFile() {
		return ontologyFile;
	}

	public String getNameSpace() {
		return namespace;
	}

	public boolean isFileoutput() {
		return fileoutput;
	}

	public String getFileoutput_dir() {
		return fileoutput_dir;
	}

	public Properties getPingo_props() {
		return pingo_props;
	}

	public String getCluster_name() {
		return cluster_name;
	}

	public String getTest() {
		return test;
	}

	public Map<Gene, Map<Gene, Double>> getGraph() {
		return graph;
	}

	public String getCorrection() {
		return correction;
	}

	public BigDecimal getSignificance() {
		return significance;
	}

	public HashSet getSelectedNodes() {
		return selectedNodes;
	}

	public HashSet getAllNodes() {
		return allNodes;
	}

	public Set<Gene> getRefSet() {
		return refSet;
	}

	public boolean getStatus() {
		return status;
	}

	public BingoParameters getBingoParameters() {
		return bp;
	}

	public void setBingoParameters(BingoParameters bp) {
		this.bp = bp;
	}

	public void setAllNodes(HashSet allNodes) {
		this.allNodes = allNodes;
		bp.setAllNodes(allNodes);
	}

	public void setSelectedNodes(HashSet selectedNodes) {
		this.selectedNodes = selectedNodes;
		bp.setSelectedNodes(allNodes);
	}

	public void setPingoDir(String pingoDir) {
		this.pingoDir = pingoDir;
		bp.setBingoDir(pingoDir);
	}

	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
		bp.setCluster_name(cluster_name);
	}

	public void setGraphFile(String graphFile) {
		this.graphFile = graphFile;
	}

	public void setStartGoCats(String startGoCats) {
		this.startGoCats = startGoCats;
	}

	public void setFilterGoCats(String filterGoCats) {
		this.filterGoCats = filterGoCats;
	}

	public void setTargetGoCats(String targetGoCats) {
		this.targetGoCats = targetGoCats;
	}

	public void setGeneDescriptionFile(String geneDescriptionFile) {
		this.geneDescriptionFile = geneDescriptionFile;
	}

	public void setCytoscapeInput(boolean b) {
		this.cytoscapeInput = b;
	}

	public boolean isCytoscapeInput() {
		return this.cytoscapeInput;
	}

	public void setOverOrUnder(String o) {
		this.overunder = o;
		bp.setOverOrUnder(overunder);
	}

	public void setTest(String test) {
		this.test = test;
		bp.setTest(test);
	}

	public void setGraph(Map<Gene, Map<Gene, Double>> graph) {
		this.graph = graph;
	}

	public void setRefSet(HashSet<Gene> refSet) {
		this.refSet = refSet;
	}

	public void setVisualization(String visualization) {
		this.visualization = visualization;
		bp.setVisualization(visualization);
	}

	public void setTabMode(String tabMode) {
		this.tabMode = tabMode;
	}

	public void setStarMode(String starMode) {
		this.starMode = starMode;
	}

	public void setCorrection(String correction) {
		this.correction = correction;
		bp.setCorrection(correction);
	}

	public void setSignificance(BigDecimal significance) {
		this.significance = significance;
		bp.setSignificance(significance);
	}

	public void setCategory(String category) {
		this.category = category;
		bp.setCategory(category);
	}

	public void setReferenceSet(String referenceSet) {
		this.referenceSet = referenceSet;
		bp.setReferenceSet(referenceSet);
	}

	public void setSpecies(String species) {
		this.species = species;
		bp.setSpecies(species);
	}

	public void setAnnotationFile(String annotationFile) {
		this.annotationFile = annotationFile;
		bp.setAnnotationFile(annotationFile);
	}

	public void setOntologyFile(String ontologyFile) {
		this.ontologyFile = ontologyFile;
		bp.setOntologyFile(ontologyFile);
	}

	public void setAnnotation_default(boolean annotation_default) {
		this.annotation_default = annotation_default;
		bp.setAnnotation_default(annotation_default);
	}

	public void setOntology_default(boolean ontology_default) {
		this.ontology_default = ontology_default;
		bp.setOntology_default(ontology_default);
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
		bp.setAnnotation(annotation);
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
		bp.setOntology(ontology);
	}

	public void setNameSpace(String namespace) {
		this.namespace = namespace;
		bp.setNameSpace(namespace);
	}

	public void setAlias(Map<String, Set<String>> alias) {
		this.alias = alias;
		bp.setAlias(alias);
	}

	public void setGeneMap(Map<String, Gene> geneMap) {
		this.geneMap = geneMap;
	}

	public void setDeleteCodes(HashSet deleteCodes) {
		this.deleteCodes = deleteCodes;
		bp.setDeleteCodes(deleteCodes);
	}

	public void setStatus(boolean status) {
		this.status = status;
		bp.setStatus(status);
	}

	public void setFileoutput(boolean fileoutput) {
		this.fileoutput = fileoutput;
		bp.setFileoutput(fileoutput);
	}

	public void setFileoutput_dir(String fileoutput_dir) {
		this.fileoutput_dir = fileoutput_dir;
		bp.setFileoutput_dir(fileoutput_dir);
	}

	private void checkProps() throws Exception {
		if (!pingo_props.containsKey("file_output") || !pingo_props.containsKey("namespace.gosubset_prok")
				|| !pingo_props.containsKey("overrep_def") || !pingo_props.containsKey("ontology.GO_PROCESS")
				|| !pingo_props.containsKey("target_go_def") || !pingo_props.containsKey("ontology.GOSLIM_GENERIC")
				|| !pingo_props.containsKey("correction_def") || !pingo_props.containsKey("outputdir_def")
				|| !pingo_props.containsKey("namespace.cellular_component")
				|| !pingo_props.containsKey("annotation_default") || !pingo_props.containsKey("tests_def")
				|| !pingo_props.containsKey("visual_def") || !pingo_props.containsKey("namespace.goslim_generic")
				|| !pingo_props.containsKey("genedescription_def") || !pingo_props.containsKey("ontology.GO_COMPONENT")
				|| !pingo_props.containsKey("ontology.GOSLIM_YEAST")
				|| !pingo_props.containsKey("namespace.molecular_function") || !pingo_props.containsKey("star_def")
				|| !pingo_props.containsKey("tab_def") || !pingo_props.containsKey("signif_def")
				|| !pingo_props.containsKey("annotation_file_def") || !pingo_props.containsKey("namespace.goslim_pir")
				|| !pingo_props.containsKey("ontology_default") || !pingo_props.containsKey("namespace.goslim_candida")
				|| !pingo_props.containsKey("filter_go_def")
				|| !pingo_props.containsKey("namespace.biological_process")
				|| !pingo_props.containsKey("namespace.goslim_plant") || !pingo_props.containsKey("categories_def")
				|| !pingo_props.containsKey("namespace.goslim_yeast") || !pingo_props.containsKey("refset_def")
				|| !pingo_props.containsKey("ontology.GO_FUNCTION") || !pingo_props.containsKey("namespace_def")
				|| !pingo_props.containsKey("graph_def") || !pingo_props.containsKey("start_go_def")
				|| !pingo_props.containsKey("ontology.GO_FULL") || !pingo_props.containsKey("ontology.GOSLIM_PLANTS")
				|| !pingo_props.containsKey("ontology.GOSLIM_GOA") || !pingo_props.containsKey("namespace.goslim_goa")
				|| !pingo_props.containsKey("ontology_file_def")) {
			throw new Exception();
		}
	}

}
