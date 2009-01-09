// AnnotationFlatFileReader.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------
// $Revision$  $Date$
//------------------------------------------------------------------------------
package cytoscape.data.annotation.readers;
//------------------------------------------------------------------------------
import java.io.*; 
import java.util.*;

import cytoscape.data.annotation.*;
import cytoscape.data.readers.*;
//-------------------------------------------------------------------------
public class AnnotationFlatFileReader { 
  Annotation annotation;
  String annotationType;
  String species;
  String curator;
  String filename;
  File directoryAbsolute;
  String fullText;
  String [] lines;
//-------------------------------------------------------------------------
public AnnotationFlatFileReader (File file) throws Exception
{
  this (file.getPath ());
}
//-------------------------------------------------------------------------
public AnnotationFlatFileReader (String filename) throws Exception
{
  //System.out.println ("AnnotationFlatFileReader on " + filename);
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
  catch (Exception e0) {
    System.err.println ("-- Exception while reading ontology flat file " + filename);
    System.err.println (e0.getMessage ());
    return;
    }

  /****************
  this.species = species;
  this.annotationType = annotationType;
  directoryAbsolute = file.getAbsoluteFile().getParentFile ();
  TextFileReader reader = new TextFileReader (file.getPath ());
  reader.read ();
  fullText = reader.getText ();
  *************************/

  lines = fullText.split ("\n");
  // System.out.println ("number of lines: " + lines.length);
  parseHeader (lines [0]);
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
public void parseHeader (String firstLine) throws Exception
{
  String [] tokens = firstLine.trim ().split ("\\)");

  String errorMsg = "error in AnnotationFlatFileReader.parseHeader ().\n";
  errorMsg += "First line of " + filename + " must have form:\n";
  errorMsg += "   (species=Homo sapiens) (type=Biological Process) (curator=GO)\n";
  errorMsg += "instead found:\n";
  errorMsg += "   " + firstLine + "\n";

  if (tokens.length !=3) throw new IllegalArgumentException (errorMsg);

  for (int i=0; i < tokens.length; i++) {
    String [] subTokens = tokens [i].split ("=");
    if (subTokens.length != 2) throw new IllegalArgumentException (errorMsg);
    String name = subTokens [0].trim ();
    String value = subTokens [1].trim ();
    if (name.equalsIgnoreCase ("(species"))
      species = value;
    else if (name.equalsIgnoreCase ("(type"))
      annotationType = value;
    else if (name.equalsIgnoreCase ("(curator"))
      curator = value;
    }

} // parseHeader
//-------------------------------------------------------------------------
private void parse () throws Exception
{
  annotation = new Annotation (species, annotationType, curator);

  for (int i=1; i < lines.length; i++) {
    String line = lines [i];
    if (line.length () < 2) continue;
    String [] tokens = line.split ("=");
    String entityName = tokens [0].trim ();
    int id = stringToInt (tokens [1].trim());    
    annotation.add (entityName, id);
    }

  // System.out.println ("AnnotationFlatFileReader.parse, annotation:\n" + annotation);

} // parse
//-------------------------------------------------------------------------
public Annotation getAnnotation ()
{
  return annotation;
}
//-------------------------------------------------------------------------
} // class AnnotationFlatFileReader


