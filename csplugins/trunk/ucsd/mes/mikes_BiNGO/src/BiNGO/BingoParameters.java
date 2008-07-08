package BiNGO;

import cytoscape.data.annotation.Annotation;
import cytoscape.data.annotation.Ontology;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.jar.*;
import java.net.*;
import cytoscape.*;

/**
 * Created by
 * User: risserlin
 * Date: Jun 12, 2006
 * Time: 8:30:34 AM
 */
public class BingoParameters {

    //Bingo properties
    private Properties bingo_props;

    private String bingoDir;

    //parameters extracted from the Bingo interface
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
    //private String identifier;

    //annotation and ontology files
    private String annotationFile;
    private String ontologyFile;
    private Annotation annotation;
    private Ontology ontology;
    private HashSet deleteCodes;
    private HashMap<String,HashSet<String>> alias;

    private AnnotationParser annParser;
    //indicates whether we are using the default files (if false then we are using
    // custom files)
    private boolean annotation_default;
    private boolean ontology_default;

    private boolean fileoutput;
    private String fileoutput_dir;

    //Variables that hold the species, the identifiers and ontologies
    //That are available to use for any computation.
    //These are dictated by what is in the properties file.
    private TreeMap speciesfileHash;
    private TreeMap filespeciesHash;
    //private TreeMap identifierHash;
    private TreeMap ontologyHash;
    private int number_species;
    private int number_identifiers;
    private int number_ontology;

    //stores list of selected nodes and the all nodes in the reference set
    private HashSet selectedNodes;
    private HashSet allNodes;

    public BingoParameters(String bingoDir) throws IOException {

        this.bingoDir = bingoDir;
        this.deleteCodes = new HashSet() ;

        bingo_props = new Properties();
        //Open the properties file
        try {
            bingo_props.load(JarReader("bingo_gui.properties"));
        } catch (IOException e) {
            //throw e;
            String propfile_path = openResourceFile("bingo_gui.properties");
            System.out.println("can't find the path of the properties file" + propfile_path);
        }
        initializeSpeciesHash();
        //initializeIdentifierHash();
        initializeOntologyHash();

        //Give default values to main parameters
        test = BingoAlgorithm.HYPERGEOMETRIC;
        correction = BingoAlgorithm.BENJAMINI_HOCHBERG_FDR;
        significance = new BigDecimal(0.05);
        category =BingoAlgorithm.CATEGORY_CORRECTION;
        referenceSet = BingoAlgorithm.GENOME;

    }
    
    private String openResourceFile(String name) {
        return getClass().getResource("/"+name).toString();
    }
    
    public InputStream JarReader (String name) throws IOException {
        URL url = getClass().getResource("/"+name);
        JarURLConnection juc = (JarURLConnection) url.openConnection ();
        JarFile jarFile = juc.getJarFile();
        InputStream is = jarFile.getInputStream (jarFile.getJarEntry(name));
        return is;
    } 

    public void initializeSpeciesHash() {
        Object f;
        String property_value;
        this.speciesfileHash = new TreeMap();
        this.filespeciesHash = new TreeMap();
        number_species = 0;
        for (Enumeration e = bingo_props.propertyNames(); e.hasMoreElements();) {
            f = e.nextElement();

            //Check to see if the element is part of the species subset
            if (f.toString().indexOf("species.") != -1 ) {
                    //.contains("species.")) {
                number_species++;
                property_value = bingo_props.getProperty(f.toString());
                String filename = property_value;
                //Take the & sign out key and replace with a space
                String key = f.toString().substring(8);
                String [] tokens_key = key.split("_");
                String formatted_key = "";
                int j= 0;
                for(; j<tokens_key.length-1;j++){
                    formatted_key = formatted_key + tokens_key[j] + " ";
                }
                formatted_key = formatted_key + tokens_key[j];
				System.out.println(formatted_key);

                speciesfileHash.put(formatted_key,filename);
                filespeciesHash.put(filename,formatted_key);
            }
        }
    }

    public String getSpeciesFilename(String specified_species){
        return (String)speciesfileHash.get(specified_species);
    }
    
    public String getSpeciesNameFromFilename(String filename){
        return (String)filespeciesHash.get(filename);
    }

    /*public void initializeIdentifierHash() {
        Object f;
        String property_value;
        this.identifierHash = new TreeMap();
        number_identifiers = 0;

        for (Enumeration e = bingo_props.propertyNames(); e.hasMoreElements();) {
            f = e.nextElement();

            //Check to see if the element is part of the identifier subset
            if (f.toString().indexOf("identifier.") != -1 ){
                    //(f.toString().contains("identifier.")) {
                number_identifiers++;
                property_value = bingo_props.getProperty(f.toString());
				System.out.println(property_value);
				System.out.println(f.toString().substring(11));
                identifierHash.put(f.toString().substring(11), property_value);
            }
        }
    }*/

    public void initializeOntologyHash() {
        Object f;
        String property_value;
        this.ontologyHash = new TreeMap();
        number_ontology = 0;

        for (Enumeration e = bingo_props.propertyNames(); e.hasMoreElements();) {
            f = e.nextElement();

            //Check to see if the element is part of the identifier subset
            if(f.toString().indexOf("ontology.") != -1 ){ 
                    //(f.toString().contains("ontology.")) {
                number_ontology++;
                property_value = bingo_props.getProperty(f.toString());
                ontologyHash.put(f.toString().substring(9), property_value);
            }
        }
    }

    public String[] getSpeciesLabels() {
        int label_num = 0;
        String [] labels = new String[number_species+2];
        //Add the None label
        labels[label_num] = BingoAlgorithm.NONE;
        label_num++ ;
        
        for (Iterator iter = speciesfileHash.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            labels[label_num] = (String) entry.getKey();
            label_num++;
        }
        //Add the Custom label
        labels[label_num] = BingoAlgorithm.CUSTOM;

        return labels;
    }

   /* public String[] getIdentifiersLabels() {
        int label_num = 0;
        String [] labels = new String[number_identifiers];

        for (Iterator iter = identifierHash.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            labels[label_num] = (String) entry.getValue();
            label_num++;
        }
        return labels;
    }*/

    /* Method to get the labels of the ontology files.
  * The labels are the values in the Ontology TreeMap.
    */
    public String[] getOntologyLabels() {
        int label_num = 0;
        String [] labels = new String[number_ontology+2];
        //Add the None label
        labels[label_num] = BingoAlgorithm.NONE;
        label_num++ ;

        //ontologyHash.values()
        for (Iterator iter = ontologyHash.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            labels[label_num] = (String) entry.getValue();
            label_num++;
        }

        //Add the Custom label
        labels[label_num] = BingoAlgorithm.CUSTOM;

        return labels;
    }

    public AnnotationParser getAnnParser() {
        return annParser;
    }

    public HashSet getDeleteCodes(){
        return deleteCodes;
    }
    
    public AnnotationParser initializeAnnotationParser() {
        annParser = new AnnotationParser(this);
        return annParser;
    }


    /*public void setIdentifierHash(TreeMap identifierHash) {
        this.identifierHash = identifierHash;
    }*/

    public TreeMap getOntologyHash() {
        return ontologyHash;
    }
    
    public HashMap getAlias(){
        return alias;
    }

  /*  public TreeMap getIdentifierHash() {
        return identifierHash;
    }*/


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

    /*public String getIdentifier() {
        return identifier;
    }*/

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

    public HashSet getSelectedNodes() {
        return selectedNodes;
    }

    public HashSet getAllNodes() {
        return allNodes;
    }

    public void setAllNodes(HashSet allNodes) {
        this.allNodes = allNodes;
    }

    public void setSelectedNodes(HashSet selectedNodes) {
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
    
    public void setAlias(HashMap alias){
        this.alias = alias;
    }
    
    public void setDeleteCodes(HashSet deleteCodes){
        this.deleteCodes = deleteCodes;
    }

    /*public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }*/

    public void setFileoutput(boolean fileoutput) {
        this.fileoutput = fileoutput;
    }

    public void setFileoutput_dir(String fileoutput_dir) {
        this.fileoutput_dir = fileoutput_dir;
    }
}
