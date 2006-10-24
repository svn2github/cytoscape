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
 * * Date: Nov.15.2005
 * * Description: class that counts the small n, big N, small x, big X which serve as input for the statistical underrepresentation tests.     
 **/

import java.util.*;  
import cytoscape.data.annotation.*;



/***************************************************************
 * DistributionCountNeg.java   Steven Maere (c) November 2005
 * ----------------------
 *
 * class that counts the small n, big N, small x, big X which serve as input for the statistical underrepresentation tests.
 ***************************************************************/


public class DistributionCountNeg {
    
    
    
    
   	/*--------------------------------------------------------------
      FIELDS.
      --------------------------------------------------------------*/

	/** the annotation.*/
	private static Annotation annotation;
	/** the ontology.*/
	private static Ontology ontology;
	/** Vector of selected nodes */
	private static Vector selectedNodes;
	/** Vector of reference nodes */
	private static Vector refNodes;
	/** hashmap with values of small n ; keys GO labels.*/
	private static HashMap mapSmallN;
	/** hashmap with values of small x ; keys GO labels.*/
	private static HashMap mapSmallX;
	/** int containing value for big N.*/
	private static int bigN;	
	/** int containing value for big X.*/
	private static int bigX;
    
    
    
    
    
	/*--------------------------------------------------------------
      CONSTRUCTOR.
      --------------------------------------------------------------*/

	public DistributionCountNeg(Annotation annotation, 
							 	Ontology ontology,
							 	Vector selectedNodes,
								Vector refNodes){
		this.annotation = annotation;
		this.ontology = ontology;
		annotation.setOntology(ontology);		
		this.selectedNodes = selectedNodes;
		this.refNodes = refNodes;
	}
    
    
    
    
    
	/*--------------------------------------------------------------
		METHODS.
      --------------------------------------------------------------*/
  
	/**
	* method for compiling GO classifications for given node
	*/


	public HashSet getNodeClassifications (String node){

		// HashSet for the classifications of a particular node
		HashSet classifications = new HashSet();
		
		// array for go labels.
		int [] goID;
		goID = annotation.getClassifications(node + "");
		for (int t = 0; t < goID.length; t++){
			classifications.add(goID[t] + "");
//			omitted : all parent classes of GO class that node is assigned to are also explicitly included in classifications from the start
//			up(goID[t], classifications) ;	
		}
		return classifications ;
	}

	/**
	* method for compiling represented GO categories for all nodes ; for underrepresentation, nodes in the set with 0 occurrences but some occurrence in the reference set are also considered
	*/

	
 	public HashSet getAllClassifications (){

		// HashSet for the classifications of a particular node
		HashSet classifications = new HashSet();

		Iterator i = refNodes.iterator() ;
 		while (i.hasNext()){ 
			int [] goID = annotation.getClassifications(i.next().toString() + "");
			for (int t = 0; t < goID.length; t++){
				classifications.add(goID[t] + "");
	//			omitted : all parent classes of GO class that node is assigned to are also explicitly included in classifications from the start
	//			up(goID[t], classifications) ;	
			}
		}
		return classifications ;
	}
	
	
   /**
	* method for recursing through tree to root
	*/

/*  public void up (int goID, HashSet classifications){	
	    OntologyTerm child  = ontology.getTerm(goID);	
		  int [] parents =  child.getParentsAndContainers ();	
			for(int t = 0; t < parents.length; t++){
				classifications.add(parents[t] + "");
				up(parents[t],classifications);
			}	
	}
*/
	/**
	 * method for making the hashmap for small n.      
	 */
    public void countSmallN (){
			mapSmallN = this.count(refNodes);
	}


	/**
	 * method for making the hashmap for the small x.
	 */
	public void countSmallX (){
		mapSmallX = this.count(selectedNodes);
	}	
	
	
	/**
	 * method that counts for small n and small x.
	 */
	public HashMap count(Vector nodes){

		HashMap map = new HashMap();
		Integer id;
		
		HashSet allClassifications = getAllClassifications() ;
		
		Iterator iterator1 = allClassifications.iterator();
			
		while(iterator1.hasNext()){
			id = new Integer(iterator1.next().toString());
			if(!map.containsKey(id)){
				map.put(id, new Integer(0));
			}	
		}
		
		Iterator i = nodes.iterator() ;
		while (i.hasNext()){
			HashSet classifications = getNodeClassifications(i.next().toString()) ;	
			
			Iterator iterator = classifications.iterator();
				
			// puts the classification counts in a map
			while(iterator.hasNext()){
					id = new Integer(iterator.next().toString());
					map.put(id, new Integer(new Integer(map.get(id).toString()).intValue() + 1));
			}

		}

		return map;
	}
	
	/**
	 * counts big N. unclassified nodes are not counted ; no correction for function_unknown nodes (yet)(requires user input)
	 */
	public void countBigN(){
		bigN = refNodes.size();
		Iterator i = refNodes.iterator() ;
 		while (i.hasNext()){
			HashSet classifications = getNodeClassifications(i.next().toString()) ;
			Iterator iterator = classifications.iterator();
			if(!iterator.hasNext()){bigN-- ;}
		}	
	}
		
	/**
	 * counts big X. unclassified nodes are not counted ; no correction for function_unknown nodes (yet)(requires user input)
	 */
	public void countBigX(){
		bigX = selectedNodes.size();
		Iterator i = selectedNodes.iterator() ;
		while (i.hasNext()){
		  HashSet classifications = getNodeClassifications(i.next().toString()) ;
			Iterator iterator = classifications.iterator();
			if(!iterator.hasNext()){bigX-- ;}
		}
	}
    
    
    
    
    
	/*--------------------------------------------------------------
		GETTERS.
      --------------------------------------------------------------*/	
	/** 
	 * returns small n hashmap.
	 *
	 * @return hashmap mapSmallN
	 */
	public HashMap getHashMapSmallN(){
		return mapSmallN;
	}
	
	/** 
	 * returns small x hashmap.	 
	 *
	 * @return hashmap mapSmallX
	 */
	public HashMap getHashMapSmallX(){
		return mapSmallX;
	}

	/** 
	 * returns the int for the big N.	 
	 *
	 * @return int bigN
	 */
	public int getBigN(){
		return bigN;
	}
	
	/** 
	 * returns the int for the big X.
	 *
	 * @return int bigX.
	 */
	public int getBigX(){
		return bigX;
	}
	
}
