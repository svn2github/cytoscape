/**
 * Populate a <code>Project</code> object from a valid XML document.
 *
 * @author Paul Shannon
 * @author Iliana Avila
 */
package phenotypeGenetics.xml;

import phenotypeGenetics.*;
import cytoscape.*;
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.*;
import java.util.jar.*;
import java.net.*;
import javax.swing.JOptionPane;

public class ProjectXmlReader { 

  public static final String NAME = "name";
  public static final String ORGANISM = "organism";
  public static final String NOTE = "note";
  public static final String EXPERIMENT = "experiment";
  public static final String CONDITION = "condition";
  public static final String OBSERVATIONS = "observations";
  public static final String PHENOTYPE = "phenotype";
  public static final String DISCRETE_PHENOTYPES = "discrete_phenotypes";
  public static final String CATEGORY = "category";
  public static final String GENETIC = "genetic";
  public static final String ENVIRONMENTAL = "environmental";
  public static final String ALLELE_FORM = "alleleForm";
  public static final String GENOTYPE = "genotype";
  public static final String LF = "lf";
  public static final String LF_PARTIAL = "lf(partial)";
  public static final String GF = "gf";
  public static final String GF_PARTIAL = "gf(partial)";
  public static final String DN = "dn";
  public static final String ALLELE = "allele";
  public static final String MANIPULATION = "manipulation";
  public static final String GENE = "gene";
  public static final String VALUE = "value";
  protected String xmlFilename;
  protected Project project;

  /**
   * A Map from phenotype names to their possible DISCRETE values.  
   * For example,
   * "cell_aliveness" -> ("dead","alive")
   */
  protected Map discretePhenotypes;

  /** 
   * @param xml_filename the path of the XML file to read
   */
  public ProjectXmlReader (String xml_filename) throws Exception{
    
    this.discretePhenotypes = new HashMap();

    if( xml_filename.startsWith("jar://") ){
      this.xmlFilename = readJarFileLikeCytoscape2x(xml_filename);
      // Cytoscape 2.0 throws an exception here:
      // C2.1 does not seem to have a TextJarReader
      //TextJarReader tjReader = new TextJarReader(xmlFilename);
      //tjReader.read();
      //File temp = File.createTempFile("phenotypeGenetics", ".xml");
      //BufferedWriter out = new BufferedWriter(new FileWriter(temp));
      //out.write(tjReader.getText());
      //out.close();
      //this.xmlFilename = temp.getPath();
          
    }else if(xml_filename.startsWith("http://")){
    
      URL url = new URL(xml_filename);
      this.xmlFilename = url.getPath();
      
      //System.out.println("---- URL = " + url + 
      //                 "----- xmlFilename = " + this.xmlFilename);
      // This gives permission errors (of the kind that people describe
      // in Cytoscape discuss mailing group):
      //Object content = url.getContent();
      //System.out.println("content = " + content);
    
    }else{
      this.xmlFilename = xml_filename;
    }
    
  }//ProjectXmlReader

  /**
   * Reads the given jar file in the same way that Cytoscape2.x does
   * This method should be eliminated once Cytoscape2.1 is released.
   * NOTE: THIS DOES NOT WORK EITHER!!!
   */
  protected String readJarFileLikeCytoscape2x (String jar_filename) throws IOException{
    
    // set up
    StringBuffer sb = new StringBuffer();
    String filename = jar_filename.substring(6);
    
    ClassLoader cl = ProjectXmlReader.this.getClass().getClassLoader();
    // --- THIS IS NOT WORKING  --------
    URL url = cl.getResource(filename);
    if(url == null){
      System.out.println("url is null");
    }
    // ---------------------------------
    JarURLConnection juc = (JarURLConnection)url.openConnection();
    JarFile jarFile = juc.getJarFile();
    InputStream is = jarFile.getInputStream(jarFile.getJarEntry(filename));
    InputStreamReader reader = new InputStreamReader(is);
    
    // read
    //System.out.println ("-- reading " + filename);
    char [] cBuffer = new char [1024];
    int bytesRead;
    while ((bytesRead = reader.read(cBuffer,0,1024)) != -1){
      sb.append (new String(cBuffer, 0, bytesRead));
    }//while

    File temp = File.createTempFile("phenotypeGenetics", ".xml");
    BufferedWriter out = new BufferedWriter(new FileWriter(temp));
    out.write(sb.toString());
    out.close();
    
    return temp.getPath();
  
  }//readJarFileLikeCytoscape2x
 
  /**
   *
   */
  public void read () throws Exception{
    
    SAXBuilder builder = new SAXBuilder(); 
    Document doc = builder.build(new File(xmlFilename));
    Element root = doc.getRootElement();
    ListIterator iterator;

    String name = root.getChild(NAME).getText().trim();
    String organism = root.getChild(ORGANISM).getText().trim();
    this.project = new Project(name, organism);

    List notes = root.getChildren(NOTE);
    if(notes != null) {
      iterator = notes.listIterator();
      while(iterator.hasNext()) {
        Element noteE =(Element) iterator.next();
        String note = noteE.getText().trim();
        this.project.addNote(note);
      } // while
    } // if notes

    List experiments = root.getChildren(EXPERIMENT);
    if(experiments != null) {
      iterator = experiments.listIterator();
      while(iterator.hasNext()) {
        Element experiment =(Element) iterator.next();
        this.project.addExperiment(parseExperiment(experiment));
      } // while
    } // if experiments

    // read any existing discrete phenotypes:
    List discretePhenos = null;
    Element dp = root.getChild(DISCRETE_PHENOTYPES);
    if(dp != null){
      discretePhenos = dp.getChildren(PHENOTYPE);
    }
    Map phenotypeToBins = new HashMap();
    if(discretePhenos != null){
      //System.out.println("There are " + discretePhenos.size() + " discrete phenos.");
      iterator = discretePhenos.listIterator();
      while(iterator.hasNext()){
        Element discretePheno = (Element)iterator.next();
        String phenoName = discretePheno.getChild(NAME).getText().trim();
        System.out.println(phenoName);
        ArrayList bins = parseDiscretePhenotype(discretePheno);
        if(bins.size() > 0){
          System.out.println("Adding bins for phenotype " + phenoName);
          phenotypeToBins.put(phenoName,bins);
        }
      }//while
    }// if discretePhenos
    
    Iterator it = this.discretePhenotypes.keySet().iterator();
    DiscretePhenotypeRanking discretePhenoRanking = new DiscretePhenotypeRanking();
    while(it.hasNext()){
      
      String discretePheno = (String)it.next();
      ArrayList bins = (ArrayList)phenotypeToBins.get(discretePheno);
      
      if(bins == null){
        // ranking not defined
        System.out.println("Ranking for " + discretePheno + "  not defined.");
        ArrayList values = (ArrayList)this.discretePhenotypes.get(discretePheno);
        String [] valArray = (String[])values.toArray(new String[0]);
        discretePhenoRanking.setUnrankedPhenotypeValues(discretePheno,valArray);
      }else{
        System.out.println("Ranking for " + discretePheno + " DEFINED.");
        discretePhenoRanking.setPhenotypeRanking(discretePheno,bins);
      }
          
    }//while it
    
    //System.out.println("Setting DiscretePhenotypeRanking in project " + name);
    project.setDiscretePhenotypeRanks(discretePhenoRanking);

  }//read

  /**
   * @return an ArrayList of ArrayLists of Strings
   * @throws IllegalStateException if the rank of one of the values is not correct
   * (for example, the rank is larger than the number of discrete values - 1) or if
   * there are gaps in the ranking (for example 0,2,3,4 the 1 is not there!), or if
   * one of the ranks is not numerical (must be digit between 0 and max-1)
   */
  protected ArrayList parseDiscretePhenotype (Element root) throws IllegalStateException{
    
    String phenotypeName = root.getChild(NAME).getText().trim();
    List discreteVals = root.getChildren(VALUE);
    
    if(discreteVals == null){
      System.out.println("parseDiscretePhenotype returning empty array");
      return new ArrayList();
    }
    
    // create array of arrays of the correct size
    int numVals = discreteVals.size();
    System.out.println("Number of discrete values for " + phenotypeName+ " is " + numVals);
    ArrayList bins = new ArrayList(numVals);
    for(int i = 0; i < numVals; i++){
      bins.add(i,new ArrayList());
    }//for i
    
    // ranks start at zero, if each value has a different rank, then the max
    // is the number of values - 1 (possible ranks are 0,1,2..max-1)
    int maxRank = numVals - 1;
    
    // read discrete values and their ranks
    ListIterator it = discreteVals.listIterator();
    while(it.hasNext()){
      Element discreteValue = (Element)it.next();
      String name = discreteValue.getChild(NAME).getText().trim();
      String value = discreteValue.getChild(VALUE).getText().trim();
      System.out.println("name = " + name + " value = " + value);
      if(Utilities.isNumerical(value)){
        double rank = Utilities.parseNumerical(value);
        if(rank > maxRank){
          throw new IllegalStateException("XML error: The rank for discrete phenotype value " 
                                          + name + " is greater than " + maxRank + "!!!");
        }
        ArrayList aBin = (ArrayList)bins.get((int)rank);
        if(aBin.size() > 0){
          // this value and at least another value have the same rank
          // so maxRank is one less
          maxRank--;
        }
        System.out.println("adding " + name + " to aBin");
        aBin.add(name);
      }else{
        // it is not numerical
        throw new IllegalStateException("XML Error: Found a rank that is not a number," +
                                        " phenotype = " + phenotypeName);
      }//if !isNumerical
    }//whie it.hasNext

    // check to see that there are no gaps in the ranking
    int size = bins.size();
    for(int i = 0; i < bins.size(); i++){
      if(bins.get(i) == null){
        throw new IllegalStateException("XML error: Found gap in ranks for discrete phenotype "
                                        + phenotypeName + " at " + i);
      }
    }//for i
    
    return bins;
    
  }//parseDiscretePhenotype
  
  /**
   *
   */
  protected Experiment parseExperiment (Element root){
    String name = root.getAttribute(NAME).getValue();
    Experiment experiment = new Experiment(name);
   
    List notes = root.getChildren(NOTE);
    if(notes != null) {
      ListIterator iterator = notes.listIterator();
      while(iterator.hasNext()) {
        Element noteE =(Element) iterator.next();
        String note = noteE.getText().trim();
        experiment.addNote(note);
      } // while
    } // if notes

    List conditions = root.getChildren(CONDITION);
    if(conditions != null) {
      ListIterator iterator = conditions.listIterator();
      while(iterator.hasNext()) {
        Condition condition = parseCondition((Element) iterator.next());
        experiment.addCondition(condition);
      } // while
    } // if conditions

    Element observations = root.getChild(OBSERVATIONS);
    List phenotypes = observations.getChildren(PHENOTYPE);
    if(phenotypes != null) {
      ListIterator iterator = phenotypes.listIterator();
      while(iterator.hasNext()) {
        Phenotype phenotype = parsePhenotype((Element) iterator.next());
        experiment.addObservation(phenotype);
      } // while
    } // if phenotypes

    return experiment;

  } // parseExperiment

  /**
   *
   */
  protected Condition parseCondition(Element root){
    Attribute attrib;
    Condition condition = new Condition();

    attrib = root.getAttribute(CATEGORY);
    if(attrib != null) {
      String category = attrib.getValue();
      if(category != null && category.length() > 0) {
        if(category.equals(GENETIC))
          condition.setCategory(Condition.GENETIC);
        else if(category.equals(ENVIRONMENTAL))
          condition.setCategory(Condition.ENVIRONMENTAL);
      } // if good value
    } // if category attribute

    attrib = root.getAttribute(ALLELE_FORM);
    // Try the old attribute name
    if(attrib == null) {
      attrib = root.getAttribute(GENOTYPE);
    }
    if(attrib != null) {
      String alleleForm = attrib.getValue();
      if(alleleForm != null && alleleForm.length() > 0) {
        if(alleleForm.equals(LF))
          condition.setAlleleForm(Condition.LF);
        else if(alleleForm.equals(LF_PARTIAL))
          condition.setAlleleForm(Condition.LF_PARTIAL);
        else if(alleleForm.equals(GF))
          condition.setAlleleForm(Condition.GF);
        else if(alleleForm.equals(GF_PARTIAL))
          condition.setAlleleForm(Condition.GF_PARTIAL);
        else if(alleleForm.equals(DN))
          condition.setAlleleForm(Condition.DN);
      } // if good value
    } // if alleleForm attribute

    attrib = root.getAttribute(ALLELE);
    // Try the old attribute name
    if(attrib == null) {
      attrib = root.getAttribute(MANIPULATION);
    }
    if(attrib != null) {
      String allele = attrib.getValue();
      condition.setAllele(allele);
    } // if allele attribute

    attrib = root.getAttribute(GENE);
    if(attrib != null) {
      String gene = attrib.getValue();
      if(gene != null && gene.length() > 0)
        condition.setGene(gene);
    } // if gene attribute

    // now parse obligatory pairs:  name=xxxx  value=yyyy
    // assign these only if both are present
    attrib = root.getAttribute(NAME);
    if(attrib != null) {
      String name = attrib.getValue();
      if(name != null && name.length() > 0) {
        attrib = root.getAttribute(VALUE);
        if(attrib != null) {
          String value = attrib.getValue();
          if(value != null && value.length() > 0) {
            condition.setName(name);
            condition.setValue(value);
          } // if good value (and thus, all conditions met)
        } // if value attrib
      } // if good name
    } // if name attrib

    return condition;

  } // parseCondition
  
  /**
   *
   */
  protected Phenotype parsePhenotype (Element root){
    
    Attribute attrib;
    Phenotype phenotype = new Phenotype();

    // parse obligatory pairs:  name=xxxx  value=yyyy
    // assign these only if both are present

    attrib = root.getAttribute(NAME);
    if(attrib != null) {
      String name = attrib.getValue();
      if(name != null && name.length() > 0) {
        attrib = root.getAttribute(VALUE);
        if(attrib != null) {
          String value = attrib.getValue();
          if(value != null && value.length() > 0) {
            phenotype.setName(name);
            phenotype.setValue(value);
            
            if( !(Utilities.isNumerical(value)) ){
              
              List values;
              
              if( !this.discretePhenotypes.containsKey(name) ){
                values = new ArrayList();
                this.discretePhenotypes.put(name, values);
              }else{
                values = (List)this.discretePhenotypes.get(name);
              }
              if( !values.contains(value) ){
                System.out.println("discrete val = " + value);
                values.add(value);
              }

            }// if !numerical

          } // if good value (and thus, all conditions met)
        } // if value attrib
      } // if good name
    } // if name attrib

    return phenotype;
  }//parsePhenotype
  
  /**
   *
   */
  public Project getProject(){
    return this.project;
  }//getProject
    
  /**
   * Sequentially reads each XML file and concatenates its data into a <code>Project</code>
   * @return a <code>Project</code>
   */
  public static Project readProject (String [] xml_project_files){
    Project project = new Project();
    try{
      ProjectXmlReader reader;
      Project newProject;
      
      System.out.println("Reading files for project...");
      for(int i = 0; i < xml_project_files.length; i++){
        reader = new ProjectXmlReader(xml_project_files[i]);
        reader.read();
        System.out.println("file: " + xml_project_files[i]);
        newProject = reader.getProject();
        project.concatenate(newProject);
      }//for i
      
    }catch(Exception e){
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                    "Error", 
                                    "There was an error while reading XML files.", 
                                    JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
    
    System.out.println("Done reading file project.");
    
    return project;
  }//readProject
  

  /**
   * Writes the information contained in the given DiscretePhenotypeRanking
   * to the given file
   */
  public static void writeDiscretePhenotypeInfo (String xml_file,
                                                 DiscretePhenotypeRanking discrete_ranking)
  throws Exception{
    SAXBuilder builder = new SAXBuilder(); 
    Document doc = builder.build(new File(xml_file));
    Element root = doc.getRootElement();
    
    Element old_dpElement = root.getChild(DISCRETE_PHENOTYPES);
    Element dpElement = new Element(DISCRETE_PHENOTYPES);
       
    String [] phenotypeNames = discrete_ranking.getPhenotypeNames();
    for(int i = 0; i < phenotypeNames.length; i++){
      Element phenotypeElement = new Element(PHENOTYPE);
      // the name
      Element phenotypeNameElement = new Element(NAME);
      phenotypeNameElement.setText(phenotypeNames[i]);
      phenotypeElement.addContent(phenotypeNameElement);
      
      // the discrete values
      String [] phenotypeVals = discrete_ranking.getUnrankedValues(phenotypeNames[i]);
      for(int j = 0; j < phenotypeVals.length; j++){
        Element phenotypeValueEl = new Element(VALUE);
        // the name
        Element valueNameEl = new Element(NAME);
        valueNameEl.setText(phenotypeVals[j]);
        phenotypeValueEl.addContent(valueNameEl);
        // the rank
        int rank = discrete_ranking.getRank(phenotypeNames[i], phenotypeVals[j]);
        Element valueRankEl = new Element(VALUE);
        valueRankEl.setText(Integer.toString(rank));
        phenotypeValueEl.addContent(valueRankEl);
        // add to the phenotype element
        phenotypeElement.addContent(phenotypeValueEl);
      }//for j
      dpElement.addContent(phenotypeElement);
    }//for i
    
    if(old_dpElement != null){
      root.removeContent(old_dpElement);
    }
    root.addContent(dpElement);
    // write
    
    XMLOutputter outp = new XMLOutputter();
    outp.setIndent("\t");
    outp.setNewlines(true);
    
    try{
      FileOutputStream stream = new FileOutputStream(xml_file); 
      outp.output(doc,stream);
    }catch(IOException exception){
      exception.printStackTrace();
    }
    
  }//writeDiscretePhenotypeInfo
  
}//class ProjectXmlReader
