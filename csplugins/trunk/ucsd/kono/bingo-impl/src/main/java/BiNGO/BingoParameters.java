package BiNGO;

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

import BiNGO.ontology.Annotation;
import BiNGO.ontology.Ontology;

/**
 * Created by User: risserlin Date: Jun 12, 2006 Time: 8:30:34 AM
 */
public class BingoParameters {

	// Bingo properties
	private Properties bingo_props;

	private String bingoDir;

	// parameters extracted from the Bingo interface
	private String cluster_name;
	private boolean textOrGraph;
	private String textInput;
	private String test;
	private String overOrUnder;
	private String visualization;
	private String correction;
	private BigDecimal significance;
	private String category;
	private String referenceSet;
	private String species;
	// private String identifier;

	// annotation and ontology files
	private String annotationFile;
	private String ontologyFile;
	private Annotation annotation;
	private Ontology ontology;
	private String namespace;
	private Set deleteCodes;
	private Map<String, Set<String>> alias;

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
	private Set selectedNodes;
	private Set allNodes;

	
	public BingoParameters(String bingoDir) throws IOException {

		this.bingoDir = bingoDir;
		this.deleteCodes = new HashSet();

		bingo_props = new Properties();
		// Open the properties file, try custom properties in /plugins folder first
		try {
			bingo_props.load(PropReader("bingo_gui.properties"));
		} catch (IOException e) {
			try {
				bingo_props.load(JarReader("bingo_gui.properties"));
			} catch (IOException e2) {
				// throw e;
				String propfile_path = openResourceFile("bingo_gui.properties");
				System.out.println("can't find default properties file" + propfile_path);
			}
		}
		initializeSpeciesHash();
		initializeOntologyHash();
		initializeNamespaceHash();

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
		File propFile = new File(bingoDir, name);
		FileInputStream is = new FileInputStream(propFile);
		return is;
	}

	public OutputStream PropWriter(String name) throws IOException {
		File propFile = new File(bingoDir, name);
		FileOutputStream os = new FileOutputStream(propFile);
		return os;
	}

	public void storeParameterSettings() throws IOException {
		OutputStream os = PropWriter("bingo_gui.properties");
		bingo_props.store(os, "");
		os.close();
	}

	public void initializeSpeciesHash() {
		Object f;
		String property_value;
		this.speciesfileHash = new TreeMap();
		this.filespeciesHash = new TreeMap();
		number_species = 0;
		for (Enumeration e = bingo_props.propertyNames(); e.hasMoreElements();) {
			f = e.nextElement();

			// Check to see if the element is part of the species subset
			if (f.toString().indexOf("species.") != -1) {
				// .contains("species.")) {
				number_species++;
				property_value = bingo_props.getProperty(f.toString());
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
				// System.out.println(formatted_key);

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

		for (Enumeration e = bingo_props.propertyNames(); e.hasMoreElements();) {
			f = e.nextElement();

			// Check to see if the element is part of the identifier subset
			if (f.toString().indexOf("ontology.") != -1) {
				// (f.toString().contains("ontology.")) {
				number_ontology++;
				property_value = bingo_props.getProperty(f.toString());
				ontologyHash.put(f.toString().substring(9), property_value);
			}
		}
	}

	public void initializeNamespaceHash() {
		Object f;
		String property_value;
		this.namespaceHash = new TreeMap();
		number_namespaces = 0;

		for (Enumeration e = bingo_props.propertyNames(); e.hasMoreElements();) {
			f = e.nextElement();

			// Check to see if the element is part of the identifier subset
			if (f.toString().indexOf("namespace.") != -1) {
				// (f.toString().contains("ontology.")) {
				number_namespaces++;
				property_value = bingo_props.getProperty(f.toString());
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
		annParser = new AnnotationParser(this, new HashSet<String>());
		return annParser;
	}

	public AnnotationParser initializeAnnotationParser(HashSet<String> genes) {
		annParser = new AnnotationParser(this, genes);
		return annParser;
	}

	public TreeMap getOntologyHash() {
		return ontologyHash;
	}

	public Map getAlias() {
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

	public String getTextInput() {
		return textInput;
	}

	public String getSpecies() {
		return species;
	}

	public boolean isOntology_default() {
		return ontology_default;
	}

	public String getBingoDir() {
		return bingoDir;
	}

	public boolean isTextOrGraph() {
		return textOrGraph;
	}

	public String getOverOrUnder() {
		return overOrUnder;
	}

	public String getVisualization() {
		return visualization;
	}

	public String getCategory() {
		return category;
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

	/*
	 * public String getIdentifier() { return identifier; }
	 */

	public boolean isFileoutput() {
		return fileoutput;
	}

	public String getFileoutput_dir() {
		return fileoutput_dir;
	}

	public Properties getBingo_props() {
		return bingo_props;
	}

	public boolean getTextOrGraph() {
		return textOrGraph;
	}

	public String getCluster_name() {
		return cluster_name;
	}

	public String getTest() {
		return test;
	}

	public String getCorrection() {
		return correction;
	}

	public BigDecimal getSignificance() {
		return significance;
	}

	public Set getSelectedNodes() {
		return selectedNodes;
	}

	public Set getAllNodes() {
		return allNodes;
	}

	public boolean getStatus() {
		return status;
	}

	public void setAllNodes(Set allNodes) {
		this.allNodes = allNodes;
	}

	public void setSelectedNodes(Set selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public void setBingoDir(String bingoDir) {
		this.bingoDir = bingoDir;
	}

	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}

	public void setTextOrGraph(boolean textOrGraph) {
		this.textOrGraph = textOrGraph;
	}

	public void setTextInput(String textInput) {
		this.textInput = textInput;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public void setOverOrUnder(String overOrUnder) {
		this.overOrUnder = overOrUnder;
	}

	public void setVisualization(String visualization) {
		this.visualization = visualization;
	}

	public void setCorrection(String correction) {
		this.correction = correction;
	}

	public void setSignificance(BigDecimal significance) {
		this.significance = significance;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setReferenceSet(String referenceSet) {
		this.referenceSet = referenceSet;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public void setAnnotationFile(String annotationFile) {
		this.annotationFile = annotationFile;
	}

	public void setOntologyFile(String ontologyFile) {
		this.ontologyFile = ontologyFile;
	}

	public void setAnnotation_default(boolean annotation_default) {
		this.annotation_default = annotation_default;
	}

	public void setOntology_default(boolean ontology_default) {
		this.ontology_default = ontology_default;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public void setNameSpace(String namespace) {
		this.namespace = namespace;
	}

	public void setAlias(Map alias) {
		this.alias = alias;
	}

	public void setDeleteCodes(HashSet deleteCodes) {
		this.deleteCodes = deleteCodes;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public void setFileoutput(boolean fileoutput) {
		this.fileoutput = fileoutput;
	}

	public void setFileoutput_dir(String fileoutput_dir) {
		this.fileoutput_dir = fileoutput_dir;
	}
}
