// getPathForTerm.java:  display all paths to root from a given GO term
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
public class getPathsForTerm {
//------------------------------------------------------------------------------
static void main (String [] args) throws Exception
{
  if (args.length != 1) {
    System.out.println ("getPath <goTerm>");
    System.exit (0);
     }

  String s = args [0];
  int goTerm = 0;
  goTerm = Integer.parseInt(s);
    
  String serverName = "rmi://localhost/biodata";
  BioDataServer server = (BioDataServer) Naming.lookup (serverName);

  Vector idPaths = server.getAllGoHierarchyPaths (goTerm);
  // System.out.println ("number of paths: " + idPaths.size ());

  for (int v=0; v < idPaths.size(); v++) {
    Vector idPath = (Vector) idPaths.elementAt (v);
    for (int m=0; m < idPath.size (); m++) {
      int id = ((Integer) idPath.elementAt (m)).intValue ();
      String molecularFunction = server.getMolecularFunctionName (id);
      System.out.print (id + " ");
      } // for m
    System.out.println ();
    } // for v

} // main
//------------------------------------------------------------------------------
} // getPathsForTerm

