// ThesaurusFlatFileReader.java
//------------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data.synonyms.readers;
//------------------------------------------------------------------------------
import java.io.*; 
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

import cytoscape.data.synonyms.*;
import cytoscape.data.readers.TextFileReader;
//-------------------------------------------------------------------------
public class ThesaurusFlatFileReader { 
  File textFile;
  Thesaurus thesaurus;
//-------------------------------------------------------------------------
public ThesaurusFlatFileReader (String filename) throws Exception
{
  textFile = new File (filename);
  if (!textFile.canRead ()) {
    String msg = "---- data.synonyms.readers.ThesaurusFlatFileReader error, cannot read: " +
                  textFile;
    throw new Exception (msg);
    }

  read ();
}
//-------------------------------------------------------------------------
private void read () throws Exception
{
  TextFileReader reader = new TextFileReader (textFile.getPath ());
  reader.read ();
  String fullText = reader.getText ();
  String [] lines = fullText.split ("\n");

  String species = lines [0].trim();
  thesaurus = new Thesaurus (species);

  for (int i=1; i < lines.length; i++) {
    if (lines [i].trim().startsWith ("#")) continue;
    String [] tokens = lines [i].split ("\\s+", 0);
    if (tokens.length < 2) continue;  
    String canonicalName = tokens [0].trim();
    String commonName = tokens [1].trim();
    thesaurus.add (canonicalName, commonName);
    for (int t=2; t < tokens.length; t++) 
      thesaurus.addAlternateCommonName (canonicalName, tokens [t].trim());
    } // for i
  

} // read
//-------------------------------------------------------------------------
public Thesaurus getThesaurus ()
{
  return thesaurus;
}
//-------------------------------------------------------------------------
} // class ThesaurusFlatFileReader
