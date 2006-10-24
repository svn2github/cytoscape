/* Modified from BiNGO.AnnotationParser
 * 
 */

/*
 * ZRemapAnnotation.java
 *
 * Created on August 2, 2006, 6:11 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut has been advised of the possibility
 * of such damage. See the GNU General Public License for more details:
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package GOlorize.BiNGO;

import cytoscape.data.annotation.*;
import java.util.*;
/**
 *
 * @author ogarcia
 */

/**
 * vilement copie colle de chez BiNGO
 */



public class ZRemapAnnotation {
    
    Annotation annotation;
    Ontology ontology;
    Annotation parsedAnnotation;
    private HashSet parentsSet ;
    /**
     * Creates a new instance of ZRemapAnnotation
     */
    public ZRemapAnnotation(Annotation annotation, Ontology ontology) {
        this.annotation = annotation;
        this.ontology=ontology;
        
        
    }
    public Annotation run(){
        checkOntology(ontology) ;
        
        parsedAnnotation = customRemap(annotation,ontology);
        
        return parsedAnnotation;
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
    public Annotation customRemap (Annotation annotation, Ontology ontology){
        Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(), annotation.getCurator());

        System.out.println(annotation.getSpecies()+ annotation.getType()+ annotation.getCurator());//////////

        HashMap annMap = annotation.getMap() ;
        Iterator it = annMap.keySet().iterator() ;
        
        while(it.hasNext()){
                
                parentsSet = new HashSet() ;
                String node = it.next() + "" ;
                // array with go labels for gene it.next().
                int [] goID;
                goID = annotation.getClassifications(node);
                for (int t = 0; t < goID.length; t++){
                        if(ontology.getTerm(goID[t]) != null){
                                parsedAnnotation.add(node, goID[t]);
                                // 200905 NEXT LINE WITHIN LOOP <-> REMAP IN ORDER TO AVOID TRYING TO PARSE LABELS NOT DEFINED IN 'ONTOLOGY'...
                                // all parent classes of GO class that node is assigned to are also explicitly included in classifications
                                up(node, goID[t], parsedAnnotation, ontology, ontology) ;	
                    }
                }	
        }	
        return parsedAnnotation ;
    }
    public void up (String node, int goID, Annotation parsedAnnotation, Ontology ontology, Ontology flOntology){	
        OntologyTerm child  = flOntology.getTerm(goID);	
        if (child!=null){
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
    }
    public void up_go (int startID, int goID, Ontology ontology){	
        OntologyTerm child  = ontology.getTerm(goID);	
        int [] parents =  child.getParentsAndContainers ();	
        for(int t = 0; t < parents.length; t++){
                if(parents[t] == startID){
                        
                        //JOptionPane.showMessageDialog(settingsPanel,
                         //                                      "Your ontology file contains a cycle at ID " + startID);
                }	
                else if(!parentsSet.contains(new Integer(parents[t]))){
                        parentsSet.add(new Integer(parents[t])) ;
                        up_go(startID, parents[t],ontology);
                }
        }			
    }
    
    
}
