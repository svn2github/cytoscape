package BiNGO ;

// Adapted from : OntologyFlatFileReader.java in Cytoscape
//------------------------------------------------------------------------------
// $Revision: 1.4 $  $Date: 2006/07/24 13:29:50 $
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
* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
* * Changes : 1) correction for GO labels with multiple GO identifiers, these should be included only once in the ontology.
* * synonymous identifiers are remapped on a unique identifier for each GO label.
* * 2) ensure that root node (Gene_ontology 003673 in the case of GO) gets read in as well
* * 3) correction to avoid incorrect category names if they contain [ in the name
* *
* */


import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.OntologyTerm;
import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextHttpReader;
import cytoscape.data.readers.TextJarReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
//-------------------------------------------------------------------------

public class BiNGOOntologyFlatFileReader {
    Ontology ontology;
    String curator = "unknown";
    String ontologyType = "unknown";
    String filename;
    String fullText;
    String [] lines;
    HashMap synonymHash;
    HashMap goMap;
//-------------------------------------------------------------------------

    public BiNGOOntologyFlatFileReader(File file) throws IllegalArgumentException, IOException, Exception {
        this(file.getPath());
    }
//-------------------------------------------------------------------------

    public BiNGOOntologyFlatFileReader(String filename) throws IllegalArgumentException, IOException, Exception {
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
        this.synonymHash = new HashMap();
        this.goMap = new HashMap();
        lines = fullText.split("\n");
        parseHeader();
        synonyms();
        parse();

    } // ctor
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

    private void parseHeader() throws Exception {
        String firstLine = lines[0].trim();
        String [] tokens = firstLine.split("\\)");

        String errorMsg = "error in OntologyFlatFileReader.parseHeader ().\n";
        errorMsg += "First line of " + filename + " must have form:\n";
        errorMsg += "   (curator=GO) (type=all) \n";
        errorMsg += "instead found:\n";
        errorMsg += "   " + firstLine + "\n";

        if (tokens.length != 2) throw new IllegalArgumentException(errorMsg);

        String [] curatorRaw = tokens[0].split("=");
        if (curatorRaw.length != 2) throw new IllegalArgumentException(errorMsg);
        curator = curatorRaw[1].trim();

        String [] typeRaw = tokens[1].split("=");
        if (typeRaw.length != 2) throw new IllegalArgumentException(errorMsg);
        ontologyType = typeRaw[1].trim();

    } // parseHeader
//-------------------------------------------------------------------------

    private void synonyms() throws Exception {
        for (int i = 1; i < lines.length; i ++) {
            String line = lines[i];
            int equals = line.indexOf("=");
            String idString = line.substring(0, equals).trim();
            int id = stringToInt(idString);
            String value = line.substring(equals + 1);

            //adjusted : to avoid incorrect names if they contain [ in the name...
            int firstLeftBracket = value.indexOf("[isa: ");
            if (firstLeftBracket < 0) {
                firstLeftBracket = value.indexOf("[partof: ");
            }

            if (firstLeftBracket < 0) {
                String name = value.substring(0).trim();
                if (goMap.containsKey(name)) {
                    Integer id2 = new Integer(id);
                    synonymHash.put(id2, (Integer) goMap.get(name));
                } else {
                    Integer id2 = new Integer(id);
                    goMap.put(name, id2);
                    synonymHash.put(id2, (Integer) goMap.get(name));
                }
                continue;
            }
            String name = value.substring(0, firstLeftBracket).trim();
            if (goMap.containsKey(name)) {
                Integer id2 = new Integer(id);
                synonymHash.put(id2, (Integer) goMap.get(name));
            } else {
                Integer id2 = new Integer(id);
                goMap.put(name, id2);
                synonymHash.put(id2, (Integer) goMap.get(name));
            }

        }
		
		/*for(Object id : synonymHash.keySet()){
			System.out.println(id + "\t" + synonymHash.get(id)) ;
		}	*/
    }


    private void parse() throws Exception {
        ontology = new Ontology(curator, ontologyType);
        for (int i = 1; i < lines.length; i ++) {
            String line = lines[i];
            int equals = line.indexOf("=");
            String idString = line.substring(0, equals).trim();
            int id = stringToInt(idString);
            String value = line.substring(equals + 1);

            //adjusted : to avoid incorrect names if they contain [ in the name...
            int firstLeftBracket = value.indexOf("[isa: ");
            if (firstLeftBracket < 0) {
                firstLeftBracket = value.indexOf("[partof: ");
            }

            if (firstLeftBracket < 0) {
                String name = value.substring(0).trim();
                Integer id2 = new Integer(id);
				if(!ontology.containsTerm(((Integer) synonymHash.get(id2)).intValue())){
                  OntologyTerm term = new OntologyTerm(name, ((Integer) synonymHash.get(id2)).intValue());
                  ontology.add(term);
			    }
                continue;
            }
            String name = value.substring(0, firstLeftBracket).trim();
            Integer id2 = new Integer(id);
			OntologyTerm term ;
			//if(!ontology.containsTerm(((Integer) synonymHash.get(id2)).intValue())){
            	term = new OntologyTerm(name, ((Integer) synonymHash.get(id2)).intValue());
				ontology.add(term);
		    /*}
			else{
				term = ontology.getTerm(((Integer) synonymHash.get(id2)).intValue());
			}	*/
            int isaStart = value.indexOf("[isa: ");
            if (isaStart >= 0) {
                int isaEnd = value.indexOf("]", isaStart);
                String rawIsa = value.substring(isaStart + 6, isaEnd).trim();
                String [] allIsas = rawIsa.split(" ");

                for (int j = 0; j < allIsas.length; j++) {
                    Integer id3 = new Integer(stringToInt(allIsas[j]));
					//if((id3 == 6944) && (id2 == 6906)){System.out.println("isa OK " + ((Integer) synonymHash.get(id2)).intValue() + "\t" + ((Integer) synonymHash.get(id3)).intValue());}
                    term.addParent(((Integer) synonymHash.get(id3)).intValue());
                }
            } // found "[isa: "

            int partofStart = value.indexOf("[partof: ");
            if (partofStart >= 0) {
                int partofEnd = value.indexOf("]", partofStart);
                String rawPartof = value.substring(partofStart + 9, partofEnd).trim();
                String [] allPartofs = rawPartof.split(" ");
                for (int j = 0; j < allPartofs.length; j++) {
                    Integer id3 = new Integer(stringToInt(allPartofs[j]));
					//if((id3 == 6944) && (id2 == 6906)){System.out.println("partof OK");}
                    term.addContainer(((Integer) synonymHash.get(id3)).intValue());
                }
            } // if
        } // for i

    } // read
//-------------------------------------------------------------------------

    public Ontology getOntology() {
        return ontology;
    }

    public HashMap getSynonymHash() {
        return synonymHash;
    }

//-------------------------------------------------------------------------
} // class BiNGOOntologyFlatFileReader
