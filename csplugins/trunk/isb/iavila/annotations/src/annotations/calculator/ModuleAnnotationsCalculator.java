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
 * This class finds overepresented annotation-classifications 
 * for groups of genes. Overepresentation is a relative term, 
 * so input parameters are used to determine it.
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org, iliana.avila@gmail.com>
 */
package annotations.calculator;

import cytoscape.data.annotation.*;
import annotations.*;
import annotations.ModuleAnnotationsMap;
import java.util.*;


public class ModuleAnnotationsCalculator {
 
  /**
   * The default value of <code>this.maxPval</code>.
   */
  public static final double DEFAULT_MAX_P_VAL = 0.05;
 
  /**
   * The default value for <code>minClassificationRepeats</code>.
   */
  public static final int DEFAULT_MIN_CLASSIFICATION_REPS = 2;

  
  /**
   * Randomly assigns to each module (row) a gene (cell) from all
   * the genes in the 2D array. Each module will have the same number
   * of members as in its non-randomized version.
   *
   * @return the randomized version of the input 2D array. The input
   * 2D array is unchanged after this call.
   */
  public static String[][] randomize (String [][] input){
    
    if(input == null || input.length == 0){
      return new String[0][0];
    }
    
    ArrayList population = new ArrayList();
    for(int i = 0; i < input.length; i++){
      for(int j = 0; j < input[i].length;j++){
        population.add(input[i][j]);
      }//for j
    }//for i
    
    Random randomNumGenerator = new Random();
    // Fisher-Yates shuffling (done 3 times, to make sure this is randomized).
    for(int reps = 0; reps < 3; reps++){
      randomNumGenerator.setSeed(System.currentTimeMillis());
      for(int i = population.size()-1; i >= 0; i--){
        // Get a random integer between 0 and i
        int j = randomNumGenerator.nextInt(i+1);
        if(i == j){
          continue;
        }
        //Swap them
        String temp = (String)population.get(i);
        population.set(i, (String)population.get(j));
        population.set(j, temp);
      }//for i
    }//for reps
    
    String [][] randomized = new String[input.length][];
    int position = 0;
    for(int i = 0; i < randomized.length; i++){
      randomized[i] = new String[input[i].length];
      for(int j = 0; j < randomized[i].length; j++){
        randomized[i][j] = (String)population.get(position);
        position++;
      }
    }//for i
    
    return randomized;
  }//randomize
  
  /**
   * Calculates overepresented annotations for groups of genes.
   * 
   * @param annotation the <code>cytoscape.data.annotation.Annotation</code> object that contains the annotations
   * @param module_ids keys to identify the groups and used in the returned <code>ModuleAnnotationsMap</code>, if null,
   * Integer keys corresponding to the row indices in 'gene_groups' will be used
   * @param gene_groups each row in this 2D array corresponds to a group of genes for which overepresented annotations will be calculated
   * @param max_pval an annotation's p-value must be at most this large to be considered overepresented
   * @param randomize whether or not to randomize group membership while maintaining the same number of members per group before calculating
   * overepresented annotations (good for paper figures)
   * @return a ModuleAnnotationsMap that contains the overepresented annotations for each module
   */
  public static ModuleAnnotationsMap calculateAnnotations (
  		Annotation annotation, 
		Object [] module_ids, 
		String [][] gene_groups, 
		double max_pval, 
		boolean randomize){
    
    //TODO: Remove
    //System.out.println("--- In calculateAnnotations ---");
    
    // ------ Collect numbers needed for p-val calculation ------

    //TODO: Remove
    //System.out.println("Collecting numbers needed for p-val calculation...");
    
    if(module_ids == null){
      module_ids = new Integer[gene_groups.length];
      for(int i = 0; i < gene_groups.length; i++){
        module_ids[i] = new Integer(i);
      }
    }
    
    Ontology ontology = annotation.getOntology();
    HashMap ontID_to_ontValue = new HashMap();
    int population = 0;
    
    String [][] modules;
    if(randomize){
      modules = randomize(gene_groups);
    }else{
      modules = gene_groups;
    }
    
    // For each module, collect statistics for p-value calculation:
    for(int moduleIndex = 0; moduleIndex < modules.length; moduleIndex++){
      //TODO: Remove
      //System.out.println("MODULE " + moduleIndex);
      for(int col = 0; col < modules[moduleIndex].length; col++){
        String geneCanonical = modules[moduleIndex][col];
        population++;
        //TODO: Remove
        //System.out.println("Gene " + geneCanonical);
        // Each gene in this module has an array of classifications
        int [] geneClassifications = 
          annotation.getClassifications(geneCanonical);
        HashSet seenOntologyIDs = new HashSet(); // Used to avoid overcounting
                                                 // since there could be multiple paths
                                                 // to the root of the Ontology
        // Each classification has a path or paths to the root of the Ontology
        for(int geneCl = 0; geneCl < geneClassifications.length; geneCl++){
          int [][] hierarchyPaths = ontology.getAllHierarchyPaths(geneClassifications[geneCl]);
          // Collect statistics for each node to the root
          for(int hrPath = 0; hrPath < hierarchyPaths.length; hrPath++){
            // We are looking at a path from a gene classification to the root of an ontology
            for(int node = 0; node < hierarchyPaths[hrPath].length; node++){
              // This is a node in the path:
              OntologyTerm ontTerm = ontology.getTerm(hierarchyPaths[hrPath][node]);
              Integer ontTermID = new Integer(ontTerm.getId());
              if(seenOntologyIDs.contains(ontTermID)){
                // Avoid counting the same OntologyTerm for the same gene more
                // than once
                continue;
              }else{
                seenOntologyIDs.add(ontTermID);
              }
              OntologyHashValue ontHashValue = (OntologyHashValue)ontID_to_ontValue.get(ontTermID);
              if(ontHashValue == null){
                ontHashValue = new OntologyHashValue();
                ontHashValue.moduleToNumGenes.put(module_ids[moduleIndex], new Integer(1));
                ontHashValue.numGenes++;
                ontID_to_ontValue.put(ontTermID, ontHashValue);
              }else{
                Object moduleID = module_ids[moduleIndex];
                Integer ng = (Integer)ontHashValue.moduleToNumGenes.get(moduleID);
                if(ng == null){
                  ng = new Integer(1);
                }else{
                  ng = new Integer(ng.intValue() + 1);
                }
                ontHashValue.moduleToNumGenes.put(moduleID,ng);
                ontHashValue.numGenes++;
              }
            }// for node in a hierarchy path
          }//for hierarchy path
        }//for gene classification
      }//for gene
    }// for module
    
    //TODO: Remove
    //System.out.println("Done collecting numbers.");
    
    // ----- We have the needed numbers for p-value calculation, so calculate p-vals! ---
    //TODO: Remove
    //System.out.println("Finding p-vals for modules...");
    //System.out.println("population = " + population);

    int population_true, sample, sample_true;
    Integer [] ontTermIDs = // all of the OntologyTerm ids
      (Integer[])( ( ontID_to_ontValue.keySet() ).toArray(new Integer[ontID_to_ontValue.size()]));
    ModuleAnnotationsMap annotationsMap = new ModuleAnnotationsMap();
       
    for(int moduleIndex = 0; moduleIndex < modules.length; moduleIndex++){
      Object moduleID = module_ids[moduleIndex];
      for(int i = 0; i < ontTermIDs.length; i++){
        Integer ontTermID = ontTermIDs[i];
        OntologyHashValue ontHashVal =  (OntologyHashValue)ontID_to_ontValue.get(ontTermID);
        Integer ng = (Integer)ontHashVal.moduleToNumGenes.get(moduleID);
        //if ng == null, then this OntologyTerm is not represented in this module
        if(ng != null  && ng.intValue() >= ModuleAnnotationsCalculator.DEFAULT_MIN_CLASSIFICATION_REPS){
          // This ontology term passed the test to be considered for p-val calculation
          population_true = ontHashVal.numGenes;
          sample = modules[moduleIndex].length;
          sample_true = ng.intValue();
          double pVal = HypDistanceCalculator.calculateHypDistance(population,
                                                                   population_true,
                                                                   sample,
                                                                   sample_true,
                                                                   true);
          if(pVal > max_pval){
          // Not over-represented
            continue;
          }
          
          OntologyTerm ontTerm = ontology.getTerm(ontTermID.intValue());
          //System.out.println("Module = " + moduleIndex 
          //                 + "\nOntology Term = " +ontTerm.getName()
          //                 + "\nPval = " + pVal);
          boolean added = annotationsMap.add(moduleID, ontTerm, pVal);
          //TODO: Remove
          if(!added){
            throw new IllegalStateException("OntologyTerm " + ontTerm + 
                                            " was not added to module with ID " + moduleID); 

          }
          
        }//if
        
      }//for i
    }//for moduleIndex
    
    //TODO: Remove
    //System.out.println("...done calculating P-vals.");  
    return annotationsMap;
  }//calculateAnnotations

  /**
   * Internal class that packages into one object a <code>HashMap</code> and an <code>int</code>.
   * It is used for creating values for a <code>HashMap</code>, in which the keys
   * are <code>OntologyTerm</code> ID's.
   */
  protected static class OntologyHashValue {
    
    /**
     * A <code>HashMap</code> of <code>Integers</code> to <code>Integers</code>.
     * The keys are indeces of rows in <code>this.geneModules</code>,
     * the values are the number of genes within the indicated module
     * that have a certain <code>OntologyTerm</code>.
     */
    public HashMap moduleToNumGenes;
    
    /**
     * The total number of genes that meet these conditions:
     * 1. The genes have this <code>OntologyTerm</code>
     * 2. The genes are within a module (any module) in <code>this.geneModules</code>
     */
    public int numGenes;

    OntologyHashValue (){
      this.moduleToNumGenes = new HashMap();
    }//OntologyHashValue
    
  }//internal class OntologyHashValue

}//class ModuleAnnotationsCalculator
