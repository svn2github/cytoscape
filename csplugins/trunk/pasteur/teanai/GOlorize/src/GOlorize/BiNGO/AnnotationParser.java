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
 * * Authors: Steven Maere
 * * Date: Apr.11.2005
 * * Description: Class that parses the annotation files in function of the chosen ontology.         
 **/

import java.util.*; 
import java.io.*;

import cytoscape.data.annotation.*;
import cytoscape.data.annotation.readers.*;
import cytoscape.data.readers.*;

import javax.swing.*;
import javax.swing.event.*;

/***************************************************************
 * AnnotationParser.java   
 * --------------------------
 *
 * Steven Maere (c) April 2005
 *
 * Class that parses the annotation files in function of the chosen ontology.  
 ***************************************************************/


public class AnnotationParser implements MonitorableTask {
    
    
    
    
	/*--------------------------------------------------------------
      FIELDS.
      --------------------------------------------------------------*/
	
	/** constant string for the loadcorrect of the filechooser.*/
	private final String LOADCORRECT = "LOADCORRECT";
	/** bingo dir path string*/
	private String bingoDir ;
	/** string with path to some GO structure files.*/
	private String fullGoPath ;
	private String processGoPath ;
	private String functionGoPath ;
	private String componentGoPath ;
		
	/** settingspanel as parent for displaying error messages	*/
	private SettingsPanel settingsPanel ;
		
	/** annotation and ontology */
	private Annotation annotation ;
	private Annotation parsedAnnotation ;
	private Ontology ontology ;
	/** full ontology which is used for remapping the annotations to one of the default ontologies (not for custom ontologies) */
	private Ontology fullOntology ;
	private HashMap synonymHash ;
	private String idOption ;
	
	/** annotation and ontology choosers inherited from SettingsPanelActionListener */
	private ChooseAnnotationPanel annotationPanel ;
	private ChooseOntologyPanel ontologyPanel ;
	
	/** boolean loading correctly ?*/
	private boolean status = true ;
	/** true if found annotation categories which are not in ontology */
	private boolean orphansFound = false ;
	/** false if none of the categories in the annotation match the ontology*/
	private boolean consistency = false ;
	
	// Keep track of progress for monitoring:
	protected int currentProgress;
	protected int lengthOfTask = -1;
	protected String statusMessage;
	protected boolean done;
	protected boolean canceled;
	
	private HashSet parentsSet ;
    
    
    
    
	/*--------------------------------------------------------------
      CONSTRUCTOR.
      --------------------------------------------------------------*/

	
	 public AnnotationParser (ChooseAnnotationPanel annotationPanel, ChooseOntologyPanel ontologyPanel, String idOption, SettingsPanel settingsPanel, String bingoDir){
		this.annotationPanel = annotationPanel ;
		this.ontologyPanel = ontologyPanel ;
		this.settingsPanel = settingsPanel ;
		this.idOption = idOption ;
		this.bingoDir = bingoDir ;
		File tmp = new File(bingoDir,"BiNGO") ;
		this.fullGoPath = (new File(tmp,"GO_Full")).toString() ;
		this.processGoPath = (new File(tmp,"GO_Biological_Process")).toString() ;
		this.functionGoPath = (new File(tmp,"GO_Molecular_Function")).toString() ;
		this.componentGoPath = (new File(tmp,"GO_Cellular_Component")).toString() ;
		this.currentProgress = 0;
		this.lengthOfTask = -1;
		this.done = false;
		this.canceled = false;
	 }
    
         public AnnotationParser (Annotation annotation, Ontology ontology){
             this.annotation=annotation;
             this.ontology=ontology;
             checkOntology(ontology) ;
             System.out.println("j'ai checke");
             parsedAnnotation = customRemap(annotation,ontology);
             System.out.println("j'ai remappe");
         }
    
    
    
	/*--------------------------------------------------------------
		METHODS.
      --------------------------------------------------------------*/	
		
	/**
	 * method that governs loading and remapping of annotation files
	 */
	public void calculate(){

		this.currentProgress = 0;
		this.lengthOfTask = -1;
		this.done = false;
		this.canceled = false;

		
		if(!ontologyPanel.getDefault()){
			String loadOntologyString = setCustomOntology();
				
			// loaded a correct ontology file?		
			if(!loadOntologyString.equals(LOADCORRECT)){
				status = false ;
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       "Your ontology file contains errors " + loadOntologyString);
	      	}
			
			//check for cycles
			checkOntology(ontology) ;
			
			String loadAnnotationString ;
			if(!annotationPanel.getDefault()){
				loadAnnotationString = setCustomAnnotation();
			}
			else{		
				loadAnnotationString = setDefaultAnnotation(idOption);
			}				
			
			// loaded a correct annotation file?
			if (!loadAnnotationString.equals(LOADCORRECT)){
				status = false ;	
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       "Your annotation file contains errors " + loadAnnotationString);
	      	}
			// annotation consistent with ontology ?
			if((status == true) && (consistency == false)){
				status = false ;	
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
			}
			if (status == true){
				parsedAnnotation = customRemap(annotation, ontology) ;
			}			
		}
		
		else{
			String loadAnnotationString ;
			String loadFullOntologyString = setFullOntology();
			if(!loadFullOntologyString.equals(LOADCORRECT)){
				status = false ;	
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       "Your full ontology file contains errors " + loadFullOntologyString);
			}
			
			//check for cycles
			checkOntology(fullOntology) ;
			
			String loadOntologyString = setDefaultOntology(synonymHash);
			if(!loadOntologyString.equals(LOADCORRECT)){
				status = false ;	
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       loadOntologyString);
			}
			
			//check for cycles
			checkOntology(ontology) ;
			
			if(!annotationPanel.getDefault()){
				loadAnnotationString = setCustomAnnotation();
			}
			else{
				loadAnnotationString = setDefaultAnnotation(idOption);
			}	
			
			// loaded a correct annotation file?
			if (!loadAnnotationString.equals(LOADCORRECT)){
				status = false ;	
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       loadAnnotationString);
			}
			
			if((status == true) && (consistency == false)){
				status = false ;	
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
			}
			
			if (status == true){
				// full remap not needed for non-Slim ontologies, instead custom remap
				// bug 20/9/2005 changed annotationPanel to ontologyPanel
				if(ontologyPanel.getFile().toString().equals(fullGoPath) || ontologyPanel.getFile().toString().equals(processGoPath) || ontologyPanel.getFile().toString().equals(functionGoPath) || ontologyPanel.getFile().toString().equals(componentGoPath)){
					parsedAnnotation = customRemap(annotation, ontology) ;
				}
				// full remap for Slim Ontologies
				else{parsedAnnotation = remap(annotation, ontology) ;}
			}
		}	

		this.done = true;
		this.currentProgress = this.lengthOfTask;
		
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
	public String setCustomAnnotation(){

		String fileString = annotationPanel.getFile().toString();
		annotation = null;

		String resultString = "";

		// flat file reader for custom annotation
		try{
			BiNGOAnnotationFlatFileReader readerAnnotation = new BiNGOAnnotationFlatFileReader (new File (fileString), synonymHash, settingsPanel);
			annotation = readerAnnotation.getAnnotation ();
			if(readerAnnotation.getOrphans()){orphansFound = true ;}
			if(readerAnnotation.getConsistency()){consistency = true ;}
			resultString = LOADCORRECT;
		}	
		catch (IllegalArgumentException e){
			resultString = "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
		}
		catch (IOException e){
			resultString = "Annotation file could not be located...\n" ;
		}
		catch (Exception e){
		    resultString = "" + e ;
		}	
		return resultString;
	}

	/**
     * Method that parses the default annotation file into an annotation-object 
	 * given the choice of ontology and term identifier
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
	public String setDefaultAnnotation(String idOption){

		String fileString = annotationPanel.getFile().toString();
		annotation = null;

		String resultString = "";

		// flat file
		try{
			BiNGOAnnotationDefaultReader readerAnnotation = 
				new BiNGOAnnotationDefaultReader (new File (fileString), synonymHash, settingsPanel, idOption, annotationPanel.getSelection(), ontologyPanel.getSelection(), "GO") ;
			annotation = readerAnnotation.getAnnotation ();
			if(readerAnnotation.getOrphans()){orphansFound = true ;}
			if(readerAnnotation.getConsistency()){consistency = true ;}
			resultString = LOADCORRECT;
		}
		catch (IllegalArgumentException e){
			resultString = "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
		}		
		catch (IOException e){
			resultString = "Annotation file could not be located...\n" + 
				"Possibly, you unzipped BiNGO in the wrong place (See FAQ on website)\n" ;
		}
        catch (Exception e){
		    resultString = "" + e ;
		}
		return resultString;
	}

    /**
     * Method that parses the ontology file into an ontology-object and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
	public String setCustomOntology(){

		String fileString = ontologyPanel.getFile().toString();
		ontology = null ;
		synonymHash = null ;
		String resultString = "";

		// flat file.
		try{
				BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader (new File (fileString));
				ontology = readerOntology.getOntology ();
				synonymHash = readerOntology.getSynonymHash() ;
				resultString = LOADCORRECT;
		}
		catch (IllegalArgumentException e){
			resultString = "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;	
		}
	  	catch (IOException e){
			resultString = "Ontology file could not be located...\n" ;
		}
	    catch (Exception e){
		    resultString = "" + e ;
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
		
	public String setDefaultOntology(HashMap synonymHash){

		String fileString = ontologyPanel.getFile().toString();
		ontology = null ;
		String resultString = "";

		// flat file.
		try{
				BiNGOOntologyDefaultReader readerOntology = new BiNGOOntologyDefaultReader (new File (fileString), synonymHash);
				ontology = readerOntology.getOntology ();
				resultString = LOADCORRECT;
		}
		catch (IllegalArgumentException e){
			resultString = "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;	
		}
		catch (IOException e){
			resultString = "Ontology file could not be located...\n" + 
				"Possibly, you unzipped BiNGO in the wrong place (See FAQ on website)\n" ;
		}	
	 	catch (Exception e){
		    resultString = "" + e ;
		}
		return resultString;

	}
	
	/**
     * Method that parses the ontology file into an ontology-object and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
	public String setFullOntology(){
		fullOntology = null ;
		synonymHash = null ;
		String resultString = "";

		// read full ontology.
		try{
				BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader (new File (fullGoPath));
				fullOntology = readerOntology.getOntology ();
				synonymHash = readerOntology.getSynonymHash() ;
				resultString = LOADCORRECT;
		}
		catch (IllegalArgumentException e){
			resultString = "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;	
		}
		catch (IOException e){
			resultString = "Ontology file could not be located...\n" + 
				"Possibly, you unzipped BiNGO in the wrong place (See FAQ on website)\n" ;
		}	
	 	catch (Exception e){
		    resultString = "" + e ;
		}
		return resultString;

	}
	
	
	public void checkOntology(Ontology ontology){
		HashMap ontMap = ontology.getTerms() ;
		Iterator it = ontMap.keySet().iterator() ;
		while(it.hasNext()){
			parentsSet = new HashSet() ;
			int childNode = new Integer(it.next().toString()).intValue() ;
			up_go(childNode, childNode, ontology) ;	
		}	
	}
	
	
	/**
	* method for remapping annotation to reduced ontology e.g. GOSlim, and explicitly including genes in all parental categories
	*/
	
    public Annotation remap(Annotation annotation, Ontology ontology){
		Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(), annotation.getCurator());
		HashMap annMap = annotation.getMap() ;
		Iterator it = annMap.keySet().iterator() ;
		lengthOfTask = annMap.keySet().size() ;
		while(it.hasNext()){
			currentProgress++ ;
			parentsSet = new HashSet() ;
			String node = it.next() + "" ;
			// array with go labels for gene it.next().
			int [] goID;
			goID = annotation.getClassifications(node);
			for (int t = 0; t < goID.length; t++){
				if(ontology.getTerm(goID[t]) != null){
					parsedAnnotation.add(node, goID[t]);
				}
				// all parent classes of GO class that node is assigned to are also explicitly included in classifications
				//CHECK IF goID EXISTS IN fullOntology...
				if(fullOntology.getTerm(goID[t]) != null){
				  up(node, goID[t], parsedAnnotation, ontology, fullOntology) ;	
				}
				else{
					orphansFound = true ;
				}	
			}	
		}	
		return parsedAnnotation ;
	}		
    
	/**
	* method for explicitly including genes in custom annotation in all parental categories of custom ontology
	*/
	
	public Annotation customRemap (Annotation annotation, Ontology ontology){
		Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(), annotation.getCurator());
		
                //System.out.println(annotation.getSpecies()+ annotation.getType()+ annotation.getCurator());//////////
                
                HashMap annMap = annotation.getMap() ;
		Iterator it = annMap.keySet().iterator() ;
		lengthOfTask = annMap.keySet().size() ;
		while(it.hasNext()){
			currentProgress++ ;
			parentsSet = new HashSet() ;
			String node = it.next() + "" ;
			// array with go labels for gene it.next().
			int [] goID;
			goID = annotation.getClassifications(node);
			for (int t = 0; t < goID.length; t++){
				if(ontology.getTerm(goID[t]) != null){
                                    
                                    //System.out.println("nouveau papa"+goID[t]);
                                    
					parsedAnnotation.add(node, goID[t]);
					// 200905 NEXT LINE WITHIN LOOP <-> REMAP IN ORDER TO AVOID TRYING TO PARSE LABELS NOT DEFINED IN 'ONTOLOGY'...
					// all parent classes of GO class that node is assigned to are also explicitly included in classifications
					up(node, goID[t], parsedAnnotation, ontology, ontology) ;	
			    }
			}	
		}	
		return parsedAnnotation ;
	}	
	
	
	/**
	* method for recursing through tree to root
	*/

	public void up (String node, int goID, Annotation parsedAnnotation, Ontology ontology, Ontology flOntology){	
		OntologyTerm child  = flOntology.getTerm(goID);	
		int [] parents =  child.getParentsAndContainers ();	
		for(int t = 0; t < parents.length; t++){
			if(!parentsSet.contains(new Integer(parents[t]))){
				parentsSet.add(new Integer(parents[t])) ;
				if(ontology.getTerm(parents[t]) != null){
					parsedAnnotation.add(node, parents[t]);
				}			
				up(node, parents[t], parsedAnnotation, ontology, flOntology);
			}	
		}			
	}	
	
 	/**
	* method for recursing through tree to root and detecting cycles
	*/

	public void up_go (int startID, int goID, Ontology ontology){	
		OntologyTerm child  = ontology.getTerm(goID);	
		int [] parents =  child.getParentsAndContainers ();	
		for(int t = 0; t < parents.length; t++){
			if(parents[t] == startID){
				status = false ;	
				done = true ;
				JOptionPane.showMessageDialog(settingsPanel,
	         		     				       "Your ontology file contains a cycle at ID " + startID);
			}	
			else if(!parentsSet.contains(new Integer(parents[t]))){
				parentsSet.add(new Integer(parents[t])) ;
				up_go(startID, parents[t],ontology);
			}
		}			
	}	
	
    
	/*--------------------------------------------------------------
		GETTERS.
      --------------------------------------------------------------*/	
	/**
	* @return the parsed annotation
	*/

	public Annotation getAnnotation(){
		return parsedAnnotation ;
	}
	
	/**
	* @return the ontology
	*/

	public Ontology getOntology(){
		return ontology ;
	}
	
	/**
	* @return true if there are categories in the annotation which are not found in the ontology
	*/
	public boolean getOrphans(){
		return orphansFound ;
	}
	
	/**
	* @return the parser status : true if OK, false if something's wrong
	*/	
	public boolean getStatus(){
		return status ;
	}	
  	/**
     * @return the current progress
     */
    public int getCurrentProgress() {
        return this.currentProgress;
    }//getCurrentProgress

    /**
     * @return the total length of the task
     */
    public int getLengthOfTask() {
        return this.lengthOfTask;
    }//getLengthOfTask

    /**
     * @return a <code>String</code> describing the task being performed
     */
    public String getTaskDescription() {
        return "Parsing Annotation...";
    }//getTaskDescription

    /**
     * @return a <code>String</code> status message describing what the task
     *         is currently doing (example: "Completed 23% of total.", "Initializing...", etc).
     */
    public String getCurrentStatusMessage() {
        return this.statusMessage;
    }//getCurrentStatusMessage

    /**
     * @return <code>true</code> if the task is done, false otherwise
     */
    public boolean isDone() {
        return this.done;
    }//isDone

    /**
     * Stops the task if it is currently running.
     */
    public void stop() {
        this.canceled = true;
        this.statusMessage = null;
    }//stop

    /**
     * @return <code>true</code> if the task was canceled before it was done
     *         (for example, by calling <code>MonitorableSwingWorker.stop()</code>,
     *         <code>false</code> otherwise
     */
    // TODO: Not sure if needed
    public boolean wasCanceled() {
        return this.canceled;
    }//wasCanceled
	
	public void start(boolean return_when_done) {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new DoTask();
            }//construct
        };
        worker.start();
        if (return_when_done) {
            worker.get(); // maybe use finished() instead
        }
    }//starts

    class DoTask {
        DoTask() {
            calculate();
        }
    }

	
}

