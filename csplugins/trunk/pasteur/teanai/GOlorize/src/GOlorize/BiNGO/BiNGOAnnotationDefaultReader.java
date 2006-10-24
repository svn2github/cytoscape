package GOlorize.BiNGO ;

// Adapted from : AnnotationFlatFileReader.java in Cytoscape
//------------------------------------------------------------------------------
// $Revision: 1.5 $  $Date: 2003/07/16 00:16:16 $
//------------------------------------------------------------------------------
// Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute


/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
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
 * * Modified by : Steven Maere
 * * Date: Apr.11.2005
 * * Description: Class that parses default annotation files in function of the chosen organism.         
 **/


import java.io.*; 
import java.util.*;
import javax.swing.* ;
import java.awt.*;

import cytoscape.data.annotation.*;
import cytoscape.data.readers.*;
//-------------------------------------------------------------------------
public class BiNGOAnnotationDefaultReader { 

	private final String YEAST = "Saccharomyces cerevisiae"  ;
    private final String ARABIDOPSIS = "Arabidopsis thaliana" ;
	private final String ORYZA_NIVARA = "Oryza nivara" ; 
	private final String POMBE = "Schizosaccharomyces pombe";
	private final String TRYPANOSOMA = "Trypanosoma brucei"  ;
	private final String C_ELEGANS = "Caenorhabditis elegans"  ;
	private final String DROSOPHILA = "Drosophila melanogaster"  ;
	private final String ZEBRA = "Brachydanio rerio" ;
	private final String HUMAN = "Homo Sapiens" ;
	private final String MOUSE = "Mus musculus" ;
	private final String RAT = "Rattus norvegicus" ;
	private final String PLASMODIUM = "Plasmodium falsiparum" ;
 	private final String ORYZA_SATIVA = "Oryza sativa" ; 
	private final String ANTHRAX = "Bacillus anthracis" ;
	private final String SHEWANELLA = "Shewanella oneidensis" ;
	private final String PSEUDOMONAS_SYRINGAE = "Pseudomonas syringae" ;
	private final String COXIELLA_BURNETII = "Coxiella burnetii" ;
	private final String GEOBACTER_SULFURREDUCENS = "Geobacter sulfurreducens" ;
	private final String METHYLOCOCCUS_CAPSULATUS = "Methylococcus capsulatus" ;
	private final String LISTERIA_MONOCYTOGENES = "Listeria monocytogenes" ;	
	
	private final String NR_YEAST = "4932" ;
    private final String NR_ARABIDOPSIS = "3702" ;
	private final String NR_ORYZA_NIVARA = "4536" ; 
	private final String NR_POMBE = "4896" ;
	private final String NR_TRYPANOSOMA = "5691" ;
	private final String NR_C_ELEGANS = "6239" ;
	private final String NR_DROSOPHILA = "7227" ;
	private final String NR_ZEBRA = "7955" ;
	private final String NR_HUMAN = "9606" ;
	private final String NR_MOUSE = "10090" ;
	private final String NR_RAT = "10116" ;
	private final String NR_PLASMODIUM = "36329" ;
 	private final String NR_ORYZA_SATIVA = "39947" ; 
	private final String NR_ANTHRAX = "198094" ;
	private final String NR_SHEWANELLA = "211586" ;
	private final String NR_PSEUDOMONAS_SYRINGAE = "223283" ;
	private final String NR_COXIELLA_BURNETII = "227377" ;
	private final String NR_GEOBACTER_SULFURREDUCENS = "243231" ;
	private final String NR_METHYLOCOCCUS_CAPSULATUS = "243233" ;
	private final String NR_LISTERIA_MONOCYTOGENES = "265669" ;	
	
	private static String GENEIDSTRING = "Entrez GeneID";
	private static String SYMBOLSTRING = "Gene Symbol";
	private static String UNIGENESTRING = "Unigene";
	private static String LOCUSTAGSTRING = "LocusTag";
	
  Annotation annotation;
  /** type for Annotation constructor*/	
  String annotationType;
  /** species for Annotation constructor*/	
  String species;
  /** curator for Annotation constructor*/	
  String curator;
  /** species taxonomy nr for parsing */
  String speciesNumber ;	
  String filename;
  File directoryAbsolute;
  String fullText;
  String [] lines;
  HashMap synonymHash ;	
  /** parent component for warning messages*/	
  Component settingsPanel ;	
  /** type of gene identifier to be extracted from annotation file*/	
  String idString ;	
  /** HashMap linking species name to species NCBI Taxonomy ID	*/
  HashMap speciesNr ;
  /** chosen type of gene identifier	*/
  int type ;	
  /** true if there are categories in the annotation which are not defined in the ontology*/	
  private boolean orphansFound = false ;	
  /** false if none of the categories in the annotation match the ontology*/	
  private boolean consistency = false ;	
	
//-------------------------------------------------------------------------
public BiNGOAnnotationDefaultReader (File file, HashMap synonymHash, Component settingsPanel, String idString, String species, String annotationType, String curator) throws IllegalArgumentException, IOException, Exception
{
  this (file.getPath(), synonymHash, settingsPanel, idString, species, annotationType, curator);
}
//-------------------------------------------------------------------------
public BiNGOAnnotationDefaultReader (String filename, HashMap synonymHash, Component settingsPanel, String idString, String species, String annotationType, String curator) throws IllegalArgumentException, IOException, Exception
{
  //System.out.println ("AnnotationFlatFileReader on " + filename);
  this.idString = idString ;	
  this.filename = filename;
  try {
    if (filename.trim().startsWith ("jar://")) {
      TextJarReader reader = new TextJarReader (filename);
      reader.read ();
      fullText = reader.getText ();
      }
    else if (filename.trim().startsWith ("http://")) {
      TextHttpReader reader = new TextHttpReader (filename);      
      reader.read ();
      fullText = reader.getText ();
      }
    else {
      TextFileReader reader = new TextFileReader (filename);
      reader.read ();
      fullText = reader.getText ();
      }
    }
  catch (IOException e0) {
    System.err.println ("-- Exception while reading annotation flat file " + filename);
    System.err.println (e0.getMessage ());
	throw e0 ;
    //return;
    }

  	speciesNr = new HashMap() ;
 	speciesNr.put(YEAST, NR_YEAST) ;
 	speciesNr.put(ARABIDOPSIS, NR_ARABIDOPSIS) ;
	speciesNr.put(ORYZA_NIVARA, NR_ORYZA_NIVARA) ;
	speciesNr.put(POMBE, NR_POMBE) ;
	speciesNr.put(TRYPANOSOMA, NR_TRYPANOSOMA) ;
	speciesNr.put(C_ELEGANS, NR_C_ELEGANS) ;
	speciesNr.put(DROSOPHILA, NR_DROSOPHILA) ;
	speciesNr.put(ZEBRA, NR_ZEBRA) ;
	speciesNr.put(HUMAN, NR_HUMAN) ;
	speciesNr.put(MOUSE, NR_MOUSE) ;
	speciesNr.put(RAT, NR_RAT) ;
	speciesNr.put(PLASMODIUM, NR_PLASMODIUM) ;
	speciesNr.put(ORYZA_SATIVA, NR_ORYZA_SATIVA) ;
	speciesNr.put(ANTHRAX, NR_ANTHRAX) ;
	speciesNr.put(SHEWANELLA, NR_SHEWANELLA) ;
	speciesNr.put(PSEUDOMONAS_SYRINGAE, NR_PSEUDOMONAS_SYRINGAE) ;
	speciesNr.put(COXIELLA_BURNETII, NR_COXIELLA_BURNETII) ;
	speciesNr.put(GEOBACTER_SULFURREDUCENS, NR_GEOBACTER_SULFURREDUCENS) ;
	speciesNr.put(METHYLOCOCCUS_CAPSULATUS, NR_METHYLOCOCCUS_CAPSULATUS) ;
	speciesNr.put(LISTERIA_MONOCYTOGENES, NR_LISTERIA_MONOCYTOGENES) ;
	
  this.species = species;  	
  this.speciesNumber = speciesNr.get(species).toString() ;	
  this.annotationType = annotationType;
  this.curator = curator ;	
  this.synonymHash = synonymHash ;	
  this.settingsPanel = settingsPanel ;	
  if(idString.equals(GENEIDSTRING)){type = 1 ;}
  else if(idString.equals(SYMBOLSTRING)){type = 2 ;}
  else if(idString.equals(UNIGENESTRING)){type = 3 ;}
  else if(idString.equals(LOCUSTAGSTRING)){type = 4 ;}
  lines = fullText.split ("\n");
  parse ();

}
//-------------------------------------------------------------------------
private int stringToInt (String s)
{
  try {
    return Integer.parseInt (s);
    }
  catch (NumberFormatException nfe) {
    return -1;
    }
}
//-------------------------------------------------------------------------

private void parse () throws Exception
{
  annotation = new Annotation (species, annotationType, curator);	
  HashSet orphans = new HashSet() ;
  for (int i=1; i < lines.length; i++) {
    String line = lines [i];
    if (line.length () < 2) continue;
    String [] tokens = line.split ("\t");
	String spec = tokens [0].trim() ;
	if(spec.equals(speciesNumber)){
		String entityName = tokens [type].trim().toUpperCase() ;
		int id = stringToInt (tokens [5].trim());   
		Integer id2 = new Integer(id) ;	
		Integer mainId = (Integer) synonymHash.get(id2) ;	
		if(mainId != null){
			if((entityName != null)&&(entityName.length() != 0)&&(id != -1)){
                            annotation.add (entityName, mainId.intValue());
                        }
			consistency = true ;
		}
		else{orphans.add(id2) ; orphansFound = true ;}
	}	
  }

} // parse
//-------------------------------------------------------------------------
public Annotation getAnnotation ()
{
  return annotation;
}

public boolean getOrphans(){
	return orphansFound ;
}

public boolean getConsistency(){
	return consistency ;
}

//-------------------------------------------------------------------------
} // class BiNGOAnnotationDefaultReader


