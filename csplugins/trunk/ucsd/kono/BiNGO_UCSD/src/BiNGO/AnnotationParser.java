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
 * * Authors: Steven Maere
 * * Date: Apr.11.2005
 * * Description: Class that parses the annotation files in function of the chosen ontology.         
 **/

import cytoscape.data.annotation.Annotation;
import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.OntologyTerm;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


/**
 * ************************************************************
 * AnnotationParser.java
 * --------------------------
 * <p/>
 * Steven Maere (c) April 2005
 * <p/>
 * Class that parses the annotation files in function of the chosen ontology.
 * *************************************************************
 */


public class AnnotationParser implements Task {

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/

    /**
     * constant string for the loadcorrect of the filechooser.
     */
    private final String LOADCORRECT = "LOADCORRECT";
    /**
     * bingo dir path string
     */
    private String bingoDir;
    /**
     * string with path to some GO structure files.
     */
    private String fullGoPath;
    private String processGoPath;
    private String functionGoPath;
    private String componentGoPath;

    /**
     * annotation and ontology
     */
    private Annotation annotation;
    private Annotation parsedAnnotation;
    private Ontology ontology;
    private HashMap alias;
    /**
     * full ontology which is used for remapping the annotations to one of the default ontologies (not for custom ontologies)
     */
    private Ontology fullOntology;
    private HashMap synonymHash;
//    private String idOption;

    private BingoParameters params;

    /**
     * boolean loading correctly ?
     */
    private boolean status = true;
    /**
     * true if found annotation categories which are not in ontology
     */
    private boolean orphansFound = false;
    /**
     * false if none of the categories in the annotation match the ontology
     */
    private boolean consistency = false;

    // Keep track of progress for monitoring:
    private int maxValue;
    private TaskMonitor taskMonitor = null;
    private boolean interrupted = false;

    private HashSet parentsSet;

    /*--------------------------------------------------------------
    CONSTRUCTOR.
    --------------------------------------------------------------*/


    public AnnotationParser(BingoParameters params) {
        this.params = params;

 //       this.idOption = params.getIdentifier();
        this.bingoDir = params.getBingoDir();

        File tmp = new File(bingoDir, "BiNGO");
		
	this.fullGoPath = openResourceFile("GO_Full");
	this.processGoPath = openResourceFile("GO_Biological_Process");
	this.functionGoPath = openResourceFile("GO_Molecular_Function");
	this.componentGoPath = openResourceFile("GO_Cellular_Component");
	

        this.maxValue = -1;
    }

    public AnnotationParser(BingoParameters params, TaskMonitor taskMonitor) {
        this(params);
        this.taskMonitor = taskMonitor;
    }

    /*--------------------------------------------------------------
      METHODS.
    --------------------------------------------------------------*/

    private String openResourceFile(String name) {
        return getClass().getResource("/"+name).toString();
    }
	
    /**
     * method that governs loading and remapping of annotation files
     */
    public void calculate() {

        if (!params.isOntology_default()) {
            String loadOntologyString = setCustomOntology();

            // loaded a correct ontology file?
            if (!loadOntologyString.equals(LOADCORRECT)) {
                status = false;
                //System.out.println("Your ontology file contains errors " + loadOntologyString);
            }
            if(status == true){
                //check for cycles
                checkOntology(ontology);
                if(status = true){
                    String loadAnnotationString;
                    if (!params.isAnnotation_default()) {
                        loadAnnotationString = setCustomAnnotation();
                    } else {
                        loadAnnotationString = setDefaultAnnotation();
                    }

                    // loaded a correct annotation file?
                    if (!loadAnnotationString.equals(LOADCORRECT)) {
                        status = false;
                        //System.out.println("Your annotation file contains errors " + loadAnnotationString);
                    }
                    // annotation consistent with ontology ?
                    if ((status == true) && (consistency == false)) {
                        status = false;
                        Exception e = new Exception() ;
                        taskMonitor.setException(e, "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                        //System.out.println("None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                    }
                    if (status == true) {
                        parsedAnnotation = customRemap(annotation, ontology);
                    }
                }    
            }
        } else {
            String loadAnnotationString;
            String loadFullOntologyString = setFullOntology();
            if (!loadFullOntologyString.equals(LOADCORRECT)) {
                status = false;
                //System.out.println("Your full ontology file contains errors " + loadFullOntologyString);
            }
            if (status == true) {
                //check for cycles
                checkOntology(fullOntology);
                if (status == true) {
                    String loadOntologyString = setDefaultOntology(synonymHash);
                    if (!loadOntologyString.equals(LOADCORRECT)) {
                        status = false;
                        //System.out.println(loadOntologyString);
                    }
                    if (status == true) {
                        //check for cycles
                        checkOntology(ontology);                        
                        if (status == true) {
                            if (!params.isAnnotation_default()) {
                                loadAnnotationString = setCustomAnnotation();
                            } else {			
                                loadAnnotationString = setDefaultAnnotation();
                            }
            
                            // loaded a correct annotation file?
                            if (!loadAnnotationString.equals(LOADCORRECT)) {
                                status = false;
                            // System.out.println(loadAnnotationString);
                            }

                            if ((status == true) && (consistency == false)) {
                                status = false;
                                Exception e = new Exception();
                                taskMonitor.setException(e, "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                            //  System.out.println("None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                            }

                            if (status == true) {
                                // full remap not needed for non-Slim ontologies, instead custom remap
                                // bug 20/9/2005 changed annotationPanel to ontologyPanel
                                if (params.getOntologyFile().equals(fullGoPath) || params.getOntologyFile().equals(processGoPath) || params.getOntologyFile().equals(functionGoPath) || params.getOntologyFile().equals(componentGoPath)){
                                    parsedAnnotation = customRemap(annotation, ontology);
                                }
                                // full remap for Slim Ontologies
                                else {
                                    parsedAnnotation = remap(annotation, ontology);
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
     * Method that parses the custom annotation file into an annotation-object and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
    public String setCustomAnnotation() {

        String fileString = params.getAnnotationFile();
        annotation = null;

        String resultString = "";
        
        //if fileString contains "gene_association" then assume you're using GO Consortium annotation files
        if(fileString.contains("gene_association")){
            try {
                BiNGOConsortiumAnnotationReader readerAnnotation = new BiNGOConsortiumAnnotationReader(fileString, synonymHash, params, "Consortium", "GO");
                annotation = readerAnnotation.getAnnotation();
                if (readerAnnotation.getOrphans()) {
                    orphansFound = true;
                }
                if (readerAnnotation.getConsistency()) {
                    consistency = true;
                }
                alias = readerAnnotation.getAlias();
                resultString = LOADCORRECT;
            }
            catch (IllegalArgumentException e) {
                taskMonitor.setException(e, "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:");
                resultString = "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
            }
            catch (IOException e) {
                taskMonitor.setException(e, "Annotation file could not be located...");
                resultString = "Annotation file could not be located...";
            }
            catch (Exception e) {
                taskMonitor.setException(e, "");
                resultString = "" + e;
            }
        }        
        else{

            // flat file reader for custom annotation
            try {
                BiNGOAnnotationFlatFileReader readerAnnotation = new BiNGOAnnotationFlatFileReader(fileString, synonymHash);
                annotation = readerAnnotation.getAnnotation();
                if (readerAnnotation.getOrphans()) {
                   orphansFound = true;
                }
                if (readerAnnotation.getConsistency()) {
                    consistency = true;
                }
                alias = readerAnnotation.getAlias();
                resultString = LOADCORRECT;
            }
            catch (IllegalArgumentException e) {
                taskMonitor.setException(e, "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:");
                resultString = "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
            }
            catch (IOException e) {
                taskMonitor.setException(e, "Annotation file could not be located...");
                resultString = "Annotation file could not be located...";
            }
            catch (Exception e) {
                System.out.println(e);
                taskMonitor.setException(e, "");
                resultString = "" + e;
            }
        }
/*        else{
          annotation = params.getAnnotation();   
          alias = params.getAlias();
          resultString = LOADCORRECT;
          consistency = true;
        }
 */       
        
        return resultString;
    }

    /**
     * Method that parses the default annotation file into an annotation-object
     * given the choice of ontology and term identifier
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
    public String setDefaultAnnotation() {

        String fileString = params.getAnnotationFile();
        annotation = null;

        String resultString = "";

        // flat file
        try {
            BiNGOAnnotationDefaultReader readerAnnotation =
                    new BiNGOAnnotationDefaultReader(fileString, synonymHash, params, "default", "GO");
            annotation = readerAnnotation.getAnnotation();
            if (readerAnnotation.getOrphans()) {
                orphansFound = true;
            }
            if (readerAnnotation.getConsistency()) {
                consistency = true;
            }
            alias = readerAnnotation.getAlias();
            resultString = LOADCORRECT;
        } catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Annotation file parsing error, please check file format");
            resultString = "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
        }
        catch (IOException e) {
            taskMonitor.setException(e, "Annotation file could not be located...");
            resultString = "Annotation file could not be located...";
        }
        catch (Exception e) {
            System.out.println(e) ;
            taskMonitor.setException(e, "");
            resultString = "" + e;
        }

        return resultString;
    }

    /**
     * Method that parses the ontology file into an ontology-object and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
    public String setCustomOntology() {

        String fileString = params.getOntologyFile();
        ontology = null;
        synonymHash = null;
        String resultString = "";
        
        //if fileString == null use ontology from Cytoscape 
        if(fileString != null){
        
        // flat file.
        try {
            BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader(fileString);
            ontology = readerOntology.getOntology();
            synonymHash = readerOntology.getSynonymHash();
            resultString = LOADCORRECT;
        }
        catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Ontology file parsing error, please check file format");
            resultString = "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
        }
        catch (IOException e) {
            taskMonitor.setException(e, "Ontology file could not be located...");
            resultString = "Ontology file could not be located...";
        }
        catch (Exception e) {
            taskMonitor.setException(e, "");
            resultString = "" + e;
        }
        
        }
        else{
          ontology = params.getOntology();   
          resultString = LOADCORRECT;
        }

        return resultString;

    }


    /**
     * Method that parses the default ontology file into an ontology-object (using
     * the full Ontology synonymHash) and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */

    public String setDefaultOntology(HashMap synonymHash) {

        String fileString = params.getOntologyFile().toString();
        ontology = null;
        String resultString = "";

        // flat file.
        try {
            BiNGOOntologyDefaultReader readerOntology = new BiNGOOntologyDefaultReader(fileString, synonymHash);
            ontology = readerOntology.getOntology();
            resultString = LOADCORRECT;
        }
        catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Ontology file parsing error, please check file format");
            resultString = "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
        }
        catch (IOException e) {
            taskMonitor.setException(e, "Ontology file could not be located...");
            resultString = "Ontology file could not be located...";
        }
        catch (Exception e) {
            taskMonitor.setException(e, "");
            resultString = "" + e;
        }
        return resultString;

    }

    /**
     * Method that parses the ontology file into an ontology-object and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
    public String setFullOntology() {
        fullOntology = null;
        synonymHash = null;
        String resultString = "";

        // read full ontology.
        try {
            BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader(fullGoPath);
            fullOntology = readerOntology.getOntology();
            synonymHash = readerOntology.getSynonymHash();
            resultString = LOADCORRECT;
        }
        catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Full ontology file parsing error, please check file format");
            resultString = "FULL ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
        }
        catch (IOException e) {
            taskMonitor.setException(e, "Full ontology file could not be located...");
            resultString = "Full ontology file could not be located... ";
        }
        catch (Exception e) {
            taskMonitor.setException(e, "");
            resultString = "" + e;
        }
        return resultString;

    }


    public void checkOntology(Ontology ontology) {
        HashMap ontMap = ontology.getTerms();
        Iterator it = ontMap.keySet().iterator();
        while (it.hasNext()) {
            parentsSet = new HashSet();
            int childNode = new Integer(it.next().toString()).intValue();
            up_go(childNode, childNode, ontology);
        }
		//System.out.println("Ontology check finished") ;
    }


    /**
     * method for remapping annotation to reduced ontology e.g. GOSlim, and explicitly including genes in all parental categories
     */

    public Annotation remap(Annotation annotation, Ontology ontology) {
        Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(), annotation.getCurator());
        HashMap annMap = annotation.getMap();
        Iterator it = annMap.keySet().iterator();
        maxValue = annMap.keySet().size();
        int currentProgress = 0;
        try {
            while (it.hasNext()) {
                // Calculate Percentage.  This must be a value between 0..100.
                int percentComplete = (int) (((double) currentProgress / maxValue) * 100);

                //  Estimate Time Remaining
                long timeRemaining = maxValue - currentProgress;

                //  Update the Task Monitor.
                //  This automatically updates the UI Component w/ progress bar.
                if (taskMonitor != null) {
                    taskMonitor.setPercentCompleted(percentComplete);
                    taskMonitor.setStatus("Parsing Annotation " + currentProgress + " of " + maxValue);
                    taskMonitor.setEstimatedTimeRemaining(timeRemaining);
                }

                currentProgress++;

                parentsSet = new HashSet();
                String node = it.next() + "";
                // array with go labels for gene it.next().
                int [] goID;
                goID = annotation.getClassifications(node);
                for (int t = 0; t < goID.length; t++) {
                    if (ontology.getTerm(goID[t]) != null) {
                        parsedAnnotation.add(node, goID[t]);
                    }
                    // all parent classes of GO class that node is assigned to are also explicitly included in classifications
                    //CHECK IF goID EXISTS IN fullOntology...
                    if (fullOntology.getTerm(goID[t]) != null) {
                        up(node, goID[t], parsedAnnotation, ontology, fullOntology);
                    } else {
			//System.out.println ("Orphan found " + goID[t]) ; 
                        orphansFound = true;
                    }
                }
                if (interrupted) {
                    status = false;
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException e) {
            taskMonitor.setException(e, "Annotation Parse cancelled");
        }
        return parsedAnnotation;
    }

    /**
     * method for explicitly including genes in custom annotation in all parental categories of custom ontology
     */

    public Annotation customRemap(Annotation annotation, Ontology ontology) {
        Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(), annotation.getCurator());
        HashMap annMap = annotation.getMap();
        Iterator it = annMap.keySet().iterator();
        maxValue = annMap.keySet().size();
        int currentProgress = 0;
        try {
            while (it.hasNext()) {
                // Calculate Percentage.  This must be a value between 0..100.
                int percentComplete = (int) (((double) currentProgress / maxValue) * 100);

                //  Estimate Time Remaining
                long timeRemaining = maxValue - currentProgress;

                //  Update the Task Monitor.
                //  This automatically updates the UI Component w/ progress bar.
                if (taskMonitor != null) {
                    taskMonitor.setPercentCompleted(percentComplete);
                    taskMonitor.setStatus("Parsing Annotation: " + currentProgress + " of " + maxValue);
                    taskMonitor.setEstimatedTimeRemaining(timeRemaining);
                }

                currentProgress++;

                parentsSet = new HashSet();
                String node = it.next() + "";
                // array with go labels for gene it.next().
                int [] goID;
                goID = annotation.getClassifications(node);
                for (int t = 0; t < goID.length; t++) {
                    if (ontology.getTerm(goID[t]) != null) {
                        parsedAnnotation.add(node, goID[t]);
                        // 200905 NEXT LINE WITHIN LOOP <-> REMAP IN ORDER TO AVOID TRYING TO PARSE LABELS NOT DEFINED IN 'ONTOLOGY'...
                        // all parent classes of GO class that node is assigned to are also explicitly included in classifications
                        up(node, goID[t], parsedAnnotation, ontology, ontology);
                    }
                }
                if (interrupted) {
                    status = false;
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException e) {
            taskMonitor.setException(e, "Annotation Parse cancelled");            
        }
        return parsedAnnotation;
    }


    /**
     * method for recursing through tree to root
     */

    public void up(String node, int id, Annotation parsedAnnotation, Ontology ontology, Ontology flOntology) {
        OntologyTerm child = flOntology.getTerm(id);
        int [] parents = child.getParentsAndContainers();
        for (int t = 0; t < parents.length; t++) {
            if (!parentsSet.contains(new Integer(parents[t]))) {
                parentsSet.add(new Integer(parents[t]));
                if (ontology.getTerm(parents[t]) != null) {
                    parsedAnnotation.add(node, parents[t]);                            
                }
                up(node, parents[t], parsedAnnotation, ontology, flOntology);  
		//else{System.out.println("term not in ontology: "+ parents[t]);}
            }
        }
        
    }

    /**
     * method for recursing through tree to root and detecting cycles
     */

    public void up_go(int startID, int id, Ontology ontology) {
        OntologyTerm child = ontology.getTerm(id);
        int [] parents = child.getParentsAndContainers();
		//if (startID == 6906){for (int t = 0; t < parents.length; t++) {System.out.println(id + "\t" + parents[t]);}}
        for (int t = 0; t < parents.length; t++) {
            if (parents[t] == startID) {
                status = false;
                Exception e = new Exception();
                taskMonitor.setException(e, "Your ontology file contains a cycle at ID " + startID);                
                //System.out.println("Your ontology file contains a cycle at ID " + startID);
            } else if (!parentsSet.contains(new Integer(parents[t]))) {
                 if (ontology.getTerm(parents[t]) != null) {
                    parentsSet.add(new Integer(parents[t]));
                    up_go(startID, parents[t], ontology);
                 }    
                 else{System.out.println("term not in ontology: "+ parents[t]);}
            }
        }
    }

    /*--------------------------------------------------------------
      GETTERS.
    --------------------------------------------------------------*/

    /**
     * @return the parsed annotation
     */

    public Annotation getAnnotation() {
        return parsedAnnotation;
    }

    /**
     * @return the ontology
     */

    public Ontology getOntology() {
        return ontology;
    }
    
    public HashMap getAlias(){
        return alias;
    }

    /**
     * @return true if there are categories in the annotation which are not found in the ontology
     */
    public boolean getOrphans() {
        return orphansFound;
    }

    /**
     * @return the parser status : true if OK, false if something's wrong
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Run the Task.
     */
    public void run() {
        calculate();
    }

    /**
     * Non-blocking call to interrupt the task.
     */
    public void halt() {
        this.interrupted = true;
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) {
        if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return human readable task title.
     */
    public String getTitle() {
        return new String("Parsing Annotation");
    }


}

