// loadGoPathTerminators.java:  load goPathTerminators from a text file
// named on the command line.  a 'go path' is an ontological hierarchy
// (for biological process, molecular function, and cellular component)
// obtained from the gene ontology consortiums, expressed as a list of numbers.
// the numbers usually start with an ontological leaf node, and traverse
// the GO directed acyclic graph to a root node.  for example:
// yeast's gal4 gene has two leaf node go term ID's: 6012 & 6355
// (galactose metabolism & transcription regulation, respectively)
// the full path for 6012 is
//
//              1. [6012, 5996, 5975, 8152, 8151, 8150, 3673]
//                    # 6012   galactose metabolism
//                    # 5996   monosaccharide metabolism
//                    # 5975   carbohydrate metabolism
//                    # 8152   metabolism
//                    # 8151   cell growth and/or maintenance
//                    # 8150   biological_process
//                    # 3673   Gene_Ontology
//
// in some analytical setting, galactose metabolism might be best understood
// as an unspecified subclass of carbohydrate metabolism:  the path
// terminator would then be 5795.
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.*;
import cytoscape.data.readers.GoPathTerminatorTextReader;
import java.io.*;
import java.util.Vector;
//------------------------------------------------------------------------------
public class loadGoPathTerminators {
//------------------------------------------------------------------------------
public static void main (String [] args) throws Exception
{
  if (args.length != 1) {
    System.out.println ("usage:  loadGoPathTerminators <terminators text file>");
    System.exit (1);
    }

  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  String filename = args [0];
  GoPathTerminatorTextReader reader = new GoPathTerminatorTextReader (filename);
  Vector result = reader.read ();
  System.out.println ("loading " + result.size () + " gene goPathTerminatorss...");
  server.addGoPathTerminators (result);
  System.out.println (server.describe ());

} // main
//------------------------------------------------------------------------------
} // loadGoPathTerminators
