package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
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
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class which creates a file with information about the selected
 * * cluster: ontology type and curator, time of creation, alpha,
 * * sort of test and correction, p-values and corrected 
 * * p-values, term id and name, x, X, n, N.     
 **/


import cytoscape.data.annotation.Annotation;
import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.OntologyTerm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;


/**
 * *****************************************************************
 * CreateAnnotationFile.java     Steven Maere (c) 2006
 * --------------------
 * <p/>
 * Class which creates a custom annotation file from the selected annotation
 * and the selected set of genes
 * 
 * ******************************************************************
 */


public class CreateAnnotationFile {

    /*--------------------------------------------------------------
    FIELDS.
    --------------------------------------------------------------*/

    
    private Annotation annotation;
    /**
     * the ontology.
     */
    private Ontology ontology;
    /**
     * the annotation file path.
     */

    private String dirName;
    /**
     * the file name for the data file.
     */
    private String fileName;
    /**
     * the clusterVsString.
     */
    /**
     * HashSet with the names of the selected nodes.
     */
    private HashSet selectedCanonicalNameVector;
    
    private HashMap<String,HashSet<String>> alias;

    /*--------------------------------------------------------------
    CONSTRUCTORS.
    --------------------------------------------------------------*/

   

    /**
     * Constructor 
     *
     * @param annotation                  the Annotation.
     * @param ontology                    the Ontology.
     * @param fileName                    String with the name for the anno-file.
     * @param dirName                     Directory 
     * @param selectedCanonicalNameVector HashSet with the selected genes.
     * @param alias                       aliases of genes
     */
    public CreateAnnotationFile(
                           Annotation annotation,
                           HashMap alias,
                           Ontology ontology,
                           String dirName,
                           String fileName,
                           HashSet selectedCanonicalNameVector) {

        
        this.annotation = annotation;
        this.ontology = ontology;
        this.dirName = dirName;
        this.fileName = fileName;
        this.selectedCanonicalNameVector = selectedCanonicalNameVector;
        this.alias = alias;
    }

    /*--------------------------------------------------------------
    METHODS.
    --------------------------------------------------------------*/


    public void makeFile() {
        
        Annotation parsedAnnotation = parse(selectedCanonicalNameVector);

        // actual writing of the file.


        try {
            File results = new File(dirName, fileName);
            BufferedWriter output = new BufferedWriter(new FileWriter(results));
            output.write("(species=" + parsedAnnotation.getSpecies() + ")(type=" + parsedAnnotation.getType() + ")(curator=" + parsedAnnotation.getCurator() + ")\n");
            
            Iterator it = selectedCanonicalNameVector.iterator();
            while(it.hasNext()) {   
                String name = it.next() + "";
                int [] nodeClassifications = parsedAnnotation.getClassifications(name);
                for (int k = 0; k < nodeClassifications.length; k++) {
                    output.write(name + " = " + nodeClassifications[k] + "\n");
                }    
            }      

            output.close();
        }

        catch (Exception e) {
            System.out.println("Error: " + e);

        }

    }

    private Annotation parse(HashSet selectedCanonicalNameVector){
        Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(), annotation.getCurator()) ;
        Iterator it = selectedCanonicalNameVector.iterator();
        while(it.hasNext()) {   
            String name = it.next() + "";
            HashSet tmp = alias.get(name);
            if(tmp != null){
                Iterator it2 = tmp.iterator();
                while(it2.hasNext()){
                    int[] nodeClassifications = annotation.getClassifications(it2.next() + "");
                    for (int k = 0; k < nodeClassifications.length; k++) {
                        boolean b = true;
                        for (int l = 0; l < nodeClassifications.length; l++) {
                            if(ontology.getTerm(nodeClassifications[k]).isParentOrContainerOf(ontology.getTerm(nodeClassifications[l]))){
                                b = false;
                            }
                        }
                        if(b == true){
                            parsedAnnotation.add(name, nodeClassifications[k]);
                        }
                    }
                }    
            }
        }     
        return parsedAnnotation ;
    }



}



