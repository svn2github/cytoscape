// synonyms.java:  report all known synonyms of a gene name
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class synonyms {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  if (args.length != 1) {
    System.out.println ("synonyms <geneName>");
    System.exit (0);
     }
  
  String geneName = args [0];
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);
  String [] synonyms = server.getSynonyms (geneName); 
  for (int i=0; i < synonyms.length; i++) 
     System.out.println (synonyms [i]);

} // main
//------------------------------------------------------------------------------
} // synonyms
