// loadUpstreamSequences
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package csplugins.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import java.io.*;
import java.util.Vector;

import csplugins.motifFinder.data.*;
import csplugins.motifFinder.data.readers.*;
//------------------------------------------------------------------------------
/**
 *  load an upstreamSequences array, for the specified species, into an rmi 
 *  biodata server.  
 */
public class loadUpstreamSequences {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{ 
  if (args.length != 2) {
    System.err.println ("usage:  loadUpstreamSequences <server name> <upstreamSequences.xml>");
    System.exit (1);
    }

  String serverName = args [0];
  BioDataServer server = new BioDataServer (serverName);

  String filename = args [1];

  UpstreamSequenceXmlReader reader = new UpstreamSequenceXmlReader (new File (filename));
  UpstreamSequence [] seqs = reader.getSequences ();
  String species = reader.getSpecies ();
  System.out.println ("found " + seqs.length + "  sequences for " + species);
  server.addUpstreamSequences (species, seqs);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadUpstreamSequences
