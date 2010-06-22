package BiNGO ;

// Adapted from : AnnotationFlatFileReader.java in Cytoscape
//------------------------------------------------------------------------------
// $Revision: 1.13 $  $Date: 2006/07/31 11:50:03 $
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


import cytoscape.data.annotation.Annotation;
import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextHttpReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

//-------------------------------------------------------------------------

public class BiNGOAnnotationDefaultReader {

    Annotation annotation;
    /**
     * type for Annotation constructor
     */
    String annotationType;
    /**
     * species for Annotation constructor
     */
    String species;
    /**
     * curator for Annotation constructor
     */
    String curator;

    String filename;
    String fullText;
    String [] lines;
    HashMap synonymHash;
    HashMap<String,HashSet<String>> alias;
    HashSet deleteCodes;


    /**
     * true if there are categories in the annotation which are not defined in the ontology
     */
    private boolean orphansFound = false;
    /**
     * false if none of the categories in the annotation match the ontology
     */
    private boolean consistency = false;

//-------------------------------------------------------------------------

   public BiNGOAnnotationDefaultReader(File file, HashMap synonymHash, BingoParameters params, String annotationType, String curator) throws IllegalArgumentException, IOException, Exception {
        this(file.getPath(), synonymHash, params, annotationType, curator);
    }
//-------------------------------------------------------------------------

    public BiNGOAnnotationDefaultReader(String filename, HashMap synonymHash, BingoParameters params, String annotationType, String curator) throws IllegalArgumentException, IOException, Exception {
        //System.out.println ("AnnotationFlatFileReader on " + filename);
        this.filename = filename;
        try {
            if (filename.trim().startsWith("jar:")) {
                BiNGOJarReader reader = new BiNGOJarReader(filename);
                reader.read();
                fullText = reader.getText();
            } else if (filename.trim().startsWith("http://")) {
                TextHttpReader reader = new TextHttpReader(filename);
                reader.read();
                fullText = reader.getText();
            } else {
                TextFileReader reader = new TextFileReader(filename);
                reader.read();
                fullText = reader.getText();
            }
        }
        catch (IOException e0) {
            System.err.println("-- Exception while reading annotation flat file " + filename);
            System.err.println(e0.getMessage());
            throw e0;

        }

        this.species = params.getSpecies();
        this.annotationType = annotationType;
        this.curator = curator;
        this.synonymHash = synonymHash;
        this.deleteCodes = params.getDeleteCodes() ;
        lines = fullText.split("\n");
        
        parse();

    }

    

//-------------------------------------------------------------------------

    private int stringToInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException nfe) {
            return -1;
        }
    }
//-------------------------------------------------------------------------

    private void parse() throws Exception {
        annotation = new Annotation(species, annotationType, curator);
        HashSet orphans = new HashSet();
        alias = new HashMap() ;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() < 2) continue;
            String [] tokens = line.split("\t");
            //Get the GO annotation which is the third thing on the line
            int id = stringToInt(tokens[2].trim());
            //Get the evidence code which is the fourth thing on the line
            String evidenceCode = tokens[3].trim().toUpperCase();
            if(!deleteCodes.contains(evidenceCode)){
                String primaryID = tokens[1].trim().toUpperCase();
                if(alias.containsKey(primaryID)){
                  alias.get(primaryID).add(primaryID);
                }
                else{
                  HashSet tmp = new HashSet(); tmp.add(primaryID);  
                  alias.put(primaryID,tmp);  
                }            
                for(int k = 4; k < tokens.length; k++){
                  if(!tokens[k].trim().equals("")){
                    if(alias.containsKey(tokens[k].trim().toUpperCase())){
                        alias.get(tokens[k].trim().toUpperCase()).add(primaryID);
                    }
                    else{
                        HashSet tmp = new HashSet(); tmp.add(primaryID);  
                        alias.put(tokens[k].trim().toUpperCase(),tmp);  
                    }
                  }  
                }
            
                Integer id2 = new Integer(id);
                Integer mainId = (Integer) synonymHash.get(id2);
                if (mainId != null) {
                    if ((primaryID != null) && (primaryID.length() != 0) && (id != -1)) {
                        annotation.add(primaryID, mainId.intValue());
                    }
                    consistency = true;
                } else {
                    orphans.add(id2);
                    System.out.println("orphan GO category: " + id2);
                    orphansFound = true;
                }
            }    
        }

    } // parse
//-------------------------------------------------------------------------

    public Annotation getAnnotation() {
        return annotation;
    }

    public boolean getOrphans() {
        return orphansFound;
    }

    public boolean getConsistency() {
        return consistency;
    }
    
    public HashMap getAlias(){
        return alias;
    }

//-------------------------------------------------------------------------
} // class BiNGOAnnotationDefaultReader


