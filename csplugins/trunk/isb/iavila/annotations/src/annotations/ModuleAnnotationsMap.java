/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
/**
 * A container of module annotations. Each module has a unique ID through
 * which the user can access its sorted annotations.
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.orgl, iliana.avila@gmail.com>
 */
package annotations;

import java.util.*;
import java.io.Serializable;
import cytoscape.data.annotation.*;

public class ModuleAnnotationsMap {

  /**
   * A map of module unique IDs to <code>SortedSet</code> objects
   * that contain <code>ModuleAnnotation</code> objects.
   */
  protected Map moduleID_to_Annotations;

  /**
   * The <code>Comparator</code> to keep the
   * <code>ModuleAnnotation</code> objects sorted.
   */
  protected static final Comparator  ANNOTATIONS_COMPARATOR = 
    new ModuleAnnotationComparator();

  /**
   * Constructor.
   */
  public ModuleAnnotationsMap (){
    this.moduleID_to_Annotations = new HashMap();
  }//ModuleAnnotationsMap

   /**
   * Adds an annotation for the module with the given ID.
   * The p_value argument will be used for sorting the annotations
   * for the given module in ascending order.
   *
   * @param module_id the module unique id
   * @param ontology_term the <code>OntologyTerm</code> which represents the annotation
   * @param p_value the pValue of <code>ontology_term</code>
   * @return true if the annotation was succesfully added, false otherwise
   */
  public boolean add (Object module_id, OntologyTerm ontology_term, double p_value){
    ModuleAnnotation module_annotation = 
      new ModuleAnnotation (ontology_term, p_value);
    return add(module_id, module_annotation);
  }//add

  /**
   * Adds the <code>ModuleAnnotation</code> for the module with the given ID.
   *
   * @param module_id the module unique id
   * @param module_annotation the <code>ModuleAnnotation</code> for the module
   * @return true if the annotation was succesfully added, false otherwise
   */
  public boolean add (Object module_id, ModuleAnnotation module_annotation){
    SortedSet annotationsSet = (SortedSet)this.moduleID_to_Annotations.get(module_id);
    if(annotationsSet == null){
      annotationsSet = new TreeSet(ANNOTATIONS_COMPARATOR);
      this.moduleID_to_Annotations.put(module_id, annotationsSet);
    }
    return annotationsSet.add(module_annotation);
  }//add

  /**
   * Removes an annotation for the module with the given ID.
   *
   * @param module_id the module unique id
   * @param module_annotation the <code>ModuleAnnotation</code> to be removed
   * @return true if the annotation was succesfully removed, false otherwise
   */
  public boolean remove (Object module_id, ModuleAnnotation  module_annotation){
    SortedSet annotationsSet = (SortedSet)this.moduleID_to_Annotations.get(module_id);
    if(annotationsSet == null){
      return false;
    }
    return annotationsSet.remove(module_annotation);
  }//remove

  /**
   * Returns an array of <code>ModuleAnnotation</code> objects that belong
   * to the given module ID.&nbsp;The array can either contain all annotations
   * for the module, or, contain the most specific ones.&nbsp;A <code>ModuleAnnotation</code>
   * is considered less specific than another one if it has a >= p-value as the other, 
   * and its <code>OntologyTerm</code> object is a parent or container of the other's 
   * <code>OntologyTerm</code>.&nbsp;The annotations are sorted in order of ascending p-value, 
   * and descending <code>OntologyTerm</code> specificity.
   *
   * @param module_id the module unique id
   * @param make_specific whether the returned array should only contain the most specific
   *                      annotations
   * @return an array of sorted <code>ModuleAnnotation</code> objects
   */
  public ModuleAnnotation [] get (Object module_id, boolean make_specific){
    SortedSet annotationsSet = (SortedSet)this.moduleID_to_Annotations.get(module_id);
    if(annotationsSet != null){
      if(make_specific){
        makeSpecific(annotationsSet);
      }
      ModuleAnnotation [] annotationsArray = 
        (ModuleAnnotation[])annotationsSet.toArray(new ModuleAnnotation[annotationsSet.size()]);
      return annotationsArray;
    }
    // Either the module has no annotations, or it does not exist
    return new ModuleAnnotation [0];
  }//get

  /**
   * It returns all the annotations for all modules in this map.
   *
   * @param make_specific whether or not the returned annotations should be made specific
   * @return an array of <code>ModuleAnnotation</code> objects that are contained in this
   * map
   */
  public ModuleAnnotation [] getAllAnnotations (boolean make_specific){
    Collection values = this.moduleID_to_Annotations.values();
    if(values == null || values.size() == 0){
      return new ModuleAnnotation[0];
    }
    SortedSet [] setArray = (SortedSet[])values.toArray(new SortedSet[values.size()]);
    ArrayList allAnnotations = new ArrayList();
    for(int i = 0; i < setArray.length; i++){
      SortedSet aSortedSet = setArray[i];
      if(make_specific){
        makeSpecific(aSortedSet);
      }
      allAnnotations.addAll(aSortedSet);
    }//for i
    return (ModuleAnnotation[])allAnnotations.toArray(new ModuleAnnotation[allAnnotations.size()]);
  }//getAllAnnotations

  
  /**
   * Returns a sorted map of OntologyTerms to Integers. The integers represent how many times
   * the OntologyTerm they have as a key appear in this map. If recursive_freq is true,
   * then the frequency includes children annotations that are in this map.
   *
   * @param make_specific whether or not annotations should be made specific
   * @param recursive_freq whether ot not the frequency of annotations that have children
   * that are also in this map should include them
   * @param ontology the cytoscape.data.annotation.Ontology object to which OntologyTerms
   * belong to, it can be null if recursive_freq is false
   * @return a SortedMap from OntologyTerm objects to Integers sorted in descending order with
   * respect to the Integers
   */
  public SortedMap getAnnotationsFrequency (boolean make_specific, 
                                            boolean recursive_freq,
                                            Ontology ontology){
    ModuleAnnotation [] allAnnotations = getAllAnnotations(make_specific);
    final Map termToFreq = new HashMap();
    if(allAnnotations == null || allAnnotations.length == 0){
      return new TreeMap();
    }
    // How many times each annotation is seen as overrepresented
    for(int i = 0; i < allAnnotations.length; i++){
      OntologyTerm currentTerm = allAnnotations[i].getOntologyTerm();
      Integer freq = (Integer)termToFreq.get(currentTerm);
      if(freq == null){
        termToFreq.put(currentTerm, new Integer(1));
      }else{
        termToFreq.put(currentTerm, new Integer(freq.intValue()+1));
      }
    }//for i
    
    // Cummulative frequency
    if(recursive_freq && ontology != null){
      Set terms = termToFreq.keySet();
      //System.err.println("There are " + terms.size() + " unique over-represented annotations.");
      OntologyTerm [] termsArray = (OntologyTerm[])terms.toArray(new OntologyTerm[terms.size()]);
      for(int i = 0; i < termsArray.length; i++){
        OntologyTerm currentTerm = termsArray[i];
        int [][] hierarchyPaths = ontology.getAllHierarchyPaths(currentTerm.getId());
        ArrayList seen = new ArrayList(); // TODO:use something else
        for(int p = 0; p < hierarchyPaths.length; p++){// for each path
          for(int t = 0; t < hierarchyPaths[p].length; t++){// for each annotation in the path
            OntologyTerm otherTerm = ontology.getTerm(hierarchyPaths[p][t]);
            if(otherTerm != currentTerm && 
               !seen.contains(otherTerm) &&
               termToFreq.containsKey(otherTerm)){ // make sure it is overrepresented
              Integer freq = (Integer)termToFreq.get(otherTerm);
              termToFreq.put(otherTerm, new Integer(freq.intValue() + 1));
              seen.add(otherTerm);
            }
          }// for t
        }//for p
        
      }//while it.hasNext()
    }// if recursive_freq && ontology != null
    
    TreeMap sortedMap = new TreeMap(
                                    new Comparator () {
                                      public int compare(Object k1, Object k2){
                                        Integer f1 = (Integer)termToFreq.get(k1);
                                        if(f1 == null){
                                          f1 = new Integer(0);
                                        }
                                        Integer f2 = (Integer)termToFreq.get(k2);
                                        if(f2 == null){
                                          f2 = new Integer(0);
                                        }
                                        int retVal =  f2.compareTo(f1); // descending order
                                        // if they are the same, return in alphabetical order
                                        if(retVal == 0){
                                          retVal = 
                                ((OntologyTerm)k1).getName().compareTo(((OntologyTerm)k2).getName());
                                        }
                                        return retVal;
                                      }//compare
                                    }
                                    );
    sortedMap.putAll(termToFreq);
    return sortedMap;
  }//getAnnotationsFrequency
  
  /**
   * Returns an array of all the unique module IDs in this <code>ModuleAnnotationsMap</code>.
   * @return an array of unique module IDs
   */
  public Object [] getModuleIDs (){
    Set idSet = this.moduleID_to_Annotations.keySet();
    Object [] idArray = idSet.toArray();
    return idArray;
  }//getModuleIDs

  /**
   * @return true if the given module id is a key in this <code>ModuleAnnotationsMap</code>,
   *         false otherwise.
   */
  public boolean contains (Object module_id){
    return this.moduleID_to_Annotations.containsKey(module_id);
  }//contains

  /**
   * @return the number of module IDs in this <code>ModuleAnnotationsMap</code>.
   */
  public int size (){
    return this.moduleID_to_Annotations.size();
  }//size

  /**
   * Removes from the given <code>SortedSet</code> those <code>ModuleAnnotation</code>
   * objects that have >= p-values as other <code>ModuleAnnotation</code> objects
   * in the <code>SortedSet</code>, but that are more general (meaning, their 
   * <code>OntologyTerm</code> object is a parent or a container of the <code>OntologyTerm</code>
   * object of the other <code>ModuleAnnotation</code> objects).
   *
   * @param annotations_set the <code>SortedSet</code> of <code>ModuleAnnotations</code>
   */
  protected static void makeSpecific (SortedSet annotations_set){
    ModuleAnnotation [] annotationsArray = 
      (ModuleAnnotation [])annotations_set.toArray(new ModuleAnnotation[annotations_set.size()]);
    
    //TODO: Remove
    //System.out.println("In makeSpecific. annotationsArray array:");
    //for(int i = 0; i < annotationsArray.length; i++){
    //int [] parentsAndContainers = annotationsArray[i].getOntologyTerm().getParentsAndContainers();
    //System.out.print(annotationsArray[i].getOntologyTerm().getId() +
    //                   " p&c = "); 
    //for(int j = 0; j < parentsAndContainers.length; j++){
    //  System.out.print(parentsAndContainers[j] + " ");
    //}
    //System.out.print(" pval = " + annotationsArray[i].getPValue());
    //System.out.println();
    //}
    
    for(int i = annotationsArray.length - 1; i >= 0; i--){
      for(int j = i-1; j >= 0; j--){
        if(annotationsArray[i].getPValue() >= annotationsArray[j].getPValue() &&
(annotationsArray[i].getOntologyTerm() ).isParentOrContainerOf(annotationsArray[j].getOntologyTerm()) )
          {
            annotations_set.remove(annotationsArray[i]);
            //TODO: Remove
            //boolean removed = annotations_set.remove(annotationsArray[i]);
            // if(!removed){
            //throw new IllegalStateException("OntTermSignificance object " + 
            //                                annotationsArray[i] +
            //                                " is not in annotations_set. Iterations: i = " 
            //                                + i + " j = " + j);
            //}
            //else{
            //System.out.println("Succesfully removed OntTermSignificance object + " +
            //                   annotationsArray[i] + "Iterations i = " + i + " j = " + j);
            //}
            break;
          }
      }
    }
  }//makeSpecific 

   /**
    * Internal class that compares two <code>ModuleAnnodation</code> objects
    * depending on their p-value and parent-child (or part-of) relationship.
    * <p>
    * The annotation with the largest p-value is considered
    * > the other. If the annotations have the same
    * p-value, the one with the least specific <code>OntologyTerm</code> 
    * (meaning, the one that is the parent or container of the other) is considered 
    * larger. If their pvalues are equal and they don't have a parent-child 
    * (or part-of) relationship, the ontology term with the smallest id 
    * is considered > the other.
    */
  protected static class ModuleAnnotationComparator
    implements Comparator, Serializable {
    public int compare (Object obj1, Object obj2){
      
      if(obj1 == obj2){
        return 0;
      }
      
      if(  ( ((ModuleAnnotation)obj1).getPValue() ==
             ((ModuleAnnotation)obj2).getPValue() ) &&
           ( ((ModuleAnnotation)obj1).getOntologyTerm().getId() ==
             ((ModuleAnnotation)obj2).getOntologyTerm().getId() ) ){
        //They have the same pValue and the same ontology annotation
        return 0;
      }
      
      if(((ModuleAnnotation)obj1).getPValue() ==
         ((ModuleAnnotation)obj2).getPValue()){
        // The p-values are the same, return an int
        // such as the least specific annotation is
        // > than the most specific annotation
        OntologyTerm ot1 = ((ModuleAnnotation)obj1).getOntologyTerm();
        OntologyTerm ot2 = ((ModuleAnnotation)obj2).getOntologyTerm();
      
        if(ot1.isChildOfOrContainedIn(ot2)){
          // ot1 is the child of(or contained in) ot2, which means ot1 is the most specific, 
          // therefore ot1 is smaller
          return -1;
        }
        if(ot1.isParentOrContainerOf(ot2)){
          // ot1 is the parent (or container) of ot2, which means ot1 is the least specific,
          // therefore ot1 is larger
          return 1;
        }
        // No parent-child relationship, just return according to their ids
        // Usually larger id's correspond to more specific annotations
        if(ot1.getId() > ot2.getId()){
          return -1;
        }else{
          return 1;
        }
      }// if they have the same p-value
      
      if(((ModuleAnnotation)obj1).getPValue() >
         ((ModuleAnnotation)obj2).getPValue()){
        return 1;
      }
      return -1;
      
    }//compare
    
  }//internal class ModuleAnnotationComparator
  
  
}//class ModuleAnnotationsMap
