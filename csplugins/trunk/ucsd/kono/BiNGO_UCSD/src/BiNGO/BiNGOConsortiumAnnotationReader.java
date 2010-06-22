package BiNGO ;

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

public class BiNGOConsortiumAnnotationReader {

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

    public BiNGOConsortiumAnnotationReader(File file, HashMap synonymHash, BingoParameters params, String annotationType, String curator) throws IllegalArgumentException, IOException, Exception {
        this(file.getPath(), synonymHash, params, annotationType, curator);
    }
//-------------------------------------------------------------------------

    public BiNGOConsortiumAnnotationReader(String filename, HashMap synonymHash, BingoParameters params, String annotationType, String curator) throws IllegalArgumentException, IOException, Exception {
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
        

        this.species = filename;
        this.annotationType = annotationType;
        this.curator = curator;
        this.synonymHash = synonymHash;
        this.deleteCodes = params.getDeleteCodes() ;
        lines = fullText.split("\n");

        int start = parseHeader();
        parse(start);

    }

    private int parseHeader() throws Exception {
        boolean ok = false; int i = 0 ;
        while(ok == false){
        
          String line = lines[i].trim();
          if(!line.startsWith("!")){
              ok = true;
          }
          i++;
        }  
        return i;
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

    private void parse(int start) throws Exception {
        annotation = new Annotation(species, annotationType, curator);
        alias = new HashMap() ;
        HashSet orphans = new HashSet();
        for (int i = start; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() < 2) continue;
            String [] tokens = line.split("\t");
            String evidenceCode = tokens[6].trim().toUpperCase();
            String qualifier = tokens[3].trim();
            if(!deleteCodes.contains(evidenceCode) && (qualifier.length() == 0)){
                String primaryID = tokens[1].trim().toUpperCase();
                if(alias.containsKey(primaryID)){
                  alias.get(primaryID).add(primaryID);
                }
                else{
                  HashSet tmp = new HashSet(); tmp.add(primaryID);  
                  alias.put(primaryID,tmp);  
                }
                String secondaryID = tokens[2].trim().toUpperCase();
                if(alias.containsKey(secondaryID)){
                  alias.get(secondaryID).add(primaryID);
                }  
                else{
                  HashSet tmp = new HashSet(); tmp.add(primaryID);  
                  alias.put(secondaryID,tmp);  
                }
                String [] aliases = tokens[10].split("\\|");
                for(int j = 0; j < aliases.length; j++){
                    if(alias.containsKey(aliases[j].trim().toUpperCase())){
                        alias.get(aliases[j].trim().toUpperCase()).add(primaryID);
                    }
                    else{
                        HashSet tmp = new HashSet(); tmp.add(primaryID);  
                        alias.put(aliases[j].trim().toUpperCase(),tmp);  
                    }
                }
                String goID = tokens[4].trim().toUpperCase().substring(3);
                //System.out.println("goID " + goID + "\n");
                int id = stringToInt(goID);
                Integer id2 = new Integer(id);
                Integer mainId = (Integer) synonymHash.get(id2);
                if (mainId != null) {
                    if ((primaryID != null) && (primaryID.length() != 0) && (id != -1)) {
                        annotation.add(primaryID, mainId.intValue());
                    }
                    consistency = true;
                } else {
                    orphans.add(id2);
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
} // class BiNGOConsortiumAnnotationReader



