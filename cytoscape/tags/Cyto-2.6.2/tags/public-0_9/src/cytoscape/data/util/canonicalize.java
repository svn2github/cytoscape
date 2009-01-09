// canonicalize.java
//   read a simple list of yeast common names from the named file,
//   write the corresponding ORF names (where known) to stdout.
//------------------------------------------------------------------------------
//  $Revision$  
//  $Date$
//------------------------------------------------------------------------------
package cytoscape.data.util;
//------------------------------------------------------------------------------
import java.io.*; 
import java.util.*;
import java.rmi.*;

import cytoscape.data.servers.*;
import cytoscape.data.readers.TextFileReader;
//-------------------------------------------------------------------------
public class canonicalize { 
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  if (args.length != 2) {
    System.err.println ("usage: canonicalize <bioDataServer URI> <simpleListFile>");
    System.err.println ("  bioDataServer URI may be either a directory, or an RMI server:");
    System.err.println ("    /package/genome/cytoscape/data/GO");
    System.err.println ("       or ");
    System.err.println ("   rmi://localhost/biodata");
    System.exit (1);
    }

  String bioDataServerURI = args [0];
  String filename = args [1];
  BioDataServer bioDataServer = BioDataServerFactory.create (bioDataServerURI);

  TextFileReader reader = new TextFileReader (filename);
  reader.read ();
  String fullText = reader.getText ();
  String [] commonNames = fullText.split ("\n");

  for (int i=0; i < commonNames.length; i++) {
    String canonicalName = bioDataServer.getCanonicalName (commonNames [i]);
    System.out.println (canonicalName);
    }


} // main
//------------------------------------------------------------------------------
} // canonicalize
