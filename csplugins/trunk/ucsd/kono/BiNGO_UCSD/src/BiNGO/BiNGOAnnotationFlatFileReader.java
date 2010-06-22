package BiNGO ;

// Adapted from : AnnotationFlatFileReader.java in Cytoscape
//------------------------------------------------------------------------------
// $Revision: 1.5 $  $Date: 2006/07/24 13:29:50 $
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
**/

/* * Modified Date: Mar.25.2005
 * * by : Steven Maere
 * * Changes : case-insensitive input ; correction for GO labels with multiple GO identifiers, 
 * * synonymous identifiers are remapped on a unique identifier for each GO label through a synonyms HashMap 
 * * made in the BiNGOOntologyFlatFileReader class
 * */

import cytoscape.data.annotation.Annotation;
import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextHttpReader;
import cytoscape.data.readers.TextJarReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
//-------------------------------------------------------------------------

public class BiNGOAnnotationFlatFileReader {
    private Annotation annotation;
    private String annotationType;
    private String species;
    private String curator;
    private String filename;
    private File directoryAbsolute;
    private String fullText;
    private String [] lines;
    private HashMap synonymHash;
    private HashMap<String,HashSet<String>> alias;

    /**
     * true if there are categories in the annotation which are not defined in the ontology
     */
    private boolean orphansFound = false;
    /**
     * false if none of the categories in the annotation match the ontology
     */
    private boolean consistency = false;
//-------------------------------------------------------------------------

    public BiNGOAnnotationFlatFileReader(File file, HashMap synonymHash) throws IllegalArgumentException, IOException, Exception {
        this(file.getPath(), synonymHash);
    }
//-------------------------------------------------------------------------

    public BiNGOAnnotationFlatFileReader(String filename, HashMap synonymHash) throws IllegalArgumentException, IOException, Exception {
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
            System.err.println("-- Exception while reading ontology flat file " + filename);
            System.err.println(e0.getMessage());
            throw e0;
            //return;
        }

        /****************
         this.species = species;
         this.annotationType = annotationType;
         directoryAbsolute = file.getAbsoluteFile().getParentFile ();
         TextFileReader reader = new TextFileReader (file.getPath ());
         reader.read ();
         fullText = reader.getText ();
         *************************/

        this.synonymHash = synonymHash;
        lines = fullText.split("\n");
        // System.out.println ("number of lines: " + lines.length);
        parseHeader(lines[0]);
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

    public void parseHeader(String firstLine) throws Exception {
        String [] tokens = firstLine.trim().split("\\)");

        String errorMsg = "error in AnnotationFlatFileReader.parseHeader ().\n";
        errorMsg += "First line of " + filename + " must have form:\n";
        errorMsg += "   (species=Homo sapiens) (type=Biological Process) (curator=GO)\n";
        errorMsg += "instead found:\n";
        errorMsg += "   " + firstLine + "\n";

        if (tokens.length != 3) throw new IllegalArgumentException(errorMsg);

        for (int i = 0; i < tokens.length; i++) {
            String [] subTokens = tokens[i].split("=");
            if (subTokens.length != 2) throw new IllegalArgumentException(errorMsg);
            String name = subTokens[0].trim();
            String value = subTokens[1].trim();
            if (name.equalsIgnoreCase("(species"))
                species = value;
            else if (name.equalsIgnoreCase("(type"))
                annotationType = value;
            else if (name.equalsIgnoreCase("(curator"))
                curator = value;
        }

    } // parseHeader
//-------------------------------------------------------------------------

    private void parse() throws Exception {
        annotation = new Annotation(species, annotationType, curator);
        HashSet orphans = new HashSet();
        alias = new HashMap();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.length() < 2) continue;
            String [] tokens = line.split("=");
            String entityName = tokens[0].trim().toUpperCase();
            int id = stringToInt(tokens[1].trim());
            Integer id2 = new Integer(id);
            Integer mainId = (Integer) synonymHash.get(id2);
            if (mainId != null) {
                if ((entityName != null) && (entityName.length() != 0) && (id != -1)) {
                    annotation.add(entityName, mainId.intValue());
                    HashSet tmp = new HashSet(); tmp.add(entityName);  
                    alias.put(entityName,tmp);
                }
                consistency = true;
            } else {
                orphans.add(id2);
                orphansFound = true;
            }
        }

    } // parse
//-------------------------------------------------------------------------

    public Annotation getAnnotation() {
        return annotation;
    }
    
    public HashMap getAlias(){
        return alias;
    }

    public boolean getConsistency() {
        return consistency;
    }

    public boolean getOrphans() {
        return orphansFound;
    }

//-------------------------------------------------------------------------
} // class BinGOAnnotationFlatFileReader


